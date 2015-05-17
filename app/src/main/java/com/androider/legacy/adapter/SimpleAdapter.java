package com.androider.legacy.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androider.legacy.R;

/**
 * Created by Think on 2015/5/17.
 */
public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.RecyclerHolder> {

    String wording;

    public SimpleAdapter(String wording){
        this.wording = wording;
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent,false);
        return new RecyclerHolder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        holder.textView.setText(wording);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public RecyclerHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.wording);
        }
    }
}
