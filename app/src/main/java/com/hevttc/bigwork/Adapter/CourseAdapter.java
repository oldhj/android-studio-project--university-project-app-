package com.hevttc.bigwork.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hevttc.bigwork.R;
import com.hevttc.bigwork.bean.Course;

import java.text.BreakIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private final List<Course> courseList;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCourseName, tvTime, tvRoom;

        public ViewHolder(View view) {
            super(view);
            tvCourseName = view.findViewById(R.id.tv_course_name);
            tvTime = view.findViewById(R.id.tv_time);
            tvRoom = view.findViewById(R.id.tv_room);
        }
    }

    public CourseAdapter(List<Course> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Course course = courseList.get(position);
//        holder.tvCourseName.setText(course.getName());
//        holder.tvTime.setText(course.getTime());
//        holder.tvRoom.setText(course.getRoom());
        Course course = courseList.get(position);
        holder.tvCourseName.setText(course.getName());
        holder.tvTime.setText(course.getWeekDayChinese() + " " + course.getTimeRange());
        holder.tvRoom.setText("教室：" + course.getRoom());


    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }
    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName, tvTime, tvRoom, tvTeacher;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tv_course_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvRoom = itemView.findViewById(R.id.tv_room);

        }
    }
    private String getCourseStatus(Course course) {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date start = sdf.parse(course.getStartTime());
            Date end = sdf.parse(course.getEndTime());
            Date current = sdf.parse(sdf.format(now.getTime()));

            if (current.before(start)) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(start.getTime() - current.getTime());
                return "还有" + minutes + "分钟开始";
            } else if (current.before(end)) {
                return "进行中";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    private int getStatusColor(Context context, String status) {
        if (status.contains("进行中")) {
            return ContextCompat.getColor(context, R.color.colorPrimary);
        } else {
            return ContextCompat.getColor(context, R.color.colorAccent);
        }
    }
}
