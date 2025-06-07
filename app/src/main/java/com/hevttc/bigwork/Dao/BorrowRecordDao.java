package com.hevttc.bigwork.Dao;

import static com.hevttc.bigwork.util.StudentHelper.COLUMN_BOOK_ID;
import static com.hevttc.bigwork.util.StudentHelper.COLUMN_BORROW_DATE;
import static com.hevttc.bigwork.util.StudentHelper.COLUMN_DUE_DATE;
import static com.hevttc.bigwork.util.StudentHelper.COLUMN_RECORD_ID;
import static com.hevttc.bigwork.util.StudentHelper.COLUMN_RETURNED;
import static com.hevttc.bigwork.util.StudentHelper.COLUMN_SELECTION_STUDENT_ID;
import static com.hevttc.bigwork.util.StudentHelper.COLUMN_STOCK;
import static com.hevttc.bigwork.util.StudentHelper.TABLE_BOOKS;
import static com.hevttc.bigwork.util.StudentHelper.TABLE_BORROW_RECORDS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hevttc.bigwork.bean.BorrowRecord;
import com.hevttc.bigwork.util.StudentHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BorrowRecordDao {
    private final StudentHelper dbHelper;

    public BorrowRecordDao(Context context) {
        dbHelper = StudentHelper.getInstance(context);
    }

    public boolean borrowBook(String studentId, int bookId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();

            // ========== 1. 检查是否已借未还 ==========
            Cursor checkCursor = db.query(
                    TABLE_BORROW_RECORDS,
                    new String[]{COLUMN_RECORD_ID},
                    COLUMN_SELECTION_STUDENT_ID + " = ? AND " +
                            COLUMN_BOOK_ID + " = ? AND " +
                            COLUMN_RETURNED + " = 0",
                    new String[]{studentId, String.valueOf(bookId)},
                    null, null, null
            );

            if (checkCursor.getCount() > 0) {
                checkCursor.close();
                return false; // 已有未归还记录
            }
            checkCursor.close();

            // ========== 2. 检查库存 ==========
            Cursor stockCursor = db.query(
                    TABLE_BOOKS,
                    new String[]{COLUMN_STOCK},
                    COLUMN_BOOK_ID + " = ?",
                    new String[]{String.valueOf(bookId)},
                    null, null, null
            );

            if (!stockCursor.moveToFirst() || stockCursor.getInt(0) <= 0) {
                stockCursor.close();
                return false;
            }
            int currentStock = stockCursor.getInt(0);
            stockCursor.close();

            // ========== 3. 更新库存 ==========
            ContentValues bookValues = new ContentValues();
            bookValues.put(COLUMN_STOCK, currentStock - 1);

            int updateRows = db.update(
                    TABLE_BOOKS,
                    bookValues,
                    COLUMN_BOOK_ID + " = ?",
                    new String[]{String.valueOf(bookId)}
            );

            if (updateRows == 0) {
                return false;
            }

            // ========== 4. 添加借阅记录 ==========
            ContentValues recordValues = new ContentValues();
            recordValues.put(COLUMN_SELECTION_STUDENT_ID, studentId);
            recordValues.put(COLUMN_BOOK_ID, bookId);
            // 设置借阅时间（使用系统时间）
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            recordValues.put(COLUMN_BORROW_DATE, sdf.format(new Date()));
            // 设置应还时间（14天后）
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 14);
            recordValues.put(COLUMN_DUE_DATE, sdf.format(calendar.getTime()));

            long result = db.insert(TABLE_BORROW_RECORDS, null, recordValues);

            db.setTransactionSuccessful();
            return result != -1;
        } finally {
            db.endTransaction();
        }
    }
    public List<BorrowRecord> getRecordsByUser(String studentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<BorrowRecord> records = new ArrayList<>();

        // 添加归还状态筛选条件
        String selection = COLUMN_SELECTION_STUDENT_ID + " = ? AND "
                + StudentHelper.COLUMN_RETURNED + " = 0";
        String[] selectionArgs = { studentId };

        Cursor cursor = db.query(
                StudentHelper.TABLE_BORROW_RECORDS,
                null,
                selection,
                selectionArgs,
                null, null, null
        );

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        while (cursor.moveToNext()) {
            BorrowRecord record = new BorrowRecord();
            record.setRecordId(cursor.getInt(0));
            record.setStudentId(cursor.getString(1));
            record.setBookId(cursor.getInt(2));

            try {
                record.setBorrowDate(sdf.parse(cursor.getString(3)));
                record.setDueDate(sdf.parse(cursor.getString(4)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            record.setReturned(cursor.getInt(5) == 1);
            records.add(record);
        }
        cursor.close();
        return records;
    }
    // BorrowRecordDao.java 中的 returnBook 方法完整实现
    public boolean returnBook(int recordId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();

            // ========== 1. 获取图书ID和当前库存 ==========
            int bookId = -1;
            int currentStock = 0;

            // (1) 查询借阅记录获取图书ID
            Cursor borrowCursor = db.rawQuery(
                    "SELECT " + COLUMN_BOOK_ID +
                            " FROM " + StudentHelper.TABLE_BORROW_RECORDS +
                            " WHERE " + COLUMN_RECORD_ID + " = ?",
                    new String[]{String.valueOf(recordId)}
            );

            if (borrowCursor.moveToFirst()) {
                bookId = borrowCursor.getInt(0);

                // (2) 查询图书当前库存
                Cursor bookCursor = db.query(
                        TABLE_BOOKS,
                        new String[]{COLUMN_STOCK},
                        COLUMN_BOOK_ID + " = ?",
                        new String[]{String.valueOf(bookId)},
                        null, null, null
                );

                if (bookCursor.moveToFirst()) {
                    currentStock = bookCursor.getInt(0); // 正确获取库存值
                    Log.d("DB_DEBUG", "当前库存: " + currentStock);
                } else {
                    Log.e("DB_ERROR", "未找到对应图书, ID: " + bookId);
                    return false;
                }
                bookCursor.close();
            }
            borrowCursor.close();

            if (bookId == -1) {
                Log.e("DB_ERROR", "无效的借阅记录ID: " + recordId);
                return false;
            }

            // ========== 2. 更新库存 ==========
            ContentValues bookValues = new ContentValues();
            bookValues.put(COLUMN_STOCK, currentStock + 1);

            int updateRows = db.update(
                    TABLE_BOOKS,
                    bookValues,
                    COLUMN_BOOK_ID + " = ?",
                    new String[]{String.valueOf(bookId)}
            );

            if (updateRows == 0) {
                Log.e("DB_ERROR", "库存更新失败，bookId: " + bookId);
                return false;
            }

            // ========== 3. 标记归还状态 ==========
            ContentValues recordValues = new ContentValues();
            recordValues.put(StudentHelper.COLUMN_RETURNED, 1);

            int affectedRows = db.update(
                    StudentHelper.TABLE_BORROW_RECORDS,
                    recordValues,
                    COLUMN_RECORD_ID + " = ?",
                    new String[]{String.valueOf(recordId)}
            );

            db.setTransactionSuccessful();
            return affectedRows > 0;

        } catch (Exception e) {
            Log.e("DB_ERROR", "归还操作异常: ", e);
            return false;
        } finally {
            db.endTransaction();
            //db.close(); // 确保关闭数据库连接
        }
    }
    public boolean hasUnreturnedRecord(String studentId, int bookId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_BORROW_RECORDS,
                new String[]{COLUMN_RECORD_ID},
                COLUMN_SELECTION_STUDENT_ID + " = ? AND " +
                        COLUMN_BOOK_ID + " = ? AND " +
                        COLUMN_RETURNED + " = 0",
                new String[]{studentId, String.valueOf(bookId)},
                null, null, null
        );

        boolean hasRecord = cursor.getCount() > 0;
        cursor.close();
        return hasRecord;
    }
}