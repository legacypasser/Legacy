package com.androider.legacy.adapter;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androider.legacy.R;
import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Session;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Think on 2015/4/17.
 */
public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.RecycleHolder>{
    private LinkedList<Session> data = new LinkedList<>();
    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_item, parent, false);
        return new RecycleHolder(item, listner);
    }

    private OnItemClickListner listner;
    public interface OnItemClickListner{
        void onItemClick(int id);
    }

    public void setOnclickListener(OnItemClickListner listener){
        this.listner = listener;
    }

    @Override
    public void onBindViewHolder(RecycleHolder holder, int position) {
        holder.nickname.setText(data.get(position).nickname);
        switch (position%6){
            case 0:
                holder.card.setBackgroundColor(MainActivity.instance.getResources().getColor(R.color.color_one));
                break;
            case 1:
                holder.card.setBackgroundColor(MainActivity.instance.getResources().getColor(R.color.color_two));
                break;
            case 2:
                holder.card.setBackgroundColor(MainActivity.instance.getResources().getColor(R.color.color_three));
                break;
            case 3:
                holder.card.setBackgroundColor(MainActivity.instance.getResources().getColor(R.color.color_four));
                break;
            case 4:
                holder.card.setBackgroundColor(MainActivity.instance.getResources().getColor(R.color.color_five));
                break;
            case 5:
                holder.card.setBackgroundColor(MainActivity.instance.getResources().getColor(R.color.color_six));
                break;
        }
    }

    public void addData(Session one){
        int pos = 0;
        data.add(pos, one);
        notifyItemInserted(pos);
    }

    public void refresh(Session one){
        int pos = data.indexOf(one);
        data.remove(one);
        data.addFirst(one);
        notifyItemMoved(pos, 0);
    }

    public void clearData(){
        int iniSize = data.size();
        if(iniSize != 0){
            data.clear();
            notifyItemRangeRemoved(0, iniSize);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecycleHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nickname;
        private OnItemClickListner sessionListener;
        public RelativeLayout card;
        public RecycleHolder(View itemView, OnItemClickListner listner) {
            super(itemView);
            nickname = (TextView)itemView.findViewById(R.id.session_name);
            card = (RelativeLayout)itemView.findViewById(R.id.entry_holder);
            sessionListener = listner;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            sessionListener.onItemClick(data.get(getPosition()).peer);
        }
    }
}
