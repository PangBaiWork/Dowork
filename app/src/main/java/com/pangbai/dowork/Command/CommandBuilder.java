package com.pangbai.dowork.Command;

import android.app.Activity;
import android.graphics.Typeface;
import android.widget.RelativeLayout;

import com.pangbai.terminal.TerminalSession;
import com.pangbai.view.ExtraKeysView;
import com.pangbai.view.SuperTerminalView;
import com.pangbai.view.TerminalView;

public class CommandBuilder {

        public SuperTerminalView cmdView;
        public static ExtraKeysView keysView;
        public int textcolor=0;
        public static boolean roll=true;
        String dataDir,termDir,cmd,envp[];
        String args[]={"sh",};
       //String args[];
        public   CommandBuilder(Activity ct , RelativeLayout layout, int textsize) {
            //	layout.setBackgroundColor(Color.BLACK);
            /*背景*/
            //commandview. setBackgroundDrawable(ct.getResources().getDrawable(R.drawable.bg));

            cmdView = new SuperTerminalView(ct);
            keysView=new ExtraKeysView(ct,cmdView);
            cmdView.setKeyView(keysView);
            envp= mainCmd();
            cmdView.setTextSize(textsize);
            cmdView. setTypeface(Typeface.createFromFile("/data/data/com.pangbai.dowork/files/dowork/terminal/font.ttf"));
            layout.addView(cmdView);

            if (textcolor != 0)
                cmdView.mRenderer.deffontcolor = textcolor;

            cmdView.setProcess(cmd, "/data/data/com.pangbai.dowork/files/dowork/cli", args, envp, 0);

            cmdView.runProcess();


            //commandview.setClickable(false);

            cmdView.requestFocus();
            cmdView.setKeepScreenOn(true);




        }

     public String[] mainCmd(){

         dataDir="/data/data/com.pangbai.dowork/files";
         termDir=dataDir + "/tmp";
         //String cmd=datadir+"/a";
         //if (Command.roll)
         cmd = dataDir + "/usr/bin/busybox";
         //else
         //cmd = "/system/bin/sh";

      //   String args[]={"sh",};
         String envp[]={
                 //   "PATH=" + "/system/bin"
                 "PATH=" + "/data/data/com.pangbai.dowork/files/usr/bin:/product/bin:/apex/com.android.runtime/bin:/apex/com.android.art/bin:/system_ext/bin:/system/bin:/system/xbin:/vendor/bin",
                 "HOME=" + dataDir,
                 "PREFIX=" + dataDir + "/usr",
                 "LD_LIBRARY_PATH=" + dataDir + "/usr/lib",
                 "PS1=\\[\\e[1\\;31m\\])➜ \\[\\e[1;36m\\]\\W\\[\\e[m\\] ",
                 "TERM=xterm-256color",
                 "LANG=en_US.UTF-8",
                 "ANDROID_DATA=/data",
                 "ANDROID_ROOT=/system"
         };



           return  envp;
     }






}
