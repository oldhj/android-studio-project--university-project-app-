package com.hevttc.bigwork.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hevttc.bigwork.Dao.BookDao;
import com.hevttc.bigwork.Dao.BorrowRecordDao;
import com.hevttc.bigwork.R;
import com.hevttc.bigwork.bean.Book;
import com.hevttc.bigwork.bean.BorrowRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BorrowRecordAdapter extends RecyclerView.Adapter<BorrowRecordAdapter.ViewHolder>{
    private List<BorrowRecord> records;

    public BorrowRecordAdapter(List<BorrowRecord> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_borrow_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ViewHolder finalHolder = holder;
        BorrowRecord record = records.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        holder.tvBookTitle.setText("书名：" + getBookTitleById(record.getBookId(), finalHolder));
        holder.tvBorrowDate.setText("借阅日期：" + sdf.format(record.getBorrowDate()));
        holder.tvDueDate.setText("应还日期：" + sdf.format(record.getDueDate()));

        holder.btnReturn.setOnClickListener(v -> {
            new Thread(() -> {
                BorrowRecordDao dao = new BorrowRecordDao(v.getContext());
                boolean success = dao.returnBook(record.getRecordId());

                v.post(() -> {
                    if (success) {
                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            records.remove(pos);
                            notifyItemRemoved(pos);
                        }
                    }
                });
            }).start();
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    private String getBookTitleById(int bookId, ViewHolder holder) {
        // 通过 ViewHolder 的上下文获取
        Context context = holder.itemView.getContext(); // 需要将 holder 设为 final
        BookDao bookDao = new BookDao(context);

        List<Book> books = bookDao.getAllBooks();
        for (Book book : books) {
            if (book.getBookId() == bookId) {
                return book.getTitle();
            }
        }
        return "未知书籍";
    }

    // 添加数据更新方法
    public void updateData(List<BorrowRecord> newRecords) {
        this.records = new ArrayList<>(newRecords); // 创建新列表
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // 声明视图组件
        TextView tvBookTitle, tvBorrowDate, tvDueDate;
        Button btnReturn;

        public ViewHolder(View itemView) {
            super(itemView);
            // 初始化视图
            tvBookTitle = itemView.findViewById(R.id.tv_book_title);
            tvBorrowDate = itemView.findViewById(R.id.tv_borrow_date);
            tvDueDate = itemView.findViewById(R.id.tv_due_date);
            btnReturn = itemView.findViewById(R.id.btn_return);
        }
    }
}
