package com.pangbai.dowork.Command;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.RelativeLayout;

import com.pangbai.dowork.TermActivity;
import com.pangbai.dowork.tool.Init;
import com.pangbai.dowork.tool.containerInfor;
import com.pangbai.terminal.TerminalSession;
import com.pangbai.view.ExtraKeysView;
import com.pangbai.view.SuperTerminalView;
import com.pangbai.view.TerminalView;

public class CommandBuilder {

    public SuperTerminalView cmdView;
    public ExtraKeysView keysView;
    public int textcolor = 0;
    public static boolean roll = true;
    public static final int type_sh = 1;
    public static final int type_proot = 2;
    public static final int type_chroot = 3;
    String dataDir, termDir, cmd, envp[];


    //String args[];
    public CommandBuilder(TermActivity ct, int type, RelativeLayout layout) {
        //	layout.setBackgroundColor(Color.BLACK);
        /*背景*/
        //commandview. setBackgroundDrawable(ct.getResources().getDrawable(R.drawable.bg));

        cmdView = new SuperTerminalView(ct);
        keysView = new ExtraKeysView(ct, cmdView);
        cmdView.setKeyView(keysView);
        envp = envpGet();
        termDir = Init.filesDirPath + "/tmp";
        cmd = Init.filesDirPath + "/usr/bin/busybox";
        String args[];

        if (type == type_sh) {
            args = new String[]{"sh"};
        } else if (type == type_proot) {
            args = new String[]{"sh", Init.linuxDeployDirPath + "/cli.sh", "shell","bash"};
        } else {
            Log.e("linuxdeploy", "chroot");
            cmd = "/system/bin/su";
            args = new String[]{"su", "-c", "sh", Init.linuxDeployDirPath + "/cli.sh", "shell"};
        }

        cmdView.setTypeface(Typeface.createFromFile(Init.fontPath));
        layout.addView(cmdView);

        if (textcolor != 0)
            cmdView.mRenderer.deffontcolor = textcolor;

        cmdView.setProcess(cmd, Init.filesDirPath, args, envp, 0);

        cmdView.runProcess();


        //commandview.setClickable(false);

        cmdView.requestFocus();
        cmdView.setKeepScreenOn(true);


    }

    public static String[] envpGet() {


        //cmd = "/system/bin/sh";
        //   String args[]={"sh",};

        String envp[] = {
                //"PATH=" + "/system/bin"
                "PATH=" + Init.binDirPath + ":/product/bin:/apex/com.android.runtime/bin:/apex/com.android.art/bin:/system_ext/bin:/system/bin:/system/xbin:/vendor/bin",
                "HOME=" + Init.filesDirPath,
                "PREFIX=" + Init.filesDirPath + "/usr",
                "LD_LIBRARY_PATH=" + Init.filesDirPath + "/usr/lib",
                "PS1=\\[\\e[1\\;31m\\])➜ \\[\\e[1;36m\\]\\W\\[\\e[m\\] ",
                "TERM=xterm-256color",
                "LANG=en_US.UTF-8",
                "ANDROID_DATA=/data",
                "ANDROID_ROOT=/system"
        };


        return envp;
    }
  public static void stopChroot() {
        Log.e("chroot","umount");

            cmdExer.execute(Init.linuxDeployDirPath +"/cli.sh umount",true,false);

  }

    public static String[] getExeArgs(String cmd) {
        String args[] = {"sh", "-c", cmd};
        return args;
    }


}
