package com.androider.legacy.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by panboyuan on 2015/5/18.
 */
public class DividerDecorator extends RecyclerView.ItemDecoration{
    Paint paint = new Paint();
    @Override
    public void onDraw(Canvas c, RecyclerView parent,
                       RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent,
                           RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        paint.setColor(Color.LTGRAY);
        for (int i = 0, size = parent.getChildCount(); i < size; i++) {
            View child = parent.getChildAt(i);
            c.drawLine(child.getLeft(), child.getTop(),
                    child.getRight(), child.getTop(), paint);
        }
    }
}

