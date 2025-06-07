package com.hevttc.bigwork.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hevttc.bigwork.Adapter.SelectedCourseAdapter;
import com.hevttc.bigwork.Dao.CourseDao;
import com.hevttc.bigwork.Manager.LoginManager;
import com.hevttc.bigwork.R;
import com.hevttc.bigwork.SelectCourseActivity;
import com.hevttc.bigwork.bean.Course;

import java.util.ArrayList;
import java.util.List;


public class CourseFragment extends Fragment {

    private RecyclerView rvSelectedCourses;
    private CourseDao courseDao;
    private SelectedCourseAdapter adapter;
    private TextView tvEmpty; // 添加这个成员变量
    public static boolean shouldRefresh = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        View btnSelectCourse = view.findViewById(R.id.btn_select_course);
        View btnCheckGrades = view.findViewById(R.id.btn_check_grades);
        tvEmpty = view.findViewById(R.id.tv_empty); // 确保ID与XML一致
        rvSelectedCourses = view.findViewById(R.id.rv_selected_courses);

        courseDao = new CourseDao(requireContext());

        btnSelectCourse.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectCourseActivity.class);
            startActivity(intent);
        });

        btnCheckGrades.setOnClickListener(v -> {
            // 成绩查询功能待实现
            Toast.makeText(getContext(), "成绩查询功能待实现", Toast.LENGTH_SHORT).show();
        });

        setupCourseList();

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (shouldRefresh) {
            refreshCourseList();
            shouldRefresh = false;
        } else {
            // 每次回到页面时刷新数据
            refreshCourseList();
        }
    }

    private void setupCourseList() {
        // 设置布局管理器
        rvSelectedCourses.setLayoutManager(new LinearLayoutManager(getContext()));

        // 初始化适配器
        adapter = new SelectedCourseAdapter(new ArrayList<>(), courseDao, requireActivity());
        rvSelectedCourses.setAdapter(adapter);

        // 加载初始数据
        refreshCourseList();
    }

    private void refreshCourseList() {
        new Thread(() -> {
            // 获取当前学生ID
            String studentId = new LoginManager(requireContext()).getCurrentStudentId();
            if (studentId == null) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "未登录或登录已过期", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            // 获取学生已选课程
            List<Course> courses = courseDao.getSelectedCourses(studentId);

            // 确保在主线程更新UI
            requireActivity().runOnUiThread(() -> {
                // 使用新的列表创建适配器数据
                adapter.updateData(courses);

                // 控制空状态显示
                if (courses.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvSelectedCourses.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    rvSelectedCourses.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }
}