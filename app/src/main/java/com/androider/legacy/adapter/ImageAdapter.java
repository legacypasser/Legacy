package com.androider.legacy.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androider.legacy.R;
import com.joooonho.SelectableRoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Think on 2015/5/31.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    ArrayList<String> data = new ArrayList<>();
    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_img, null);
        return new ImageHolder(item);
    }

    public void addData(String url){
        data.add(url);
        notifyItemInserted(data.size() - 1);
    }
    @Override
    public void onBindViewHolder(ImageHolder holder, int position){
        ImageLoader.getInstance().displayImage(data.get(position), holder.img);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ImageHolder extends RecyclerView.ViewHolder{
        public ImageView img;
        public ImageHolder(View itemView) {
            super(itemView);
            img = (SelectableRoundedImageView)itemView.findViewById(R.id.detail_img);
        }
    }
}
