package com.sortinghat.funny.bean;

/**
 * Created by wzy on 2021/9/24
 */
public class BaseMessageBean {

    /**
     * isRead : false
     * crateAt : 2021-09-23 20:11:04
     * type : applySys
     * userId : 899814893056
     */

    private boolean isRead;
    private long crateAt;
    private String type;
    private long userId;

    public boolean isIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public long getCrateAt() {
        return crateAt;
    }

    public void setCrateAt(long crateAt) {
        this.crateAt = crateAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
