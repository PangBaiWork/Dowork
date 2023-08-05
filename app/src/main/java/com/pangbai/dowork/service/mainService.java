package com.pangbai.dowork.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.pangbai.dowork.Command.CommandBuilder;
import com.pangbai.dowork.R;
import com.pangbai.dowork.databinding.FloatWindowBinding;
import com.pangbai.dowork.tool.Init;
import com.pangbai.view.SuperTerminalView;

public class mainService extends Service{
    public static mainService mService;
    WindowManager wm;
    FloatWindowBinding binding;
    WindowManager.LayoutParams cmdViewParam;
    SuperTerminalView cmdView;
    public static boolean isCmdRunning=false;
    public static final int action_exeCmd = 1;
    public static final int action_success = 2;
    public static final int action_failed = 3;
    private int initialX, initialY;
    private float initialTouchX, initialTouchY;

    View.OnTouchListener touchListener=new View.OnTouchListener() {


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = cmdViewParam.x;
                    initialY = cmdViewParam.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int offsetX = (int) (event.getRawX() - initialTouchX);
                    int offsetY = (int) (event.getRawY() - initialTouchY);
                    cmdViewParam.x = initialX + offsetX;
                    cmdViewParam.y = initialY + offsetY;
                    wm.updateViewLayout(binding.getRoot(), cmdViewParam);
                    return true;
                case MotionEvent.ACTION_UP:
                    return true;
                default:
                    return false;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mService = this;
        wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        binding=FloatWindowBinding.inflate(LayoutInflater.from(getApplicationContext()));
        cmdView=binding.floatCmdView;
        setCmdView();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent != null) {
            // 提取附加数据中的信息
            int action = intent.getIntExtra("action", 0);
            switch (action) {
                case action_exeCmd:
                    if (!isCmdRunning&&cmdView.mTerminalSession==null) {
                        isCmdRunning=true;
                        String cmdStr = intent.getStringExtra("value");
                        if (cmdStr.isEmpty())
                            return START_STICKY;
                        String args[] = CommandBuilder.getExeArgs(cmdStr);
                        binding.getRoot().setVisibility(View.VISIBLE);
                        cmdView.setProcess(Init.busyboxPath, Init.linuxDeployDirPath, args, CommandBuilder.envpGet(), 0);
                        cmdView.runProcess();
                    }else {
                        isCmdRunning=false;
                        binding.getRoot().setVisibility(View.VISIBLE);
                        if(cmdView.mTerminalSession!=null){
                            //cmdView.mTerminalSession.mMainThreadHandler.removeCallbacks(null);
                        cmdView.mTerminalSession.finishIfRunning();
                        cmdView.mTerminalSessionClient.onSessionFinished(cmdView.mTerminalSession,0);}


                    }
                    break;

                case action_success:

                    break;


            }


        }


        return super.onStartCommand(intent, flags, startId);
    }


    public void setCmdView() {
        cmdViewParam = getCmdParams();

        //new SuperTerminalView(getApplicationContext(), null);
        wm.addView(binding.getRoot(), cmdViewParam);
        cmdView.setBackgroundColor(0x40000000);
        binding.floatCmdView.setOnTouchListener(touchListener);
    }

    private WindowManager.LayoutParams getCmdParams() {
        WindowManager.LayoutParams mParam = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //test
            //  params2.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY | WindowManager.LayoutParams.TYPE_STATUS_BAR;
            mParam.type = mParam.TYPE_APPLICATION_OVERLAY;

        } else {
            mParam.type = mParam.TYPE_SYSTEM_OVERLAY;
        }

        mParam.format = PixelFormat.TRANSLUCENT;
        mParam.flags = mParam.FLAG_NOT_FOCUSABLE ;

        mParam.gravity = Gravity.CENTER_VERTICAL|Gravity.LEFT;
        Point screen = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(screen);
        }
        mParam.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParam.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParam.alpha = 1.0f;
        return mParam;
    }



}