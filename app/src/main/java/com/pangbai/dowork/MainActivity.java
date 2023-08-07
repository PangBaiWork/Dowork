package com.pangbai.dowork;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.pangbai.dowork.databinding.ActivityMainBinding;
import com.pangbai.dowork.tool.Init;
import com.pangbai.dowork.tool.util;
import com.pangbai.linuxdeploy.PrefStore;
import com.pangbai.view.dialogUtils;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, BottomNavigationView.OnItemSelectedListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private static final int REQUEST_CODE_FLOATING_WINDOW = 1001;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        util.fullScreen(getWindow(), false);
        //  new Init(MainActivity.this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.navView.setOnItemSelectedListener(this);
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_fragment);
        NavigationUI.setupWithNavController(binding.navView, host.getNavController());

        ensureWindowPermission();
    }


    @Override
    public void onClick(View view) {
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem arg0) {
        return true;
    }






    public void ensureWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {

                dialogUtils.showConfirmationDialog(this,
                        "权限申请",
                        "为运行必要服务,请授予本软件权限,开发者承诺权限将只用于服务范围以内的用途",
                        "授权",
                        "退出",
                        () -> {Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                                Toast.makeText(this, "请授予悬浮窗权限", Toast.LENGTH_LONG).show();
                                startActivityForResult(intent, REQUEST_CODE_FLOATING_WINDOW);},
                        () -> finish());

            } else {
                // 已经有悬浮窗权限，可以在此处理相关逻辑
                new Init(MainActivity.this);
            }
        } else {
            // Android版本低于M，无需申请悬浮窗权限
            new Init(MainActivity.this);
        }
    }

    /////////////////////////////////////////////悬浮窗权限回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_FLOATING_WINDOW) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // 用户已授予悬浮窗权限，可以在此处理相关逻辑
                    new Init(MainActivity.this);
                } else {
                    // 用户未授予悬浮窗权限，可以在此处理相关逻辑
                    finish();
                }
            }

        }
    }
}

