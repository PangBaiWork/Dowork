package com.pangbai.dowork.Command;

import android.app.Activity;
import android.graphics.Typeface;
import android.widget.RelativeLayout;

import com.pangbai.dowork.TermActivity;
import com.pangbai.dowork.tool.Init;
import com.pangbai.terminal.TerminalSession;
import com.pangbai.view.ExtraKeysView;
import com.pangbai.view.SuperTerminalView;
import com.pangbai.view.TerminalView;

public class CommandBuilder {

        public SuperTerminalView cmdView;
        public  ExtraKeysView keysView;
        public int textcolor=0;
        public static boolean roll=true;
        String dataDir,termDir,cmd,envp[];
        String args[]={"sh",};
       //String args[];
        public   CommandBuilder(TermActivity ct , RelativeLayout layout) {
            //	layout.setBackgroundColor(Color.BLACK);
            /*背景*/
            //commandview. setBackgroundDrawable(ct.getResources().getDrawable(R.drawable.bg));

            cmdView = new SuperTerminalView(ct);
            keysView=new ExtraKeysView(ct,cmdView);
            cmdView.setKeyView(keysView);
            envp= envpGet();
            termDir= Init.filesDirPath + "/tmp";
            cmd = Init.filesDirPath + "/usr/bin/busybox";
            cmdView. setTypeface(Typeface.createFromFile(Init.fontPath));
            layout.addView(cmdView);

            if (textcolor != 0)
                cmdView.mRenderer.deffontcolor = textcolor;

            cmdView.setProcess(cmd, Init.filesDirPath, args, envp, 0);

            cmdView.runProcess();


            //commandview.setClickable(false);

            cmdView.requestFocus();
            cmdView.setKeepScreenOn(true);




        }

     public static String[] envpGet(){



         //cmd = "/system/bin/sh";
         //   String args[]={"sh",};

         String envp[]={
                 //"PATH=" + "/system/bin"
                 "PATH=" + Init.binDirPath+":/product/bin:/apex/com.android.runtime/bin:/apex/com.android.art/bin:/system_ext/bin:/system/bin:/system/xbin:/vendor/bin",
                 "HOME=" + Init.filesDirPath,
                 "PREFIX=" + Init.filesDirPath + "/usr",
                 "LD_LIBRARY_PATH=" + Init.filesDirPath + "/usr/lib",
                 "PS1=\\[\\e[1\\;31m\\])➜ \\[\\e[1;36m\\]\\W\\[\\e[m\\] ",
                 "TERM=xterm-256color",
                 "LANG=en_US.UTF-8",
                 "ANDROID_DATA=/data",
                 "ANDROID_ROOT=/system"
         };



           return  envp;
     }
     public static String[] getExeArgs(String cmd){
        String args[]={"sh","-c",cmd};
            return args;
    }





}
