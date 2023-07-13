package com.pangbai.dowork;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.graphics.Color;

import com.pangbai.dowork.Command.CommandBuilder;
import com.pangbai.dowork.databinding.ActivityMainBinding;
import com.pangbai.dowork.databinding.ActivityTermBinding;
import com.pangbai.dowork.tool.util;
import com.pangbai.view.ExtraKeysView;
import android.widget.RelativeLayout;
import android.view.ViewGroup;
import com.pangbai.dowork.tool.Preference;
import android.view.View.OnClickListener;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pangbai.terminal.TerminalSession;

public class TermActivity extends AppCompatActivity implements OnClickListener {

    @Override
    public void onClick(View p1) {
    }
    public ActivityTermBinding binding;
    RelativeLayout termBgView;
    CommandBuilder mBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityTermBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*设置窗口布局
        **/
        getWindow().setBackgroundDrawableResource(R.drawable.term_bg);
        util.fullScreen(getWindow(),true);
        termBgView=binding.termbgview;
        mBuilder=new CommandBuilder(this,termBgView,30);

        binding.ExtraKey.addView(mBuilder.keysView);

        //mcommand.commandview.setBackground(
        //setTheme(android.R.style.Theme_Black_NoTitleBar);
        /*
        *键盘生成
         */
        Preference mSetting=new Preference(this);

        mSetting.readkeys();
        ViewGroup.LayoutParams   lp= mBuilder.keysView.getLayoutParams();
        lp.height= (int)((37.5*mSetting.mExtraKeys.length)*getResources().getDisplayMetrics().density + 0.5);
        lp.width=-1;
        Log.e("term",""+lp.height);
        mBuilder.keysView.setLayoutParams(lp);
        mBuilder.keysView.reload(mSetting.mExtraKeys, ExtraKeysView.defaultCharDisplay);

    }


    @Override
    protected void onDestroy() {
        TerminalSession session=	 mBuilder.cmdView.mTerminalSession;
        if(session!=null&&session.isRunning())
            session.finishIfRunning();
        super.onDestroy();
    }


}
