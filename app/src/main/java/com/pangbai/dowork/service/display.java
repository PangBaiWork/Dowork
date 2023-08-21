package com.pangbai.dowork.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.cardview.widget.CardView;


import com.pangbai.dowork.R;

import com.pangbai.dowork.tool.jni;

import java.util.Timer;

public class display extends Service implements OnTouchListener, OnClickListener {


    public static final int action_startX = 1;
    public static final int action_startXvfb = 2;
    public static final int value_internal = 1;
    public static final int value_external = 2;

    @Override
    public void onClick(View p1) {
        CardView view = contentView.findViewById(R.id.displaybar);
        if (!canclick) {
            contentView.findViewById(R.id.displaymove).setOnTouchListener(null);
            view.setBackgroundColor(Color.parseColor("#FFC0CB"));
            screen.setOnTouchListener(this);
            canclick = true;
        } else {
            contentView.findViewById(R.id.displaymove).setOnTouchListener(touch);
            view.setBackgroundColor(Color.WHITE);
            screen.setOnTouchListener(null);
            canclick = false;
        }
    }

    @Override
    public boolean onTouch(View p1, MotionEvent p2) {
        // Toast.makeText(getApplication(), "b", Toast.LENGTH_SHORT).show();

        if (isStarting) {
            // Toast.makeText(getApplication(), "b", Toast.LENGTH_SHORT).show();
            jni.movePoint((int) p2.getX(), (int) p2.getY());
        }
        return false;
    }

    public static display mService = null;
    WindowManager wm;
    boolean canclick = false;
    static View contentView;
    WindowManager.LayoutParams param;
    Timer listenDisplay;
    boolean isStarting = false;
    static SurfaceView screen;
    OnTouchListener touch;
    Thread Xvfb;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mService = this;
        wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        contentView = LayoutInflater.from(getApplication()).inflate(R.layout.float_display, null);
        if (param == null) param = new WindowManager.LayoutParams();
        /** 设置参数 */
        param.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        // 设置窗口的行为准则
        param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        param.format = PixelFormat.TRANSLUCENT;

        // 设置透明度
        param.alpha = 1.0f;
        // 设置内部视图对齐方式，这边位置为左边靠上
        param.gravity = Gravity.LEFT | Gravity.TOP;
        // 窗口的左上角坐标
        // param.x = 0;
        // param.y = 0;
        // 设置窗口的宽高,这里为自动
        param.width = -2;
        param.height = -2;
        wm.addView(contentView, param);

        touch = new OnTouchListener() {
            int startx, starty, movex, movey, endx, endy;

            @Override
            public boolean onTouch(View p1, MotionEvent p2) {
                switch (p2.getAction()) {
                    case MotionEvent.ACTION_OUTSIDE:
                        break;
                    case MotionEvent.ACTION_DOWN:
                        startx = (int) p2.getRawX();
                        starty = (int) p2.getRawY();
                        movex = 0;
                        movey = 0;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        endx = (int) p2.getRawX();
                        endy = (int) p2.getRawY();
                        movex = endx - startx;
                        movey = endy - starty;
                        startx = endx;
                        starty = endy;
                        param.y += movey;
                        param.x += movex;
                        wm.updateViewLayout(contentView, param);
                }
                return true;
            }
        };
        contentView.findViewById(R.id.displaymove).setOnTouchListener(touch);
        screen = contentView.findViewById(R.id.surface);
        // screen.getHolder().getSurface();
        contentView.findViewById(R.id.displaybar).setOnClickListener(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            // 提取附加数据中的信息
            int action = intent.getIntExtra("action", 0);
            switch (action) {
                case action_startX:
                    int type = intent.getIntExtra("value", 0);
                    listenDisplay(type);
                    break;
                case action_startXvfb:
                    startXvfb();

            }


        }
        // screen.setOnTouchListener(this);


        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        wm.removeView(contentView);
        mService = null;
        super.onDestroy();
    }
    public  void startXvfb(){
     new Thread() {
            @Override
            public void run() {
                final int a = jni.initxvfb("800x600x24");
            }
        }.start();
    }
    String listen;
    boolean internet;
    public void listenDisplay(int type) {

        if (type==value_internal){
            internet=false;
            startXvfb();}
        if (type==value_external)
            internet=true;
        listen = "127.0.0.1";
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                    }
                    int perline = jni.init(internet,listen , 0);
                    Log.e("X11",  "return "+perline);
                    if (!isStarting && perline != -1&&perline != -2) {
                        Log.e("X11",  "sucess");
                        isStarting = true;
                        updateDisplay(800, 600);
                        String c = jni.startx(screen.getHolder().getSurface());
                        updateDisplay(0, 0);
                    }
                }
            }
        }.start();


    /*
    new Thread() {
      @Override
      public void run() {
        waitSocket mserver = new waitSocket();
      try{
        mserver.shellServer();}
        catch (Exception e) {
          }
        super.run();
      }
    }.start();*/
    }

    public static void updateDisplay(int width, int height) {
        if (screen == null)
            return;
        if (width == 0) {
            Log.e("dowork", "width=0");
        }
        final ViewGroup.LayoutParams displayLayout = screen.getLayoutParams();
        displayLayout.width = width;
        displayLayout.height = height;

        screen.post(
                new Runnable() {
                    @Override
                    public void run() {
                        screen.setLayoutParams(displayLayout);
                    }
                });
    }
}
