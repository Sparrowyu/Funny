package com.sortinghat.fymUpdate.common;

public class UpdateAppBean {
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

    public static class DataBean {

        private int isUpgrade;//1有新版本
        private int isForceUpgrade;//1强制更新
        private int versionCode;
        private String versionName;
        private String upgradeDescription;
        private String apkUrl;
        private String createTime;


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
    }
}
