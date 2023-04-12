package com.sortinghat.funny.bean;

import java.io.Serializable;

/**
 * Created by wzy on 2021/7/19
 */
public class TopicListBean implements Serializable {

    /**
     * applyStatus : 1
     * createdAt : 2021-07-23 19:07:14
     * cuid : -1
     * id : 10005
     * isRecommend : 0
     * likeStatus : 0
     * level : 0
     * name : 迷惑行为大赏
     * preTopicId : 1
     * source : 0
     * status : 0
     * thumb : http://oss.sortinghat.cn/postfile/20210816/b12a72a5e9fd476da85e3f6134219b88.mp4?x-oss-process=video/snapshot,t_1,f_png,m_fast,ar_auto
     * topicIcon :
     * type : 0
     * updatedAt : 1631600486000
     * url : http://oss.sortinghat.cn/postfile/20210816/b12a72a5e9fd476da85e3f6134219b88.mp4
     */

    private int applyStatus;
    private String createdAt;
    private long cuid;
    private long id;
    private int isRecommend;
    private int likeStatus;
    private int level;
    private String name;
    private long preTopicId;
    private int source;
    private int status;
    private String thumb;
    private String topicIcon;
    private int type;
    private long updatedAt;
    private String url;

    public int getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(int applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public long getCuid() {
        return cuid;
    }

    public void setCuid(long cuid) {
        this.cuid = cuid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(int isRecommend) {
        this.isRecommend = isRecommend;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPreTopicId() {
        return preTopicId;
    }

    public void setPreTopicId(long preTopicId) {
        this.preTopicId = preTopicId;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTopicIcon() {
        return topicIcon;
    }

    public void setTopicIcon(String topicIcon) {
        this.topicIcon = topicIcon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
