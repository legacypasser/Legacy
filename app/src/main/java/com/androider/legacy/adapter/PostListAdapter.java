package com.androider.legacy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.view.MaterialListView;

/**
 * Created by Think on 2015/4/17.
 */
public class PostListAdapter extends MaterialListView.Adapter<PostListAdapter.ViewHolder>{

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends MaterialListView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
