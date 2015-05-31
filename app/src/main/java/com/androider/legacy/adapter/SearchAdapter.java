package com.androider.legacy.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androider.legacy.R;
import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.activity.SearchActivity;
import com.androider.legacy.data.Post;
import com.androider.legacy.fragment.PostDetailFragment;
import com.androider.legacy.util.DateConverter;
import com.gc.materialdesign.views.Card;
import com.joooonho.SelectableRoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Think on 2015/5/31.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {

    ArrayList<Post> data = new ArrayList<>();
    public void setData(ArrayList<Post> data){
        this.data = data;
    }

    public void addData(Post one){
        data.add(one);
        notifyItemInserted(data.size() - 1);
    }
    @Override
    public SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, null);
        return new SearchHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchHolder holder, int position) {
        final Post item = data.get(position);
        holder.title.setText(item.abs);
        holder.price.setText("" + item.price + "元");
        holder.pub.setText(DateConverter.justDate(item.publish));
        ImageLoader.getInstance().loadImage(item.getAbsImg(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.img.setImageBitmap(loadedImage);
                Palette p = Palette.from(loadedImage).generate();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                Bitmap already = ImageLoader.getInstance().loadImageSync(imageUri);
                holder.img.setImageBitmap(already);
                Palette p = Palette.from(already).generate();
            }
        });

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostDetailFragment.currentId = item.id;
                SearchActivity.instance.switchFragment(PostDetailFragment.class.getSimpleName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class SearchHolder extends RecyclerView.ViewHolder{
        public ImageView img;
        public TextView title;
        public TextView pub;
        public TextView pos;
        public TextView price;
        CardView card;
        public SearchHolder(View itemView) {
            super(itemView);
            img = (SelectableRoundedImageView)itemView.findViewById(R.id.search_img);
            title = (TextView)itemView.findViewById(R.id.search_title);
            pub = (TextView)itemView.findViewById(R.id.search_pub);
            pos = (TextView)itemView.findViewById(R.id.search_pos);
            price = (TextView)itemView.findViewById(R.id.search_price);
            card = (CardView)itemView.findViewById(R.id.search_card);
        }
    }
}
