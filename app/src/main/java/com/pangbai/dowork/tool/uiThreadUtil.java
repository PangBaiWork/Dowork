package com.pangbai.dowork.tool;
import android.os.Handler;
import android.os.Looper;

public class uiThreadUtil {

    public static Handler handler = new Handler(Looper.getMainLooper());

    public static void runOnUiThread(Runnable runnable) {
        handler.postDelayed(runnable,2);
    }
}
