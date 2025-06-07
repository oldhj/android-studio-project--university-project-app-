package com.hevttc.bigwork.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hevttc.bigwork.Adapter.BorrowRecordAdapter;
import com.hevttc.bigwork.Adapter.SeatAdapter;
import com.hevttc.bigwork.Adapter.SeatReservationAdapter;
import com.hevttc.bigwork.BookBorrowActivity;
import com.hevttc.bigwork.Dao.BorrowRecordDao;
import com.hevttc.bigwork.Dao.SeatReservationDao;
import com.hevttc.bigwork.Manager.LoginManager;
import com.hevttc.bigwork.R;
import com.hevttc.bigwork.SeatReservationActivity;
import com.hevttc.bigwork.bean.BorrowRecord;
import com.hevttc.bigwork.bean.Seat;

import java.util.ArrayList;
import java.util.List;


public class LibraryFragment extends Fragment {
    private boolean isReceiverRegistered = false;
    private RecyclerView rvSeatReservations;
    private SeatReservationAdapter seatReservationAdapter;
    private TextView tvMySeatReservations;
    private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadBorrowRecords();
            loadSeatReservations(); // 添加这行
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        RecyclerView rv = view.findViewById(R.id.rvBorrowRecords);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new BorrowRecordAdapter(new ArrayList<>())); // 初始空数据

        view.findViewById(R.id.card_book_borrow).setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), BookBorrowActivity.class));
        });
        view.findViewById(R.id.card_seat_reservation).setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), SeatReservationActivity.class));
        });
        loadBorrowRecords();
        // 座位预约信息部分
        tvMySeatReservations = view.findViewById(R.id.tvMySeatReservations);
        rvSeatReservations = view.findViewById(R.id.rvSeatReservations);
        rvSeatReservations.setLayoutManager(new LinearLayoutManager(getContext()));
        seatReservationAdapter = new SeatReservationAdapter(new ArrayList<>());
        rvSeatReservations.setAdapter(seatReservationAdapter);

        // 加载座位预约信息
        loadSeatReservations();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isReceiverRegistered) {
            // 注册两个广播
            IntentFilter filter = new IntentFilter();
            filter.addAction("REFRESH_BORROW_RECORDS");
            filter.addAction("REFRESH_SEAT_RESERVATIONS");
            requireContext().registerReceiver(refreshReceiver, filter);
            isReceiverRegistered = true;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        loadBorrowRecords(); // 每次进入页面强制刷新
        loadSeatReservations(); // 添加座位预约刷新
    }

    @Override
    public void onStop() {
        super.onStop();
        // 在onStop中注销接收器，并检查注册状态
        if (isReceiverRegistered) {
            requireContext().unregisterReceiver(refreshReceiver);
            isReceiverRegistered = false;
        }
    }

    private void loadBorrowRecords() {
        new Thread(() -> {
            // 获取当前登录学生ID
            LoginManager loginManager = new LoginManager(requireContext());
            String studentId = loginManager.getCurrentStudentId();

            if (studentId == null || studentId.isEmpty()) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            BorrowRecordDao dao = new BorrowRecordDao(requireContext());
            List<BorrowRecord> records = dao.getRecordsByUser(studentId); // 使用当前用户ID

            requireActivity().runOnUiThread(() -> {
                BorrowRecordAdapter adapter = (BorrowRecordAdapter) ((RecyclerView) requireView()
                        .findViewById(R.id.rvBorrowRecords)).getAdapter();
                if (adapter != null) {
                    adapter.updateData(records);
                }
            });
        }).start();
    }
    // 加载座位预约信息
    private void loadSeatReservations() {
        new Thread(() -> {
            // 获取当前登录学生ID
            LoginManager loginManager = new LoginManager(requireContext());
            String studentId = loginManager.getCurrentStudentId();

            if (studentId == null || studentId.isEmpty()) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show()
                );
                return;
            }
            SeatReservationDao seatDao = new SeatReservationDao(requireContext());
            List<Seat> reservations = seatDao.getUserReservations(studentId); // 使用当前用户ID

            requireActivity().runOnUiThread(() -> {
                if (reservations != null && !reservations.isEmpty()) {
                    // 确保视图可见
                    tvMySeatReservations.setVisibility(View.VISIBLE);
                    rvSeatReservations.setVisibility(View.VISIBLE);

                    // 更新适配器数据
                    seatReservationAdapter.updateData(reservations);

                    // 打印调试信息
                    Log.d("SeatReservation", "显示预约记录: " + reservations.size());
                    for (Seat seat : reservations) {
                        Log.d("SeatReservation", "座位: " + seat.getId() + ", 时间: " + seat.getReservationTime());
                    }
                } else {
                    tvMySeatReservations.setVisibility(View.GONE);
                    rvSeatReservations.setVisibility(View.GONE);
                    Log.d("SeatReservation", "没有预约记录");
                }
            });
        }).start();
    }
}