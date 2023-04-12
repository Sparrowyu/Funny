package com.sortinghat.funny.bean;

/**
 * Created by wzy on 2021/9/24
 */
public class TaskRankHeaderBean {

    private TaskRankListBean firstRank;
    private TaskRankListBean myRank;

    public TaskRankListBean getFirstRank() {
        return firstRank;
    }

    public void setFirstRank(TaskRankListBean firstRank) {
        this.firstRank = firstRank;
    }

    public TaskRankListBean getMyRank() {
        return myRank;
    }

    public void setMyRank(TaskRankListBean myRank) {
        this.myRank = myRank;
    }
}
