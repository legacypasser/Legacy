package com.androider.legacy.card;

import com.androider.legacy.card.model.CardInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baojianting on 2015/4/9.
 *
 */
public class CacheCardInfo {

    private List<CardInfo> mCardInfo;
    private int mCursor;
    public CacheCardInfo() {
        mCardInfo = new ArrayList<CardInfo>();
        mCursor = 0;
    }

    public void clearCache() {
        mCardInfo.clear();
    }

    // 根据时间倒序将CardInfo添加到缓存
    public void addCardInfo(CardInfo cardInfo) {
        if (mCardInfo.size() == 0) {
            mCardInfo.add(cardInfo);
        } else {
            boolean flag = false;
            for(int i = 0; i < mCardInfo.size(); i++) {
                if (cardInfo.isNewerThan(mCardInfo.get(i))) {
                    mCardInfo.add(i, cardInfo);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                mCardInfo.add(cardInfo);
            }
        }
    }

    public void addCardInfo(List<CardInfo> cardInfos) {
        for(int i = 0; i < cardInfos.size(); i++) {
            addCardInfo(cardInfos.get(i));
        }
    }

    public int size() {
        return mCardInfo.size();
    }

    public void updateCardInfoIsAssociateWithView(List<CardInfo> cardInfos) {
        for(int i = 0; i < cardInfos.size(); i++) {
            if (!cardInfos.get(i).getIsAssociateWithView()) {
                cardInfos.get(i).setIsAssociateWithView(true);
            }
        }
    }

    public List<CardInfo> getCardInfo(int num) {
        int restSize = size() - mCursor;
        List<CardInfo> retCardInfos = new ArrayList<CardInfo>();
        if (restSize <= 0) {
            return null;
        } else if (restSize < num) {
            for(int i = mCursor; i < size(); i++) {
                if (!mCardInfo.get(i).getIsAssociateWithView()) {
                    retCardInfos.add(mCardInfo.get(i));
                }
            }
            // 更新指针
            mCursor += restSize;
            return retCardInfos;
        } else {
            int tempCount = 0;
            for(;; mCursor++) {
                if(mCursor == size()) {
                    break;
                }
                if(!mCardInfo.get(mCursor).getIsAssociateWithView()) {
                    retCardInfos.add(mCardInfo.get(mCursor));
                    tempCount++;
                }
                if (tempCount == num) {
                    break;
                }
            }
            mCursor += 1;
            return retCardInfos;
        }
    }
}
