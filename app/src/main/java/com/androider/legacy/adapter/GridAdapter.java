package com.androider.legacy.adapter;

import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Think on 2015/5/7.
 */
public class GridAdapter extends BaseAdapter {
    ArrayList<ImageView> thumbs = new ArrayList<>();

    public void addThumb(ImageView added){
        thumbs.add(added);
        notifyDataSetChanged();
    }

    public void clearImg(){
        thumbs.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return thumbs.size();
    }

    @Override
    public Object getItem(int position) {
        return thumbs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return thumbs.get(position);
    }


}
