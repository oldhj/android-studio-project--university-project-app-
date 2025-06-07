package com.hevttc.bigwork.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hevttc.bigwork.bean.Todo;
import com.hevttc.bigwork.util.StudentHelper;

import java.util.ArrayList;
import java.util.List;

public class TodoDao {
    private final StudentHelper dbHelper;

    public TodoDao(Context context) {
        dbHelper = StudentHelper.getInstance(context);
    }
    // 添加待办
    public long insertTodo(String studentId, Todo todo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StudentHelper.COLUMN_STUDENT_ID, studentId); // 添加学生ID
        values.put(StudentHelper.COLUMN_CONTENT, todo.getContent());
        return db.insert(StudentHelper.TABLE_TODOS, null, values);
    }

    // 获取所有待办
    public List<Todo> getAllTodos(String studentId) {
        List<Todo> todos = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                StudentHelper.COLUMN_TODO_ID,
                StudentHelper.COLUMN_CONTENT,
                StudentHelper.COLUMN_CREATED_AT,
                StudentHelper.COLUMN_IS_COMPLETED
        };

        // 添加学生ID查询条件
        String selection = StudentHelper.COLUMN_STUDENT_ID + " = ?";
        String[] selectionArgs = { studentId };

        Cursor cursor = db.query(
                StudentHelper.TABLE_TODOS,
                projection,
                selection,  // 使用带学生ID的查询条件
                selectionArgs,
                null,
                null,
                StudentHelper.COLUMN_CREATED_AT + " DESC"
        );

        while (cursor.moveToNext()) {
            Todo item = new Todo();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(StudentHelper.COLUMN_TODO_ID)));
            item.setContent(cursor.getString(cursor.getColumnIndexOrThrow(StudentHelper.COLUMN_CONTENT)));
            item.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(StudentHelper.COLUMN_CREATED_AT)));
            item.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(StudentHelper.COLUMN_IS_COMPLETED)) == 1);
            todos.add(item);
        }
        cursor.close();
        return todos;
    }

    // 更新完成状态
    public int updateCompletionStatus(int todoId, String studentId, boolean isCompleted) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StudentHelper.COLUMN_IS_COMPLETED, isCompleted ? 1 : 0);

        // 添加学生ID条件
        String whereClause = StudentHelper.COLUMN_TODO_ID + " = ? AND " +
                StudentHelper.COLUMN_STUDENT_ID + " = ?";
        String[] whereArgs = {String.valueOf(todoId), studentId};

        return db.update(
                StudentHelper.TABLE_TODOS,
                values,
                whereClause,
                whereArgs
        );
    }

    // 删除待办
    public int deleteTodo(int todoId, String studentId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 添加学生ID条件
        String whereClause = StudentHelper.COLUMN_TODO_ID + " = ? AND " +
                StudentHelper.COLUMN_STUDENT_ID + " = ?";
        String[] whereArgs = {String.valueOf(todoId), studentId};

        return db.delete(
                StudentHelper.TABLE_TODOS,
                whereClause,
                whereArgs
        );
    }
}
