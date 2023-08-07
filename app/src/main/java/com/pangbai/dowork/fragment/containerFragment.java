package com.pangbai.dowork.fragment;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.pangbai.dowork.tool.uiThreadUtil;
import com.pangbai.dowork.tool.util;
import com.pangbai.linuxdeploy.PrefStore;
import com.pangbai.view.dialogUtils;

import java.io.File;
import java.util.List;

public class containerFragment extends Fragment implements View.OnClickListener,ctAdapter.OnItemChange{
    FragmentContainerBinding binding;
    ctAdapter adapter;
    containerInfor currentContainer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContainerBinding.inflate(inflater);
        binding.ctAdd.setOnClickListener(this);
        adapter = new ctAdapter(containerInfor.ctList,this);
        binding.ctList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.ctDelete.setOnClickListener(this);
        binding.ctRename.setOnClickListener(this);

        new MyAsyncTask(binding.ctList).execute();
        return binding.getRoot();
    }


    List<containerInfor> initList() {
        List<String> ctName = containerInfor.getProfiles(getContext());
        return containerInfor.setInforList(ctName);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.ctAdd) {
            util.startActivity(getActivity(), PropertiesActivity.class, false);

        }else if (view==binding.ctDelete){
             int containerSize= containerInfor.ctList.size();
            if (containerSize==1){
                Toast.makeText(getContext(),"请确保至少一个容器",Toast.LENGTH_SHORT).show();
                return;}
            if (currentContainer!=null)
                    dialogUtils.showConfirmationDialog(getContext(),
                            "删除容器",
                            "确定要删除"+currentContainer.name+"吗？你将会丢失容器的数据。",
                            "确认删除",
                            "取消",
                            () -> {
                                Dialog mdialog= dialogUtils.showCustomLayoutDialog(getContext(),"删除容器"+currentContainer.name, R.layout.dialog_loading);
                                new Thread(){
                                    @Override
                                    public void run() {
                                    boolean result= containerInfor.reMoveContainer(currentContainer);
                                     mdialog.dismiss();
                                     if(!result)
                                         return;
                                     if (--ctAdapter.selectedPosition<0)
                                        ctAdapter.selectedPosition=0;
                                    containerInfor.ctList.remove(currentContainer);
                                    currentContainer= containerInfor.ctList.get(ctAdapter.selectedPosition);
                                    PrefStore.changeProfile(getContext(),currentContainer.name);
                                        uiThreadUtil.runOnUiThread(() ->{ adapter.notifyDataSetChanged();});
                                    }}.start();},
                            null);

        }else if (view==binding.ctRename){
            dialogUtils.showInputDialog(getContext(),
                    "重命名容器",
                    (dialogUtils.DialogInputListener) userInput -> {
                        if (containerInfor.getContainerByName(userInput)!=null){
                            Toast.makeText(getActivity(),"重命名失败,容器已存在",Toast.LENGTH_LONG).show();
                            return;}
                        File oldFile = PrefStore.getPropertiesConfFile(getContext());
                        File newFile = new File(PrefStore.getEnvDir(getContext()) + "/config/" + userInput + ".conf");
                        oldFile.renameTo(newFile);
                        currentContainer.name=userInput;
                        adapter.notifyDataSetChanged();
                        PrefStore.changeProfile(getContext(),userInput);
            });


        }
    }


    @Override
    public void onResume() {
        super.onResume();

        adapter.notifyDataSetChanged();
    }

    @Override
    public void OnItemChange(containerInfor infor) {
        binding.ctMethod.setText(infor.method.toUpperCase());
        currentContainer=infor;
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
            // 例如从网络获取数据、读取数据库等
            return initList();
        }

        @Override
        protected void onPostExecute(List<containerInfor> result) {
            //  ctAdapter mAdapter=new ctAdapter(result);
            //  recyclerView.setAdapter(mAdapter);
            binding.ctList.setAdapter(adapter);
            adapter.setData(result);
            adapter.notifyDataSetChanged();
            // LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            // recyclerView.setLayoutManager(layoutManager);
            binding.ctBar.setVisibility(View.VISIBLE);
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

        binding = null;
    }
}
