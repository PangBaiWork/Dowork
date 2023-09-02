package com.pangbai.dowork.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.pangbai.dowork.Command.CommandBuilder;
import com.pangbai.dowork.Command.cmdExer;
import com.pangbai.dowork.databinding.FloatWindowBinding;
import com.pangbai.dowork.preference.DoworkPreference;
import com.pangbai.dowork.tool.Init;
import com.pangbai.dowork.tool.containerInfor;
import com.pangbai.dowork.tool.uiThreadUtil;
import com.pangbai.dowork.tool.util;
import com.pangbai.linuxdeploy.PrefStore;
import com.pangbai.terminal.TerminalSession;
import com.pangbai.terminal.TerminalSessionClient;
import com.pangbai.view.SuperTerminalView;

public class mainService extends Service {
    public static mainService mService;
    WindowManager wm;
    FloatWindowBinding binding;
    WindowManager.LayoutParams cmdViewParam;
    SuperTerminalView cmdView;
    public static boolean isCmdRunning = false;
    public static final int action_exeCmd = 0;
    public static final int action_stopCmd = 1;
    public static final int action_success = 2;
    public static final int action_failed = 3;
    public static final int action_task = 4;
    private int initialX, initialY;
    private float initialTouchX, initialTouchY;
    TerminalSession taskSessin;
    serviceCallback mCallback;
    Thread mThread;
    WindowManager.LayoutParams mParam;


    View.OnTouchListener touchListener = new View.OnTouchListener() {
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
        binding = FloatWindowBinding.inflate(LayoutInflater.from(getApplicationContext()));
        cmdView = binding.floatCmdView;
        setCmdView();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent != null) {
            // 提取附加数据中的信息
            int action = intent.getIntExtra("action", 0);
            containerInfor ct = containerInfor.getContainerInfor(PrefStore.getProfileName(this));
            boolean isProot = containerInfor.isProot(ct);
            String shell = "su -c ";
            if (isProot)
                shell = "";
            switch (action) {
                case action_exeCmd:
                    if (!isCmdRunning && cmdView.mTerminalSession == null) {
                        //执行

                        isCmdRunning = true;
                        String cmdStr = intent.getStringExtra("value");
                        if (cmdStr.isEmpty())
                            return START_STICKY;
                        String args[] = CommandBuilder.getExeArgs(shell + cmdStr);
                        binding.floatCmdView.setVisibility(View.VISIBLE);
                        cmdView.setProcess(Init.busyboxPath, Init.linuxDeployDirPath, args, CommandBuilder.envpGet(), 0);
                        cmdView.runProcess();
                    }
                    break;
                case action_stopCmd:
                    //暂停
                    if (!isProot && mThread ==null) {
                        stopChroot();
                    }
                    isCmdRunning = false;
                    binding.floatCmdView.setVisibility(View.VISIBLE);
                    if (cmdView.mTerminalSession != null) {
                        //cmdView.mTerminalSession.mMainThreadHandler.removeCallbacks(null);
                        cmdView.mTerminalSession.finishIfRunning();
                        cmdView.mTerminalSessionClient.onSessionFinished(cmdView.mTerminalSession, 0);
                    }
                    // cmdExer.execute(Init.linuxDeployDirPath + "/cli.sh umount",Init.isRoot,false);
                    break;

                case action_success:

                    Toast.makeText(this, "Succeed", Toast.LENGTH_LONG).show();
                    //暂停
                    if (!isProot && mThread==null) {
                        stopChroot();
                    }

                    if (mCallback != null)
                        mCallback.callback(0);
                    //   cmdExer.execute(Init.linuxDeployDirPath + "/cli.sh umount",Init.isRoot,false);
                    break;
                case action_failed:
                    //暂停
                    if (!isProot && mThread==null) {
                        stopChroot();
                    }
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
                    if (mCallback!=null)
                         mCallback.callback(1);
                    //cmdExer.execute(Init.linuxDeployDirPath + "/cli.sh umount",Init.isRoot,false);

                    break;
              /*  case action_task:
                    binding.floatCmdView.setVisibility(View.GONE);
                    String cmdStr = intent.getStringExtra("value");
                    if (cmdStr.isEmpty())
                        return START_STICKY;
                    String args[] = CommandBuilder.getExeArgs(cmdStr);
                   TerminalSessionClient client=new TerminalSessionClient() {
                       @Override
                       public void onSessionFinished(TerminalSession finishedSession, int DelayTime) {
                           Toast.makeText(mainService.this,"任务执行完成",Toast.LENGTH_LONG).show();
                           finishedSession=null;
                       }
                       @Override
                       public void setTerminalShellPid(TerminalSession session, int pid) {
                       }};
                    Toast.makeText(mainService.this,args[2],Toast.LENGTH_LONG).show();
                    taskSessin = new TerminalSession(Init.busyboxPath, Init.linuxDeployDirPath, args, CommandBuilder.envpGet(), 0,client);
                    taskSessin.initializeEmulator(20,20);
                    break;*/


            }


        }


        return START_NOT_STICKY;
    }

    void stopChroot() {
        Log.e("chroot","umount");
        mThread = new Thread(() -> {
            cmdExer.execute(Init.linuxDeployDirPath + "/cli.sh umount", true, true);
            mThread = null;
        });
        mThread.start();
    }

    public void setCmdView() {
        cmdViewParam = getCmdParams();

        //new SuperTerminalView(getApplicationContext(), null);
        wm.addView(binding.getRoot(), cmdViewParam);
        cmdView.setBackgroundColor(0x40000000);
        binding.floatCmdView.setOnTouchListener(touchListener);
    }

    public void setCallback(serviceCallback callback) {
        this.mCallback = callback;
    }

    private WindowManager.LayoutParams getCmdParams() {
       mParam = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //test
            //  params2.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY | WindowManager.LayoutParams.TYPE_STATUS_BAR;
            mParam.type = mParam.TYPE_APPLICATION_OVERLAY;

        } else {
            mParam.type = mParam.TYPE_SYSTEM_OVERLAY;
        }

        mParam.format = PixelFormat.TRANSLUCENT;
        mParam.flags = mParam.FLAG_NOT_FOCUSABLE;

        mParam.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        Point screen = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(screen);
        }
        DoworkPreference pref=new DoworkPreference(this);
       int width= pref.getIntStoredAsString("floatcmd_width",300);
        int height= pref.getIntStoredAsString("floatcmd_height",100);
        ViewGroup.LayoutParams cmdParams=cmdView.getLayoutParams();
        cmdParams.height=util.Dp2Px(this,height);
        cmdParams.width=util.Dp2Px(this,width);
        cmdView.setLayoutParams(cmdParams);
        mParam.width = -2;
        mParam.height=-2;
        mParam.alpha = 1.0f;
        return mParam;
    }

    public class MyBinder extends Binder {
        public mainService getService() {
            return mainService.this;
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mService = null;
    }
}