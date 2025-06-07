package com.hevttc.bigwork.Manager;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginManager {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private final SharedPreferences sharedPreferences;

    public LoginManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // 保存登录状态
    public void saveLoginState(String studentId) {
        sharedPreferences.edit()
                .putString(KEY_STUDENT_ID, studentId)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();
    }

    // 获取当前学生ID
    public String getCurrentStudentId() {
        return sharedPreferences.getString(KEY_STUDENT_ID, "");
    }

    // 检查是否已登录
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // 退出登录
    public void logout() {
        sharedPreferences.edit().clear().apply();
    }
}
