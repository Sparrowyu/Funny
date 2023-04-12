package com.sortinghat.funny.bean;

import java.util.List;

/**
 * Created by wzy on 2021/7/16
 */
public class PostReviewBean {

    /**
     * commentId : 190
     * commentStatus : {"dislikeNum":0,"id":190,"likeNum":0,"maxLikeNum":0,"replyNum":0,"shareNum":0}
     * content : 臭哥哥乖乖
     * createTime : 1627820452000
     * curUserLikeType : 0
     * nickName :
     * parentId : 0
     * replyNickname : 搞笑艺术家0jyb0yxn
     * replyUserId : 520832535808
     * subComments : [{"commentId":191,"commentStatus":{"dislikeNum":0,"id":191,"likeNum":0,"maxLikeNum":0,"replyNum":0,"shareNum":0},"content":"vvvvvv不不不","createTime":1627820464000,"curUserLikeType":0,"nickName":"","parentId":190,"replyNickname":"test6672","replyUserId":107316316672,"tinyUser":{"avatar":"http://39.107.224.111:22010/upload/client-resource/head@3x.png","nickname":"test6672"},"userId":107316316672}]
     * tinyUser : {"avatar":"http://39.107.224.111:22010/upload/client-resource/head@3x.png","nickname":"test6672"}
     * userId : 107316316672
     */

