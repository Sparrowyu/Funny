package com.sortinghat.funny.bean;

/**
 * Created by wzy on 2021/9/24
 */
public class AuditNoticeMessageBean extends BaseMessageBean {

    private ContentBean content;

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean {

        /**
         * applyReason : 很遗憾，你的新头像不符合社区规范，建议您修改后再次提交审核，如有异议，可以通过QQ申诉：1785636085。
         * type : userIconApplyRefuse
         * title : 审核提醒
         */

        private String applyReason;
        private String type;
        private String title;

        public String getApplyReason() {
            return applyReason;
        }

        public void setApplyReason(String applyReason) {
            this.applyReason = applyReason;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
