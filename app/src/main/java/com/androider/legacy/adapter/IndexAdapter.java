package com.androider.legacy.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androider.legacy.R;
import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Post;
import com.androider.legacy.util.DateConverter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by Think on 2015/5/27.
 */
public class IndexAdapter extends RecyclerView.Adapter<IndexAdapter.IndexHolder>{


    protected ArrayList<Post> data = new ArrayList<>();

    public interface BottomListener{
        void onEndReach();
    }

    private BottomListener listener;
    private View.OnClickListener clickListener;
    public IndexAdapter(BottomListener listener, View.OnClickListener clicked) {
        this.listener = listener;
        clickListener = clicked;
    }

    @Override
    public IndexHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.index_item, parent, false);
        return new IndexHolder(item);
    }

    public void setData(ArrayList<Post> data){
        this.data = data;
    }

    public void addData(Post one){
        data.add(one);
        notifyItemInserted(data.size() - 1);
    }

    @Override
    public void onBindViewHolder(final IndexHolder holder, final int position) {
        final Post item = data.get(position);
        holder.title.setText(item.abs);
        holder.price.setText(Constants.emptyString + item.price + "元");
        holder.pub.setText(DateConverter.justDate(item.publish));
        holder.location.setText(item.school);
        ImageLoader.getInstance().loadImage(item.getAbsImg(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.img.setImageBitmap(loadedImage);
                Palette p = Palette.from(loadedImage).generate();
                holder.banner.setBackgroundColor(p.getLightMutedColor(Color.WHITE));
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                Bitmap already = ImageLoader.getInstance().loadImageSync(imageUri);
                holder.img.setImageBitmap(already);
                Palette p = Palette.from(already).generate();
                holder.banner.setBackgroundColor(p.getLightMutedColor(Color.WHITE));
            }
        });
        holder.indexCard.setTag(data.get(position).id);
        holder.indexCard.setOnClickListener(clickListener);
        if(position == data.size() - 1)
            listener.onEndReach();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class IndexHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView price;
        public TextView location;
        public TextView pub;
        public ImageView img;
        public CardView indexCard;
        public LinearLayout banner;
        public IndexHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.index_title);
            price = (TextView)itemView.findViewById(R.id.index_price);
            location = (TextView)itemView.findViewById(R.id.index_pos);
            pub = (TextView)itemView.findViewById(R.id.index_pub);
            img = (ImageView)itemView.findViewById(R.id.index_img);
            indexCard = (CardView)itemView.findViewById(R.id.index_card);
            banner = (LinearLayout)itemView.findViewById(R.id.index_banner);
        }
    }
}
