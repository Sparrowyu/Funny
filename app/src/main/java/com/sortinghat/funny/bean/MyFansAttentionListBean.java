package com.sortinghat.funny.bean;

public class MyFansAttentionListBean {

    /**
     * avatar :
     * fansCount : 0
     * followCount : 0
     * gender : 0
     * lastPubPostTime :
     * likedCount : 0
     * mutualFollow : 0
     * nickname :
     * slogan :
     * userId : 0
     */

    private String avatar;
    private int fansCount;
    private int followCount;
    private int gender;
    private long lastPubPostTime;
    private int likedCount;
    private int mutualFollow;// 0-双方未关注 1-双方已经关注 2-仅我关注对方 3-仅对方关注我"
    private String nickname;
    private String slogan;
    private long userId;
    private String pendantUrl;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public long getLastPubPostTime() {
        return lastPubPostTime;
    }

    public void setLastPubPostTime(long lastPubPostTime) {
        this.lastPubPostTime = lastPubPostTime;
    }

    public int getLikedCount() {
        return likedCount;
    }

    public void setLikedCount(int likedCount) {
        this.likedCount = likedCount;
    }

    public int getMutualFollow() {
        return mutualFollow;
    }

    public void setMutualFollow(int mutualFollow) {
        this.mutualFollow = mutualFollow;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getPendantUrl() {
        return pendantUrl;
    }

    public void setPendantUrl(String pendantUrl) {
        this.pendantUrl = pendantUrl;
    }
}
