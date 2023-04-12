package com.sortinghat.funny.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wzy on 2021/7/1
 */
public class TopicBean implements Serializable {

    /**
     * id : 1
     * name : 搞笑
     * preTopicId : 0
     * isLike : 0
     * isUnLike : -1
     * likeStatus : 0
     * thumb :
     * url :
     * subTopic : [{"id":10003,"name":"搞笑图文","preTopicId":1},{"id":10004,"name":"搞笑视频","preTopicId":1},{"id":10005,"name":"迷惑行为大赏","preTopicId":1},{"id":10006,"name":"脱口秀","preTopicId":1},{"id":10007,"name":"德云女孩","preTopicId":1},{"id":10008,"name":"买家秀","preTopicId":1},{"id":10009,"name":"搞笑配音","preTopicId":1},{"id":10010,"name":"搞笑表情包","preTopicId":1},{"id":10011,"name":"社畜","preTopicId":1},{"id":10012,"name":"整蛊","preTopicId":1},{"id":10013,"name":"脱口秀演员","preTopicId":1},{"id":10014,"name":"沈腾","preTopicId":1},{"id":10015,"name":"黄渤","preTopicId":1},{"id":10016,"name":"李诞","preTopicId":1},{"id":10017,"name":"郭麒麟","preTopicId":1}]
     */

    private int id;
    private String name;
    private int preTopicId;
    private int isLike;
    private int isUnLike;
    private int likeStatus;
    private String thumb;
    private String url;
    private List<SubTopicBean> subTopic;
    private boolean isSelect;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPreTopicId() {
        return preTopicId;
    }

    public void setPreTopicId(int preTopicId) {
        this.preTopicId = preTopicId;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public int getIsUnLike() {
        return isUnLike;
    }

    public void setIsUnLike(int isUnLike) {
        this.isUnLike = isUnLike;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<SubTopicBean> getSubTopic() {
        return subTopic;
    }

    public void setSubTopic(List<SubTopicBean> subTopic) {
        this.subTopic = subTopic;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public static class SubTopicBean implements Serializable {

        /**
         * id : 10003
         * name : 搞笑图文
         * preTopicId : 1
         * isLike : 0
         * isUnLike : -1
         * likeStatus : 0
         * thumb : http://oss.sortinghat.cn/postfile/20210816/ACC8C242155441039095590BE4AFF2B1.mp4?x-oss-process=video/snapshot,t_1,f_png,m_fast,ar_auto
         * url : http://oss.sortinghat.cn/postfile/20210816/ACC8C242155441039095590BE4AFF2B1.mp4
         */

        private int id;
        private String name;
        private int preTopicId;
        private int isLike;
        private int isUnLike;
        private int likeStatus;
        private String thumb;
        private String url;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPreTopicId() {
            return preTopicId;
        }

        public void setPreTopicId(int preTopicId) {
            this.preTopicId = preTopicId;
        }

        public int getIsLike() {
            return isLike;
        }

        public void setIsLike(int isLike) {
            this.isLike = isLike;
        }

        public int getIsUnLike() {
            return isUnLike;
        }

        public void setIsUnLike(int isUnLike) {
            this.isUnLike = isUnLike;
        }

        public int getLikeStatus() {
            return likeStatus;
        }

        public void setLikeStatus(int likeStatus) {
            this.likeStatus = likeStatus;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
