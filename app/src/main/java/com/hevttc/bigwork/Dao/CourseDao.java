package com.hevttc.bigwork.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hevttc.bigwork.bean.Course;
import com.hevttc.bigwork.util.StudentHelper;

import java.util.ArrayList;
import java.util.List;

public class CourseDao {
    private final StudentHelper dbHelper;

    public CourseDao(Context context) {
        dbHelper = StudentHelper.getInstance(context); // 复用单例Helper
    }

    // 插入课程
    public long insertCourse(Course course) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", course.getName());
        values.put("week_day", course.getWeekDay());
        values.put("start_time", course.getStartTime());
        values.put("end_time", course.getEndTime());
        values.put("room", course.getRoom());
        values.put("teacher", course.getTeacher());
        return db.insert("course", null, values);
        // 注意：不要调用 db.close()，由 Helper 管理连接
    }


    // 获取某天的课程
    public List<Course> getCoursesByDay(int weekDay) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Course> courses = new ArrayList<>();
        String[] columns = {
                "id", "name", "week_day", "start_time", "end_time", "room", "teacher"
        };
        Cursor cursor = db.query(
                "course", columns, "week_day=?",
                new String[]{String.valueOf(weekDay)}, null, null, "start_time ASC"
        );

        while (cursor.moveToNext()) {
            Course course = new Course();
            course.setId(cursor.getInt(0));
            course.setName(cursor.getString(1));
            course.setWeekDay(cursor.getInt(2));
            course.setStartTime(cursor.getString(3));
            course.setEndTime(cursor.getString(4));
            course.setRoom(cursor.getString(5));
            course.setTeacher(cursor.getString(6));
            courses.add(course);
        }
        cursor.close();
        return courses;
    }
    public List<Course> getTodayUpcomingCourses(int weekDay, String currentTime) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Course> courses = new ArrayList<>();

        String[] columns = {
                "id", "name", "week_day", "start_time", "end_time", "room", "teacher"
        };

        // 查询条件：当天 + 开始时间 > 当前时间
        String selection = "week_day = ? AND start_time > ?";
        String[] selectionArgs = {String.valueOf(weekDay), currentTime};

        Cursor cursor = db.query(
                "course",
                columns,
                selection,
                selectionArgs,
                null,
                null,
                "start_time ASC" // 按开始时间升序排列
        );

        while (cursor.moveToNext()) {
            Course course = new Course();
            course.setId(cursor.getInt(0));
            course.setName(cursor.getString(1));
            course.setWeekDay(cursor.getInt(2));
            course.setStartTime(cursor.getString(3));
            course.setEndTime(cursor.getString(4));
            course.setRoom(cursor.getString(5));
            course.setTeacher(cursor.getString(6));
            courses.add(course);
        }
        cursor.close();
        return courses;
    }

    // 在CourseDao中添加方法
    public List<Course> getCoursesByStudentAndDay(String studentId, int weekDay) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Course> courses = new ArrayList<>();

        String query = "SELECT c.* FROM course c " +
                "INNER JOIN student_course sc ON c.id = sc.course_id " +
                "WHERE sc.student_id = ? AND c.week_day = ? " +
                "ORDER BY c.start_time ASC";

        Cursor cursor = db.rawQuery(query, new String[]{studentId, String.valueOf(weekDay)});

        while (cursor.moveToNext()) {
            Course course = new Course();
            course.setId(cursor.getInt(0));
            course.setName(cursor.getString(1));
            course.setWeekDay(cursor.getInt(2));
            course.setStartTime(cursor.getString(3));
            course.setEndTime(cursor.getString(4));
            course.setRoom(cursor.getString(5));
            course.setTeacher(cursor.getString(6));
            courses.add(course);
        }
        cursor.close();
        return courses;
    }

    public List<Course> getTodayUpcomingCoursesByStudent(int weekDay, String currentTime, String studentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Course> courses = new ArrayList<>();

        String query = "SELECT c.* FROM " + StudentHelper.TABLE_COURSE + " c " +
                "INNER JOIN " + StudentHelper.TABLE_COURSE_SELECTION + " sc " +
                "ON c." + StudentHelper.COLUMN_COURSE_ID + " = sc." + StudentHelper.COLUMN_SELECTION_COURSE_ID + " " +
                "WHERE sc." + StudentHelper.COLUMN_SELECTION_STUDENT_ID + " = ? " +
                "AND c." + StudentHelper.COLUMN_WEEK_DAY + " = ? " +
                "AND c." + StudentHelper.COLUMN_START_TIME + " > ? " +
                "ORDER BY c." + StudentHelper.COLUMN_START_TIME + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{studentId, String.valueOf(weekDay), currentTime});

        while (cursor.moveToNext()) {
            Course course = new Course();
            course.setId(cursor.getInt(0));
            course.setName(cursor.getString(1));
            course.setWeekDay(cursor.getInt(2));
            course.setStartTime(cursor.getString(3));
            course.setEndTime(cursor.getString(4));
            course.setRoom(cursor.getString(5));
            course.setTeacher(cursor.getString(6));
            courses.add(course);
        }
        cursor.close();
        return courses;
    }

    // 添加新课程
    public long addCourse(Course course) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", course.getName());
        values.put("week_day", course.getWeekDay());
        values.put("start_time", course.getStartTime());
        values.put("end_time", course.getEndTime());
        values.put("room", course.getRoom());
        values.put("teacher", course.getTeacher());

        // 添加检查避免重复课程
        if (isCourseExists(course)) {
            return -1; // 课程已存在
        }

        long result = db.insert("course", null, values);
        return result;
    }
    // 检查课程是否已存在
    private boolean isCourseExists(Course course) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("course",
                new String[]{"id"},
                "name = ? AND week_day = ? AND start_time = ?",
                new String[]{course.getName(), String.valueOf(course.getWeekDay()), course.getStartTime()},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public List<Course> getAllCourses() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Course> courses = new ArrayList<>();
        Cursor cursor = db.query("course", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Course course = new Course();
            course.setId(cursor.getInt(0));
            course.setName(cursor.getString(1));
            course.setWeekDay(cursor.getInt(2));
            course.setStartTime(cursor.getString(3));
            course.setEndTime(cursor.getString(4));
            course.setRoom(cursor.getString(5));
            course.setTeacher(cursor.getString(6));
            courses.add(course);
        }
        cursor.close();
        return courses;
    }
    // 获取学生某天的待上课程
    public List<Course> getStudentUpcomingCourses(String studentId, int weekDay, String currentTime) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Course> courses = new ArrayList<>();

        String sql = "SELECT c.* FROM course c " +
                "JOIN course_selection cs ON c.id = cs.course_id " +
                "WHERE cs.student_id = ? " +
                "AND c.week_day = ? " +
                "AND c.start_time > ? " +
                "ORDER BY c.start_time ASC";

        Cursor cursor = db.rawQuery(sql, new String[]{studentId, String.valueOf(weekDay), currentTime});

        while (cursor.moveToNext()) {
            Course course = new Course();
            course.setId(cursor.getInt(0));
            course.setName(cursor.getString(1));
            course.setWeekDay(cursor.getInt(2));
            course.setStartTime(cursor.getString(3));
            course.setEndTime(cursor.getString(4));
            course.setRoom(cursor.getString(5));
            course.setTeacher(cursor.getString(6));
            courses.add(course);
        }
        cursor.close();
        return courses;
    }
    // 获取学生某天的所有课程
    public List<Course> getStudentCoursesByDay(String studentId, int weekDay) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Course> courses = new ArrayList<>();

        String sql = "SELECT c.* FROM course c " +
                "JOIN course_selection cs ON c.id = cs.course_id " +
                "WHERE cs.student_id = ? " +
                "AND c.week_day = ? " +
                "ORDER BY c.start_time ASC";

        Cursor cursor = db.rawQuery(sql, new String[]{studentId, String.valueOf(weekDay)});

        while (cursor.moveToNext()) {
            Course course = new Course();
            course.setId(cursor.getInt(0));
            course.setName(cursor.getString(1));
            course.setWeekDay(cursor.getInt(2));
            course.setStartTime(cursor.getString(3));
            course.setEndTime(cursor.getString(4));
            course.setRoom(cursor.getString(5));
            course.setTeacher(cursor.getString(6));
            courses.add(course);
        }
        cursor.close();
        return courses;
    }
    public int deleteCourse(int courseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("course", "id=?", new String[]{String.valueOf(courseId)});
    }

    // 学生选课
    public boolean selectCourse(String studentId, int courseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 检查是否已经选过
        if (hasSelectedCourse(studentId, courseId)) {
            Log.d("Database", "Course already selected: " + courseId + " by " + studentId);
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(StudentHelper.COLUMN_SELECTION_STUDENT_ID, studentId);
        values.put(StudentHelper.COLUMN_SELECTION_COURSE_ID, courseId);

        long result = db.insert(StudentHelper.TABLE_COURSE_SELECTION, null, values);

        if (result != -1) {
            Log.d("Database", studentId + " selected course: " + courseId);
            return true;
        } else {
            Log.e("Database", "Failed to select course: " + courseId + " for " + studentId);
            return false;
        }
    }

    // 学生退选课程
    public int dropCourse(String studentId, int courseId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("course_selection",
                "student_id = ? AND course_id = ?",
                new String[]{studentId, String.valueOf(courseId)});
    }

    // 检查学生是否已选某课程
    public boolean hasSelectedCourse(String studentId, int courseId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT 1 FROM course_selection WHERE student_id = ? AND course_id = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{studentId, String.valueOf(courseId)});

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
    // 获取学生已选课程
    public List<Course> getSelectedCourses(String studentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Course> courses = new ArrayList<>();

        // 使用正确的表名和字段名
        String sql = "SELECT c.* FROM " + StudentHelper.TABLE_COURSE + " c " +
                "INNER JOIN " + StudentHelper.TABLE_COURSE_SELECTION + " sc " +
                "ON c." + StudentHelper.COLUMN_COURSE_ID + " = sc." + StudentHelper.COLUMN_SELECTION_COURSE_ID + " " +
                "WHERE sc." + StudentHelper.COLUMN_SELECTION_STUDENT_ID + " = ? " +
                "ORDER BY c." + StudentHelper.COLUMN_WEEK_DAY + ", c." + StudentHelper.COLUMN_START_TIME + " ASC";

        Cursor cursor = db.rawQuery(sql, new String[]{studentId});

        while (cursor.moveToNext()) {
            Course course = new Course();
            course.setId(cursor.getInt(cursor.getColumnIndexOrThrow(StudentHelper.COLUMN_COURSE_ID)));
            course.setName(cursor.getString(cursor.getColumnIndexOrThrow(StudentHelper.COLUMN_COURSE_NAME)));
            course.setWeekDay(cursor.getInt(cursor.getColumnIndexOrThrow(StudentHelper.COLUMN_WEEK_DAY)));
            course.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(StudentHelper.COLUMN_START_TIME)));
            course.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(StudentHelper.COLUMN_END_TIME)));
            course.setRoom(cursor.getString(cursor.getColumnIndexOrThrow(StudentHelper.COLUMN_ROOM)));
            course.setTeacher(cursor.getString(cursor.getColumnIndexOrThrow(StudentHelper.COLUMN_TEACHER)));
            courses.add(course);
        }
        cursor.close();
        return courses;
    }
}
