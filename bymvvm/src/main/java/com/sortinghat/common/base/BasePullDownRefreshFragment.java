package com.sortinghat.common.base;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.AndroidViewModel;

/**
 * 下拉刷新基类
 * Created by wzy on 2020/3/27
 */
public class BasePullDownRefreshFragment<VM extends AndroidViewModel, SV extends ViewDataBinding> extends BaseFragment<VM, SV> {

    protected int currentPageNumber = 1;

    protected boolean isLoadMoreFail = false;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initViews() {
    }

    @Override
    protected void initData() {
    }
}
