package com.hevttc.bigwork;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar; // 正确的导入

import com.hevttc.bigwork.Dao.StudentDao;
import com.hevttc.bigwork.Manager.LoginManager;
import com.hevttc.bigwork.bean.Student;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnSubmit;
    private String studentId;
    private StudentDao studentDao;
    private LoginManager loginManager;
    private Toolbar toolbar_alter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        toolbar_alter = findViewById(R.id.toolbar_alter);
        loginManager = new LoginManager(this);
        // 获取当前登录用户ID
        studentId = loginManager.getCurrentStudentId();
        studentDao = new StudentDao(this);
        Student student = studentDao.getStudent(studentId);

        initViews();
        // 显示当前学号
        TextView tvStudentId = findViewById(R.id.tv_student_id);
        tvStudentId.setText(student.getStudentId());
        toolbar_alter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initViews() {
        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSubmit = findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // 验证输入
        if (TextUtils.isEmpty(oldPassword)) {
            showError(etOldPassword, "请输入旧密码");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            showError(etNewPassword, "请输入新密码");
            return;
        }

        if (newPassword.length() < 6) {
            showError(etNewPassword, "密码长度至少6位");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError(etConfirmPassword, "两次输入的密码不一致");
            return;
        }

        if (oldPassword.equals(newPassword)) {
            showError(etNewPassword, "新密码不能与旧密码相同");
            return;
        }

        // 验证旧密码是否正确
        if (!studentDao.verifyOldPassword(studentId, oldPassword)) {
            showError(etOldPassword, "旧密码输入错误");
            return;
        }

        // 更新密码
        if (studentDao.updatePassword(studentId, newPassword)) {
            Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();

            // 返回结果给MineFragment
            Intent resultIntent = new Intent();
            resultIntent.putExtra("password_changed", true);
            setResult(RESULT_OK, resultIntent);

            finish(); // 关闭当前Activity，返回MineFragment
        } else {
            Toast.makeText(this, "密码修改失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
    private void showError(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
    }
}
