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

import java.util.ArrayList;

/**
 * Created by Think on 2015/4/17.
 */
public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.RecycleHolder>{
    private ArrayList<Session> data = new ArrayList<>();
    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_item, parent, false);
        return new RecycleHolder(item, listner);
    }

    private OnItemClickListner listner;
    public interface OnItemClickListner{
        public void onItemClick(int id);
    }

    public void setOnclickListener(OnItemClickListner listener){
        this.listner = listener;
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

    public class RecycleHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView portrait;
        public TextView nickname;
        private OnItemClickListner sessionListener;
        public RecycleHolder(View itemView, OnItemClickListner listner) {
            super(itemView);
            portrait = (ImageView)itemView.findViewById(R.id.session_img);
            nickname = (TextView)itemView.findViewById(R.id.session_name);
            sessionListener = listner;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            sessionListener.onItemClick(data.get(getPosition()).peer);
        }
    }
}
