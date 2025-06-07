package com.hevttc.bigwork.Dao;

import static com.hevttc.bigwork.util.StudentHelper.COLUMN_IS_RESERVED;
import static com.hevttc.bigwork.util.StudentHelper.COLUMN_SEAT_ID;
import static com.hevttc.bigwork.util.StudentHelper.COLUMN_SELECTION_STUDENT_ID;
import static com.hevttc.bigwork.util.StudentHelper.TABLE_SEAT_RESERVATIONS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hevttc.bigwork.bean.Seat;
import com.hevttc.bigwork.util.StudentHelper;

import java.util.ArrayList;
import java.util.List;

public class SeatReservationDao {
    private final StudentHelper dbHelper;

    public SeatReservationDao(Context context) {
        dbHelper = StudentHelper.getInstance(context);
    }

    public boolean saveReservation(Seat seat, String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 使用事务确保原子性
        db.beginTransaction();
        try {
            // 1. 取消用户所有现有预约
            cancelUserReservations(userId);

            // 2. 保存新预约
            ContentValues values = new ContentValues();
            values.put(COLUMN_SEAT_ID, seat.getId());
            values.put(StudentHelper.COLUMN_SEAT_ROW, seat.getRow());
            values.put(StudentHelper.COLUMN_SEAT_COLUMN, seat.getColumn());
            values.put(COLUMN_IS_RESERVED, 1);
            values.put(COLUMN_SELECTION_STUDENT_ID, userId);
            values.put(StudentHelper.COLUMN_RESERVATION_TIME, System.currentTimeMillis());

            long result = db.insert(TABLE_SEAT_RESERVATIONS, null, values);

            db.setTransactionSuccessful();
            return result != -1;
        } finally {
            db.endTransaction();
        }
    }

    public List<Seat> getAllSeats() {
        List<Seat> seats = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 只查询有效预约
        String selection = COLUMN_IS_RESERVED + " = 1";

        Cursor cursor = db.query(
                TABLE_SEAT_RESERVATIONS,
                null,
                selection,
                null,
                null, null, null
        );

        while (cursor.moveToNext()) {
            Seat seat = new Seat();

            // 安全获取列索引
            int idIndex = cursor.getColumnIndex(COLUMN_SEAT_ID);
            int rowIndex = cursor.getColumnIndex(StudentHelper.COLUMN_SEAT_ROW);
            int colIndex = cursor.getColumnIndex(StudentHelper.COLUMN_SEAT_COLUMN);
            int timeIndex = cursor.getColumnIndex(StudentHelper.COLUMN_RESERVATION_TIME);

            if (idIndex >= 0) seat.setId(cursor.getString(idIndex));
            if (rowIndex >= 0) seat.setRow(cursor.getInt(rowIndex));
            if (colIndex >= 0) seat.setColumn(cursor.getInt(colIndex));
            seat.setReserved(true); // 所有查询结果都是已预约
            if (timeIndex >= 0) seat.setReservationTime(cursor.getLong(timeIndex));

            seats.add(seat);
        }

        if (cursor != null) cursor.close();
        return seats;
    }
    // SeatReservationDao.java
    public List<Seat> getUserReservations(String userId) {
        List<Seat> seats = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_SEAT_RESERVATIONS,
                new String[]{
                        COLUMN_SEAT_ID,
                        StudentHelper.COLUMN_SEAT_ROW,   // 使用正确的常量
                        StudentHelper.COLUMN_SEAT_COLUMN, // 使用正确的常量
                        COLUMN_IS_RESERVED,
                        StudentHelper.COLUMN_RESERVATION_TIME
                },
                COLUMN_SELECTION_STUDENT_ID + " = ?",
                new String[]{userId},
                null, null, null
        );

