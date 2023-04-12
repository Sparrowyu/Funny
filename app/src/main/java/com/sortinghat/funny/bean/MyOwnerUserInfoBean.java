package com.sortinghat.funny.bean;

public class MyOwnerUserInfoBean {


    /**
     * avatar : http://39.107.224.111:22010/upload/5d6142e59cfa4dd8835b56f770c06756.jpg
     * birthday : 2021-7-14
     * city : 北京
     * createdTime : 1625741907000
     * deviceId : 2ecacdbfd60b83124909f1becd9b430ff
     * gender : 1
     * id : 172315250432
     * nickname : Rhett
     * phoneNum :
     * professional : 专业人士（教师/医生/律师等）
     * qqId :
     * school : 北京外星语学校
     * slogan : 我已经做出了我的选择
     * status : 0
     * updatedTime : 1625741907000
     * wxOpenId :
     * wxUnionid :
     */
    private String authToken;
    private String longTermToken;
    private int days;
    private UserBaseBean userBase;
    /**
     * commentCount : 0
     * createPostCount : 0
     * fansCount : 0
     * followCount : 0
     * lastLogin : 1626665189000
     * likePostCount : 1
     * likedCount : 0
     * userId : 172315250432
     */

    private UserStatusBean userStatus;

    public UserBaseBean getUserBase() {
        return userBase;
    }

    public void setUserBase(UserBaseBean userBase) {
        this.userBase = userBase;
    }

    public UserStatusBean getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatusBean userStatus) {
        this.userStatus = userStatus;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getLongTermToken() {
        return longTermToken;
    }

    public void setLongTermToken(String longTermToken) {
        this.longTermToken = longTermToken;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public static class UserBaseBean {
        private String account;
        private String avatar;
        private String birthday;
        private String city;
        private String deviceId;
        private int gender;//性别 0: 未设置 1: 男 2: 女,示例值(1)
        private long id;
        private String nickname;
        private String phoneNumShow;
        private String professional;
        private String qqId;
        private String school;
        private String slogan;
        private int status;//user_status 0:游客 1:注册成功 2:账户注销中 3:账户已经注销
        private String wxOpenId;
        private String wxUnionid;
        private String pendantUrl;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPhoneNumShow() {
            return phoneNumShow;
        }

        public void setPhoneNumShow(String phoneNumShow) {
            this.phoneNumShow = phoneNumShow;
        }

        public String getProfessional() {
            return professional;
        }

        public void setProfessional(String professional) {
            this.professional = professional;
        }

        public String getQqId() {
            return qqId;
        }

        public void setQqId(String qqId) {
            this.qqId = qqId;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }

        public String getSlogan() {
            return slogan;
        }

        public void setSlogan(String slogan) {
            this.slogan = slogan;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getWxOpenId() {
            return wxOpenId;
        }

        public void setWxOpenId(String wxOpenId) {
            this.wxOpenId = wxOpenId;
        }

        public String getWxUnionid() {
            return wxUnionid;
        }

        public void setWxUnionid(String wxUnionid) {
            this.wxUnionid = wxUnionid;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getPendantUrl() {
            return pendantUrl;
        }

        public void setPendantUrl(String pendantUrl) {
            this.pendantUrl = pendantUrl;
        }
    }

    public static class UserStatusBean {
        private int commentCount;
        private int createPostCount;
        private int fansCount;
        private int laughCount;
        private int followCount;
        private long lastLogin;
        private int likePostCount;
        private int likedCount;
        private long userId;

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }

        public int getCreatePostCount() {
            return createPostCount;
        }

        public void setCreatePostCount(int createPostCount) {
            this.createPostCount = createPostCount;
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

        public long getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(long lastLogin) {
            this.lastLogin = lastLogin;
        }

        public int getLikePostCount() {
            return likePostCount;
        }

        public void setLikePostCount(int likePostCount) {
            this.likePostCount = likePostCount;
        }

        public int getLikedCount() {
            return likedCount;
        }

        public void setLikedCount(int likedCount) {
            this.likedCount = likedCount;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public int getLaughCount() {
            return laughCount;
        }

        public void setLaughCount(int laughCount) {
            this.laughCount = laughCount;
        }
    }
}
