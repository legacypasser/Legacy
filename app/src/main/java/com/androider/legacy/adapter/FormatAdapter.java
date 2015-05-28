package com.androider.legacy.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androider.legacy.R;
import com.androider.legacy.data.Douban;
import com.androider.legacy.util.WatcherSimplifier;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Think on 2015/5/28.
 */
public class FormatAdapter extends RecyclerView.Adapter<FormatAdapter.FormatHolder> {

    public ArrayList<Douban> provided = new ArrayList<>();

    public void addBook(Douban item){
        provided.add(item);
        notifyItemInserted(provided.size() - 1);
    }

    @Override
    public FormatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.format_book, null);
        return new FormatHolder(item);
    }

    @Override
    public void onBindViewHolder(final FormatHolder holder, final int position) {
        holder.title.setText(provided.get(position).name);
        ImageLoader.getInstance().displayImage(provided.get(position).img, holder.img);
        holder.price.addTextChangedListener(new WatcherSimplifier() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")){
                    provided.get(position).price = 0;
                }else{
                    int setPrice = Integer.parseInt(holder.price.getText().toString());
                    provided.get(position).price = setPrice;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return provided.size();
    }

    public static class FormatHolder extends RecyclerView.ViewHolder{
        public ImageView img;
        public TextView title;
        public EditText price;
        public FormatHolder(View itemView) {
            super(itemView);
            img = (ImageView)itemView.findViewById(R.id.douban_img);
            title = (TextView)itemView.findViewById(R.id.douban_name);
            price = (MaterialEditText)itemView.findViewById(R.id.douban_price);
        }
    }
}
