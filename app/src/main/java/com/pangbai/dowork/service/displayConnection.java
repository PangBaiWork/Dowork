package com.pangbai.dowork.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.Surface;

import com.pangbai.dowork.tool.jni;

public class displayConnection implements ServiceConnection {


    Surface surface;
    serviceCallback callback;

    public displayConnection(Surface surface, serviceCallback callback) {
        this.surface = surface;
        this.callback = callback;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        // 设置回调
        display.MyBinder myBinder = (display.MyBinder) iBinder;
        display mService = myBinder.getService();
        if (mService != null) {
            mService.surface = surface;
            mService.setCallback(callback);
            jni.stopDraw();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        callback = null;
        surface = null;
    }


}
