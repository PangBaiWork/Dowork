package com.pangbai.dowork.tool;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pangbai.dowork.databinding.ListContainerBinding;

import java.util.List;

public     class ctAdapter extends RecyclerView.Adapter<MyViewHoder> {
    List<containerInfor> mList ;


    public void setData( List<containerInfor> newData) {
        mList = newData;
        notifyDataSetChanged();
    }
   public ctAdapter(List<containerInfor> mList) {
       this.mList=mList;
   }
    @NonNull
    @Override
    public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListContainerBinding binding = ListContainerBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
          MyViewHoder myViewHoder = new MyViewHoder(binding);
        return myViewHoder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoder holder, int position) {

        containerInfor infor = (containerInfor) mList.get(position);
        holder.ctIcon.setBackgroundResource(infor.iconId);
        holder.ctName.setText(infor.name);
        holder.ctVersion.setText(infor.version);
       // holder.ctInfor.setText(infor.name+"\n"+infor.version);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

class MyViewHoder extends RecyclerView.ViewHolder {
    TextView ctVersion;
    TextView ctName;
    LinearLayout ctIcon;

    public MyViewHoder(@NonNull ListContainerBinding binding) {
        super(binding.getRoot());
        ctVersion=binding.ctVersion;
        ctName=binding.ctName;
        ctIcon=binding.ctIcon;
    }
}

