package com.pangbai.dowork.tool;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pangbai.dowork.Command.cmdExer;
import com.pangbai.dowork.R;
import com.pangbai.dowork.service.serviceCallback;

import java.util.List;

public class procAdapter extends RecyclerView.Adapter<procAdapter.ViewHolder> {
    private List<procMap> map;
    serviceCallback callback;

    public procAdapter(List<procMap> itemList, serviceCallback callback) {
        this.map = itemList;
        this.callback=callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_proc, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = map.get(position).name;
        holder.procName.setText(text);

        holder.procKill.setOnClickListener(v -> {
           Toast.makeText(v.getContext(),"已结束进程"+ map.get(position).pid,Toast.LENGTH_SHORT).show();
            cmdExer.execute("kill -9 "+map.get(position).pid,true,false);
            map.remove(position);
            callback.callback(map.size());
            notifyDataSetChanged();

          //todo
        });
    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView procName;
        TextView procKill;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            procName = itemView.findViewById(R.id.proc_name);
            procKill = itemView.findViewById(R.id.proc_kill);
        }
    }


}


