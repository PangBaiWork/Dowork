package com.pangbai.dowork;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pangbai.dowork.databinding.ActivityPropertiesBinding;
import com.pangbai.dowork.fragment.PropertiesFragment;
import com.pangbai.dowork.service.mainService;
import com.pangbai.dowork.tool.Init;
import com.pangbai.linuxdeploy.PrefStore;

public class PropertiesActivity extends AppCompatActivity implements View.OnClickListener {
     ActivityPropertiesBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefStore.setLocale(this);
        PrefStore.changeProfile(this,"linux");
        binding=ActivityPropertiesBinding.inflate(getLayoutInflater());
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
        setTitle(titleMsg);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Update configuration file

    }

    @Override
    public void onClick(View view) {
        if (view==binding.ctExit){
            PrefStore.dumpProperties(this);
            finish();
        }else if (view==binding.ctActionRun){
            if (mainService.isCmdRunning)
                binding.ctActionRun.setBackgroundResource(R.drawable.ct_run_task);
            else
                binding.ctActionRun.setBackgroundResource(R.drawable.stop);
            PrefStore.dumpProperties(this);
            Intent mIntent=new Intent(this, mainService.class);
            mIntent.putExtra("action",mainService.action_exeCmd);
            mIntent.putExtra("value", Init.linuxDeployDirPath+"/cli.sh deploy");
            startService(mIntent);

            //Toast.makeText(this, "嘿嘿", Toast.LENGTH_SHORT).show();

        }

    }
}
