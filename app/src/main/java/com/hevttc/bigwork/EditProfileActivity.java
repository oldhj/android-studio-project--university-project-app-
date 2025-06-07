package com.hevttc.bigwork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.hevttc.bigwork.Dao.StudentDao;
import com.hevttc.bigwork.Manager.LoginManager;
import com.hevttc.bigwork.bean.Student;

public class EditProfileActivity extends AppCompatActivity {
    private TextInputEditText etName, etFaculty, etMajor, etPhone, etEmail;
    private StudentDao studentDao;
    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // 初始化组件
        etName = findViewById(R.id.et_name);
        etFaculty = findViewById(R.id.et_faculty);
        etMajor = findViewById(R.id.et_major);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        Button btnSave = findViewById(R.id.btn_save);

        // 初始化 DAO 和 Manager
        studentDao = new StudentDao(this);
        loginManager = new LoginManager(this);

        // 加载当前用户信息
        loadCurrentProfile();

        findViewById(R.id.toolbar_person).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // 保存按钮点击事件
        btnSave.setOnClickListener(v -> saveProfile());
    }

//    private void loadCurrentProfile() {
//        String studentId = loginManager.getCurrentStudentId();
//        Student student = studentDao.getStudent(studentId);
//        if (student != null) {
//            etName.setText(student.getName());
//            etFaculty.setText(student.getFaculty());
//            etMajor.setText(student.getMajor());
//        }
//    }
    private void loadCurrentProfile() {
        String studentId = loginManager.getCurrentStudentId();
        if (studentId == null || studentId.isEmpty()) {
            Toast.makeText(this, "未找到用户信息", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Student student = studentDao.getStudent(studentId);
        if (student != null) {
            etName.setText(student.getName());
            etFaculty.setText(student.getFaculty());
            etMajor.setText(student.getMajor());

            // 设置手机号和邮箱
            etPhone.setText(student.getPhone());
            etEmail.setText(student.getEmail());
        } else {
            Toast.makeText(this, "加载信息失败", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveProfile() {
        String studentId = loginManager.getCurrentStudentId();
        if (studentId == null || studentId.isEmpty()) {
            Toast.makeText(this, "用户未登录", Toast.LENGTH_SHORT).show();
            return;
        }

        String newName = etName.getText().toString().trim();
        String newFaculty = etFaculty.getText().toString().trim();
        String newMajor = etMajor.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // 验证必填字段
        if (TextUtils.isEmpty(newName) ||
                TextUtils.isEmpty(newFaculty) ||
                TextUtils.isEmpty(newMajor)) {
            Toast.makeText(this, "姓名、学院和专业不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 验证手机号格式
        if (!TextUtils.isEmpty(phone) && !isValidPhone(phone)) {
            etPhone.setError("请输入有效的手机号");
            return;
        }

        // 验证邮箱格式
        if (!TextUtils.isEmpty(email) && !isValidEmail(email)) {
            etEmail.setError("请输入有效的邮箱地址");
            return;
        }

        // 更新数据库
        boolean isUpdated = studentDao.updateStudent(
                studentId,
                newName,
                newFaculty,
                newMajor,
                phone,
                email
        );

        if (isUpdated) {
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            finish(); // 关闭 Activity
        } else {
            Toast.makeText(this, "保存失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    // 验证手机号格式
    private boolean isValidPhone(String phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }

    // 验证邮箱格式
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

//    private void saveProfile() {
//        String studentId = loginManager.getCurrentStudentId();
//        String newName = etName.getText().toString().trim();
//        String newFaculty = etFaculty.getText().toString().trim();
//        String newMajor = etMajor.getText().toString().trim();
//
//        if (newName.isEmpty() || newFaculty.isEmpty() || newMajor.isEmpty()) {
//            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        boolean isUpdated = studentDao.updateStudent(studentId, newName, newFaculty, newMajor);
//        if (isUpdated) {
//            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
//            finish(); // 关闭 Activity
//        } else {
//            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
//        }
//    }
}