    private long commentId;
    private CommentStatusBean commentStatus;
    private String content;
    private long createTime;
    private int curUserLikeType;
    private String nickName;
    private int parentId;
    private String replyNickname;
    private long replyUserId;
    private TinyUserBean tinyUser;
    private long userId;
    private List<SubCommentsBean> subComments;
    private boolean reviewReplyListUnfoldStatus;


    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public CommentStatusBean getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(CommentStatusBean commentStatus) {
        this.commentStatus = commentStatus;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getCurUserLikeType() {
        return curUserLikeType;
    }

    public void setCurUserLikeType(int curUserLikeType) {
        this.curUserLikeType = curUserLikeType;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getReplyNickname() {
        return replyNickname;
    }

    public void setReplyNickname(String replyNickname) {
        this.replyNickname = replyNickname;
    }

    public long getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(long replyUserId) {
        this.replyUserId = replyUserId;
    }

    public TinyUserBean getTinyUser() {
        return tinyUser;
    }

    public void setTinyUser(TinyUserBean tinyUser) {
        this.tinyUser = tinyUser;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<SubCommentsBean> getSubComments() {
        return subComments;
    }

    public void setSubComments(List<SubCommentsBean> subComments) {
        this.subComments = subComments;
    }

    public boolean isReviewReplyListUnfoldStatus() {
        return reviewReplyListUnfoldStatus;
    }

    public void setReviewReplyListUnfoldStatus(boolean reviewReplyListUnfoldStatus) {
        this.reviewReplyListUnfoldStatus = reviewReplyListUnfoldStatus;
    }

    public static class CommentStatusBean {

        /**
         * dislikeNum : 0
         * id : 190
         * likeNum : 0
         * maxLikeNum : 0
         * replyNum : 0
         * shareNum : 0
         */

        private int dislikeNum;
        private int id;
        private int likeNum;
        private int maxLikeNum;
        private int replyNum;
        private int shareNum;


        public int getDislikeNum() {
            return dislikeNum;
        }

        public void setDislikeNum(int dislikeNum) {
            this.dislikeNum = dislikeNum;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLikeNum() {
            return likeNum;
        }

        public void setLikeNum(int likeNum) {
            this.likeNum = likeNum;
        }

        public int getMaxLikeNum() {
            return maxLikeNum;
        }

        public void setMaxLikeNum(int maxLikeNum) {
            this.maxLikeNum = maxLikeNum;
        }

        public int getReplyNum() {
            return replyNum;
        }

        public void setReplyNum(int replyNum) {
            this.replyNum = replyNum;
        }

        public int getShareNum() {
            return shareNum;
        }

        public void setShareNum(int shareNum) {
            this.shareNum = shareNum;
        }
    }

    public static class TinyUserBean {

        /**
         * avatar : http://39.107.224.111:22010/upload/client-resource/head@3x.png
         * nickname : test6672
         */

        private String avatar;
        private String nickname;
        private String pendantUrl;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPendantUrl() {
            return pendantUrl;
        }

        public void setPendantUrl(String pendantUrl) {
            this.pendantUrl = pendantUrl;
        }
    }

    public static class SubCommentsBean {

        /**
         * commentId : 191
         * commentStatus : {"dislikeNum":0,"id":191,"likeNum":0,"maxLikeNum":0,"replyNum":0,"shareNum":0}
         * content : vvvvvv不不不
         * createTime : 1627820464000
         * curUserLikeType : 0
         * nickName :
         * parentId : 190
         * replyNickname : test6672
         * replyUserId : 107316316672
         * tinyUser : {"avatar":"http://39.107.224.111:22010/upload/client-resource/head@3x.png","nickname":"test6672"}
         * userId : 107316316672
         */

        private long commentId;
        private CommentStatusBeanX commentStatus;
        private String content;
        private long createTime;
        private int curUserLikeType;
        private String nickName;
        private long parentId;
        private String replyNickname;
        private long replyUserId;
        private TinyUserBeanX tinyUser;
        private long userId;

        public long getCommentId() {
            return commentId;
        }

        public void setCommentId(long commentId) {
            this.commentId = commentId;
        }

        public CommentStatusBeanX getCommentStatus() {
            return commentStatus;
        }

        public void setCommentStatus(CommentStatusBeanX commentStatus) {
            this.commentStatus = commentStatus;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getCurUserLikeType() {
            return curUserLikeType;
        }

        public void setCurUserLikeType(int curUserLikeType) {
            this.curUserLikeType = curUserLikeType;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public long getParentId() {
            return parentId;
        }

        public void setParentId(long parentId) {
            this.parentId = parentId;
        }

        public String getReplyNickname() {
            return replyNickname;
        }

        public void setReplyNickname(String replyNickname) {
            this.replyNickname = replyNickname;
        }

        public long getReplyUserId() {
            return replyUserId;
        }

        public void setReplyUserId(long replyUserId) {
            this.replyUserId = replyUserId;
        }

        public TinyUserBeanX getTinyUser() {
            return tinyUser;
        }

        public void setTinyUser(TinyUserBeanX tinyUser) {
            this.tinyUser = tinyUser;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public static class CommentStatusBeanX {

            /**
             * dislikeNum : 0
             * id : 191
             * likeNum : 0
             * maxLikeNum : 0
             * replyNum : 0
             * shareNum : 0
             */

            private int dislikeNum;
            private int id;
            private int likeNum;
            private int maxLikeNum;
            private int replyNum;
            private int shareNum;

            public int getDislikeNum() {
                return dislikeNum;
            }

            public void setDislikeNum(int dislikeNum) {
                this.dislikeNum = dislikeNum;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getLikeNum() {
                return likeNum;
            }

            public void setLikeNum(int likeNum) {
                this.likeNum = likeNum;
            }

            public int getMaxLikeNum() {
                return maxLikeNum;
            }

            public void setMaxLikeNum(int maxLikeNum) {
                this.maxLikeNum = maxLikeNum;
            }

            public int getReplyNum() {
                return replyNum;
            }

            public void setReplyNum(int replyNum) {
                this.replyNum = replyNum;
            }

            public int getShareNum() {
                return shareNum;
            }

            public void setShareNum(int shareNum) {
                this.shareNum = shareNum;
            }
        }

        public static class TinyUserBeanX {

            /**
             * avatar : http://39.107.224.111:22010/upload/client-resource/head@3x.png
             * nickname : test6672
             */

            private String avatar;
            private String nickname;

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }
        }
    }
}
