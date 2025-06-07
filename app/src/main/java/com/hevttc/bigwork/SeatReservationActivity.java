package com.hevttc.bigwork;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hevttc.bigwork.Adapter.SeatAdapter;
import com.hevttc.bigwork.Dao.SeatReservationDao;
import com.hevttc.bigwork.Manager.LoginManager;
import com.hevttc.bigwork.bean.Seat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SeatReservationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SeatAdapter adapter;
    private List<Seat> seatList = new ArrayList<>();
    private SeatReservationDao seatDao;
    private String currentUserId; // 当前用户ID
    private Seat currentUserReservation; // 存储用户当前预约
    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_reservation);
        // 初始化登录管理器
        loginManager = new LoginManager(this);

        // 检查登录状态
        if (!loginManager.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        currentUserId = loginManager.getCurrentStudentId();
        seatDao = new SeatReservationDao(this);
        // 初始化座位数据（示例数据）
        initializeSeats();

        recyclerView = findViewById(R.id.rv_seats);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        adapter = new SeatAdapter(seatList, this::handleSeatClick);
        recyclerView.setAdapter(adapter);
        findViewById(R.id.toolbar_seat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initializeSeats() {
        seatList.clear();
        // 从数据库加载已有的预约状态
        List<Seat> reservedSeats = seatDao.getAllSeats();

        // 创建所有座位 (8行5列)
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 5; col++) {
                String seatId = "S" + row + "-" + col;
                Seat seat = new Seat();
                seat.setId(seatId);
                seat.setRow(row);
                seat.setColumn(col);

                // 检查是否已预约
                for (Seat reservedSeat : reservedSeats) {
                    if (reservedSeat.getId().equals(seatId)) {
                        seat.setReserved(true);
                        seat.setReservationTime(reservedSeat.getReservationTime());

                        // 如果是当前用户的预约，记录下来
                        if (currentUserReservation != null &&
                                currentUserReservation.getId().equals(seatId)) {
                            currentUserReservation = seat;
                        }
                        break;
                    }
                }

                seatList.add(seat);
            }
        }
        // 通知适配器数据已更新
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

//    private void handleSeatClick(Seat seat) {
//        final int position = seatList.indexOf(seat);
//        if (position == -1) return;
//
//        if (seat.isReserved()) {
//            showSeatInfo(seat);
//        } else {
//            new AlertDialog.Builder(this)
//                    .setTitle("确认预约")
//                    .setMessage("是否确认预约座位 " + seat.getId() + "?")
//                    .setPositiveButton("确认", (dialog, which) -> {
//                        // 创建新座位对象而不是修改原始对象
//                        // 取消用户的其他预约
//                        seatDao.cancelOtherReservations(currentUserId, seat.getId());
//
//                        Seat updatedSeat = new Seat();
//                        updatedSeat.setId(seat.getId());
//                        updatedSeat.setRow(seat.getRow());
//                        updatedSeat.setColumn(seat.getColumn());
//                        updatedSeat.setReserved(true);
//                        updatedSeat.setReservationTime(System.currentTimeMillis());
//
//                        // 保存到数据库
//                        seatDao.saveReservation(updatedSeat, currentUserId);
//
//                        // 更新列表中的座位对象
//                        seatList.set(position, updatedSeat);
//
//                        // 刷新单个项目
//                        adapter.notifyItemChanged(position);
//
//                        Intent refreshIntent = new Intent("REFRESH_SEAT_RESERVATIONS");
//                        sendBroadcast(refreshIntent);
//
//                        Toast.makeText(this, "座位预约成功！", Toast.LENGTH_SHORT).show();
//                    })
//                    .setNegativeButton("取消", null)
//                    .show();
//        }
//    }

    private void showSeatInfo(Seat seat) {
        String message = "座位 " + seat.getId() + " 已被预约";
        if (seat.getReservationTime() > 0) {
            java.util.Date date = new java.util.Date(seat.getReservationTime());
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
            message += "\n预约时间: " + sdf.format(date);
        }

        new AlertDialog.Builder(this)
                .setTitle("座位信息")
                .setMessage(message)
                .setPositiveButton("确定", null)
                .show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // 加载用户当前预约
        loadUserReservation();
    }

    private void loadUserReservation() {
        // 获取用户当前预约
        currentUserReservation = seatDao.getUserCurrentReservation(currentUserId);
    }

    private void handleSeatClick(Seat seat) {
        final int position = seatList.indexOf(seat);
        if (position == -1) return;

        if (seat.isReserved()) {
            showSeatInfo(seat);
        } else {
            // 检查用户是否已有预约
            if (currentUserReservation != null) {
                showReservationConflictDialog(seat);
            } else {
                showConfirmationDialog(seat, position);
            }
        }
    }
    private void showReservationConflictDialog(Seat newSeat) {
        new AlertDialog.Builder(this)
                .setTitle("预约冲突")
                .setMessage("您已预约了座位 " + currentUserReservation.getId() +
                        "\n是否要取消当前预约并预约新座位?")
                .setPositiveButton("是", (dialog, which) -> {
                    // 取消当前预约
                    cancelCurrentReservation();
                    // 预约新座位
                    reserveSeat(newSeat);
                })
                .setNegativeButton("否", null)
                .show();
    }

    private void showConfirmationDialog(Seat seat, int position) {
        new AlertDialog.Builder(this)
                .setTitle("确认预约")
                .setMessage("是否确认预约座位 " + seat.getId() + "?")
                .setPositiveButton("确认", (dialog, which) -> {
                    reserveSeat(seat);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void cancelCurrentReservation() {
        if (currentUserReservation != null) {
            // 找到当前预约座位在列表中的位置
            for (int i = 0; i < seatList.size(); i++) {
                Seat s = seatList.get(i);
                if (s.getId().equals(currentUserReservation.getId())) {
                    // 创建未预约状态的座位对象
                    Seat updatedSeat = new Seat(
                            s.getId(),
                            s.getRow(),
                            s.getColumn(),
                            false,
                            0
                    );

                    // 更新列表
                    seatList.set(i, updatedSeat);
                    adapter.notifyItemChanged(i);
                    break;
                }
            }

            // 取消数据库中的预约
            seatDao.cancelUserReservations(currentUserId);
            currentUserReservation = null;
        }
    }

    private void reserveSeat(Seat seat) {
        final int position = seatList.indexOf(seat);
        if (position == -1) return;

        // 创建新的预约座位对象
        Seat updatedSeat = new Seat(
                seat.getId(),
                seat.getRow(),
                seat.getColumn(),
                true,
                System.currentTimeMillis()
        );

        // 保存到数据库
        boolean success = seatDao.saveReservation(updatedSeat, currentUserId);

        if (success) {
            // 更新当前预约
            currentUserReservation = updatedSeat;

            // 更新列表
            seatList.set(position, updatedSeat);
            adapter.notifyItemChanged(position);

            Toast.makeText(this, "座位预约成功！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "预约失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
}