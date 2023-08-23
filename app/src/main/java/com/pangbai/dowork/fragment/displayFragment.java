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
    public static final String width="display_width",height="display_height",depth="display_depth";


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
        binding.displaySave.setOnClickListener(this);
       screen= display.getScreenFromPref(getContext());
       binding.displayWidth.setText(String.valueOf(screen[0]));
        binding.displayHeight.setText(String.valueOf(screen[1]));
        binding.diaplayDepth.setText(String.valueOf(screen[2]));


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
        if (view == binding.diaplayInternal||view == binding.diaplayExternal) {
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
            PrefStore.SETTINGS.set(getContext(), "Xserver_internal", Boolean.toString(isInternal));
        }else if (view==binding.displaySave){
            screen = getFromEditText();
            PrefStore.SETTINGS.set(getContext(),width,String.valueOf(screen[0]));
            PrefStore.SETTINGS.set(getContext(),height,String.valueOf(screen[1]));
            PrefStore.SETTINGS.set(getContext(),depth,String.valueOf(screen[2]));

        }
    }

    @Override
    public void onResume() {
        super.onResume();
           switchXServerUI(isXserverInternal(getContext()));
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Intent mIntent = new Intent(getContext(), display.class);
        if (isChecked) {
            mIntent.putExtra("action", display.action_startXvfb);
        } else {
            mIntent.putExtra("action", display.action_stopXvfb);
        }
        getContext().startService(mIntent);
        PrefStore.SETTINGS.set(getContext(), "Xserver_xvfb",Boolean.toString(isChecked));
    }



    public static boolean isXserverInternal(Context c){
        return PrefStore.SETTINGS.get(c, "Xserver_internal").equals("true");
    }
    public int[] getFromEditText() {
        int width,height,depth;
        try {
            width = Integer.parseInt(binding.displayWidth.getText().toString());
            height = Integer.parseInt(binding.displayHeight.getText().toString());
            depth = Integer.parseInt(binding.diaplayDepth.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(),"error input,num only",Toast.LENGTH_LONG).show();
            return new int[]{800, 600, 24};
        }
        Toast.makeText(getContext(),"Saved ,please restart for application",Toast.LENGTH_LONG).show();
        return new int[]{width, height, depth};
    }


}

