package com.pangbai.dowork.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.Surface;

import com.pangbai.dowork.tool.jni;

public class displayConnection implements ServiceConnection {
    Surface surface;

    public displayConnection(Surface surface) {
        this.surface = surface;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        // 设置回调
        display.MyBinder myBinder = (display.MyBinder) iBinder;
        display mService = myBinder.getService();
        mService.hideDisplay();
        display.isFulllScreen = true;
        if (mService != null) {
            mService.surface = surface;
            jni.stopDraw();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        surface = null;
    }


}
