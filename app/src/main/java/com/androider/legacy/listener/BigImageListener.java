package com.androider.legacy.listener;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;

import com.dexafree.materialList.cards.BigImageButtonsCard;
import com.dexafree.materialList.cards.BigImageCard;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by Think on 2015/4/28.
 */
public class BigImageListener extends SimpleImageLoadingListener{
    public BigImageListener(BigImageCard card){
        this.card = card;
    }

    private BigImageCard card;
    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
        Log.v("panbo", "load complete");
        card.setDrawable(new BitmapDrawable(bitmap));
    }
}
