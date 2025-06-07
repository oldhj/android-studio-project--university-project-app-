package com.hevttc.bigwork.Fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hevttc.bigwork.Adapter.CourseAdapter;
import com.hevttc.bigwork.Adapter.TodoAdapter;
import com.hevttc.bigwork.AddTodoActivity;
import com.hevttc.bigwork.Dao.CourseDao;
import com.hevttc.bigwork.Dao.StudentDao;
import com.hevttc.bigwork.Dao.TodoDao;
import com.hevttc.bigwork.EditProfileActivity;
import com.hevttc.bigwork.Manager.LoginManager;
import com.hevttc.bigwork.R;
import com.hevttc.bigwork.bean.Course;
import com.hevttc.bigwork.bean.Student;
import com.hevttc.bigwork.bean.Todo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private CourseDao courseDao;
    private StudentDao studentDao;
    private TextView tvStudentName, tvStudentInfo, tvLastLogin;
    private LoginManager loginManager;
    private RecyclerView rvTodos;
    private TodoAdapter todoAdapter;
    private TodoDao todoDao;
    private TextView tvEmptyTodo;
    private static final int REQUEST_ADD_TODO = 1001;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvStudentName = view.findViewById(R.id.tv_student_name);
        tvStudentInfo = view.findViewById(R.id.tv_student_info);
        tvLastLogin = view.findViewById(R.id.tv_last_login);
        recyclerView = view.findViewById(R.id.rv_today_courses);
        emptyTextView = view.findViewById(R.id.tv_empty);
        FloatingActionButton fabEdit = view.findViewById(R.id.fab_add_info);
        todoDao = new TodoDao(requireContext());
        rvTodos = view.findViewById(R.id.rv_todos);
        tvEmptyTodo = view.findViewById(R.id.tv_empty_todo);

        // 初始化CourseDao
        courseDao = new CourseDao(requireContext());
        studentDao = new StudentDao(requireContext());
        loginManager = new LoginManager(requireContext());
        todoDao = new TodoDao(requireContext());

        String studentId = loginManager.getCurrentStudentId();

        // 设置RecyclerView
        rvTodos.setLayoutManager(new LinearLayoutManager(getContext()));
        todoAdapter = new TodoAdapter(new ArrayList<>(), getContext(), studentId);
        rvTodos.setAdapter(todoAdapter);




        loadUserInfo();
        loadTodayCourses();
        loadTodos();
        fabEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add_todo);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddTodoActivity.class);
            startActivityForResult(intent, REQUEST_ADD_TODO);
        });


        return view;

    }
    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo(); // 每次回到Fragment时刷新数据
        loadTodayCourses(); // 刷新课程数据
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_TODO && resultCode == RESULT_OK) {
            loadTodos();
        }
    }

    private void loadUserInfo() {
        new Thread(() -> {
            String studentId = loginManager.getCurrentStudentId();
            Student student = studentDao.getStudent(studentId);
            requireActivity().runOnUiThread(() -> {
                if (student != null) {
                    tvStudentName.setText(student.getName());
                    tvStudentInfo.setText(student.getFaculty() + " | " + student.getStudentId());
                    tvLastLogin.setText("上次登录：" + (student.getLastLogin() != null ? student.getLastLogin() : "未记录"));
                } else {
                    tvStudentName.setText("未知用户");
                    tvStudentInfo.setText("请检查登录状态或重新登录");
                }
            });
        }).start();
    }
    private void loadTodos() {
        new Thread(() -> {
            // 获取当前登录学生ID
            String studentId = loginManager.getCurrentStudentId();

            // 传入学生ID查询
            List<Todo> todos = todoDao.getAllTodos(studentId);

            requireActivity().runOnUiThread(() -> {
                todoAdapter.setTodoList(todos);
                tvEmptyTodo.setVisibility(todos.isEmpty() ? View.VISIBLE : View.GONE);
            });
        }).start();
    }

    private void loadTodayCourses() {
        new Thread(() -> {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String currentTime = sdf.format(calendar.getTime());

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int dbWeekDay = (dayOfWeek == 1) ? 7 : dayOfWeek - 1;

            // 获取当前登录学生的ID
            String studentId = loginManager.getCurrentStudentId();

            // 确保 studentId 不为 null
            if (studentId == null) {
                studentId = "";
            }

            // 获取学生今天的待上课程
            List<Course> upcomingCourses = courseDao.getStudentUpcomingCourses(
                    studentId, dbWeekDay, currentTime
            );

            requireActivity().runOnUiThread(() -> {
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                CourseAdapter adapter = new CourseAdapter(upcomingCourses);
                recyclerView.setAdapter(adapter);

                if (upcomingCourses.isEmpty()) {
                    emptyTextView.setText("今日已无待上课程");
                    emptyTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyTextView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }
    private void setupCourseList() {
        // 获取今天是星期几（1=周日，2=周一...7=周六）
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // 转换为数据库中的星期表示（1=周一，2=周二...7=周日）
        int dbWeekDay = (dayOfWeek == 1) ? 7 : dayOfWeek - 1;

        // 从数据库获取今天的课程
        List<Course> todayCourses = courseDao.getCoursesByDay(dbWeekDay);

        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CourseAdapter(todayCourses));

        // 根据课程数量显示/隐藏空状态文本
        if (todayCourses.isEmpty()) {
            emptyTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

}