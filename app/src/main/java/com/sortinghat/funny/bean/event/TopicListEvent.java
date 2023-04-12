package com.sortinghat.funny.bean.event;

/**
 * Created by wzy on 2021/10/19
 */
public class TopicListEvent {

    public final String source;
    public final String topicId;
    public final String topicName;
    public final int operateType;

    public TopicListEvent(String source, String topicId, String topicName, int operateType) {
        this.source = source;
        this.topicId = topicId;
        this.topicName = topicName;
        this.operateType = operateType;
    }
}
