package com.androider.legacy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Think on 2015/5/27.
 */
public class IndexAdapter extends RecyclerView.Adapter<IndexAdapter.IndexHolder>{

    @Override
    public IndexHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(IndexHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class IndexHolder extends RecyclerView.ViewHolder{
        public IndexHolder(View itemView) {
            super(itemView);
        }
    }
}
