package com.sortinghat.funny.bean;

/**
 * Created by wzy on 2021/9/24
 */
public class TaskPackageItemBean {


    /**
     * nickName : 搞笑星球土著
     * avatar : http://oss.sortinghat.cn/userIcon/1191546372608/1632449054614.jpg
     * otherUserId : 1191546372608
     * tips : 关注了你
     * relation : 1
     */

    private int id;
    private long userId;
    private int proId;
    private String productName;
    private String productUrl;
    private int status;//是否穿戴道具，0：未穿戴，1：已穿戴，2：已过期
    private int delStatus;
    private String expireTime;
    private String createdTime;
    private boolean isClicked;//是否是编辑模式


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getProId() {
        return proId;
    }

    public void setProId(int proId) {
        this.proId = proId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(int delStatus) {
        this.delStatus = delStatus;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }
}
