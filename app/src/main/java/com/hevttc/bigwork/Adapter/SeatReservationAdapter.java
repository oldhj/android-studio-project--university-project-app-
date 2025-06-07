package com.hevttc.bigwork.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hevttc.bigwork.R;
import com.hevttc.bigwork.bean.Seat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SeatReservationAdapter extends RecyclerView.Adapter<SeatReservationAdapter.ViewHolder>{
    private List<Seat> reservations;

    public SeatReservationAdapter(List<Seat> reservations) {
        this.reservations = reservations;
    }

    public void updateData(List<Seat> newReservations) {
        this.reservations = newReservations;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public SeatReservationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seat_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatReservationAdapter.ViewHolder holder, int position) {
        Seat seat = reservations.get(position);
        holder.tvSeatId.setText("座位号: " + seat.getId());

        // 格式化预约时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String time = sdf.format(new Date(seat.getReservationTime()));
        holder.tvReservationTime.setText("预约时间: " + time);
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSeatId;
        public TextView tvReservationTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSeatId = itemView.findViewById(R.id.tvSeatId);
            tvReservationTime = itemView.findViewById(R.id.tvReservationTime);
        }
    }
}
