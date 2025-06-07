package com.hevttc.bigwork.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.hevttc.bigwork.util.StudentHelper;

public class StudentCourseDao {
    private final StudentHelper dbHelper;

    public StudentCourseDao(Context context) {
        dbHelper = StudentHelper.getInstance(context);
    }

    // 添加学生选课关系
    public long addStudentCourse(String studentId, int courseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("course_id", courseId);
        return db.insert("student_course", null, values);
    }

    // 删除学生选课关系
    public int deleteStudentCourse(String studentId, int courseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("student_course",
                "student_id=? AND course_id=?",
                new String[]{studentId, String.valueOf(courseId)});
    }
}
