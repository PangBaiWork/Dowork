package com.pangbai.dowork;

import android.os.Bundle;
import android.util.Log;

import com.pangbai.dowork.Command.CommandBuilder;
import com.pangbai.dowork.databinding.ActivityTermBinding;
import com.pangbai.dowork.preference.DoworkPreference;
import com.pangbai.dowork.tool.Init;
import com.pangbai.dowork.tool.containerInfor;
import com.pangbai.dowork.tool.util;
import com.pangbai.linuxdeploy.PrefStore;
import com.pangbai.view.ExtraKeysView;

import android.widget.RelativeLayout;
import android.view.ViewGroup;

import android.view.View.OnClickListener;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pangbai.terminal.TerminalSession;
import com.pangbai.view.dialogUtils;

public class TermActivity extends AppCompatActivity implements OnClickListener {


    public DoworkPreference mTermSetting;
    public ActivityTermBinding binding;
    RelativeLayout termBgView;
    CommandBuilder mBuilder;
    boolean isChroot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*设置窗口布局
         **/
        getWindow().setBackgroundDrawableResource(R.drawable.bg_term);
        util.fullScreen(getWindow(), true);
        mTermSetting = new DoworkPreference(this);
        termBgView = binding.termbgview;

        if (containerInfor.ct == null) {
            new Init(this);
            String name = PrefStore.getProfileName(this);
            containerInfor.ct = containerInfor.getContainerInfor(name);
        }
        if (containerInfor.ct == null || containerInfor.ct.version.equals("Unknown"))
            mBuilder = new CommandBuilder(this, CommandBuilder.type_sh, termBgView);
        else {
            isChroot = !containerInfor.isProot(containerInfor.ct);
            if (isChroot)
                mBuilder = new CommandBuilder(this, CommandBuilder.type_chroot, termBgView);
            else
                mBuilder = new CommandBuilder(this, CommandBuilder.type_proot, termBgView);
        }

        binding.ExtraKey.addView(mBuilder.keysView);


        //mcommand.commandview.setBackground(
        //setTheme(android.R.style.Theme_Black_NoTitleBar);
        /*
         *键盘生成
         */


        mTermSetting.readkeys();
        ViewGroup.LayoutParams lp = mBuilder.keysView.getLayoutParams();
        lp.height = (int) ((37.5 * mTermSetting.mExtraKeys.length) * getResources().getDisplayMetrics().density + 0.5);
        lp.width = -1;
        Log.e("term", "" + lp.height);
        mBuilder.keysView.setLayoutParams(lp);
        mBuilder.keysView.reload(mTermSetting.mExtraKeys, ExtraKeysView.defaultCharDisplay);

    }


    @Override
    protected void onDestroy() {
     /* if (isChroot){
            CommandBuilder.stopChroot();
        }*/
        TerminalSession session = mBuilder.cmdView.mTerminalSession;
        if (session != null && session.isRunning())
            session.finishIfRunning();
        super.onDestroy();
    }

    public void onBackPressed() {

        if (isChroot)
            dialogUtils.showConfirmationDialog(this,
                    "退出终端",
                    "是否同时卸载容器",
                    "退出并卸载",
                    "后台运行进程",
                    () -> {
                        CommandBuilder.stopChroot();
                        finish();

                    },
                    this::finish);
        else
            finish();
    }

    @Override
    public void onClick(View view) {

    }
}
