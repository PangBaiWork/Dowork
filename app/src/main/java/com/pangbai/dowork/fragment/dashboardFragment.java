package com.pangbai.dowork.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pangbai.dowork.R;
import com.pangbai.dowork.databinding.FragmentDashboardBinding;
import com.pangbai.dowork.preference.DoworkPreference;
import com.pangbai.dowork.service.display;
import com.pangbai.dowork.service.mainService;
import com.pangbai.dowork.service.mainServiceConnection;
import com.pangbai.dowork.tool.IO;
import com.pangbai.dowork.tool.Init;
import com.pangbai.dowork.tool.containerInfor;
import com.pangbai.dowork.tool.uiThreadUtil;
import com.pangbai.dowork.tool.util;
import com.pangbai.dowork.*;
import com.pangbai.linuxdeploy.PrefStore;
import com.pangbai.view.dialogUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import leakcanary.LeakCanary;

public class dashboardFragment extends Fragment implements View.OnClickListener {
    FragmentDashboardBinding binding;
    //containerInfor currentContainer;
    mainServiceConnection serviceConnection;
    static boolean firstStart = false;
    DoworkPreference pref;


    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        binding = FragmentDashboardBinding.inflate(getLayoutInflater());

    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        //  binding = FragmentDashboardBinding.inflate(getLayoutInflater());
        binding.ctTerminal.setOnClickListener(this);
        binding.ctStartStop.setOnClickListener(this);

        executorService = Executors.newSingleThreadExecutor();
        if (mainService.isCmdRunning) {
            binding.ctStartStop.setBackgroundResource(R.drawable.stop);
        }
        if (mainService.mService != null) {
            Intent mIntent = new Intent(getContext(), mainService.class);
            serviceConnection = new mainServiceConnection(result -> {
                if (binding != null)
                    binding.ctStartStop.setBackgroundResource(R.drawable.ct_start);
                if (serviceConnection != null)
                    getActivity().unbindService(serviceConnection);
                serviceConnection = null;
            });
            getActivity().bindService(mIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }


        pref = new DoworkPreference(getContext());
        return binding.getRoot();
    }


    long memory = 0;
    String size = null;

    public void doInBackground(String path) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                String Permission = checkPermission();

                if (pref.getBoolStoredAsString("container_size", true))
                    size = IO.countDirSize(path);
                else
                    size = null;

                Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
                Debug.getMemoryInfo(memoryInfo);
                memory = memoryInfo.getTotalPss() >> 10;
                //memory=Runtime.getRuntime().totalMemory()>>20;
                if (memory > 999)
                    memory = -(memory >> 10);


                uiThreadUtil.runOnUiThread(() -> {
                    binding.permissonStatus.setText(Permission);
                    if (binding == null)
                        return;
                    int last;
                    if (size != null && (last = size.indexOf("/")) != -1)
                        size = size.substring(0, last).trim();
                    else
                        size = "...";
                    binding.ctSize.setText("空间占用:" + size);

                    String tmp = "RAM占用:";
                    if (memory < 0)
                        binding.ctRam.setText(tmp + memory + "G");
                    else
                        binding.ctRam.setText(tmp + memory + "M");

                });


                if (!firstStart) {
                    display.startXservice(getContext());
                    if (displayFragment.isAudioOn(getContext())) {
                        Intent intent = new Intent(getContext(), display.class);
                        intent.putExtra("action", display.action_startAudio);
                        getContext().startService(intent);
                    }
                    firstStart = true;
                }
                try {
                    //wait For service Start;
                    if (display.mService == null)
                        Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                String status = checkXStatus();
                uiThreadUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status != null)
                            binding.displayStatus.setText(status);
                    }
                });


            }
        });
    }

    public String checkXStatus() {
        boolean x11, xserver, pulseaudio;
        x11 = display.Xdraw != null && display.Xdraw.isAlive();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            xserver = display.process_Xvfb == null ? false : display.process_Xvfb.isAlive();
            pulseaudio = display.process_PulseAudio == null ? false : display.process_PulseAudio.isAlive();
        } else {
            xserver = display.process_Xvfb == null;
            pulseaudio = display.process_PulseAudio == null;
        }
        String status, run = " Running", destory = " Destroy";
        status = "X11:" + (x11 ? run : destory) + "\nXserver:" + (xserver ? run : destory)
                + "\nPulseaudio:" + (pulseaudio ? run : destory);
        return status;
    }

    public String checkPermission() {
        boolean root = util.isRooted();
        Init.isRoot = root;
        boolean window = util.windowPermissionCheck(getContext());
        String allow = "Allowed", denied = "Denied";
        return "Root:" + (root ? allow : denied) + "\nWindow:" + (window ? allow : denied);
    }




    @Override
    public void onDestroyView() {
        if (!executorService.isShutdown())
            executorService.shutdownNow();
        if (serviceConnection != null)
            getActivity().unbindService(serviceConnection);
        serviceConnection = null;
        binding.ctTerminal.setOnClickListener(null);
        binding.ctStartStop.setOnClickListener(null);

        super.onDestroyView();
        // binding=null;
    }


    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onClick(View view) {
        if (view == binding.ctTerminal) {
            util.startActivity(getActivity(), TermActivity.class, false);


        } else if (view == binding.ctStartStop) {
            Intent mIntent = new Intent(getContext(), mainService.class);
            if (mainService.isCmdRunning) {
                mIntent.putExtra("action", mainService.action_stopCmd);
                getActivity().startService(mIntent);
                if (serviceConnection != null)
                    getActivity().unbindService(serviceConnection);
                serviceConnection = null;
                binding.ctStartStop.setBackgroundResource(R.drawable.ct_start);
            } else {
                Toast.makeText(getContext(), "s", Toast.LENGTH_LONG).show();
                dialogUtils.showInputDialog(getContext(),
                        "执行任务",
                        (dialogUtils.DialogInputListener) userInput -> {
                            if (userInput == null)
                                return;
                            if (userInput.contains(">"))
                                Toast.makeText(getContext(), "注意该任务的输出重定向将可能会定向到容器外部，请使用绝对路径", Toast.LENGTH_LONG);
                            mIntent.putExtra("action", mainService.action_exeCmd);
                            mIntent.putExtra("value", Init.linuxDeployDirPath + "/cli.sh exec " + userInput);
                            getContext().startService(mIntent);
                            binding.ctStartStop.setBackgroundResource(R.drawable.stop);
                            serviceConnection = new mainServiceConnection(result -> {
                                if (binding != null)
                                    binding.ctStartStop.setBackgroundResource(R.drawable.ct_start);
                            });
                            getActivity().bindService(mIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                        });
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        String name = PrefStore.getProfileName(getContext());
        containerInfor.ct = containerInfor.getContainerInfor(name);
        if (containerInfor.ct == null) {
            Toast.makeText(getContext(), "no container found", Toast.LENGTH_LONG).show();
            return;

        }

        binding.ctInfor.setText("NAME: " + containerInfor.ct.name + "\n" + containerInfor.ct.version);
        binding.ctIcon.setBackgroundResource(containerInfor.ct.iconId);

        doInBackground(containerInfor.ct.path);
      /*
        }*/


    }
}
