package com.pangbai.dowork.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pangbai.dowork.TermActivity;
import com.pangbai.dowork.databinding.FragmentDashboardBinding;
import com.pangbai.dowork.databinding.FragmentDisplayBinding;
import com.pangbai.dowork.tool.util;



public class diaplayFragment extends Fragment {
    FragmentDisplayBinding binding;


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentDisplayBinding.inflate(inflater);
        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }
}

