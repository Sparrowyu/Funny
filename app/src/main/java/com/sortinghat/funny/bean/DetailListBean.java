package com.sortinghat.funny.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DetailListBean implements Serializable {

    private List<HomeVideoImageListBean.ListBean> homeVideoBeanList = new ArrayList<>();
    private int pageNum;


    public List<HomeVideoImageListBean.ListBean> getHomeVideoBeanList() {
        return homeVideoBeanList;
    }

    public void setHomeVideoBeanList(List<HomeVideoImageListBean.ListBean> homeVideoBeanList) {
        this.homeVideoBeanList = homeVideoBeanList;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
}
