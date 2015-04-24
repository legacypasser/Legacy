package com.androider.legacy.listener;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.dexafree.materialList.cards.BigImageButtonsCard;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by Think on 2015/4/24.
 */
public class ImageListener extends SimpleImageLoadingListener {

    public ImageListener(BigImageButtonsCard card){
        this.card = card;
    }

    private BigImageButtonsCard card;

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
        card.setDrawable(new BitmapDrawable(bitmap));
    }

}
