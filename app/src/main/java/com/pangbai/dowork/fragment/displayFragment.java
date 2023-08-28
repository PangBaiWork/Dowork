package com.pangbai.dowork.fragment;

import android.content.Context;
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

import com.pangbai.dowork.databinding.FragmentDisplayBinding;
import com.pangbai.dowork.service.display;
import com.pangbai.linuxdeploy.PrefStore;


public class displayFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    FragmentDisplayBinding binding;
    public static boolean isInternal = true;
    int screen[];
    public static final String str_width = "display_width", str_height = "display_height", str_depth = "display_depth", str_internal = "Xserver_xvfb", str_audio = "Xserver_auido";


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
        binding.switchPulseaudio.setOnCheckedChangeListener(this);
        binding.displaySave.setOnClickListener(this);
        screen = display.getScreenFromPref(getContext());
        binding.displayWidth.setText(String.valueOf(screen[0]));
        binding.displayHeight.setText(String.valueOf(screen[1]));
        binding.diaplayDepth.setText(String.valueOf(screen[2]));
        switchXServerUI(isXserverInternal(getContext()));
        binding.switchPulseaudio.setChecked(isAudioOn(getContext()));

        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void switchXServerUI(boolean isInternal) {
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
        if (view == binding.diaplayInternal || view == binding.diaplayExternal) {
            isInternal = view == binding.diaplayInternal;
            switchXServerUI(isInternal);
            Intent mIntent = new Intent(getContext(), display.class);
            mIntent.putExtra("action", display.action_startX);
            if (isInternal) {
                binding.switchXserver.setChecked(true);
                mIntent.putExtra("value", display.value_internal);
            } else {
                mIntent.putExtra("value", display.value_external);
            }
            getContext().startService(mIntent);
            PrefStore.SETTINGS.set(getContext(), str_internal, Boolean.toString(isInternal));
        } else if (view == binding.displaySave) {
            screen = getFromEditText();
            PrefStore.SETTINGS.set(getContext(), str_width, String.valueOf(screen[0]));
            PrefStore.SETTINGS.set(getContext(), str_height, String.valueOf(screen[1]));
            PrefStore.SETTINGS.set(getContext(), str_depth, String.valueOf(screen[2]));

        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        Intent mIntent = new Intent(getContext(), display.class);
        int action;
        if (compoundButton == binding.switchXserver) {
            action = isChecked ? display.action_startXvfb : display.action_stopXvfb;
        } else {
            PrefStore.SETTINGS.set(getContext(), str_audio, Boolean.toString(isChecked));
            action = isChecked ? display.action_startAudio : display.action_stopAudio;
        }
        mIntent.putExtra("action", action);
        getContext().startService(mIntent);


    }


    public static boolean isXserverInternal(Context c) {
        return PrefStore.SETTINGS.get(c, str_internal).equals("true");
    }

    public static boolean isAudioOn(Context c) {
        return PrefStore.SETTINGS.get(c, str_audio).equals("true");
    }

    public int[] getFromEditText() {
        int width, height, depth;
        try {
            width = Integer.parseInt(binding.displayWidth.getText().toString());
            height = Integer.parseInt(binding.displayHeight.getText().toString());
            depth = Integer.parseInt(binding.diaplayDepth.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "error input,num only", Toast.LENGTH_LONG).show();
            return new int[]{800, 600, 24};
        }
        Toast.makeText(getContext(), "Saved ,please restart for application", Toast.LENGTH_LONG).show();
        return new int[]{width, height, depth};
    }


}

