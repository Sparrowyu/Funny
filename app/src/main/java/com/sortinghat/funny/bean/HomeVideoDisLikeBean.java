package com.sortinghat.funny.bean;

public class HomeVideoDisLikeBean {

    private long userId;
    private long postId;
    private int disLikeType;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public int getDisLikeType() {
        return disLikeType;
    }

    public void setDisLikeType(int disLikeType) {
        this.disLikeType = disLikeType;
    }

}
