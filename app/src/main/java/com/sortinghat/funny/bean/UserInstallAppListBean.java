package com.sortinghat.funny.bean;

import java.util.List;

/**
 * Created by wzy on 2021/9/24
 */
public class UserInstallAppListBean {

    private int code;
    private String msg;
    private List<dataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<dataBean> getData() {
        return data;
    }

    public void setData(List<dataBean> data) {
        this.data = data;
    }


    public static class dataBean {
        private String appName;
        private int appId;
        private String appPackageName;
        private String appTag;


        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public int getAppId() {
            return appId;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }

        public String getAppPackageName() {
            return appPackageName;
        }

        public void setAppPackageName(String appPackageName) {
            this.appPackageName = appPackageName;
        }

        public String getAppTag() {
            return appTag;
        }

        public void setAppTag(String appTag) {
            this.appTag = appTag;
        }
    }

    public static class installedBean {
        private String app_name;
        private int app_id;
        private String app_packagename;
        private String app_tag;


        public String getApp_name() {
            return app_name;
        }

        public void setApp_name(String app_name) {
            this.app_name = app_name;
        }

        public int getApp_id() {
            return app_id;
        }

        public void setApp_id(int app_id) {
            this.app_id = app_id;
        }

        public String getApp_packagename() {
            return app_packagename;
        }

        public void setApp_packagename(String app_packagename) {
            this.app_packagename = app_packagename;
        }

        public String getApp_tag() {
            return app_tag;
        }

        public void setApp_tag(String app_tag) {
            this.app_tag = app_tag;
        }
    }
}
