package com.pangbai.dowork;

import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.pangbai.dowork.databinding.ActivityMainBinding;
import com.pangbai.dowork.layout.FragmentAdapter;
import com.pangbai.dowork.tool.Init;
import com.pangbai.dowork.tool.util;
import com.pangbai.view.CustomDialog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {
  private TabLayout tabLayout;
  private ViewPager viewPager;
  private ActivityMainBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    util.fullScreen(getWindow(), false);
    new Init(MainActivity.this);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    // tabLayout = binding.tab;
    // viewPager = binding.tabPager;

    binding.navView.setOnNavigationItemSelectedListener(this);

    binding.button.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            util.startActivity(MainActivity.this, TermActivity.class, false);
          }
        });
  }

  public void setTablayout() {
    List<String> title = new ArrayList<>();
    List<Fragment> fragments = new ArrayList<>();
    title.add("Dashboard");
    title.add("Container");
    title.add("Xserver");
    title.add("Terminal");
    for (String str : title) {
      tabLayout.addTab(tabLayout.newTab().setText(str));
    }

    for (int i = 0; i < title.size(); i++) {
      fragments.add(new ListFragment());
    }
    FragmentAdapter fragmentAdapter =
        new FragmentAdapter(getSupportFragmentManager(), fragments, title);
    viewPager.setAdapter(fragmentAdapter);
    tabLayout.setupWithViewPager(viewPager);
    tabLayout.setTabsFromPagerAdapter(fragmentAdapter);
  }

  @Override
  public void onClick(View view) {}

  @Override
  public boolean onNavigationItemSelected(MenuItem arg0) {
        
      return true;
  }
}
