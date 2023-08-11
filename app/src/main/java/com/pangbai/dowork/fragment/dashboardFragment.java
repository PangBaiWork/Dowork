package com.pangbai.dowork.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.pangbai.dowork.R;
import com.pangbai.dowork.databinding.FragmentDashboardBinding;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class dashboardFragment extends Fragment implements View.OnClickListener{
  FragmentDashboardBinding binding;
  containerInfor currentContainer;
    mainServiceConnection serviceConnection;

    private ExecutorService executorService;



  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentDashboardBinding.inflate(inflater);
    binding.ctTerminal.setOnClickListener(this);
    binding.ctStartStop.setOnClickListener(this);
    executorService = Executors.newFixedThreadPool(1);
    if (mainService.isCmdRunning){
        binding.ctStartStop.setBackgroundResource(R.drawable.stop);
    }
    if (mainService.mService!=null){
        Intent mIntent = new Intent(getContext(), mainService.class);
        serviceConnection = new mainServiceConnection(result -> {
            if (binding != null)
                binding.ctStartStop.setBackgroundResource(R.drawable.ct_start);
            getContext().unbindService(serviceConnection);
        });
        getContext().bindService(mIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    return binding.getRoot();
  }




    long memory=0;
   String size=null;
    public void doInBackground(String path){
      executorService.submit(new Runnable() {
          @Override
          public void run() {
              size=IO.countDirSize(path);
              Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
              Debug.getMemoryInfo(memoryInfo);
               memory = memoryInfo.getTotalPss() >> 10;
              //memory=Runtime.getRuntime().totalMemory()>>20;
              if (memory>999)
                  memory=-(memory>>10);
              if (binding.ctRam==null||binding.ctSize==null)
                  return;
              uiThreadUtil.runOnUiThread(() -> {
                  if (size!=null){
                      size=size.substring(0,size.indexOf("/")).trim();
                     binding.ctSize.setText("空间占用:"+size);}
                  String tmp="RAM占用:";
                  if (memory < 0)
                      binding.ctRam.setText(tmp+memory+"G");
                  else
                      binding.ctRam.setText(tmp+memory+"M");

              });

          }
      });
    }




    @Override
    public void onDestroyView() {
        if (!executorService.isShutdown())
            executorService.shutdownNow();
        if (serviceConnection != null)
            getContext().unbindService(serviceConnection);
        super.onDestroyView();


        binding=null;
    }

    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onClick(View view) {
      if (view==binding.ctTerminal) {
          util.startActivity(getActivity(), TermActivity.class, false);


      }else if (view==binding.ctStartStop){
          Intent mIntent = new Intent(getContext(), mainService.class);
          if (mainService.isCmdRunning){
              mIntent.putExtra("action", mainService.action_stopCmd);
              getContext().startService(mIntent);
              if (serviceConnection != null)
                 getContext().unbindService(serviceConnection);
              serviceConnection = null;
              binding.ctStartStop.setBackgroundResource(R.drawable.ct_start);
          } else{
          Toast.makeText(getContext(),"s",Toast.LENGTH_LONG).show();
          dialogUtils.showInputDialog(getContext(),
                  "执行任务",
                  (dialogUtils.DialogInputListener) userInput -> {
                    if (userInput==null)
                        return;
                      if (userInput.contains(">"))
                         Toast.makeText(getContext(),"注意该任务的输出重定向将可能会定向到容器外部，请使用绝对路径",Toast.LENGTH_LONG);
                      mIntent.putExtra("action", mainService.action_exeCmd);
                      mIntent.putExtra("value", Init.linuxDeployDirPath + "/cli.sh exec "+userInput);
                      getContext().startService(mIntent);
                      binding.ctStartStop.setBackgroundResource(R.drawable.stop);
                      serviceConnection = new mainServiceConnection(result -> {
                          if (binding != null)
                              binding.ctStartStop.setBackgroundResource(R.drawable.ct_start);
                      });
                     getContext().bindService(mIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                  });}
      }
    }

    @Override
    public void onResume() {
        super.onResume();
       String name= PrefStore.getProfileName(getContext());
       currentContainer=containerInfor.getContainerInfor(name);
       if (currentContainer==null){
           Toast.makeText(getContext(),"no container found",Toast.LENGTH_LONG).show();
           getActivity().finish();
       }
       binding.ctInfor.setText(currentContainer.name+"\n"+currentContainer.version);
       binding.ctIcon.setBackgroundResource(currentContainer.iconId);
       doInBackground(currentContainer.path);

    }
}
