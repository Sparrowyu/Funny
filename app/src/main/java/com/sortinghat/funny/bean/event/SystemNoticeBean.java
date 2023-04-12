package com.sortinghat.funny.bean.event;

import com.sortinghat.funny.bean.BaseMessageBean;

/**
 * Created by wzy on 2021/9/24
 */
public class SystemNoticeBean extends BaseMessageBean {

    private ContentBean content;

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean {
        /*
        * "subType": "achievementLaugh",
			"title": "成就达成",
			"content": "恭喜！【笑出声10次】成就已达成"*/

        private String title;
        private String content;
        private String subType;



        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getSubType() {
            return subType;
        }

        public void setSubType(String subType) {
            this.subType = subType;
        }
    }
}
