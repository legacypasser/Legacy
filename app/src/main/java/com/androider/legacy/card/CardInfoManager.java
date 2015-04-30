package com.androider.legacy.card;

import android.nfc.Tag;

import java.util.ArrayList;
import java.util.List;

import com.androider.legacy.card.model.CardInfo;

/**
 * Created by baojianting on 2015/4/9.
 */
public final class CardInfoManager  {

    private static final String TAG = "CardInfoManager";
    // 每次取得的数据量
    private static final int PERCOUNT = 5;

    private static CardInfoManager mCardInfoManager;
    private CacheCardInfo mCacheCardInfo;
    private CardInfoManager() {
        mCacheCardInfo = new CacheCardInfo();
    }

    public static CardInfoManager getInstance() {
        synchronized (CardInfoManager.class) {
            if (null == mCardInfoManager) {
                mCardInfoManager = new CardInfoManager();
            }
        }
        return mCardInfoManager;
    }

    /*
    首先会去读取缓存，
    接着读取数据库，数据库读取成功后更新缓存, 再次去缓存取数据
    开线程请求网络资源，有数据写数据库，更新缓存，从缓存取数据
     */
    public List<CardInfo> getCardInfos(int num) {
        if (num <= 0) {
            return null;
        }
        List<CardInfo> cardInfos = mCacheCardInfo.getCardInfo(num);
        int restNum = 0;
        boolean needDB = false;
        if (null == cardInfos) {
            cardInfos = new ArrayList<CardInfo>();
            restNum = num;
            needDB = true;
        } else if (cardInfos.size() < num) {
            restNum = num - cardInfos.size();
            needDB = true;
        } else if (cardInfos.size() == num) {
            return cardInfos;
        } else {
            return null;
        }
        // 如果缓存数据不够，请求数据库
        if (needDB) {
            return null;
        }
        // 该逻辑不会走到
        else {
            return null;
        }
    }
}
