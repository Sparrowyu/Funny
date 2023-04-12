package com.sortinghat.common.base;

import android.app.Application;

import androidx.annotation.NonNull;

/**
 * 有列表的页面
 * Created by wzy on 2021/7/9
 */
public class BaseListViewModel extends BaseViewModel {

    private int pageNumber = 1;
    private int pageSize = 12;

    public BaseListViewModel(@NonNull Application application) {
        super(application);
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
