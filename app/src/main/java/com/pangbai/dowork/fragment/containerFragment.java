package com.pangbai.dowork.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pangbai.dowork.PropertiesActivity;
import com.pangbai.dowork.R;
import com.pangbai.dowork.databinding.FragmentContainerBinding;
import com.pangbai.dowork.tool.containerInfor;
import com.pangbai.dowork.tool.ctAdapter;
import com.pangbai.dowork.tool.util;

import java.util.List;

public class containerFragment extends Fragment implements View.OnClickListener {
    FragmentContainerBinding binding;
    ctAdapter  adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

/*

    initList();
        ctAdapter mAdapter=new ctAdapter(ctList);
        binding.ctProotList.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.ctProotList.setLayoutManager(layoutManager);
        binding.ctProot.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
        */
        binding=FragmentContainerBinding.inflate(inflater);
        binding.ctProotAdd.setOnClickListener(this);
        adapter = new ctAdapter(containerInfor.ctList);

        binding.ctProotList.setLayoutManager(new LinearLayoutManager(getContext()));


        new MyAsyncTask(binding.ctProotList).execute();
        return binding.getRoot();
    }




    List<containerInfor> initList() {
       List<String>  ctName=containerInfor.getProfiles(getContext());

      /*  ctList.add(new containerInfor("Debian", "Debian GNU/Linux 11 (bullseye)", R.drawable.ct_icon_debian));
        ctList.add(new containerInfor("Debian1", "Debian GNU/Linux 11 (bullseye)", R.drawable.ct_icon_debian));
        ctList.add(new containerInfor("Debian2", "Debian GNU/Linux 11 (bullseye)", R.drawable.ct_icon_debian));
       ctList.add(new containerInfor("Debian3","Debian GNU/Linux 11 (bullseye)",R.drawable.ct_icon_debian));
        ctList.add(new containerInfor("Debian4","Debian GNU/Linux 11 (bullseye)",R.drawable.ct_icon_debian));
        */
        return  containerInfor.setInforList(ctName);

    }

    @Override
    public void onClick(View view) {
      int id =view.getId();
      if (id==R.id.ct_proot_add){
          util.startActivity(getActivity(), PropertiesActivity.class, false);

        }
    }


    public class MyAsyncTask extends AsyncTask<Void, Void, List<containerInfor>> {
        private RecyclerView recyclerView;
     //   private ctAdapter adapter;

        public MyAsyncTask(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
          //  this.adapter = adapter;
        }

        @Override
        protected List<containerInfor> doInBackground(Void... voids) {
            // 在后台线程执行耗时操作，返回结果

            // 例如从网络获取数据、读取数据库等
            return  initList();
        }

        @Override
        protected void onPostExecute(List<containerInfor> result) {
          //  ctAdapter mAdapter=new ctAdapter(result);
          //  recyclerView.setAdapter(mAdapter);
            binding.ctProotList.setAdapter(adapter);
            adapter.setData(result);
            adapter.notifyDataSetChanged();
        // LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
          // recyclerView.setLayoutManager(layoutManager);
            binding.ctProot.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);

         /*   adapter.setData(result);
            recyclerView.setAdapter(adapter);*/
            // 在主线程更新 UI，使用 doInBackground 返回的结果
            // 例如更新 RecyclerView 的数据集
            // recyclerView.setAdapter(new MyAdapter(result));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding=null;
    }
}
