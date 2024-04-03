package com.pangbai.dowork.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class mainServiceConnection implements ServiceConnection {

    serviceCallback callback;
   public mainServiceConnection(serviceCallback callback){
       this.callback=callback;
   }
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        // 设置回调
        mainService.MyBinder myBinder = (mainService.MyBinder) iBinder;
        mainService mService = myBinder.getService();
        if (mService != null) {
            mService.setCallback(callback);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        callback = null;
    }
}
