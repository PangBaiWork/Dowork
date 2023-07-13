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
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.pangbai.dowork.MainActivity;
import com.pangbai.dowork.R;
import com.pangbai.dowork.databinding.LayoutLoadingBinding;
import com.pangbai.view.CustomDialog;

public class util {
    public static void startActivity(Activity ct, Class activity, boolean anim){
            Intent it = new Intent(ct.getApplicationContext(),activity);
            if(anim){
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }
            ct.startActivity(it, ActivityOptions.makeSceneTransitionAnimation(ct).toBundle());
        }

    public static void fullScreen(Window window, boolean isTransparent){
//	  activityTo.startActivity(new Context,main.class,true );
        View decorView = window.getDecorView();
        if(isTransparent){

            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE| View.SYSTEM_UI_FLAG_VISIBLE);
            window.setStatusBarColor(Color.TRANSPARENT);
        }else{
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public static int Dp2Px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    public static int Px2Dp(Context context, float px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
    public static CustomDialog popLoading(Context ct,String str){
        CustomDialog mDialog = new CustomDialog(ct);
                mDialog.show();
                mDialog.setView(R.layout.layout_loading);
               TextView text= mDialog.findViewById(R.id.loading_text);
               text.setText(str);

                mDialog.setProperty(140);
                return  mDialog;}



    @TargetApi(Build.VERSION_CODES.M)
    static   public boolean ensureStoragePermissionGranted(Activity ct) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ( ct.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ct. requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1234);
                return false;
            }
        } else {
            // Always granted before Android 6.0.
            return true;
        }
    }

}
