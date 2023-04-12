package com.sortinghat.funny.bean;

/**
 * Created by wzy on 2021/9/24
 */
public class FansMessageBean extends BaseMessageBean {

    private ContentBean content;

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean {

        /**
         * nickName : 搞笑星球土著
         * avatar : http://oss.sortinghat.cn/userIcon/1191546372608/1632449054614.jpg
         * otherUserId : 1191546372608
         * tips : 关注了你
         * relation : 1
         */

        private String nickName;
        private String avatar;
        private long otherUserId;
        private String tips;
        private int relation;
        private String pendantUrl;

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public long getOtherUserId() {
            return otherUserId;
        }

        public void setOtherUserId(long otherUserId) {
            this.otherUserId = otherUserId;
        }

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }

        public int getRelation() {
            return relation;
        }

        public void setRelation(int relation) {
            this.relation = relation;
        }

        public String getPendantUrl() {
            return pendantUrl;
        }

        public void setPendantUrl(String pendantUrl) {
            this.pendantUrl = pendantUrl;
        }
    }
}
