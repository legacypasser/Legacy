package com.androider.legacy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androider.legacy.R;
import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.PostConverter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Think on 2015/4/29.
 */
public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ViewHolder> {

    protected ArrayList<Post> data = new ArrayList<>();
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.abs_item, parent, false);
        return new ViewHolder(item, listener);
    }

    private RecycleClickListener listener;
    public interface RecycleClickListener{
        void onItemClick(int id);
    }

    public void setOnClickListener(RecycleClickListener listener){
        this.listener = listener;
    }

    public void setData(ArrayList<Post> data){
        this.data = data;
    }

    public void addData(Post one){
        data.add(one);
        notifyItemInserted(data.size() - 1);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.absDes.setText(data.get(position).abs);
        holder.absPub.setText(PostConverter.formater.format(data.get(position).publish));
        String path = "file://" + MainActivity.filePath + data.get(position).img.split(";")[0];
        File file = new File(path);
        if(file.exists())
            ImageLoader.getInstance().displayImage(path, holder.absImg);
        else
            ImageLoader.getInstance().displayImage(Constants.imgPath + data.get(position).img.split(";")[0], holder.absImg);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView absImg;
        public TextView absDes;
        private RecycleClickListener listener;
        public TextView absPub;
        public ViewHolder(View itemView, RecycleClickListener cycleListener) {
            super(itemView);
            absImg = (ImageView)itemView.findViewById(R.id.abs_img);
            absDes = (TextView)itemView.findViewById(R.id.abs_des);
            absPub = (TextView)itemView.findViewById(R.id.abs_pub);
            listener = cycleListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(listener != null)
                listener.onItemClick(data.get(getPosition()).id);
        }
    }
}
