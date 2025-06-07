package com.hevttc.bigwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hevttc.bigwork.Fragment.CourseFragment;
import com.hevttc.bigwork.Fragment.HomeFragment;
import com.hevttc.bigwork.Fragment.LibraryFragment;
import com.hevttc.bigwork.Fragment.MineFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private CourseFragment courseFragment;
    private LibraryFragment libraryFragment;
    private MineFragment mineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.BottomNavigationView);
        //默认首页
        selectedFragment(0);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.home){
                    selectedFragment(0);
                }else if(item.getItemId() == R.id.course){
                    selectedFragment(1);
                }else if(item.getItemId() == R.id.library){
                    selectedFragment(2);
                }else {
                    selectedFragment(3);
                }

            }
        });
    }

    private void selectedFragment(int position) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideFragment(fragmentTransaction);

        if(position==0){
            if(homeFragment==null){
                homeFragment = new HomeFragment();
                fragmentTransaction.add(R.id.content,homeFragment);
            }else{
                fragmentTransaction.show(homeFragment);
            }
        }else if(position == 1){
            if(courseFragment==null){
                courseFragment = new CourseFragment();
                fragmentTransaction.add(R.id.content,courseFragment);
            }else{
                fragmentTransaction.show(courseFragment);
            }
        }else if(position==2){
            if(libraryFragment==null){
                libraryFragment = new LibraryFragment();
                fragmentTransaction.add(R.id.content,libraryFragment);
            }else {
                fragmentTransaction.show(libraryFragment);
            }
        }else {
            if(mineFragment==null){
                mineFragment = new MineFragment();
                fragmentTransaction.add(R.id.content,mineFragment);
            }else {
                fragmentTransaction.show(mineFragment);
            }
        }

        fragmentTransaction.commit();

    }

    private void hideFragment(FragmentTransaction fragmentTransaction){
        if(homeFragment!=null){
            fragmentTransaction.hide(homeFragment);
        }
        if(courseFragment!=null){
            fragmentTransaction.hide(courseFragment);
        }
        if(libraryFragment!=null){
            fragmentTransaction.hide(libraryFragment);
        }
        if(mineFragment!=null){
            fragmentTransaction.hide(mineFragment);
        }
    }

}