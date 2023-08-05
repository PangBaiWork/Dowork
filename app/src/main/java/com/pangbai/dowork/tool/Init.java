package com.pangbai.dowork.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.pangbai.dowork.Command.cmdExer;
import com.pangbai.view.CustomDialog;

import java.io.File;

public class Init {
    public static String filesDirPath;

   public static String linuxDeployDirPath;
   public static  String fontPath;
   public  static  String keyPath;
   public static String binDirPath;
 //  public static String shellPath;
   public static String busyboxPath;
    public  Init(Activity ct){



        File files=ct.getFilesDir();

        filesDirPath=files.getAbsolutePath();
        fontPath=filesDirPath+"/dowork/terminal/font.ttf";
        linuxDeployDirPath=filesDirPath+"/dowork/cli";
        binDirPath=filesDirPath+"/usr/bin";
        //    shellPath=binDirPath+"/sh";
        keyPath=filesDirPath+"/dowork/terminal/keys";
        busyboxPath=binDirPath+"/busybox";

        if(!files.exists())
            files.mkdir();

        Log.e("初始化",""+(files.list().length));
        if(files.list().length<3) {
             CustomDialog mDialog= util.popLoading(ct,"初始化中");
            new Thread(){
                @Override
                public  void  run(){
                    IO.copyAssetsDirToSDCard(ct,"files",files.getParentFile().getAbsolutePath());
                   File bin= new File(binDirPath);
                   //File dowork= new File(files.getAbsolutePath()+"/dowork");
                    if(bin.exists()){
                        Boolean result;
                     //   String binPath= bin.getAbsolutePath();
                        String chmod="chmod 777 -R ";
                        String busybox=busyboxPath+" --install -s " +binDirPath;
                      //  Log.e("初始化",cmd);
                        result=cmdExer.execute(chmod+binDirPath);
                        result=cmdExer.execute(chmod+linuxDeployDirPath);
                        Log.e("初始化",""+result);
                        result=cmdExer.execute(busybox);
                        Log.e("初始化",""+result);
                        //cmdExer.execute(binDirPath+"/Init");
                      String ln="ln -s "+binDirPath+"/ztsd "+binDirPath;
                       result=cmdExer.execute(ln+"/unztsd");
                        cmdExer.execute(ln+"/zstdcat");
                        cmdExer.execute(ln+"/zstdmt");

                        Log.e("初始化Init",""+result);
                    }
                    mDialog.dismiss();
                    util.ensureStoragePermissionGranted(ct);

                }
            }.start();
     }


    }



}
