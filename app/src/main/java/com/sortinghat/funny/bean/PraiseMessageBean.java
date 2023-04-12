package com.sortinghat.funny.bean;

/**
 * Created by wzy on 2021/9/24
 */
public class PraiseMessageBean extends BaseMessageBean {

    private ContentBean content;

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean {

        /**
         * thumb : http://oss.sortinghat.cn/postfile/20210914/96a977df1510e6e758ff9877a71f7271.mp4?x-oss-process=video/snapshot,t_1,f_png,m_fast,ar_auto
         * commentId : 209
         * postId : 1672930035712
         * avatar : http://oss.sortinghat.cn/userIcon/1191546372608/1632449054614.jpg
         * nickName : 搞笑星球土著
         * otherUserId : 1191546372608
         * tips : 赞了你的评论
         */

        private String thumb;
        private int commentId;
        private long postId;
        private String avatar;
        private String nickName;
        private long otherUserId;
        private String tips;
        private String pendantUrl;

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public int getCommentId() {
            return commentId;
        }

        public void setCommentId(int commentId) {
            this.commentId = commentId;
        }

        public long getPostId() {
            return postId;
        }

        public void setPostId(long postId) {
            this.postId = postId;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
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

        public String getPendantUrl() {
            return pendantUrl;
        }

        public void setPendantUrl(String pendantUrl) {
            this.pendantUrl = pendantUrl;
        }
    }
}
