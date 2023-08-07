package com.pangbai.dowork.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.pangbai.dowork.R;
import com.pangbai.dowork.databinding.FragmentDashboardBinding;
import com.pangbai.dowork.tool.containerInfor;
import com.pangbai.dowork.tool.util;
import com.pangbai.dowork.*;
import com.pangbai.linuxdeploy.PrefStore;

import java.util.ArrayList;
import java.util.List;

public class dashboardFragment extends Fragment implements View.OnClickListener{
  FragmentDashboardBinding binding;
  containerInfor currentContainer;


  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentDashboardBinding.inflate(inflater);
    binding.ctTerminal.setOnClickListener(this);

    return binding.getRoot();
  }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    @Override
    public void onClick(View view) {
      if (view==binding.ctTerminal) {
          util.startActivity(getActivity(), TermActivity.class, false);
      }
    }

    @Override
    public void onResume() {
        super.onResume();
       String name= PrefStore.getProfileName(getContext());
       currentContainer=containerInfor.getContainerInfor(name);
       if (currentContainer==null){
           Toast.makeText(getContext(),"no container found",Toast.LENGTH_LONG).show();
           getActivity().finish();
       }
       binding.ctInfor.setText(currentContainer.name+"\n"+currentContainer.version);
       binding.ctIcon.setBackgroundResource(currentContainer.iconId);

    }
}
