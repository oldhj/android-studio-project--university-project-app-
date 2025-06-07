package com.hevttc.bigwork.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hevttc.bigwork.R;
import com.hevttc.bigwork.SelectCourseActivity;
import com.hevttc.bigwork.bean.Course;

import java.util.List;

public class CourseSelectionADapter extends RecyclerView.Adapter<CourseSelectionADapter.ViewHolder> {
    private List<Course> courses;
    private Context context;



    public CourseSelectionADapter(List<Course> courses, Context context) {
        this.courses = courses;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.tvCourseName.setText(course.getName());
        holder.tvTime.setText(course.getWeekDayChinese() + " " + course.getTimeRange());
        holder.tvRoom.setText(course.getRoom());
        holder.btnSelect.setOnClickListener(new View.OnClickListener() {
            private long lastClickTime = 0;

            @Override
            public void onClick(View v) {
                // 防止快速多次点击
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime < 1000) {
                    return;
                }
                lastClickTime = currentTime;

                ((SelectCourseActivity) context).onSelectCourse(course, v);
            }
        });
    }



    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName, tvTime, tvRoom;
        Button btnSelect;

        ViewHolder(View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tv_course_name);
            tvTime = itemView.findViewById(R.id.tv_course_time);
            tvRoom = itemView.findViewById(R.id.tv_course_room);
            btnSelect = itemView.findViewById(R.id.btn_select);
        }
    }
}
