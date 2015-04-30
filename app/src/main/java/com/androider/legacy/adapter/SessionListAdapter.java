package com.androider.legacy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androider.legacy.R;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Session;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Think on 2015/4/17.
 */
public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.RecycleHolder>{
    private ArrayList<Session> data = new ArrayList<>();
    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_item, parent, false);
        return new RecycleHolder(item);
    }

    @Override
    public void onBindViewHolder(RecycleHolder holder, int position) {
        holder.nickname.setText(data.get(position).nickname);
        holder.portrait.setImageResource(R.drawable.ic_person_grey600_48dp);
    }

    public void setData(ArrayList<Session> data){
        this.data = data;
    }

    public void addData(Session one){
        data.add(one);
        notifyItemInserted(data.size() - 1);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecycleHolder extends RecyclerView.ViewHolder{
        public ImageView portrait;
        public TextView nickname;
        public RecycleHolder(View itemView) {
            super(itemView);
            portrait = (ImageView)itemView.findViewById(R.id.session_img);
            nickname = (TextView)itemView.findViewById(R.id.session_name);
        }
    }
}
