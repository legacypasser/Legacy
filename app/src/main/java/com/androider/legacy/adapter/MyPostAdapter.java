package com.androider.legacy.adapter;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.data.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/**
 * Created by Think on 2015/4/30.
 */
public class MyPostAdapter extends RecyclerListAdapter{
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.absDes.setText(data.get(position).abs);
        String path = "file://" + MainActivity.filePath + data.get(position).img.split(";")[0];
        File file = new File(path);
        if(file.exists())
            ImageLoader.getInstance().displayImage(path, holder.absImg);
        else
            ImageLoader.getInstance().displayImage(Constants.imgPath + data.get(position).img.split(";")[0], holder.absImg);
    }
}
