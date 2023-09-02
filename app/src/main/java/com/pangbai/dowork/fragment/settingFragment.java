package com.pangbai.dowork.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.pangbai.dowork.R;
import com.pangbai.dowork.databinding.FragmentDisplayBinding;
import com.pangbai.dowork.databinding.FragmentSettingBinding;
import com.pangbai.dowork.tool.util;

public class settingFragment extends PreferenceFragmentCompat {
    FragmentSettingBinding binding;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getContext().setTheme(R.style.WhiteContextTheme);
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        getPreferenceManager().setSharedPreferencesName("dowork_preference");
        addPreferencesFromResource(R.xml.setting_dowork);
      //  setDividerHeight(util.Dp2Px(getContext(),8));
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }
}

