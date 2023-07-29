package com.pangbai.dowork;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.pangbai.dowork.databinding.ActivityMainBinding;
import com.pangbai.dowork.tool.Init;
import com.pangbai.dowork.tool.util;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener, BottomNavigationView.OnItemSelectedListener {
  private TabLayout tabLayout;
  private ViewPager viewPager;
    private static final int REQUEST_CODE = 1;
  private ActivityMainBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
   util.fullScreen(getWindow(), false);
    new Init(MainActivity.this);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());


    binding.navView.setOnItemSelectedListener(this);

     NavHostFragment host=(NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.nav_fragment);
    NavigationUI.setupWithNavController(binding.navView,host.getNavController());


      }


  }
    


  @Override
  public void onClick(View view) {}

  @Override
  public boolean onNavigationItemSelected(MenuItem arg0) {
      
        return true;
  }


    /////////////////////////////////////////////悬浮窗权限回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {

            } else {
                Toast.makeText(getApplication(), "请开启悬浮窗权限", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

