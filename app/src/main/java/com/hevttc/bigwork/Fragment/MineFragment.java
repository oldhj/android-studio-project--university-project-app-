package com.hevttc.bigwork.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.hevttc.bigwork.AboutAppActivity;
import com.hevttc.bigwork.ChangePasswordActivity;
import com.hevttc.bigwork.LoginActivity;
import com.hevttc.bigwork.R;
import com.hevttc.bigwork.bean.Student;

public class MineFragment extends Fragment {

    private static final int REQUEST_CHANGE_PASSWORD = 1001;
    private View rootView;
    private TextView tv_username,tv_nickname;
    private TextView exit;
    private RelativeLayout alter_password,about_App;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_mine, container, false);

        exit = rootView.findViewById(R.id.exit);
        alter_password = rootView.findViewById(R.id.alter_password);
        about_App = rootView.findViewById(R.id.about_App);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("温馨提示")
                        .setMessage("确认要退出吗")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getActivity().finish();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                            }
                        })

                        .show();
            }
        });
        alter_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivityForResult(intent, REQUEST_CHANGE_PASSWORD);
            }
        });

        about_App.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AboutAppActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHANGE_PASSWORD) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                boolean passwordChanged = data.getBooleanExtra("password_changed", false);
                if (passwordChanged) {
                    Toast.makeText(getContext(), "密码已成功更新", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}