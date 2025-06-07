package com.hevttc.bigwork.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hevttc.bigwork.Dao.CourseDao;
import com.hevttc.bigwork.R;
import com.hevttc.bigwork.bean.Course;

import java.util.ArrayList;
import java.util.List;

public class SelectedCourseAdapter extends RecyclerView.Adapter<SelectedCourseAdapter.ViewHolder> {
    private List<Course> courseList;
    private CourseDao courseDao;
    private final Context context; // 新增上下文引用

    public SelectedCourseAdapter(List<Course> courseList, CourseDao courseDao, Context context) {
        this.courseList = courseList;
        this.courseDao = courseDao;
        this.context = context; // 保存上下文引用
    }

    public void updateData(List<Course> newList) {
        // 创建新列表防止引用问题
        List<Course> updatedList = new ArrayList<>(newList);

        // 使用DiffUtil优化更新
        CourseDiffCallback diffCallback = new CourseDiffCallback(this.courseList, updatedList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        // 更新数据
        this.courseList.clear();
        this.courseList.addAll(updatedList);

        // 通知适配器更新
        diffResult.dispatchUpdatesTo(this);
    }

    // 添加DiffUtil回调类
    private static class CourseDiffCallback extends DiffUtil.Callback {
        private final List<Course> oldList;
        private final List<Course> newList;

        public CourseDiffCallback(List<Course> oldList, List<Course> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName, tvTime, tvRoom;
        Button btnDrop;

        ViewHolder(View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tv_course_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvRoom = itemView.findViewById(R.id.tv_room);
            btnDrop = itemView.findViewById(R.id.btn_drop);
            setupDropButton();
        }

        void bind(Course course) {
            tvCourseName.setText(course.getName());
            tvTime.setText(String.format("%s %s-%s",
                    course.getWeekDayChinese(),
                    course.getStartTime(),
                    course.getEndTime()));
            tvRoom.setText(course.getRoom());
        }
        private void setupDropButton() {
            btnDrop.setOnClickListener(v -> {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    return;
                }

                Course currentCourse = courseList.get(adapterPosition);

                new AlertDialog.Builder(context)
                        .setTitle("确认退课")
                        .setMessage("确定要退选" + currentCourse.getName() + "吗?")
                        .setPositiveButton("确定", (dialog, which) -> dropCourse(adapterPosition))
                        .setNegativeButton("取消", null)
                        .show();
            });
        }

        private void dropCourse(int adapterPosition) {
            Course courseToDrop = courseList.get(adapterPosition);
            new Thread(() -> {
                int result = courseDao.deleteCourse(courseToDrop.getId());

                ((Activity) context).runOnUiThread(() -> {
                    if (result > 0) {
                        courseList.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                        notifyItemRangeChanged(adapterPosition, getItemCount() - adapterPosition);
                        Toast.makeText(context, "退课成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "退课失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        }
    }


}
