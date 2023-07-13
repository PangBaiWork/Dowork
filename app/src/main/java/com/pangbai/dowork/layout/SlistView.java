package com.pangbai.dowork.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pangbai.dowork.R;
import com.pangbai.dowork.tool.containerInfor;

import org.json.JSONArray;

import java.util.List;

public class SlistView extends ListView {
        Context ct;
    public SlistView(Context context, JSONArray json) {
        super(context);
        this.ct=context;
    }
}

class ctAdapter extends ArrayAdapter<containerInfor> {
    private int newResourceId;

    public ctAdapter(Context context, int resourceId, List<containerInfor> cityList){
        super(context, resourceId, cityList);
        newResourceId = resourceId;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        containerInfor ctInfor = getItem (position);
        View view = LayoutInflater.from (getContext ()).inflate (newResourceId, parent, false);

        TextView ctName = view.findViewById (R.id.container_name);
        ImageView ctImage = view.findViewById (R.id.container_image);
        TextView ctSize=view.findViewById(R.id.container_size);
        ctName.setText (ctInfor.name);
        ctImage.setImageResource (ctInfor.srcId);
        ctSize.setText(ctInfor.size);
        return view;
    }
}