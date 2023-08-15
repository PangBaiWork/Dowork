package com.pangbai.dowork.tool;


import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pangbai.dowork.PropertiesActivity;
import com.pangbai.dowork.R;
import com.pangbai.dowork.databinding.ListContainerBinding;
import com.pangbai.linuxdeploy.PrefStore;

import java.util.List;

public class ctAdapter extends RecyclerView.Adapter<MyViewHoder> {
    List<containerInfor> mList;
    public OnItemChange ItemChange;
   // public   ListContainerBinding binding;
    public static int selectedPosition = RecyclerView.NO_POSITION;
    //临时记录上次选择的位置
    int tmp = -1;
    Drawable background;


    public void setData(List<containerInfor> newData) {
        mList = newData;
        notifyDataSetChanged();
    }

    public ctAdapter(List<containerInfor> mList, OnItemChange mOnItemChange) {
        this.ItemChange = mOnItemChange;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mview = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_container, parent, false);
        MyViewHoder myViewHoder = new MyViewHoder(mview);
        background = mview.getBackground();
        String Name = PrefStore.getProfileName(parent.getContext());
        containerInfor CurrentContainer = containerInfor.getContainerByName(Name);


        /////////从配置文件获取当前的容器
        tmp =selectedPosition;

        if (CurrentContainer != null) {
            selectedPosition = containerInfor.ctList.indexOf(CurrentContainer);
            ItemChange.OnItemChange(CurrentContainer);
        } else {
            if (containerInfor.ctList.isEmpty())
                return myViewHoder;
            containerInfor current = containerInfor.ctList.get(0);
            PrefStore.changeProfile(parent.getContext(), current.name);
            selectedPosition = 0;
            ItemChange.OnItemChange(current);

        }

            //PrefStore.changeProfile(parent.getContext(), current.name);

        ////////列表项目点击
        myViewHoder.itemView.setOnClickListener(v -> {
            if (selectedPosition != myViewHoder.getAdapterPosition()) {
                // 点击了未选中的项，更新选中的位置
                // selectedPosition;
                selectedPosition = myViewHoder.getAdapterPosition();
                containerInfor infor = mList.get(selectedPosition);
                PrefStore.changeProfile(v.getContext(), infor.name);
                ItemChange.OnItemChange(infor);
                notifyDataSetChanged();
            }
            // 通知RecyclerView刷新列表项，以显示选中效果
        });
        ////////按钮点击
        mview.findViewById(R.id.ct_run_configure).setOnClickListener(view -> {
            PrefStore.changeProfile(view.getContext(), mList.get(myViewHoder.getAdapterPosition()).name);
            util.startActivity(view.getContext(), PropertiesActivity.class, false);
        });
        return myViewHoder;
    }

    public void  notifyLastItem(){
        if (tmp!=-1)
             notifyItemChanged(tmp);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHoder holder, int position) {
        if (mList.isEmpty())
            return;
        containerInfor infor = (containerInfor) mList.get(position);
        holder.ctIcon.setBackgroundResource(infor.iconId);
        holder.ctName.setText(infor.name);
        holder.ctVersion.setText(infor.version);
        // 根据选中的项的位置为每个项设置不同的背景


        if (position == selectedPosition)
            holder.itemView.setBackgroundResource(R.drawable.card_border);
        else
            holder.itemView.setBackground(background);


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface OnItemChange {
        public void OnItemChange(containerInfor infor);

    }
}

class MyViewHoder extends RecyclerView.ViewHolder {
    TextView ctVersion;
    TextView ctName;
    LinearLayout ctIcon;

    // CardView card;
    public MyViewHoder(@NonNull View view) {
        super(view);
        ctVersion = view.findViewById(R.id.ct_version);
        ctName = view.findViewById(R.id.ct_name);
        ctIcon =view.findViewById(R.id.ct_icon);
        // card=binding.getRoot();
    }
}

