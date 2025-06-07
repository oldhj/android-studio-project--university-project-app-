package com.hevttc.bigwork.Adapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hevttc.bigwork.Dao.BorrowRecordDao;
import com.hevttc.bigwork.R;
import com.hevttc.bigwork.bean.Book;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>{
    private final List<Book> bookList;
    private final OnItemClickListener listener;
    private final SparseBooleanArray buttonEnabled = new SparseBooleanArray();
    private final String currentUserId; // 新增用户ID字段

    public interface OnItemClickListener {
        void onBorrowClick(int position);
    }
    // 添加按钮状态控制方法
    public void disableButton(int position) {
        buttonEnabled.put(position, false);
        notifyItemChanged(position);
    }

    public void enableButton(int position) {
        buttonEnabled.put(position, true);
        notifyItemChanged(position);
    }


    public BookAdapter(List<Book> bookList, OnItemClickListener listener, String currentUserId) {
        this.bookList = bookList;
        this.listener = listener;
        this.currentUserId = currentUserId; // 接收用户ID
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText("作者：" + book.getAuthor());
        holder.tvStock.setText("库存：" + book.getStock());
        boolean isBorrowed = isBookBorrowed(holder.itemView, book.getBookId());

        holder.btnBorrow.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBorrowClick(position);
            }
        });
        if (book.getStock() > 0) {
            holder.btnBorrow.setEnabled(true);
            holder.btnBorrow.setText("立即借阅");
        } else {
            holder.btnBorrow.setEnabled(false);
            holder.btnBorrow.setText("已无库存");
        }
        // 根据状态设置按钮
        if (book.getStock() <= 0) {
            holder.btnBorrow.setEnabled(false);
            holder.btnBorrow.setText("已售罄");
        } else if (isBorrowed) {
            holder.btnBorrow.setEnabled(false);
            holder.btnBorrow.setText("已借阅");
        } else {
            holder.btnBorrow.setEnabled(buttonEnabled.get(position, true));
            holder.btnBorrow.setText("立即借阅");
        }


        if (isBorrowed) {
            holder.btnBorrow.setEnabled(false);
            holder.btnBorrow.setText("已借阅");
        }
        // 根据状态设置按钮
        holder.btnBorrow.setEnabled(buttonEnabled.get(position, true));
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    // 新增获取数据方法
    public Book getItem(int position) {
        return bookList.get(position);
    }

    // 新增数据更新方法
    public void updateData(List<Book> newBooks) {
        bookList.clear();
        bookList.addAll(newBooks);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvStock;
        Button btnBorrow;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvStock = itemView.findViewById(R.id.tv_stock);
            btnBorrow = itemView.findViewById(R.id.btn_borrow);
        }
    }
    // 修改方法签名，添加 View 参数
    private boolean isBookBorrowed(View itemView, int bookId) {
        BorrowRecordDao dao = new BorrowRecordDao(itemView.getContext());
        return dao.hasUnreturnedRecord(currentUserId, bookId);
    }
}
