package com.pangbai.dowork.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import com.pangbai.dowork.R;
import com.pangbai.dowork.databinding.FragmentDisplayBinding;
import com.pangbai.dowork.databinding.FragmentSettingBinding;

public class settingFragment extends PreferenceFragmentCompat {
    FragmentSettingBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }
}

