package com.pangbai.dowork;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pangbai.dowork.databinding.ActivityPropertiesBinding;
import com.pangbai.dowork.fragment.PropertiesFragment;
import com.pangbai.dowork.service.mainService;
import com.pangbai.dowork.service.mainServiceConnection;
import com.pangbai.dowork.tool.Init;
import com.pangbai.linuxdeploy.PrefStore;
import com.pangbai.view.dialogUtils;

public class PropertiesActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityPropertiesBinding binding;
    boolean isSaved = false;
    mainServiceConnection serviceConnection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefStore.setLocale(this);
        //  PrefStore.changeProfile(this,"linux");
        PrefStore.restoreProperties(this);
        binding = ActivityPropertiesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_properties);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new PropertiesFragment())
                .commit();
        binding.ctExit.setOnClickListener(this);
        binding.ctActionRun.setOnClickListener(this);
        // Restore from conf file if open from main activity
        if (getIntent().getBooleanExtra("restore", false)) {
            PrefStore.restoreProperties(this);
        }
    }

    /*
        @Override
        public void setTheme(int resId) {
            super.setTheme(PrefStore.getTheme(this));
        }
    */
    @Override
    protected void onResume() {
        super.onResume();
        String titleMsg = getString(R.string.title_activity_properties)
                + ": " + PrefStore.getProfileName(this);
        binding.ctName.setText(titleMsg);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        if (view == binding.ctExit) {
            exitConfirm();

        } else if (view == binding.ctActionRun) {
            Intent mIntent = new Intent(this, mainService.class);
            if (!mainService.isCmdRunning) {
                ///////start
                PrefStore.dumpProperties(this);
                isSaved = true;
                mIntent.putExtra("action", mainService.action_exeCmd);
                mIntent.putExtra("value", Init.linuxDeployDirPath + "/cli.sh deploy");
                startService(mIntent);
                serviceConnection = new mainServiceConnection(result -> {
                    if (binding != null)
                        binding.ctActionRun.setBackgroundResource(R.drawable.ct_run_task);
                });
                bindService(mIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                binding.ctActionRun.setBackgroundResource(R.drawable.stop);
            }else {
                //////stop
                mIntent.putExtra("action", mainService.action_stopCmd);
                startService(mIntent);
                if (serviceConnection != null)
                    unbindService(serviceConnection);
                serviceConnection = null;
                binding.ctActionRun.setBackgroundResource(R.drawable.ct_run_task);
            }

        }
    }


    @Override
    public void onBackPressed() {
      exitConfirm();
    }

    @Override
    protected void onDestroy() {
        if (serviceConnection != null)
            unbindService(serviceConnection);
        binding = null;
        serviceConnection = null;
        super.onDestroy();
    }
    private  void  exitConfirm(){
        if (!isSaved){
            dialogUtils.showConfirmationDialog(this,
                "配置文件未保存",
                "确定要放弃修改吗？你将会丢失已修改的数据。",
                "保存并退出",
                "退出",
                () -> {
                    PrefStore.dumpProperties(PropertiesActivity.this);
                    finish();},
                () -> finish());}
        else
           finish();
    }
}
