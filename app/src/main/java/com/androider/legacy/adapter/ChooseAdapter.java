package com.androider.legacy.adapter;

import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androider.legacy.R;

import java.util.ArrayList;

/**
 * Created by Think on 2015/5/22.
 */
public class ChooseAdapter extends RecyclerView.Adapter<ChooseAdapter.RecycleHolder> {

    ArrayList<String> data;
    View.OnClickListener listener;
    public ChooseAdapter(ArrayList<String> names, View.OnClickListener listener) {
        data = names;
        this.listener = listener;
    }

    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name, parent, false);
        return new RecycleHolder(item);
    }

    @Override
    public void onBindViewHolder(RecycleHolder holder, int position) {
        holder.name.setText(data.get(position));
        holder.name.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class RecycleHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public RecycleHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.choose_name);
        }
    }
}
