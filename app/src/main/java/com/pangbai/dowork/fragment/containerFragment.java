package com.pangbai.dowork.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.pangbai.dowork.R;
import com.pangbai.dowork.databinding.FragmentContainerBinding;
import com.pangbai.dowork.tool.util;
import com.pangbai.dowork.*;

public class containerFragment extends Fragment {
  FragmentContainerBinding binding;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentContainerBinding.inflate(inflater);
    binding.ctTerminal.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            util.startActivity(getActivity(), TermActivity.class, false);
          }
        });

    // View view=inflater.inflate(R.layout.fragment_container,container,false);
    return binding.getRoot();
  }
}
