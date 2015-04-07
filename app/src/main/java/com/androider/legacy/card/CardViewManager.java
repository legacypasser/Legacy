package com.androider.legacy.card;

import android.content.Context;

/**
 * Created by bao on 2015/4/5.
 */
public final class CardViewManager {

    private static final String TAG = "CardViewManager";

    private static CardViewManager mCardViewManager = null;

    private Context mContext;

    private CardViewManager() {}

    public static CardViewManager getInstance() {
        synchronized (CardViewManager.class) {
            if (null == mCardViewManager) {
                mCardViewManager = new CardViewManager();
            }
            return mCardViewManager;
        }
    }

    public synchronized void setContext(Context context) {
        if (null == mContext) {
            mContext = context;
        }
    }



}
