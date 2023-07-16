package com.pangbai.dowork;

import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.pangbai.dowork.databinding.ActivityMainBinding;
import com.pangbai.dowork.tool.Init;
import com.pangbai.dowork.tool.util;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener, BottomNavigationView.OnItemSelectedListener {
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

    binding.navView.setOnItemSelectedListener(this);

     NavHostFragment host=(NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.nav_fragment);
    NavigationUI.setupWithNavController(binding.navView,host.getNavController());
        
    /*binding.button.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            util.startActivity(MainActivity.this, TermActivity.class, false);
          }
        });*/
        
  }
    


  @Override
  public void onClick(View view) {}

  @Override
  public boolean onNavigationItemSelected(MenuItem arg0) {
      
        return true;
  }
}
