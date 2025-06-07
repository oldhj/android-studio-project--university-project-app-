package com.hevttc.bigwork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hevttc.bigwork.Dao.TodoDao;
import com.hevttc.bigwork.Manager.LoginManager;
import com.hevttc.bigwork.bean.Todo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTodoActivity extends AppCompatActivity {
    private EditText etContent;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        etContent = findViewById(R.id.et_content);
        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v -> {
            String content = etContent.getText().toString().trim();
            if (!content.isEmpty()) {
                Todo item = new Todo();
                item.setContent(content);
                item.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));

                // 获取当前登录学生ID
                String studentId = new LoginManager(this).getCurrentStudentId();

                new Thread(() -> {
                    // 传入学生ID
                    new TodoDao(this).insertTodo(studentId, item);
                    runOnUiThread(() -> {
                        setResult(RESULT_OK);
                        finish();
                    });
                }).start();
            } else {
                Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            }
        });
    }
}