        while (cursor.moveToNext()) {
            try {
                Seat seat = new Seat();
                // 使用 getColumnIndexOrThrow 确保列存在
                seat.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SEAT_ID)));
                seat.setRow(cursor.getInt(cursor.getColumnIndexOrThrow(StudentHelper.COLUMN_SEAT_ROW)));
                seat.setColumn(cursor.getInt(cursor.getColumnIndexOrThrow(StudentHelper.COLUMN_SEAT_COLUMN)));
                seat.setReserved(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_RESERVED)) == 1);
                seat.setReservationTime(cursor.getLong(cursor.getColumnIndexOrThrow(StudentHelper.COLUMN_RESERVATION_TIME)));
                seats.add(seat);
            } catch (IllegalArgumentException e) {
                // 处理列不存在的异常
                e.printStackTrace();
            }
        }

        cursor.close();
        //db.close();
        return seats;
    }
    // 获取用户当前预约的座位
    public Seat getUserCurrentReservation(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
                COLUMN_SEAT_ID,
                StudentHelper.COLUMN_SEAT_ROW,
                StudentHelper.COLUMN_SEAT_COLUMN,
                COLUMN_IS_RESERVED,
                StudentHelper.COLUMN_RESERVATION_TIME
        };

        String selection = COLUMN_SELECTION_STUDENT_ID + " = ? AND " + COLUMN_IS_RESERVED + " = 1";
        String[] selectionArgs = {userId};

        try (Cursor cursor = db.query(
                TABLE_SEAT_RESERVATIONS,
                columns,
                selection,
                selectionArgs,
                null, null, null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                Seat seat = new Seat();

                int idIndex = cursor.getColumnIndex(COLUMN_SEAT_ID);
                int rowIndex = cursor.getColumnIndex(StudentHelper.COLUMN_SEAT_ROW);
                int colIndex = cursor.getColumnIndex(StudentHelper.COLUMN_SEAT_COLUMN);
                int timeIndex = cursor.getColumnIndex(StudentHelper.COLUMN_RESERVATION_TIME);

                if (idIndex >= 0) seat.setId(cursor.getString(idIndex));
                if (rowIndex >= 0) seat.setRow(cursor.getInt(rowIndex));
                if (colIndex >= 0) seat.setColumn(cursor.getInt(colIndex));
                if (timeIndex >= 0) seat.setReservationTime(cursor.getLong(timeIndex));

                seat.setReserved(true);
                return seat;
            }
        } catch (Exception e) {
            Log.e("SeatReservationDao", "获取用户预约错误: " + e.getMessage());
        }
        return null;
    }

    // 取消用户所有预约
    public void cancelUserReservations(String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 使用事务确保原子性
        db.beginTransaction();
        try {
            // 删除用户的所有预约记录
            db.delete(
                    TABLE_SEAT_RESERVATIONS,
                    COLUMN_SELECTION_STUDENT_ID + " = ?",
                    new String[]{userId}
            );

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    // 新增：检查座位是否被预约
    public boolean isSeatReserved(String seatId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {COLUMN_SEAT_ID};
        String selection = COLUMN_SEAT_ID + " = ? AND " + COLUMN_IS_RESERVED + " = 1";
        String[] selectionArgs = {seatId};

        try (Cursor cursor = db.query(
                TABLE_SEAT_RESERVATIONS,
                columns,
                selection,
                selectionArgs,
                null, null, null
        )) {
            return cursor.getCount() > 0;
        }
    }
    // 新增DAO方法
    public Seat getSeatDetails(String seatId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 明确指定要查询的列
        String[] columns = {
                COLUMN_SEAT_ID,
                StudentHelper.COLUMN_SEAT_ROW,
                StudentHelper.COLUMN_SEAT_COLUMN,
                COLUMN_IS_RESERVED,
                StudentHelper.COLUMN_RESERVATION_TIME
        };

        String selection = COLUMN_SEAT_ID + " = ?";
        String[] selectionArgs = {seatId};

        try (Cursor cursor = db.query(
                TABLE_SEAT_RESERVATIONS,
                columns, // 使用明确的列列表
                selection,
                selectionArgs,
                null, null, null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                Seat seat = new Seat();

                // 安全获取列索引
                int idIndex = cursor.getColumnIndex(COLUMN_SEAT_ID);
                int rowIndex = cursor.getColumnIndex(StudentHelper.COLUMN_SEAT_ROW);
                int colIndex = cursor.getColumnIndex(StudentHelper.COLUMN_SEAT_COLUMN);
                int reservedIndex = cursor.getColumnIndex(COLUMN_IS_RESERVED);
                int timeIndex = cursor.getColumnIndex(StudentHelper.COLUMN_RESERVATION_TIME);

                // 仅当列存在时才设置值
                if (idIndex >= 0) seat.setId(cursor.getString(idIndex));
                if (rowIndex >= 0) seat.setRow(cursor.getInt(rowIndex));
                if (colIndex >= 0) seat.setColumn(cursor.getInt(colIndex));
                if (reservedIndex >= 0) seat.setReserved(cursor.getInt(reservedIndex) == 1);
                if (timeIndex >= 0) seat.setReservationTime(cursor.getLong(timeIndex));

                return seat;
            }
        } catch (Exception e) {
            Log.e("SeatReservationDao", "获取座位详情错误: " + e.getMessage());
        }
        return null;
    }

}
