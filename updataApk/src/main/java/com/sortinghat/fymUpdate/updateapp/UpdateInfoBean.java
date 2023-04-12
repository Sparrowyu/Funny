package com.sortinghat.fymUpdate.updateapp;

import java.io.Serializable;

public class UpdateInfoBean implements Serializable {
    /**
     * {
     * "code": 0,
     * "data": {
     * "isUpgrade": 1,
     * "isForceUpgrade": 0,
     * "versionCode": 1001,
     * "versionName": "1.0.1",
     * "upgradeDescription": "代码优化",
     * "apkUrl": "http:\/\/dl.duantian.com\/apk\/update\/com.moying.crazyreduce_105.apk",
     * "createTime": "createTime"
     * },
     * "msg": ""
     * }
     */
    private int code;
    private DataBean data;


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {

        private int isUpgrade;//1有新版本
        private int isForceUpgrade;//1强制更新
        private int versionCode;
        private String versionName;
        private String upgradeDescription;
        private String apkUrl;
        private String createTime;
        private String downloadurl;
        private int RIcon; // 本应用对应的图标 ,从应用本身传参，非服务器获取

        public int getIsUpgrade() {
            return isUpgrade;
        }

        public void setIsUpgrade(int isUpgrade) {
            this.isUpgrade = isUpgrade;
        }

        public int getIsForceUpgrade() {
            return isForceUpgrade;
        }

        public void setIsForceUpgrade(int isForceUpgrade) {
            this.isForceUpgrade = isForceUpgrade;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getUpgradeDescription() {
            return upgradeDescription;
        }

        public void setUpgradeDescription(String upgradeDescription) {
            this.upgradeDescription = upgradeDescription;
        }

        public String getApkUrl() {
            return apkUrl;
        }

        public void setApkUrl(String apkUrl) {
            this.apkUrl = apkUrl;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getDownloadurl() {
            return downloadurl;
        }

        public void setDownloadurl(String downloadurl) {
            this.downloadurl = downloadurl;
        }

        public int getRIcon() {
            return RIcon;
        }

        public void setRIcon(int RIcon) {
            this.RIcon = RIcon;
        }
    }

}
