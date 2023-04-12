package com.sortinghat.funny.bean;

/**
 * Created by wzy on 2021/9/24
 */
public class TaskRankListBean {

    private long userId;
    private String avatar;
    private String nickname;
    private String pendantUrl;
    private long starNoteCount;
    private String rank;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPendantUrl() {
        return pendantUrl;
    }

    public void setPendantUrl(String pendantUrl) {
        this.pendantUrl = pendantUrl;
    }

    public long getStarNoteCount() {
        return starNoteCount;
    }

    public void setStarNoteCount(long starNoteCount) {
        this.starNoteCount = starNoteCount;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
