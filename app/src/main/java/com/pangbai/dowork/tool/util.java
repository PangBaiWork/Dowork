package com.pangbai.dowork.tool;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.Window;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class util {

  public  static List<String> getByTwoString(String input, String one, String two){
      Pattern pattern = Pattern.compile(one+"(.*?)"+two);
      Matcher matcher = pattern.matcher(input);
      List<String> result=new ArrayList<>();

      // 查找匹配项并输出内容
      while (matcher.find()) {
        result.add(matcher.group(1)); // 获取第一个捕获组的内容
         // System.out.println("提取的内容: " + extracted);
      }
      return result;
  }
    public static void startActivity(Context ct, Class activity, boolean anim) {
        Intent it = new Intent(ct.getApplicationContext(), activity);
        if (anim) {
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        if (ct instanceof Activity)
            ct.startActivity(it, ActivityOptions.makeSceneTransitionAnimation((Activity) ct).toBundle());
        else
            ct.startActivity(it);
    }


    public static void fullScreen(Window window, boolean isTransparent) {
//	  activityTo.startActivity(new Context,main.class,true );
        View decorView = window.getDecorView();
        int uiOptions;
        if (isTransparent) {
            //for Terminal
            uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_VISIBLE;
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.BLACK);
        } else {
            uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_VISIBLE;
            window.setNavigationBarColor(Color.parseColor("#162039"));
        }
        decorView.setSystemUiVisibility(uiOptions);

        //  window.setNavigationBarColor(Color.TRANSPARENT);
    }


    public static int Dp2Px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    public static int Px2Dp(Context context, float px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }




    @TargetApi(Build.VERSION_CODES.M)
    public static boolean ensureStoragePermissionGranted(Activity ct) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ct.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ct.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1234);
                return false;
            }
        } else {
            // Always granted before Android 6.0.
            return true;
        }
    }


    public  static boolean windowPermissionCheck(Context context) {
        Boolean result = true;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class clazz = Settings.class;
                Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                result = (Boolean) canDrawOverlays.invoke(null, context);
            } catch (Exception e) {
            }
        }
        return result;
    }



    public static boolean isRooted() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            try (DataOutputStream stdin = new DataOutputStream(process.getOutputStream());
                 BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                stdin.writeBytes("ls /data\n");
                stdin.writeBytes("exit\n");
                stdin.flush();

                return stdout.readLine() != null;
            }
        } catch (IOException e) {
            return false;
        }
    }

}
