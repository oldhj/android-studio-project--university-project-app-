package com.hevttc.bigwork.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class StudentHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "campus.db";
    private static final int DATABASE_VERSION = 8;
    private static StudentHelper instance;

    // 表名常量
    public static final String TABLE_STUDENT = "student";
    public static final String TABLE_COURSE = "course";
    public static final String TABLE_COURSE_SELECTION = "course_selection";
    public static final String TABLE_TODOS = "todos";
    public static final String TABLE_BOOKS = "books";
    public static final String TABLE_BORROW_RECORDS = "borrow_records";

    // 学生表字段
    public static final String COLUMN_STUDENT_ID = "studentId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_USER_TYPE = "userType";
    public static final String COLUMN_FACULTY = "faculty";
    public static final String COLUMN_MAJOR = "major";
    public static final String COLUMN_LAST_LOGIN = "lastLogin";

    // 课程表字段
    public static final String COLUMN_COURSE_ID = "id";
    public static final String COLUMN_COURSE_NAME = "name";
    public static final String COLUMN_WEEK_DAY = "week_day";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_TEACHER = "teacher";

    // 选课表字段
    public static final String COLUMN_SELECTION_STUDENT_ID = "student_id";
    public static final String COLUMN_SELECTION_COURSE_ID = "course_id";

    // 待办事项表字段
    public static final String COLUMN_TODO_ID = "todo_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_IS_COMPLETED = "is_completed";

    //图书表字段
    public static final String COLUMN_BOOK_ID = "book_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_ISBN = "isbn";
    public static final String COLUMN_STOCK = "stock";

    //借阅表字段
    public static final String COLUMN_RECORD_ID = "record_id";
    public static final String COLUMN_BORROW_DATE = "borrow_date";
    public static final String COLUMN_DUE_DATE = "due_date";
    public static final String COLUMN_RETURNED = "returned";

    //座位预约字段
    public static final String TABLE_SEAT_RESERVATIONS = "seat_reservations";
    public static final String COLUMN_SEAT_ID = "seat_id";
    public static final String COLUMN_SEAT_ROW = "row";   // 原 "row"
    public static final String COLUMN_SEAT_COLUMN = "column"; // 原 "column"
    public static final String COLUMN_IS_RESERVED = "is_reserved";
    public static final String COLUMN_RESERVATION_TIME = "reservation_time";



    public static synchronized StudentHelper getInstance(Context context) {
        if (instance == null) {
            instance = new StudentHelper(context.getApplicationContext());
        }
        return instance;
    }

    public StudentHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
