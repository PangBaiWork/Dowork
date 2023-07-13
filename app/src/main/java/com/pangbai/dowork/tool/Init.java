package com.pangbai.dowork.tool;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.pangbai.dowork.Command.cmdExer;
import com.pangbai.view.CustomDialog;

import java.io.File;

public class Init {
    public  Init(Activity ct){

        File files=ct.getFilesDir();
        if(!files.exists())
            files.mkdir();
        Log.e("初始化",""+(files.list().length));
        if(files.list().length<3) {
             CustomDialog mDialog= util.popLoading(ct,"初始化中");
            new Thread(){
                @Override
                public  void  run(){
                    IO.copyAssetsDirToSDCard(ct,"files",files.getParentFile().getAbsolutePath());
                    File bin= new File(files.getAbsolutePath()+"/usr/bin");
                    File dowork= new File(files.getAbsolutePath()+"/dowork");
                    if(bin.exists()){
                        cmdExer.cmdResult result;
                        String binPath= bin.getAbsolutePath();
                        String chmod="chmod +x -R ";
                        String busybox=binPath+"/busybox"+" --install -s " +binPath;
                      //  Log.e("初始化",cmd);
                        result=cmdExer.execute(chmod+binPath);
                        result=cmdExer.execute(chmod+dowork);

                        Log.e("初始化",""+result.getOutput());
                        result=cmdExer.execute(busybox);
                        Log.e("初始化",""+result.getOutput());
                    }
                    mDialog.dismiss();
                    util.ensureStoragePermissionGranted(ct);

                }
            }.start();


     }
     }

}
