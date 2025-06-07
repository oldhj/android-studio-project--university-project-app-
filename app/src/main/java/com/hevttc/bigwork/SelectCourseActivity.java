package com.hevttc.bigwork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hevttc.bigwork.Adapter.CourseSelectionADapter;
import com.hevttc.bigwork.Dao.CourseDao;
import com.hevttc.bigwork.Dao.StudentCourseDao;
import com.hevttc.bigwork.Fragment.CourseFragment;
import com.hevttc.bigwork.Manager.LoginManager;
import com.hevttc.bigwork.bean.Course;

import java.util.ArrayList;
import java.util.List;

public class SelectCourseActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CourseSelectionADapter adapter;
    private List<Course> availableCourses = new ArrayList<>();
    private CourseDao courseDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course);

        courseDao = new CourseDao(this);

        initializeCourseData();

        recyclerView = findViewById(R.id.rv_available_courses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseSelectionADapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        loadAvailableCourses();

        findViewById(R.id.toolbar_selected).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // 添加这个方法初始化课程数据
    private void initializeCourseData() {
        new Thread(() -> {
            // 检查数据库是否已有课程
            List<Course> existingCourses = courseDao.getAllCourses();

            // 如果数据库中没有课程，则添加示例课程
            if (existingCourses.isEmpty()) {
                List<Course> coursesToAdd = createSampleCourses();
                for (Course course : coursesToAdd) {
                    courseDao.addCourse(course);
                }
            }
        }).start();
    }

    // 创建示例课程列表
    private List<Course> createSampleCourses() {
        List<Course> courses = new ArrayList<>();

        courses.add(new Course("体育", 7, "17:00", "19:00", "操场"));
        courses.add(new Course("高等数学", 1, "14:00", "15:50", "A103"));
        courses.add(new Course("操作系统", 1, "8:00", "9:50", "A601"));
        courses.add(new Course("Java编程", 1, "10:10", "12:00", "实验楼602"));
        courses.add(new Course("Python应用", 1, "16:10", "18:00", "X1201"));
        courses.add(new Course("数据结构", 2, "14:00", "16:00", "A501"));
        courses.add(new Course("计算机组成", 2, "10:10", "12:00", "A412"));
        courses.add(new Course("高等数学", 3, "8:00", "10:00", "A203"));
        courses.add(new Course("高等数学", 5, "10:00", "12:00", "A103"));
        courses.add(new Course("数据结构", 4, "16:00", "18:00", "A501"));
        courses.add(new Course("计算机网络", 5, "14:00", "15:50", "A507"));

        courses.add(new Course("思政", 3, "10:30", "12:20", "G401"));
        courses.add(new Course("APP手机开发", 3, "14:00", "15:50", "H101"));
        courses.add(new Course("嵌入式开发", 3, "16:30", "18:20", "实验楼603"));
        courses.add(new Course("线性代数", 4, "8:00", "9:50", "I202"));
        courses.add(new Course("概率论", 4, "10:10", "12:00", "J301"));
        courses.add(new Course("考研英语", 4, "14:00", "15:50", "K401"));
        courses.add(new Course("马原", 5, "8:00", "9:50", "L501"));
        courses.add(new Course("思政", 5, "13:00", "14:50", "M601"));
        courses.add(new Course("嵌入式开发", 5, "16:30", "18:20", "实验楼707"));
        courses.add(new Course("APP手机开发", 6, "9:00", "10:50", "N102"));
        courses.add(new Course("线性代数", 6, "11:00", "12:50", "O201"));
        courses.add(new Course("考研英语", 6, "14:00", "15:50", "P301"));
        courses.add(new Course("概率论", 6, "16:00", "17:50", "Q401"));
        courses.add(new Course("马原", 7, "9:00", "10:50", "R101"));
        courses.add(new Course("思政", 7, "11:00", "12:50", "S201"));
        courses.add(new Course("嵌入式开发", 7, "14:00", "15:50", "实验楼808"));

//        courses.add(new Course("大学物理", 1, "10:00", "11:40", "X401"));
//        courses.add(new Course("线性代数", 3, "13:30", "15:10", "A205"));
//        courses.add(new Course("概率统计", 4, "09:00", "10:40", "A102"));
//
//        courses.add(new Course("人工智能", 1, "16:00", "17:40", "A701"));
//        courses.add(new Course("软件工程", 3, "08:30", "10:10", "A303"));
//        courses.add(new Course("数据库原理", 4, "10:20", "12:00", "X401"));
//        courses.add(new Course("编译原理", 5, "13:00", "14:40", "X302"));
//        courses.add(new Course("数字电路", 2, "09:50", "11:30", "A106"));
//        courses.add(new Course("计算机图形学", 4, "15:30", "17:10", "A402"));
//        courses.add(new Course("机器学习", 1, "14:00", "15:40", "X502"));
//        courses.add(new Course("网络安全", 3, "10:00", "11:40", "X601"));
//        courses.add(new Course("移动开发", 5, "16:00", "17:40", "A701"));
//        courses.add(new Course("大数据分析", 2, "13:30", "15:10", "X801"));
//        courses.add(new Course("物联网技术", 4, "08:00", "09:40", "A901"));
//        courses.add(new Course("云计算", 1, "15:00", "16:40", "X1001"));
//
//
//        courses.add(new Course("前端开发", 2, "14:00", "15:40", "A1301"));
//        courses.add(new Course("游戏设计", 4, "16:00", "17:40", "X1401"));
//        courses.add(new Course("嵌入式系统", 1, "13:00", "14:40", "A1501"));


        return courses;
    }
    public void onSelectCourse(Course course, View view) {
        // 禁用按钮防止多次点击
        if (view != null) {
            view.setEnabled(false);
        }

        new Thread(() -> {
            // 获取当前学生ID
            String studentId = new LoginManager(this).getCurrentStudentId();
            if (studentId == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "未登录或登录已过期", Toast.LENGTH_SHORT).show();
                    if (view != null) view.setEnabled(true);
                });
                return;
            }

            // 检查学生是否已选该课程
            if (courseDao.hasSelectedCourse(studentId, course.getId())) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "您已选过该课程", Toast.LENGTH_SHORT).show();
                    if (view != null) view.setEnabled(true);
                });
                return;
            }

            // 保存选课关系
            boolean result = courseDao.selectCourse(studentId, course.getId());

            runOnUiThread(() -> {
                if (result) {
                    Toast.makeText(this, "选课成功", Toast.LENGTH_SHORT).show();

                    // 通知课程页面刷新
                    CourseFragment.shouldRefresh = true;

                    // 刷新当前页面
                    loadAvailableCourses();
                } else {
                    Toast.makeText(this, "选课失败", Toast.LENGTH_SHORT).show();
                }
                if (view != null) view.setEnabled(true);
            });
        }).start();
    }
    private void loadAvailableCourses() {
        new Thread(() -> {
            // 获取当前学生ID
            String studentId = new LoginManager(this).getCurrentStudentId();
            if (studentId == null) {
                runOnUiThread(() ->
                        Toast.makeText(this, "未登录或登录已过期", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            // 获取所有课程
            List<Course> allCourses = courseDao.getAllCourses();

            // 获取学生已选课程
            List<Course> selectedCourses = courseDao.getSelectedCourses(studentId);

            // 计算可选课程（所有课程 - 已选课程）
            List<Course> availableCourses = new ArrayList<>();
            for (Course course : allCourses) {
                boolean isSelected = false;
                for (Course selected : selectedCourses) {
                    if (selected.getId() == course.getId()) {
                        isSelected = true;
                        break;
                    }
                }
                if (!isSelected) {
                    availableCourses.add(course);
                }
            }

            runOnUiThread(() -> {
                if (availableCourses.isEmpty()) {
                    Toast.makeText(this, "没有可选课程", Toast.LENGTH_SHORT).show();
                }

                // 更新适配器数据
                adapter = new CourseSelectionADapter(availableCourses, this);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }


}
