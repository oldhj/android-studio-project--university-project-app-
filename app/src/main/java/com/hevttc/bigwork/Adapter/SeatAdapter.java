package com.hevttc.bigwork.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hevttc.bigwork.R;
import com.hevttc.bigwork.bean.Seat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.ViewHolder>{
    private List<Seat> seatList;
    private Context context;
    private OnSeatClickListener listener;

    public interface OnSeatClickListener {
        void onSeatClick(Seat seat);
    }

    public SeatAdapter(List<Seat> seatList, OnSeatClickListener listener) {
        this.seatList = seatList;
        this.listener = listener;
    }
    public void updateData(List<Seat> newReservations) {
        this.seatList = newReservations;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public SeatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_seat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatAdapter.ViewHolder holder, int position) {

        Seat seat = seatList.get(position);
        View seatStatus = holder.itemView.findViewById(R.id.seat_status);
        // 确保上下文有效
        Context context = holder.itemView.getContext();

        // 打印座位状态用于调试
        Log.d("SeatAdapter", "座位ID: " + seat.getId() +
                ", 位置: " + position +
                ", 状态: " + (seat.isReserved() ? "已预约" : "可预约"));

        int colorResId = seat.isReserved() ?
                R.color.seat_reserved :
                R.color.seat_available;

        // 设置座位颜色
        int color = ContextCompat.getColor(holder.itemView.getContext(), colorResId);

        // 使用兼容库方法设置背景着色
        ViewCompat.setBackgroundTintList(seatStatus, ColorStateList.valueOf(color));

        // 设置透明度
        seatStatus.setAlpha(seat.isReserved() ? 0.7f : 1.0f);

        // 添加调试日志
        Log.d("SeatAdapter", "座位: " + seat.getId() +
                " | 状态: " + (seat.isReserved() ? "已预约" : "可预约") +
                " | 颜色: #" + Integer.toHexString(color).substring(2));
        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSeatClick(seat);
            }
        });
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View seatStatus;
        public ViewHolder(View itemView) {
            super(itemView);
            seatStatus = itemView.findViewById(R.id.seat_status);
        }

    }
}
