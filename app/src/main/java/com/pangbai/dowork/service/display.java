package com.pangbai.dowork.service;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;


import com.pangbai.dowork.Command.cmdExer;
import com.pangbai.dowork.DisplayActivity;
import com.pangbai.dowork.R;

import com.pangbai.dowork.databinding.FloatDisplayBinding;
import com.pangbai.dowork.fragment.displayFragment;
import com.pangbai.dowork.tool.Init;
import com.pangbai.dowork.tool.jni;
import com.pangbai.dowork.tool.uiThreadUtil;
import com.pangbai.dowork.tool.util;
import com.pangbai.linuxdeploy.PrefStore;

import java.io.IOException;
import java.util.Timer;
import java.util.zip.Inflater;

public class display extends Service implements  OnClickListener {


    public static final int action_startX = 1;
    public static final int action_startXvfb = 2;
    public static final int action_stopXvfb = 3;
    public static final int action_startAudio = 4;
    public static final int action_stopAudio = 5;
    public static final int action_fullscreen = 6;
    public static final int value_internal = 1;
    public static final int value_external = 2;
    public  Surface surface;

   public  static   View.OnTouchListener screenTouch=new OnTouchListener() {
       private  boolean scroll=false;
       private float[] initialY = new float[2];
       private boolean scrollAllowed = true;
       GestureDetector.SimpleOnGestureListener mGestureListener =new GestureDetector.SimpleOnGestureListener(){

           @Override
           public boolean onSingleTapConfirmed(MotionEvent e) {
               // 在这里处理单击事件
               Log.e("Gesture","click");
               jni.xclick((int) e.getX(), (int) e.getY(),true);
               return true;
           }
           @Override
           public boolean onDoubleTap(MotionEvent e) {
               Log.e("gesture","2f");
                  // Log.e("gesture","2f");
               jni.xclick((int) e.getX(), (int) e.getY(),false);
                   return true;

           }

           @Override
           public void onLongPress(MotionEvent e) {
               Log.e("gestrue","longPress");
            //   jni.inputString("User传递字符串 ");
           }

           @Override
           public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
               //Log.e("scroll","up");
               if (!scrollAllowed)
                   return true;
                uiThreadUtil.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollAllowed=true;
                    }
                },100);
                scrollAllowed=false;
               if (distanceY<0){
                       Log.e("scroll","up");
                       jni.mouseScroll(true);
               }else {
                       Log.e("scroll","down");
                       jni.mouseScroll(false);
               }


               return true;
           }
       };
    GestureDetector detector;
       @Override
        public  boolean onTouch(View p1, MotionEvent p2) {
            if (!isStarting)
                return true;

            if (detector==null)
                detector=new GestureDetector(p1.getContext().getApplicationContext(), mGestureListener);
                // Toast.makeText(getApplication(), "b", Toast.LENGTH_SHORT).show();

           detector.onTouchEvent(p2);
           return true;


        }
    };


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onClick(View view) {
        if (view == binding.displayFullscreen) {
            Intent mintent=new Intent(this, DisplayActivity.class);
            mintent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(mintent);
        } else if (view == binding.displayClosescreen) {
            jni.stopDraw();
            hideDisplay();
            Toast.makeText(this, "close", Toast.LENGTH_LONG).show();
        } else if (view == binding.displayPin) {
            if (!canclick) {
                binding.displayPin.setRotation(-45);
                binding.surface.setOnTouchListener(screenTouch);
                canclick = true;
            } else {
                binding.surface.setOnTouchListener(touch);
                binding.displayPin.setRotation(0);
                canclick = false;
            }
        }

    }




    public static display mService = null;
    WindowManager wm;
    boolean canclick = false;
    WindowManager.LayoutParams param;

 public    static boolean isStarting = false;
    //  static SurfaceView screen;
    OnTouchListener touch;
    public static Thread Xdraw;
    public static Process process_Xvfb = null, process_PulseAudio = null;
    public FloatDisplayBinding binding;
    int screen[];


    public static boolean isFulllScreen=false;
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }


    @Override
    public boolean onUnbind(Intent intent) {
        /**
         * caused error
         */
        isFulllScreen=false;
        Log.e("displaybind","unbind");
        surface=binding.surface.getHolder().getSurface();
       // jni.stopDraw();
      /*  jni.stopXvfb();*/
       // jni.stopDraw();

        return  true;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mService = this;
        wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        //contentView = LayoutInflater.from(getApplication()).inflate(R.layout.float_display, null);
        binding = FloatDisplayBinding.inflate(LayoutInflater.from(this));

        if (param == null) param = new WindowManager.LayoutParams();
        /** 设置参数 */
        param.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        // 设置窗口的行为准则
        param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        param.format = PixelFormat.TRANSLUCENT;

        // 设置透明度
        param.alpha = 1.0f;
        // 设置内部视图对齐方式，这边位置为左边靠上
        param.gravity = Gravity.LEFT | Gravity.TOP;
        // 窗口的左上角坐标
        // param.x = 0;
        // param.y = 0;
        // 设置窗口的宽高,这里为自动
        param.width = 0;
        param.height = 0;
        wm.addView(binding.getRoot(), param);

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
                        wm.updateViewLayout(binding.getRoot(), param);
                }
                return true;
            }
        };
        binding.surface.setOnTouchListener(touch);
        binding.displayFullscreen.setOnClickListener(this);
        binding.displayClosescreen.setOnClickListener(this);
        // screen.getHolder().getSurface();
        binding.displayPin.setOnClickListener(this);
        surface = binding.surface.getHolder().getSurface();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            // 提取附加数据中的信息
            int action = intent.getIntExtra("action", 0);
            switch (action) {
                case action_startX:
                    screen = getScreenFromPref(this);
                    Notification notification = createNotification();
                    startForeground(1, notification);
                    int type = intent.getIntExtra("value", 0);
                    listenDisplay(type);
                    break;
                case action_startXvfb:
                    startXvfb();
                    break;
                case action_stopXvfb:
                    stopXvfb();
                    break;
                case action_startAudio:
                    startAudio();
                    break;
                case action_stopAudio:
                    stopAudio();
                    break;


            }


        }
        // screen.setOnTouchListener(this);


        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        wm.removeView(binding.getRoot());
        mService = null;
        super.onDestroy();
    }

    public void startXvfb() {
        if (process_Xvfb != null)
            return;
        Log.e("xvfb", "start");
        cmdExer.execute(Init.binDirPath + "/Xvfb :0 -ac -wr -listen tcp -screen 0 " +
                screen[0] + "x" + screen[1] + "x" + screen[2], false, false);
        process_Xvfb = cmdExer.process;
    }

    public void stopXvfb() {
        jni.stopDraw();
        // hideDisplay();
        if (process_Xvfb != null) {
            Log.e("xvfb", "destroy");
            process_Xvfb.destroy();
            process_Xvfb = null;
        }
    }

    public void startAudio() {
        if (process_PulseAudio != null)
            return;
        Log.e("pulseaudio", "start");
        cmdExer.execute(Init.binDirPath + "/pulseaudio", false, false);
        process_PulseAudio = cmdExer.process;
    }

    public void stopAudio() {
        if (process_PulseAudio != null) {
            Log.e("pulseaudio", "destroy");
            process_PulseAudio.destroy();
            process_PulseAudio = null;
        }
    }

    String listen;
    boolean internet;

    public void listenDisplay(int type) {

        if (type == value_internal) {
            internet = false;
            startXvfb();
        }
        if (type == value_external) {
            internet = true;
            stopXvfb();
        }
        if (Xdraw != null && Xdraw.isAlive())
            return;
        listen = "127.0.0.1";
        Xdraw = new Thread() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                    }
                    int result = jni.init(internet, listen, 0);
                    //don't start ,-1 for xclient ,-2 for xvfb and xclient
                    Log.e("X11", "return " + result);
                    if (!isStarting && result != -1 && result != -2) {

                        Log.e("X11", "start");

                        isStarting = true;

                        if (!isFulllScreen)
                            updateFloatDisplay(screen[0], screen[1]);
                        jni.startx(surface);
                       /* if (mFullscreenCallback != null)
                            mFullscreenCallback.callback(1);
                        else*/
                            hideDisplay();

                        isStarting = false;
                    }
                }
            }
        };
        Xdraw.start();


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


    public static void startXservice(Context ct) {
        Intent intent = new Intent(ct, display.class);
        intent.putExtra("action", display.action_startX);
        int value = displayFragment.isXserverInternal(ct) ? display.value_internal : display.value_external;
        intent.putExtra("value", value);
        ct.startService(intent);

    }

    public static int[] getScreenFromPref(Context c) {
        int width, height, depth;
        try {
            width = Integer.parseInt(PrefStore.SETTINGS.get(c, displayFragment.str_width));
            height = Integer.parseInt(PrefStore.SETTINGS.get(c, displayFragment.str_height));
            depth = Integer.parseInt(PrefStore.SETTINGS.get(c, displayFragment.str_depth));
        } catch (Exception e) {
            return new int[]{800, 600, 24};
        }
        return new int[]{width, height, depth};


    }

    public void updateFloatDisplay(int width, int height) {

        param.height = height + util.Dp2Px(this, 15);
        param.width = width;
        uiThreadUtil.runOnUiThread(() -> {
            wm.updateViewLayout(binding.getRoot(), param);
            binding.getRoot().setVisibility(View.VISIBLE);

        });
    }

    public void hideDisplay() {
        uiThreadUtil.runOnUiThread(() -> binding.getRoot().setVisibility(View.GONE));
    }

    private Notification createNotification() {
        String CHANNEL_ID = "ForegroundServiceChannel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Dowork Foreground Service for X11")
                .setContentText("Running...")
                .setSmallIcon(R.drawable.ct_icon_archlinux);


        return builder.build();
    }

    public class MyBinder extends Binder {
        public display getService() {
            return display.this;
        }
    }


}
