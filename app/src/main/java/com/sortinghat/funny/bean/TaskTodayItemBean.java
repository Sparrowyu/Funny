package com.sortinghat.funny.bean;

/**
 * Created by wzy on 2021/9/24
 */
public class TaskTodayItemBean {


    /**
     * nickName : 搞笑星球土著
     * avatar : http://oss.sortinghat.cn/userIcon/1191546372608/1632449054614.jpg
     * otherUserId : 1191546372608
     * tips : 关注了你
     * relation : 1
     */

    private int id;
    private String taskName;
    private String taskDescription;
    private int canCompleteNum;//-1为无限
    private int receiveNum;
    private int taskAward;
    private int taskType;
    private int completeNum;
    private int isFinish;
    private int isReceiveAward;
    private String inviteCode;
    private String icon;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public int getCanCompleteNum() {
        return canCompleteNum;
    }

    public void setCanCompleteNum(int canCompleteNum) {
        this.canCompleteNum = canCompleteNum;
    }

    public int getTaskAward() {
        return taskAward;
    }

    public void setTaskAward(int taskAward) {
        this.taskAward = taskAward;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public int getCompleteNum() {
        return completeNum;
    }

    public void setCompleteNum(int completeNum) {
        this.completeNum = completeNum;
    }

    public int getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(int isFinish) {
        this.isFinish = isFinish;
    }

    public int getIsReceiveAward() {
        return isReceiveAward;
    }

    public void setIsReceiveAward(int isReceiveAward) {
        this.isReceiveAward = isReceiveAward;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getReceiveNum() {
        return receiveNum;
    }

    public void setReceiveNum(int receiveNum) {
        this.receiveNum = receiveNum;
    }
}
