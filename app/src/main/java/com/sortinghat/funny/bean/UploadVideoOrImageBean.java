package com.sortinghat.funny.bean;

/**
 * Created by wzy on 2021/7/5
 */
public class UploadVideoOrImageBean {

    /**
     * code : 0
     * msg : http://39.107.224.111:22010/upload/9715222486364300904be1b53f2f2402.jpg
     * data : null
     */

    private int code;
    private String msg;
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
