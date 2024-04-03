package com.pangbai.dowork.tool;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;

import com.pangbai.dowork.Command.cmdExer;
import com.pangbai.dowork.R;
import com.pangbai.view.dialogUtils;

import java.io.File;

public class Init {
    public static String filesDirPath;
    public static boolean  isRoot;
   public static String linuxDeployDirPath;
   public static  String fontPath;
   public  static  String keyPath;
    public  static  String tmpPath;
   public static String binDirPath;
   public static String []envp;

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
         envp=new String[] {
                //"PATH=" + "/system/bin"
                "PATH=" + Init.binDirPath + ":/product/bin:/apex/com.android.runtime/bin:/apex/com.android.art/bin:/system_ext/bin:/system/bin:/system/xbin:/vendor/bin",
                "HOME=" + Init.filesDirPath,
                "PREFIX=" + Init.filesDirPath + "/usr",
                "LD_LIBRARY_PATH=" + Init.filesDirPath + "/usr/lib",
                "PS1=\\[\\e[1\\;31m\\])➜ \\[\\e[1;36m\\]\\W\\[\\e[m\\] ",
                "TERM=xterm-256color",
                "LANG=en_US.UTF-8",
                "ANDROID_DATA=/data",
                "ANDROID_ROOT=/system",

        };



        if(!files.exists())
            files.mkdir();

        Log.e("初始化",""+(files.list().length));


        if(files.list().length<3) {
          Dialog mdialog= dialogUtils.showCustomLayoutDialog(ct, "初始化中",R.layout.dialog_loading);
            new Thread(){
                @Override
                public  void  run(){
                    IO.copyAssetsDirToSDCard(ct,"files",files.getParentFile().getAbsolutePath());
                   File bin= new File(binDirPath);
                    if(bin.exists()){
                        int result;
                        String chmod="chmod 777 -R ";
                        String busybox=busyboxPath+" --install -s " +binDirPath;
                        result=cmdExer.execute(chmod+binDirPath,false);
                        result=cmdExer.execute(chmod+linuxDeployDirPath,false);
                        result=cmdExer.execute(chmod+filesDirPath+"/dowork/pulseaudio",false);
                        Log.e("初始化",""+result);
                        result=cmdExer.execute(busybox,false);
                        Log.e("初始化",""+result);
                        cmdExer.execute(binDirPath+"/doinit",false);
                    /*  String ln="ln -s "+binDirPath+"/ztsd "+binDirPath;
                       result=cmdExer.execute(ln+"/unztsd");
                        cmdExer.execute(ln+"/zstdcat");
                        cmdExer.execute(ln+"/zstdmt");*/

                        Log.e("初始化Init",""+result);
                    }
                    mdialog.dismiss();
                    util.ensureStoragePermissionGranted(ct);



                }
            }.start();


     }


    }



}
