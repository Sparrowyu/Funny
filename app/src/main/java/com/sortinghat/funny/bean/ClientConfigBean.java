package com.sortinghat.funny.bean;

import android.text.TextUtils;

/**
 * Created by wzy on 2021/08/03
 */
public class ClientConfigBean {

    /**
     * panda：走旧的感受赞业务，landScape：走新的感受赞业务
     */
    private String bottomEmotion;
    private String emoticon; //panda , astronaut
    private boolean userInformation;
    private boolean userLike;
    private int adAndroid1;//首页视频广告控制： 0为不显示广告
    private String redOrNum;//首页图文红点或者数字
    private String createPostEducation;//发帖教育，-1时不显示
    private boolean userEvaluate;//评价引导
    private int androidOpenScreenAd;//开屏广告0，1
    private int androidImageTextAd;//图文广告
    private int androidFrequencyAd;//视频新版第三个出广告的AB
    private long androidOpenScreenAdKeepAlive;//毫秒值 "480000"
    private boolean androidTasksys;//是否任务
    private boolean android_through_review_state;//是否通过，true是通过了已经
    private int androidMoodReport;//报告d
    private int isVip;//1vip 0不是

    public String getBottomEmotion() {
        if (TextUtils.isEmpty(bottomEmotion)) {
            return "panda";
        }
        return bottomEmotion;
    }

    public void setBottomEmotion(String bottomEmotion) {
        this.bottomEmotion = bottomEmotion;
    }

    public String getEmoticon() {
        return emoticon;
    }

    public void setEmoticon(String emoticon) {
        this.emoticon = emoticon;
    }

    public boolean isUserInformation() {
        return userInformation;
    }

    public void setUserInformation(boolean userInformation) {
        this.userInformation = userInformation;
    }

    public boolean isUserLike() {
        return userLike;
    }

    public void setUserLike(boolean userLike) {
        this.userLike = userLike;
    }

    public int getAdAndroid1() {
        return adAndroid1;
    }

    public void setAdAndroid1(int adAndroid1) {
        this.adAndroid1 = adAndroid1;
    }

    public String getRedOrNum() {
        return redOrNum;
    }

    public void setRedOrNum(String redOrNum) {
        this.redOrNum = redOrNum;
    }

    public String getCreatePostEducation() {
        return createPostEducation;
    }

    public void setCreatePostEducation(String createPostEducation) {
        this.createPostEducation = createPostEducation;
    }

    public int getAndroidOpenScreenAd() {
        return androidOpenScreenAd;
    }

    public void setAndroidOpenScreenAd(int androidOpenScreenAd) {
        this.androidOpenScreenAd = androidOpenScreenAd;
    }

    public int getAndroidImageTextAd() {
        return androidImageTextAd;
    }

    public void setAndroidImageTextAd(int androidImageTextAd) {
        this.androidImageTextAd = androidImageTextAd;
    }

    public int getAndroidFrequencyAd() {
        return androidFrequencyAd;
    }

    public void setAndroidFrequencyAd(int androidFrequencyAd) {
        this.androidFrequencyAd = androidFrequencyAd;
    }

    public long getAndroidOpenScreenAdKeepAlive() {
        return androidOpenScreenAdKeepAlive;
    }

    public void setAndroidOpenScreenAdKeepAlive(long androidOpenScreenAdKeepAlive) {
        this.androidOpenScreenAdKeepAlive = androidOpenScreenAdKeepAlive;
    }

    public boolean isAndroidTasksys() {
        return androidTasksys;
    }

    public void setAndroidTasksys(boolean androidTasksys) {
        this.androidTasksys = androidTasksys;
    }

    public boolean isAndroid_through_review_state() {
        return android_through_review_state;
    }

    public void setAndroid_through_review_state(boolean android_through_review_state) {
        this.android_through_review_state = android_through_review_state;
    }

    public int getAndroidMoodReport() {
        return androidMoodReport;
    }

    public void setAndroidMoodReport(int androidMoodReport) {
        this.androidMoodReport = androidMoodReport;
    }

    public boolean isUserEvaluate() {
        return userEvaluate;
    }

    public void setUserEvaluate(boolean userEvaluate) {
        this.userEvaluate = userEvaluate;
    }

    public int getIsVip() {
        return isVip;
    }

    public void setIsVip(int isVip) {
        this.isVip = isVip;
    }
}