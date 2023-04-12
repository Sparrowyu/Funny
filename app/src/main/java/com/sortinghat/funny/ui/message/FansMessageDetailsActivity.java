package com.sortinghat.funny.ui.message;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.base.BasePullDownRefreshActivity;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.FansMessageDetailsAdapter;
import com.sortinghat.funny.bean.FansMessageBean;
import com.sortinghat.funny.databinding.ActivityMessageDetailsBinding;
import com.sortinghat.funny.ui.my.MyOtherUserInfoActivity;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.viewmodel.MessageViewModel;

import java.util.List;

/**
 * Created by wzy on 2021/7/19
 */
public class FansMessageDetailsActivity extends BasePullDownRefreshActivity<MessageViewModel, ActivityMessageDetailsBinding> {

    private FansMessageDetailsAdapter fansMessageDetailsAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_details;
    }

    @Override
    protected void initViews() {
        initTitleBar("粉丝");
        initAdapter();
    }

    private void initAdapter() {
        contentLayoutBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fansMessageDetailsAdapter = new FansMessageDetailsAdapter((LifecycleOwner) this, viewModel);
        contentLayoutBinding.recyclerView.setAdapter(fansMessageDetailsAdapter);
        contentLayoutBinding.recyclerView.setEmptyView(getEmptyView());
        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
    }

    @Override
    protected void setListener() {
        contentLayoutBinding.recyclerView.setOnItemClickListener((v, position) -> {
            FansMessageBean fansMessageBean = fansMessageDetailsAdapter.getItemData(position);
            if (fansMessageBean != null && fansMessageBean.getContent() != null) {
                MyOtherUserInfoActivity.starActivity(v.getContext(), fansMessageBean.getContent().getOtherUserId(), 0);
            }
        });

        contentLayoutBinding.recyclerView.setOnRefreshListener(() -> {
            if (!NetworkUtils.isConnected()) {
                if (contentLayoutBinding.recyclerView.isRefreshing()) {
                    contentLayoutBinding.recyclerView.setRefreshing(false);
                }
                ToastUtils.showLong(R.string.network_connect_fail_prompt);
                return;
            }
            currentPageNumber = viewModel.getPageNumber();
            viewModel.setPageNumber(1);
            getFansMessageDetailsData();
        });

        contentLayoutBinding.recyclerView.setOnLoadMoreListener(() -> {
            if (!NetworkUtils.isConnected()) {
                contentLayoutBinding.recyclerView.loadMoreFail();
                ToastUtils.showLong(R.string.network_connect_fail_prompt);
                return;
            }
            if (!isLoadMoreFail) {
                viewModel.setPageNumber(viewModel.getPageNumber() + 1);
            }
            getFansMessageDetailsData();
        });
    }

    @Override
    protected void initData() {
        showLoading();
        getFansMessageDetailsData();
    }

    @Override
    protected void onReload() {
        if (!NetworkUtils.isConnected()) {
            showError();
            CommonUtils.showShort(getString(R.string.network_connect_fail_prompt));
            return;
        }
        getFansMessageDetailsData();
    }

    private void getFansMessageDetailsData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "userFollow");
        RequestParamUtil.addCommonRequestParam(jsonObject);
        RequestParamUtil.addPagingParam(jsonObject, viewModel.getPageNumber(), viewModel.getPageSize());

        viewModel.getFansMessageDetailsData(jsonObject.toString()).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    if (isLoadMoreFail) {
                        isLoadMoreFail = false;
                    }
                    List<FansMessageBean> fansMessageList = resultBean.getData();
                    if (fansMessageList != null && !fansMessageList.isEmpty()) {
                        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
                        contentLayoutBinding.recyclerView.setLoadMoreEnabled(true);
                        if (viewModel.getPageNumber() == 1) {
                            fansMessageDetailsAdapter.setNewData(fansMessageList);
                            showContentView();
                        } else {
                            fansMessageDetailsAdapter.addData(fansMessageList);
                            contentLayoutBinding.recyclerView.loadMoreComplete();
                        }
                        if (fansMessageList.size() < viewModel.getPageSize()) {
                            contentLayoutBinding.recyclerView.loadMoreEnd();
                        }
                    } else {
                        if (viewModel.getPageNumber() == 1) {
                            if (contentLayoutBinding.recyclerView.isRefreshing()) {
                                contentLayoutBinding.recyclerView.setRefreshing(false);
                            }
                            showEmptyView("暂无消息", R.mipmap.empty_bg_message_group);
                            contentLayoutBinding.recyclerView.setEmptyViewEnabled(true);
                            contentLayoutBinding.recyclerView.setLoadMoreEnabled(false);
                        } else {
                            contentLayoutBinding.recyclerView.loadMoreEnd();
                        }
                    }
                } else {
                    LogUtils.e(resultBean.getMsg());
                }
            } else {
                if (viewModel.getPageNumber() == 1) {
                    if (contentLayoutBinding.recyclerView.isRefreshing()) {
                        viewModel.setPageNumber(currentPageNumber);
                        contentLayoutBinding.recyclerView.setRefreshing(false);
                        ToastUtils.showShort("更新失败，请重试");
                    } else {
                        showError();
                    }
                } else {
                    isLoadMoreFail = true;
                    contentLayoutBinding.recyclerView.loadMoreFail();
                }
            }
        });
    }
}
