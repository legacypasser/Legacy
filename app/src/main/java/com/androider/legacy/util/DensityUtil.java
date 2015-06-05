package com.androider.legacy.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Think on 2015/4/28.
 */
public class DensityUtil {
    public static int dip2px(Context context, float dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
