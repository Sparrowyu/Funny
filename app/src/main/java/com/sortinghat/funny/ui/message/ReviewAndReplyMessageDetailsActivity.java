package com.sortinghat.funny.ui.message;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.ReviewAndReplyMessageDetailsAdapter;
import com.sortinghat.funny.bean.ReviewAndReplyMessageBean;
import com.sortinghat.funny.databinding.ActivityMessageDetailsBinding;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.viewmodel.MessageViewModel;

import java.util.List;

/**
 * Created by wzy on 2021/7/19
 */
public class ReviewAndReplyMessageDetailsActivity extends BaseMessageDetailsActivity<MessageViewModel, ActivityMessageDetailsBinding> {

    private ReviewAndReplyMessageDetailsAdapter reviewAndReplyMessageDetailsAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_details;
    }

    @Override
    protected void initViews() {
        initTitleBar("评论与回复");
        initAdapter();
    }

    private void initAdapter() {
        contentLayoutBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAndReplyMessageDetailsAdapter = new ReviewAndReplyMessageDetailsAdapter();
        contentLayoutBinding.recyclerView.setAdapter(reviewAndReplyMessageDetailsAdapter);
        contentLayoutBinding.recyclerView.setEmptyView(getEmptyView());
        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
    }

    @Override
    protected void setListener() {
        contentLayoutBinding.recyclerView.setOnItemClickListener((v, position) -> {
            ReviewAndReplyMessageBean reviewAndReplyMessageBean = reviewAndReplyMessageDetailsAdapter.getItemData(position);
            if (reviewAndReplyMessageBean != null && reviewAndReplyMessageBean.getContent() != null) {
                getPostInfo(viewModel, reviewAndReplyMessageBean.getContent().getPostId(), reviewAndReplyMessageBean.getType());
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
            getReviewAndReplyMessageDetailsData();
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
            getReviewAndReplyMessageDetailsData();
        });
    }

    @Override
    protected void initData() {
        showLoading();
        getReviewAndReplyMessageDetailsData();
    }

    @Override
    protected void onReload() {
        if (!NetworkUtils.isConnected()) {
            showError();
            CommonUtils.showShort(getString(R.string.network_connect_fail_prompt));
            return;
        }
        getReviewAndReplyMessageDetailsData();
    }

    private void getReviewAndReplyMessageDetailsData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "postComment");
        RequestParamUtil.addCommonRequestParam(jsonObject);
        RequestParamUtil.addPagingParam(jsonObject, viewModel.getPageNumber(), viewModel.getPageSize());

        viewModel.getReviewAndReplyMessageDetailsData(jsonObject.toString()).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    if (isLoadMoreFail) {
                        isLoadMoreFail = false;
                    }
                    List<ReviewAndReplyMessageBean> reviewAndReplyMessageList = resultBean.getData();
                    if (reviewAndReplyMessageList != null && !reviewAndReplyMessageList.isEmpty()) {
                        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
                        contentLayoutBinding.recyclerView.setLoadMoreEnabled(true);
                        if (viewModel.getPageNumber() == 1) {
                            reviewAndReplyMessageDetailsAdapter.setNewData(reviewAndReplyMessageList);
                            showContentView();
                        } else {
                            reviewAndReplyMessageDetailsAdapter.addData(reviewAndReplyMessageList);
                            contentLayoutBinding.recyclerView.loadMoreComplete();
                        }
                        if (reviewAndReplyMessageList.size() < viewModel.getPageSize()) {
                            contentLayoutBinding.recyclerView.loadMoreEnd();
                        }
                    } else {
                        if (viewModel.getPageNumber() == 1) {
                            if (contentLayoutBinding.recyclerView.isRefreshing()) {
                                contentLayoutBinding.recyclerView.setRefreshing(false);
                            }
                            showEmptyView("暂无消息", R.drawable.load_message_data_empty);
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
