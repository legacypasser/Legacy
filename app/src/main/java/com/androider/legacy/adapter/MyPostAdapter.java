package com.androider.legacy.adapter;

import com.androider.legacy.activity.MainActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Think on 2015/4/30.
 */
public class MyPostAdapter extends RecyclerListAdapter{
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.absDes.setText(data.get(position).abs);
        String path = "file://" + MainActivity.filePath + data.get(position).img.split(";")[0];
        ImageLoader.getInstance().displayImage(path, holder.absImg);
    }
}
