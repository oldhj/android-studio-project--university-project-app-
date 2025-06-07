package com.hevttc.bigwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.hevttc.bigwork.Dao.StudentDao;
import com.hevttc.bigwork.Manager.LoginManager;

public class LoginActivity extends AppCompatActivity {
    private EditText et_sid_login;
    private EditText et_password_login;
    private Button btn_login;
    private StudentDao studentDao;
    private CheckBox checkBox;
    private boolean is_login;
    private SharedPreferences sharedPreferences;
    private LoginManager loginManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_sid_login = findViewById(R.id.et_sid_login);
        et_password_login  =findViewById(R.id.et_password_login);
        btn_login = findViewById(R.id.btn_login);
        checkBox = findViewById(R.id.checkbox);
        sharedPreferences = getSharedPreferences("student",MODE_PRIVATE);
        loginManager = new LoginManager(this);
        //是否勾选记住密码
        is_login = sharedPreferences.getBoolean("is_login", false);
        if(is_login){
            String sid =  sharedPreferences.getString("sid",null);
            String password = sharedPreferences.getString("password",null);
            et_sid_login.setText(sid);
            et_password_login.setText(password);
            checkBox.setChecked(true);
        }

        studentDao = new StudentDao(this);
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegistActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sid = et_sid_login.getText().toString();
                String password = et_password_login.getText().toString();
                if(TextUtils.isEmpty(sid) && TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "请输入学号和密码", Toast.LENGTH_SHORT).show();
                }
//                else{
//                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                    startActivity(intent);
//                }
                if (studentDao.checkLogin(sid, password)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("is_login", LoginActivity.this.is_login);
                    editor.putString("sid",sid);
                    editor.putString("password",password);
                    editor.commit();
                    loginManager.saveLoginState(sid);
                    studentDao.updateLastLogin(sid); // 更新最后登录时间
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(LoginActivity.this, "登录学生ID：" + sid, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "学号或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LoginActivity.this.is_login = b;
            }
        });

    }
}