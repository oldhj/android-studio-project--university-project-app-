package com.hevttc.bigwork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hevttc.bigwork.Dao.StudentDao;
import com.hevttc.bigwork.bean.Student;

public class RegistActivity extends AppCompatActivity {
    private EditText et_sid;
    private EditText et_password;
    private EditText et_confirmpsd;
    private EditText et_phone;
    private EditText et_email;
    private Button btn_register;
    private StudentDao studentDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        et_sid = findViewById(R.id.et_sid_login);
        et_password = findViewById(R.id.et_password_login);
        et_confirmpsd = findViewById(R.id.et_confirmpsd);
        et_phone = findViewById(R.id.et_phone);
        et_email = findViewById(R.id.et_email);
        btn_register = findViewById(R.id.btn_register);
        studentDao = new StudentDao(this);

        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //注册事件
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取输入值
                String sid = et_sid.getText().toString().trim();
                String password = et_password.getText().toString();
                String confirm = et_confirmpsd.getText().toString();
                String phone = et_phone.getText().toString().trim();
                String email = et_email.getText().toString().trim();

                // 定义正则表达式
                String sidRegex = "^\\d{10}$";
                String phoneRegex = "^\\d{11}$";

                // 校验逻辑
                if (TextUtils.isEmpty(sid)) {
                    Toast.makeText(RegistActivity.this, "学号未填写", Toast.LENGTH_SHORT).show();
                } else if (!sid.matches(sidRegex)) {
                    Toast.makeText(RegistActivity.this, "学号必须为10位数字", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegistActivity.this, "密码未填写", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(confirm)) {
                    Toast.makeText(RegistActivity.this, "确认密码未填写", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirm)) {
                    Toast.makeText(RegistActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(RegistActivity.this, "手机号未填写", Toast.LENGTH_SHORT).show();
                } else if (!phone.matches(phoneRegex)) {
                    Toast.makeText(RegistActivity.this, "手机号格式错误", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegistActivity.this, "邮箱未填写", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RegistActivity.this, "邮箱格式错误（如 example@domain.com）", Toast.LENGTH_SHORT).show();
                } else {
                    if(studentDao.isStudentIdExists(sid)){
                        Toast.makeText(RegistActivity.this, "学号已存在", Toast.LENGTH_SHORT).show();
                    }
                    Student student = new Student();
                    student.setStudentId(sid);
                    student.setPassword(password);
                    student.setPhone(phone);
                    student.setEmail(email);
                    long result = studentDao.insertStudent(student);
                    if (result != -1) {
                        Toast.makeText(RegistActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegistActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }
}