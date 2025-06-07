package com.hevttc.bigwork.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hevttc.bigwork.Dao.TodoDao;
import com.hevttc.bigwork.R;
import com.hevttc.bigwork.bean.Todo;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder>{
    private List<Todo> todoList;
    private final TodoDao todoDao;
    private final String studentId; // 添加学生ID字段

    public TodoAdapter(List<Todo> todoList, Context context, String studentId) {
        this.todoList = todoList;
        this.todoDao = new TodoDao(context);
        this.studentId = studentId; // 保存学生ID
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Todo item = todoList.get(position);
        holder.tvContent.setText(item.getContent());
        holder.tvTime.setText(item.getCreatedAt());
        holder.cbComplete.setChecked(item.isCompleted());

        // 完成状态切换（传入学生ID）
        holder.cbComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            new Thread(() -> {
                todoDao.updateCompletionStatus(item.getId(), studentId, isChecked);
                item.setCompleted(isChecked);
            }).start();
        });

        // 删除按钮（传入学生ID）
        holder.btnDelete.setOnClickListener(v -> {
            new Thread(() -> {
                todoDao.deleteTodo(item.getId(), studentId);
                ((Activity) holder.itemView.getContext()).runOnUiThread(() -> {
                    todoList.remove(position);
                    notifyItemRemoved(position);
                });
            }).start();
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }
    public void setTodoList(List<Todo> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged(); // 添加这行刷新数据
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbComplete;
        TextView tvContent;
        TextView tvTime;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbComplete = itemView.findViewById(R.id.cb_complete);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
