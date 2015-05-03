package com.androider.legacy.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;

import com.androider.legacy.R;

/**
 * Created by Think on 2015/5/3.
 */
public class LegacyProgress extends Dialog {
    public LegacyProgress(Context context) {
        super(context, R.style.MyProgressDialog);
        setContentView(R.layout.loading);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
    }
}
