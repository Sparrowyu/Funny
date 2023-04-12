package com.sortinghat.funny.bean;

/**
 * Created by wzy on 2021/9/17
 */
public class MessageCountBean {

    /**
     * userFollow : 2
     * userFollowRemind : 搞笑星球土著等人关注了你
     * userFollowLastTime : 1632818019233
     * postComment : 23
     * postCommentRemind : zhangsan21等人评论了你的作品
     * postCommentLastTime : 1632898151017
     * commentLike : 17
     * postLike : 7
     * likeRemind : zhangsan21等人赞了你的作品
     * likeLastTime : 1632894977079
     * applySys : 22
     * applySysRemind : 2021-09-26
     * applySysLastTime : 1632799329723
     * sysBroadcast : 0
     * sysBroadcastRemind : 2021-09-26
     */

    private int userFollow;
    private String userFollowRemind;
    private int sysBroadcast;
    private String likeRemind;
    private int postComment;
    private String applySysRemind;
    private int applySys;
    private String sysBroadcastRemind;
    private int commentLike;
    private int postLike;
    private String postCommentRemind;
    private long userFollowLastTime;
    private long postCommentLastTime;
    private long likeLastTime;
    private long applySysLastTime;

    public int getUserFollow() {
        return userFollow;
    }

    public void setUserFollow(int userFollow) {
        this.userFollow = userFollow;
    }

    public String getUserFollowRemind() {
        return userFollowRemind;
    }

    public void setUserFollowRemind(String userFollowRemind) {
        this.userFollowRemind = userFollowRemind;
    }

    public int getSysBroadcast() {
        return sysBroadcast;
    }

    public void setSysBroadcast(int sysBroadcast) {
        this.sysBroadcast = sysBroadcast;
    }

    public String getLikeRemind() {
        return likeRemind;
    }

    public void setLikeRemind(String likeRemind) {
        this.likeRemind = likeRemind;
    }

    public int getPostComment() {
        return postComment;
    }

    public void setPostComment(int postComment) {
        this.postComment = postComment;
    }

    public String getApplySysRemind() {
        return applySysRemind;
    }

    public void setApplySysRemind(String applySysRemind) {
        this.applySysRemind = applySysRemind;
    }

    public int getApplySys() {
        return applySys;
    }

    public void setApplySys(int applySys) {
        this.applySys = applySys;
    }

    public String getSysBroadcastRemind() {
        return sysBroadcastRemind;
    }

    public void setSysBroadcastRemind(String sysBroadcastRemind) {
        this.sysBroadcastRemind = sysBroadcastRemind;
    }

    public int getCommentLike() {
        return commentLike;
    }

    public void setCommentLike(int commentLike) {
        this.commentLike = commentLike;
    }

    public int getPostLike() {
        return postLike;
    }

    public void setPostLike(int postLike) {
        this.postLike = postLike;
    }

    public String getPostCommentRemind() {
        return postCommentRemind;
    }

    public void setPostCommentRemind(String postCommentRemind) {
        this.postCommentRemind = postCommentRemind;
    }

    public long getUserFollowLastTime() {
        return userFollowLastTime;
    }

    public void setUserFollowLastTime(long userFollowLastTime) {
        this.userFollowLastTime = userFollowLastTime;
    }

    public long getPostCommentLastTime() {
        return postCommentLastTime;
    }

    public void setPostCommentLastTime(long postCommentLastTime) {
        this.postCommentLastTime = postCommentLastTime;
    }

    public long getLikeLastTime() {
        return likeLastTime;
    }

    public void setLikeLastTime(long likeLastTime) {
        this.likeLastTime = likeLastTime;
    }

    public long getApplySysLastTime() {
        return applySysLastTime;
    }

    public void setApplySysLastTime(long applySysLastTime) {
        this.applySysLastTime = applySysLastTime;
    }
}
