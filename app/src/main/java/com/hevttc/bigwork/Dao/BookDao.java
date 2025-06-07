// BookDao.java
package com.hevttc.bigwork.Dao;

import static com.hevttc.bigwork.util.StudentHelper.COLUMN_BOOK_ID;
import static com.hevttc.bigwork.util.StudentHelper.TABLE_BOOKS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.hevttc.bigwork.bean.Book;
import com.hevttc.bigwork.util.StudentHelper;
import java.util.ArrayList;
import java.util.List;

public class BookDao {
    private final StudentHelper dbHelper;

    public BookDao(Context context) {
        dbHelper = StudentHelper.getInstance(context);
    }

    public long addBook(Book book) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StudentHelper.COLUMN_TITLE, book.getTitle());
        values.put(StudentHelper.COLUMN_AUTHOR, book.getAuthor());
        values.put(StudentHelper.COLUMN_ISBN, book.getIsbn());
        values.put(StudentHelper.COLUMN_STOCK, book.getStock());
        return db.insert(TABLE_BOOKS, null, values);
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKS,
                null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Book book = new Book();
            book.setBookId(cursor.getInt(0));
            book.setTitle(cursor.getString(1));
            book.setAuthor(cursor.getString(2));
            book.setIsbn(cursor.getString(3));
            book.setStock(cursor.getInt(4));
            books.add(book);
        }
        cursor.close();
        return books;
    }
//    public List<Book> searchBooks(String query) {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.query(
//                StudentHelper.TABLE_BOOKS,
//                null,
//                StudentHelper.COLUMN_TITLE + " LIKE ? OR " +
//                        StudentHelper.COLUMN_AUTHOR + " LIKE ?",
//                new String[]{"%" + query + "%", "%" + query + "%"},
//                null, null, null
//        );
//
//        List<Book> books = new ArrayList<>();
//        while (cursor.moveToNext()) {
//            // 解析数据逻辑...
//        }
//        cursor.close();
//        return books;
//    }
    // 确保 BookDao 的 searchBooks 方法正确实现
    public List<Book> searchBooks(String query) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_BOOKS,
                null,
                StudentHelper.COLUMN_TITLE + " LIKE ? OR " +
                        StudentHelper.COLUMN_AUTHOR + " LIKE ?",
                new String[]{"%" + query + "%", "%" + query + "%"},
                null, null, null
        );

        List<Book> books = new ArrayList<>();
        while (cursor.moveToNext()) {
            Book book = new Book();
            book.setBookId(cursor.getInt(0));
            book.setTitle(cursor.getString(1));
            book.setAuthor(cursor.getString(2));
            book.setIsbn(cursor.getString(3));
            book.setStock(cursor.getInt(4));
            books.add(book);
        }
        cursor.close();
        return books;
    }
    // BookDao.java
    public Book getBookById(int bookId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Book book = null;

        Cursor cursor = db.query(
                TABLE_BOOKS,
                null,
                COLUMN_BOOK_ID + " = ?",
                new String[]{String.valueOf(bookId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            book = new Book(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4)
            );
        }
        cursor.close();
        return book;
    }
}