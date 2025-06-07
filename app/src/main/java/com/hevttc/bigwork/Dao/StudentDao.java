package com.hevttc.bigwork.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.hevttc.bigwork.bean.Course;
import com.hevttc.bigwork.bean.Student;
import com.hevttc.bigwork.util.StudentHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudentDao {
    private final StudentHelper dbHelper;


    public StudentDao(Context context) {
        dbHelper = StudentHelper.getInstance(context); // 使用单例

    }

    // 检查学号是否存在
    public boolean isStudentIdExists(String studentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "student",
                new String[]{"studentId"},
                "studentId = ?",
                new String[]{studentId},
                null, null, null
        );
        boolean exists = cursor.getCount() > 0;
        //cursor.close();
        //db.close();
        return exists;
    }

    // 插入新学生
    public long insertStudent(Student student) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("studentId", student.getStudentId());
        values.put("password", student.getPassword());
        values.put("phone", student.getPhone());
        values.put("email", student.getEmail());
        // 其他字段可根据需求添加
        long result = db.insert("student", null, values);
        //db.close();
        return result;
    }

    // 验证登录
    public boolean checkLogin(String studentId, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "student",
                new String[]{"studentId"},
                "studentId = ? AND password = ?",
                new String[]{studentId, password},
                null, null, null
        );
        boolean isValid = cursor.moveToFirst();
        cursor.close();
        //db.close();
        return isValid;
    }

    // 更新学生信息（添加手机号和邮箱）
    public boolean updateStudent(String studentId, String newName, String newFaculty,
                                 String newMajor, String phone, String email) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("name", newName);
            values.put("faculty", newFaculty);
            values.put("major", newMajor);
            values.put("phone", phone);
            values.put("email", email);

            int rowsAffected = db.update(
                    "student",
                    values,
                    "studentId = ?",
                    new String[]{studentId}
            );
            return rowsAffected > 0;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 获取学生完整信息（添加手机号和邮箱）
    public Student getStudent(String studentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Student student = null;
        Cursor cursor = db.query(
                "student",
                new String[]{"studentId", "name", "faculty", "major", "phone", "email", "lastLogin"},
                "studentId=?",
                new String[]{studentId},
                null, null, null
        );
        if (cursor != null && cursor.moveToFirst()) {
            student = new Student();
            student.setStudentId(cursor.getString(0));
            student.setName(cursor.getString(1));
            student.setFaculty(cursor.getString(2));
            student.setMajor(cursor.getString(3));

            // 添加手机号和邮箱
            int phoneIndex = cursor.getColumnIndex("phone");
            if (phoneIndex >= 0) student.setPhone(cursor.getString(phoneIndex));

            int emailIndex = cursor.getColumnIndex("email");
            if (emailIndex >= 0) student.setEmail(cursor.getString(emailIndex));

            int lastLoginIndex = cursor.getColumnIndex("lastLogin");
            if (lastLoginIndex >= 0) student.setLastLogin(cursor.getString(lastLoginIndex));
        }
        if (cursor != null) cursor.close();
        return student;
    }

    // 更新登录时间
    public void updateLastLogin(String studentId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("lastLogin", new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date()));
        db.update("student", values, "studentId=?", new String[]{studentId});
        //db.close();
    }
    // 更新密码
    public boolean updatePassword(String studentId, String newPassword) {
        // 确保参数有效
        if (studentId == null || newPassword == null || newPassword.isEmpty()) {
            Log.e("StudentDao", "Invalid parameters for updatePassword");
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("password", newPassword);

            int rowsAffected = db.update(
                    "student",
                    values,
                    "studentId = ?",
                    new String[]{studentId}
            );

            Log.d("StudentDao", "Password updated for: " + studentId +
                    ", rows affected: " + rowsAffected);

            return rowsAffected > 0;
        } catch (SQLiteException e) {
            Log.e("StudentDao", "Error updating password: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    // 添加验证旧密码的方法
    public boolean verifyOldPassword(String studentId, String oldPassword) {
        // 确保参数不为 null
        if (studentId == null || oldPassword == null) {
            Log.e("StudentDao", "Invalid parameters: studentId=" + studentId + ", oldPassword=" + oldPassword);
            return false;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "student",
                    new String[]{"studentId"},
                    "studentId = ? AND password = ?",
                    new String[]{studentId, oldPassword},
                    null, null, null
            );
            return cursor != null && cursor.getCount() > 0;
        } catch (SQLiteException e) {
            Log.e("StudentDao", "Error verifying password: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

}
