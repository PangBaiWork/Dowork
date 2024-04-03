package com.pangbai.dowork.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.internal.ViewUtils;
import com.pangbai.dowork.Command.cmdExer;
import com.pangbai.dowork.R;
import com.pangbai.dowork.databinding.RootfsInstallBinding;
import com.pangbai.dowork.service.mainService;
import com.pangbai.dowork.tool.Init;
import com.pangbai.dowork.tool.uiThreadUtil;
import com.pangbai.dowork.tool.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RootfsInstallFragment extends BottomSheetDialogFragment implements View.OnClickListener  {

   RootfsInstallBinding binding;
    BottomSheetBehavior behavior;

    public    RootfsInstallFragment(){

    }
    @SuppressLint({"RestrictedApi"})
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());


        binding = RootfsInstallBinding.inflate(getLayoutInflater());

        bottomSheetDialog.setContentView(binding.getRoot());
        setLayout();
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheetInternal);


        View bottomSheetContent = bottomSheetInternal.findViewById(R.id.bottom_drawer_2);
        ViewUtils.doOnApplyWindowInsets(bottomSheetContent, (v, insets, initialPadding) -> {
            ViewCompat.setPaddingRelative(bottomSheetContent,
                    initialPadding.start,
                    initialPadding.top,
                    initialPadding.end,
                    initialPadding.bottom + insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });


        return bottomSheetDialog;


    }
    List<String> linuxs;
    List<String> version;
    void setLayout(){
        new Thread(() -> {
            cmdExer.destroy();
            cmdExer.execute(" rootfstool list -a arm64 -m bfsu ", false,true);
            linuxs =  util.getByTwoString( cmdExer.result,"4] ","\n");
            cmdExer.destroy();
            if (linuxs.isEmpty()) failed();
            uiThreadUtil.runOnUiThread(() -> {
                binding.linux.setSimpleItems((String [])linuxs.toArray(new String[linuxs.size()]));
            });
        }).start();
        //更新发行版本
        binding.linux.setOnItemClickListener((parent, view, position, id) -> {
            binding.linuxVersion.setText(null);
            new Thread(() -> {
                cmdExer.destroy();
                cmdExer.execute(" rootfstool search -a arm64 -m bfsu -d "+linuxs.get(position), false,true);
                version=util.getByTwoString( cmdExer.result," : ","\n");
                if (version.isEmpty()) failed();
                uiThreadUtil.runOnUiThread(() -> {
                    binding.linuxVersion.setSimpleItems((String [])version.toArray(new String[version.size()]));
                });
            }).start();


        });


        binding.rootfsInstall.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {

        cmdExer.destroy();
        cmdExer.execute("rootfstool url -a arm64 -d "+binding.linux.getText() +" -v "+binding.linuxVersion.getText()+" -m "+binding.linuxMirror.getText(), false,true);
        String cmd = Init.linuxDeployDirPath + "/cli.sh import "+ cmdExer.lastLine;
        Intent mIntent = new Intent(getActivity(), mainService.class);
        mIntent.putExtra("action", mainService.action_exeCmd);
        mIntent.putExtra("value", cmd);
       requireActivity(). startService(mIntent);
       this.dismiss();
    }

    void failed(){
        uiThreadUtil.runOnUiThread(() -> Toast.makeText(getContext(),"加载失败",Toast.LENGTH_LONG).show());
    }


}