// 创建学生表
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_STUDENT + " (" +
                        COLUMN_STUDENT_ID + " TEXT PRIMARY KEY, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_GENDER + " INTEGER DEFAULT 0, " +
                        COLUMN_PHONE + " TEXT, " +
                        COLUMN_EMAIL + " TEXT, " +
                        COLUMN_PASSWORD + " TEXT, " +
                        COLUMN_USER_TYPE + " INTEGER DEFAULT 0, " +
                        COLUMN_FACULTY + " TEXT, " +
                        COLUMN_MAJOR + " TEXT, " +
                        COLUMN_LAST_LOGIN + " TEXT" +
                        ")"
        );

        // 创建课程表
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_COURSE + " (" +
                        COLUMN_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_COURSE_NAME + " TEXT NOT NULL, " +
                        COLUMN_WEEK_DAY + " INTEGER CHECK(" + COLUMN_WEEK_DAY + " BETWEEN 1 AND 7), " +
                        COLUMN_START_TIME + " TEXT CHECK(" + COLUMN_START_TIME + " GLOB '[0-2][0-9]:[0-5][0-9]'), " +
                        COLUMN_END_TIME + " TEXT CHECK(" + COLUMN_END_TIME + " GLOB '[0-2][0-9]:[0-5][0-9]'), " +
                        COLUMN_ROOM + " TEXT NOT NULL, " +
                        COLUMN_TEACHER + " TEXT, " +
                        "UNIQUE (" + COLUMN_WEEK_DAY + ", " + COLUMN_START_TIME + ", " + COLUMN_ROOM + ")" +
                        ")"
        );

        // 创建选课关系表
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_COURSE_SELECTION + " (" +
                        COLUMN_SELECTION_STUDENT_ID + " TEXT REFERENCES " + TABLE_STUDENT + "(" + COLUMN_STUDENT_ID + "), " +
                        COLUMN_SELECTION_COURSE_ID + " INTEGER REFERENCES " + TABLE_COURSE + "(" + COLUMN_COURSE_ID + "), " +
                        "PRIMARY KEY (" + COLUMN_SELECTION_STUDENT_ID + ", " + COLUMN_SELECTION_COURSE_ID + ")" +
                        ")"
        );

        // 创建待办事项表
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_TODOS + " (" +
                        COLUMN_TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_STUDENT_ID + " TEXT NOT NULL, " + // 新增学生ID列
                        COLUMN_CONTENT + " TEXT NOT NULL, " +
                        COLUMN_CREATED_AT + " TEXT DEFAULT (datetime('now','localtime')), " +
                        COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0" +
                        ")"
        );

        // 新增图书表
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_BOOKS + " ("
                + COLUMN_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT NOT NULL,"
                + COLUMN_AUTHOR + " TEXT,"
                + COLUMN_ISBN + " TEXT UNIQUE,"
                + COLUMN_STOCK + " INTEGER DEFAULT 0 CHECK(" + COLUMN_STOCK + " >= 0))");

        // 新增借阅记录表
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_BORROW_RECORDS + " ("
                + COLUMN_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SELECTION_STUDENT_ID + " TEXT REFERENCES " + TABLE_STUDENT + "(" + COLUMN_STUDENT_ID + "),"
                + COLUMN_BOOK_ID + " INTEGER REFERENCES " + TABLE_BOOKS + "(" + COLUMN_BOOK_ID + "),"
                + COLUMN_BORROW_DATE + " TEXT,"
                + COLUMN_DUE_DATE + " TEXT,"
                + COLUMN_RETURNED + " INTEGER DEFAULT 0)");

        // 创建座位预约表
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_SEAT_RESERVATIONS + " ("
                + COLUMN_SEAT_ID + " TEXT PRIMARY KEY, "
                + COLUMN_SEAT_ROW + " INTEGER NOT NULL, "
                + COLUMN_SEAT_COLUMN + " INTEGER NOT NULL, "
                + COLUMN_IS_RESERVED + " INTEGER NOT NULL DEFAULT 0, "
                + COLUMN_SELECTION_STUDENT_ID + " TEXT, "
                + COLUMN_RESERVATION_TIME + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // 版本1→2：添加课程表
        if (i < 2) {
            sqLiteDatabase.execSQL(
                    "CREATE TABLE " + TABLE_COURSE + " (" +
                            COLUMN_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            COLUMN_COURSE_NAME + " TEXT NOT NULL, " +
                            COLUMN_WEEK_DAY + " INTEGER CHECK(" + COLUMN_WEEK_DAY + " BETWEEN 1 AND 7), " +
                            COLUMN_START_TIME + " TEXT CHECK(" + COLUMN_START_TIME + " GLOB '[0-2][0-9]:[0-5][0-9]'), " +
                            COLUMN_END_TIME + " TEXT CHECK(" + COLUMN_END_TIME + " GLOB '[0-2][0-9]:[0-5][0-9]'), " +
                            COLUMN_ROOM + " TEXT NOT NULL, " +
                            COLUMN_TEACHER + " TEXT, " +
                            "UNIQUE (" + COLUMN_WEEK_DAY + ", " + COLUMN_START_TIME + ", " + COLUMN_ROOM + ")" +
                            ")"
            );
        }

        // 版本2→3：添加选课表和lastLogin字段
        if (i < 3) {
            // 添加选课关系表
            sqLiteDatabase.execSQL(
                    "CREATE TABLE " + TABLE_COURSE_SELECTION + " (" +
                            COLUMN_SELECTION_STUDENT_ID + " TEXT REFERENCES " + TABLE_STUDENT + "(" + COLUMN_STUDENT_ID + "), " +
                            COLUMN_SELECTION_COURSE_ID + " INTEGER REFERENCES " + TABLE_COURSE + "(" + COLUMN_COURSE_ID + "), " +
                            "PRIMARY KEY (" + COLUMN_SELECTION_STUDENT_ID + ", " + COLUMN_SELECTION_COURSE_ID + ")" +
                            ")"
            );

            // 添加lastLogin字段
            sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_STUDENT + " ADD COLUMN " + COLUMN_LAST_LOGIN + " TEXT");
        }

        // 版本3→4：添加待办事项表
        if (i < 4) {
            sqLiteDatabase.execSQL(
                    "CREATE TABLE " + TABLE_TODOS + " (" +
                            COLUMN_TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            COLUMN_CONTENT + " TEXT NOT NULL, " +
                            COLUMN_CREATED_AT + " TEXT DEFAULT (datetime('now','localtime')), " +
                            COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0" +
                            ")"
            );
        }
        if(i < 5){
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_BOOKS + " ("
                    + COLUMN_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TITLE + " TEXT NOT NULL,"
                    + COLUMN_AUTHOR + " TEXT,"
                    + COLUMN_ISBN + " TEXT UNIQUE,"
                    + COLUMN_STOCK + " INTEGER DEFAULT 0 CHECK(" + COLUMN_STOCK + " >= 0))");

            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_BORROW_RECORDS + " ("
                    + COLUMN_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_SELECTION_STUDENT_ID + " TEXT REFERENCES " + TABLE_STUDENT + "(" + COLUMN_STUDENT_ID + "),"
                    + COLUMN_BOOK_ID + " INTEGER REFERENCES " + TABLE_BOOKS + "(" + COLUMN_BOOK_ID + "),"
                    + COLUMN_BORROW_DATE + " TEXT,"
                    + COLUMN_DUE_DATE + " TEXT,"
                    + COLUMN_RETURNED + " INTEGER DEFAULT 0)");
        }
        if (i < 6) {
            // 先删除旧表（如果存在）
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SEAT_RESERVATIONS);
            // 创建新表（使用新字段名）
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_SEAT_RESERVATIONS + " ("
                    + COLUMN_SEAT_ID + " TEXT PRIMARY KEY, "
                    + COLUMN_SEAT_ROW + " INTEGER NOT NULL, "
                    + COLUMN_SEAT_COLUMN + " INTEGER NOT NULL, "
                    + COLUMN_IS_RESERVED + " INTEGER NOT NULL DEFAULT 0, "
                    + COLUMN_SELECTION_STUDENT_ID + " TEXT, "
                    + COLUMN_RESERVATION_TIME + " INTEGER NOT NULL)");
        }
        if (i < 7) {
            // 添加索引优化查询性能
            sqLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS idx_course_selection_student_id ON " +
                    TABLE_COURSE_SELECTION + "(" + COLUMN_SELECTION_STUDENT_ID + ")");
            sqLiteDatabase.execSQL("CREATE INDEX IF NOT EXISTS idx_course_week_day ON " +
                    TABLE_COURSE + "(" + COLUMN_WEEK_DAY + ")");
        }
        if (i < 8) {
            // 创建临时表备份数据
            sqLiteDatabase.execSQL("CREATE TABLE temp_todos AS SELECT * FROM " + TABLE_TODOS);

            // 删除旧表
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);

            // 创建新表（带学生ID列）
            sqLiteDatabase.execSQL(
                    "CREATE TABLE " + TABLE_TODOS + " (" +
                            COLUMN_TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            COLUMN_STUDENT_ID + " TEXT NOT NULL, " + // 新增学生ID列
                            COLUMN_CONTENT + " TEXT NOT NULL, " +
                            COLUMN_CREATED_AT + " TEXT DEFAULT (datetime('now','localtime')), " +
                            COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0" +
                            ")"
            );

            // 迁移数据（为所有旧待办设置一个默认学生ID）
            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_TODOS + " (" +
                    COLUMN_TODO_ID + ", " +
                    COLUMN_STUDENT_ID + ", " + // 添加默认值
                    COLUMN_CONTENT + ", " +
                    COLUMN_CREATED_AT + ", " +
                    COLUMN_IS_COMPLETED + ") " +
                    "SELECT " +
                    COLUMN_TODO_ID + ", " +
                    "'default_student', " + // 默认学生ID
                    COLUMN_CONTENT + ", " +
                    COLUMN_CREATED_AT + ", " +
                    COLUMN_IS_COMPLETED + " " +
                    "FROM temp_todos");

            // 删除临时表
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS temp_todos");
        }
    }
}
