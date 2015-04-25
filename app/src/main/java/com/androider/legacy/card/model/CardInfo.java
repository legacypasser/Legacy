package com.androider.legacy.card.model;


import java.sql.Timestamp;

/**
 * Created by bao on 2015/4/5.
 */
public final class CardInfo implements Comparable {

    private static final String TAG = "CardInfo";
    private String userNickName;
    private String userImagePath;
    private Timestamp postTime;
    private String description;
    private String contentImagePath;
    private boolean isAssociateWithView;

    public CardInfo(String userNickName, String userImagePath, Timestamp postTime,
                    String description, String contentImagePath, boolean isAssociateWithView) {
        this.userNickName = userNickName;
        this.userImagePath = userImagePath;
        this.postTime = postTime;
        this.description = description;
        this.contentImagePath = contentImagePath;
        this.isAssociateWithView = isAssociateWithView;
    }

    public boolean getIsAssociateWithView() {
        return isAssociateWithView;
    }

    public void setIsAssociateWithView(boolean i) {
        this.isAssociateWithView = i;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserImagePath() {
        return userImagePath;
    }

    public void setUserImagePath(String userImagePath) {
        this.userImagePath = userImagePath;
    }

    public Timestamp getPostTime() {
        return postTime;
    }

    public void setPostTime(Timestamp postTime) {
        this.postTime = postTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContentImagePath() {
        return contentImagePath;
    }

    public void setContentImagePath(String contentImagePath) {
        this.contentImagePath = contentImagePath;
    }

    @Override
    public int compareTo(Object another) {
        if (another != null && another instanceof CardInfo) {
            CardInfo a = (CardInfo)another;
            return compareTimestamp(postTime, a.getPostTime());
        } else {
            return 0;
        }
    }

    public boolean isNewerThan(CardInfo another) {
        if (compareTo(another) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    private int compareTimestamp(Timestamp one, Timestamp two) {
        if (one.getTime() < two.getTime()) {
            return -1;
        } else if (one.getTime() == two.getTime()) {
            return 0;
        } else {
            return 1;
        }
    }
}
