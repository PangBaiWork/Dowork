package com.pangbai.dowork.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pangbai.dowork.TermActivity;
import com.pangbai.dowork.databinding.FragmentDashboardBinding;
import com.pangbai.dowork.databinding.FragmentDisplayBinding;
import com.pangbai.dowork.service.display;
import com.pangbai.dowork.tool.jni;
import com.pangbai.dowork.tool.util;


public class diaplayFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    FragmentDisplayBinding binding;
    public static boolean isInternal = true;


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentDisplayBinding.inflate(inflater);
        binding.diaplayExternal.setOnClickListener(this);
        binding.diaplayInternal.setOnClickListener(this);
        binding.switchXserver.setOnCheckedChangeListener(this);
        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void switchXServer(boolean isInternal) {
        TextView on;
        TextView off;
        on = isInternal ? binding.diaplayInternal : binding.diaplayExternal;
        off = !isInternal ? binding.diaplayInternal : binding.diaplayExternal;
        on.setBackgroundResource(0);
        on.setTextColor(Color.BLACK);
        off.setBackgroundColor(Color.BLACK);
        off.setTextColor(Color.WHITE);
        if (isInternal)
            binding.switchXserver.setVisibility(View.VISIBLE);
        else
            binding.switchXserver.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.diaplayInternal)
            isInternal = true;
        else if (view == binding.diaplayExternal)
            isInternal = false;
        switchXServer(isInternal);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Intent mIntent = new Intent(getContext(), display.class);
        if (isChecked) {

            mIntent.putExtra("action", display.action_startX);
            if (isInternal) {
                mIntent.putExtra("value", display.value_internal);
            } else
                mIntent.putExtra("value", display.value_external);

            getContext().startService(mIntent);
            Toast.makeText(getContext(), "start", Toast.LENGTH_LONG).show();
        } else {
            mIntent.putExtra("action", display.action_stopXvfb);
            getContext().startService(mIntent);

            Toast.makeText(getContext(), "stop", Toast.LENGTH_LONG).show();
        }


    }
}

