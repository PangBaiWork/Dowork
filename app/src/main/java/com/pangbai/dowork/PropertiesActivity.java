package com.pangbai.dowork;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pangbai.dowork.Command.cmdExer;
import com.pangbai.dowork.databinding.ActivityPropertiesBinding;
import com.pangbai.dowork.fragment.PropertiesFragment;
import com.pangbai.dowork.fragment.RootfsInstallFragment;
import com.pangbai.dowork.service.mainService;
import com.pangbai.dowork.service.mainServiceConnection;
import com.pangbai.dowork.tool.IO;
import com.pangbai.dowork.tool.Init;
import com.pangbai.dowork.tool.containerInfor;
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
        binding.ctExport.setOnClickListener(this);
        binding.ctImport.setOnClickListener(this);
      /*  if (mainService.isCmdRunning) {
            binding.ctActionRun.setBackgroundResource(R.drawable.stop);
        }*/
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
            PrefStore.dumpProperties(this);
            if (containerInfor.checkInstall(this))
                dialogUtils.showConfirmationDialog(this,"rootfs已安装","此容器Rootfs已安装,确定要重装吗",
                        "重装","取消",
                        this::installAndStop,null);
            else
                installAndStop();

        }else if (view==binding.ctImport){
            PrefStore.dumpProperties(this);
            if (containerInfor.checkInstall(this))
                dialogUtils.showConfirmationDialog(this,"Rootfs已安装","此容器Rootfs已安装,确定要重装吗",
                        "重装","取消",
                        ()->importAndExportContainer(false),null);
            else
                importAndExportContainer(false);

        }else  if (view==binding.ctExport){
            PrefStore.dumpProperties(this);
            importAndExportContainer(true);
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

    private void exitConfirm() {
        if (!isSaved) {

            dialogUtils.showConfirmationDialog(this,
                    "配置文件未保存",
                    "确定要放弃修改吗？你将会丢失已修改的数据。",
                    "保存并退出",
                    "退出",
                    () -> {
                        PrefStore.dumpProperties(PropertiesActivity.this);
                        PrefStore.changeProfile(this,containerInfor.ct.name);
                        finish();

                    },
                    () ->{ PrefStore.changeProfile(this,containerInfor.ct.name);
                            finish();});
        } else
            finish();
    }



    public void  importAndExportContainer(boolean export){
        String str_action=export?"导出":"导入",
        str_arg=export?"export":"import",
                str_http=export?"":"&url地址";

        dialogUtils.showInputDialog(this,
                "请输入"+str_action+"Rootfs绝对路径"+str_http+"\ntar.gz/tar.bz2/tar.xz/tar.zst",
                Rootfs -> {
                    if (!export&&!Rootfs.contains("http")&&!IO.isFileExsit(Rootfs)){
                        Toast.makeText(this,"Rootfs路径错误",Toast.LENGTH_LONG).show();
                        return;}
                 //   Dialog mdialog = dialogUtils.showCustomLayoutDialog(this, "正在"+str_action+"Rootfs" + containerInfor.ct.name, R.layout.dialog_loading);

                    String cmd = Init.linuxDeployDirPath + "/cli.sh "+str_arg +" "+ Rootfs;
                    Intent mIntent = new Intent(this, mainService.class);
                    mIntent.putExtra("action", mainService.action_exeCmd);
                    mIntent.putExtra("value", cmd);
                    startService(mIntent);

                });

    }

    public void installAndStop(){
        new RootfsInstallFragment().show(getSupportFragmentManager(), "");
     //   dialogUtils.showConfirmationDialog();
        /*
        Intent mIntent = new Intent(this, mainService.class);

        if (!mainService.isCmdRunning) {
            ///////start

            isSaved = true;
            mIntent.putExtra("action", mainService.action_exeCmd);
            mIntent.putExtra("value",  Init.linuxDeployDirPath + "/cli.sh deploy");
            startService(mIntent);
            if (serviceConnection == null)
                serviceConnection = new mainServiceConnection(result -> {
                    if (binding != null)
                        binding.ctActionRun.setBackgroundResource(R.drawable.ct_run_task);
     //               Toast.makeText(this,"j"+Init.isRoot,Toast.LENGTH_LONG).show();
                });
            bindService(mIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            binding.ctActionRun.setBackgroundResource(R.drawable.stop);
        } else {
            //////stop
            mIntent.putExtra("action", mainService.action_stopCmd);
            startService(mIntent);
            if (serviceConnection != null)
                unbindService(serviceConnection);
            serviceConnection = null;
            binding.ctActionRun.setBackgroundResource(R.drawable.ct_run_task);
        }

         */

    }



}
