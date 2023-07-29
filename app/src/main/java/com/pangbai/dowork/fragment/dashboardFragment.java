package com.pangbai.dowork.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.pangbai.dowork.R;
import com.pangbai.dowork.databinding.FragmentDashboardBinding;
import com.pangbai.dowork.tool.containerInfor;
import com.pangbai.dowork.tool.util;
import com.pangbai.dowork.*;

import java.util.ArrayList;
import java.util.List;

public class dashboardFragment extends Fragment {
  FragmentDashboardBinding binding;


  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentDashboardBinding.inflate(inflater);
    binding.ctTerminal.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            util.startActivity(getActivity(), TermActivity.class, false);
          }
        });

    return binding.getRoot();
  }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }
}
