package com.pangbai.dowork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pangbai.dowork.databinding.ActivityDisplayBinding;
import com.pangbai.dowork.service.display;
import com.pangbai.dowork.service.displayConnection;
import com.pangbai.dowork.service.mainService;
import com.pangbai.dowork.service.mainServiceConnection;
import com.pangbai.dowork.tool.Init;
import com.pangbai.dowork.tool.jni;
import com.pangbai.view.dialogUtils;

public class DisplayActivity extends Activity implements View.OnClickListener{
    ActivityDisplayBinding binding;
    displayConnection mdisplayConnection;
    boolean isTouchInput=true;
    View.OnTouchListener cursor=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent e) {
            jni.cursorControl(0,(int) e.getX(),(int) e.getY());
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayBinding.inflate(LayoutInflater.from(this));
//        binding.surface.getHolder().addCallback(this);


        if (Init.binDirPath == null) {
            new Init(this);
        }
        ViewGroup.LayoutParams params = binding.surface.getLayoutParams();
        int screen[] = display.getScreenFromPref(this);
        params.width = screen[0];
        params.height = screen[1];
        binding.surface.setLayoutParams(params);
        display.startXservice(this);
        Intent mIntent = new Intent(this, display.class);
        mdisplayConnection = new displayConnection(binding.surface.getHolder().getSurface(), result -> {
            if (binding != null)
                if (result == 0) {
                    Log.e("fullscreeen", "drawing");
                    // Toast.makeText(this,"drawing",Toast.LENGTH_LONG).show();
                } else {
                    Log.e("fullscreeen", "stopdraw");
                }
        });
        binding.surface.setOnTouchListener(display.screenTouch);

        bindService(mIntent, mdisplayConnection, Context.BIND_AUTO_CREATE);
        binding.inputStr.setOnClickListener(this);
        binding.inputMethod.setOnClickListener(this);
        binding.inputEnter.setOnClickListener(this);
        binding.inputDelete.setOnClickListener(this);

        setContentView(binding.getRoot());
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        Log.e("displayActivity ","destroy");
        try {
            unbindService(mdisplayConnection);
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view==binding.inputStr){
            dialogUtils.showInputDialog(this,
                    "输入字符串",
                    (dialogUtils.DialogInputListener) userInput -> {
                Log.e("input",userInput.toString());
                        jni.inputString(userInput.toString());
                    });
        }else if (view==binding.inputDelete){
            jni.inputKeyByString("BackSpace");
        }else if (view==binding.inputEnter){
            jni.inputKeyByString("Return");
        }else if (view==binding.inputMethod){
            if (isTouchInput){
                isTouchInput=false;
                jni.cursorControl(1,0,0);
                binding.inputMethod.setBackgroundResource(R.drawable.input_cursor);
                binding.surface.setOnTouchListener(cursor);
            }else {
                isTouchInput=true;
                jni.cursorControl(-1,0,0);
                binding.inputMethod.setBackgroundResource(R.drawable.input_touch);
                binding.surface.setOnTouchListener(display.screenTouch);
            }
        }

    }
}