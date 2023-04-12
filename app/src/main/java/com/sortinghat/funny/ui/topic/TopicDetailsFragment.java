package com.sortinghat.funny.ui.topic;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.base.BasePullDownRefreshFragment;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.TopicRelationPostAdapter;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.databinding.FragmentTopicDetailsBinding;
import com.sortinghat.funny.ui.common.TopicPostPreviewActivity;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.viewmodel.TopicViewModel;

import java.util.List;

import me.jingbin.library.decoration.GridSpaceItemDecoration;

/**
 * Created by wzy on 2021/8/4
 */
public class TopicDetailsFragment extends BasePullDownRefreshFragment<TopicViewModel, FragmentTopicDetailsBinding> {

    private TopicRelationPostAdapter topicRelationPostAdapter;

    private String topicId, topicName;
    private int topicTab;//标题顺序

    public static TopicDetailsFragment newInstance(int topicTab, String topicId, String topicName) {
        TopicDetailsFragment topicDetailsFragment = new TopicDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("TOPIC_TAB", topicTab);
        bundle.putString("TOPIC_ID", topicId);
        bundle.putString("TOPIC_NAME", topicName);
        topicDetailsFragment.setArguments(bundle);
        return topicDetailsFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_topic_details;
    }

    @Override
    protected void initViews() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            topicTab = bundle.getInt("TOPIC_TAB");
            topicId = bundle.getString("TOPIC_ID");
            topicName = bundle.getString("TOPIC_NAME");
        }
        initAdapter();
    }

    private void initAdapter() {
        contentLayoutBinding.recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
        contentLayoutBinding.recyclerView.addItemDecoration(new GridSpaceItemDecoration(SizeUtils.dp2px(4), true).setNoShowSpace(1, 1));
        topicRelationPostAdapter = new TopicRelationPostAdapter();
        contentLayoutBinding.recyclerView.setAdapter(topicRelationPostAdapter);
        contentLayoutBinding.recyclerView.setEmptyView(getEmptyView());
        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
    }

    @Override
    protected void setListener() {
        contentLayoutBinding.recyclerView.setOnItemClickListener((v, position) -> TopicPostPreviewActivity.starActivity("TopicDetailsFragment", topicTab,3, 0, topicId, topicName, topicRelationPostAdapter.getData(), position, viewModel.getPageNumber()));

        if (!TextUtils.isEmpty(topicId)) {
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
                getTopicRelationPostList();
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
                getTopicRelationPostList();
            });
        }
    }

    @Override
    protected void initData() {
        showLoading();
        getTopicRelationPostList();
    }

    @Override
    protected void onReload() {
        if (!NetworkUtils.isConnected()) {
            showError();
            CommonUtils.showShort(getString(R.string.network_connect_fail_prompt));
            return;
        }
        getTopicRelationPostList();
    }


    private void getTopicRelationPostList() {
        //目前只有情绪贴有数据
        if (TextUtils.isEmpty(topicId)) {
            if (contentLayoutBinding.recyclerView.isRefreshing()) {
                contentLayoutBinding.recyclerView.setRefreshing(false);
            }
            showEmptyView("当这个话题有10万人喜欢的时候，会自动解锁\n快邀请你身边感兴趣的人加入吧。", R.mipmap.empty_bg_normal);
            contentLayoutBinding.recyclerView.setEmptyViewEnabled(true);
            return;
        }
        viewModel.setPageSize(18);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("tab", 0);
        jsonObject.addProperty("topicId", topicId);
        jsonObject.addProperty("topicName", topicName);
        RequestParamUtil.addCommonRequestParam(jsonObject);
        RequestParamUtil.addPagingParam(jsonObject, viewModel.getPageNumber(), viewModel.getPageSize());

        viewModel.getTopicRelationPostList(jsonObject.toString(), topicTab).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    if (isLoadMoreFail) {
                        isLoadMoreFail = false;
                    }
                    List<HomeVideoImageListBean.ListBean> videoAndImageList = resultBean.getData().getList();
                    if (videoAndImageList != null && !videoAndImageList.isEmpty()) {
                        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
                        contentLayoutBinding.recyclerView.setLoadMoreEnabled(true);
                        if (viewModel.getPageNumber() == 1) {
                            updateTopicStatus(resultBean.getData().getLikeStatus());
                            topicRelationPostAdapter.setNewData(videoAndImageList);
                            showContentView();
                        } else {
                            topicRelationPostAdapter.addData(videoAndImageList);
                            contentLayoutBinding.recyclerView.loadMoreComplete();
                        }
                        if (videoAndImageList.size() < viewModel.getPageSize()) {
                            contentLayoutBinding.recyclerView.loadMoreEnd();
                        }
                    } else {
                        if (viewModel.getPageNumber() == 1) {
                            updateTopicStatus(resultBean.getData().getLikeStatus());
                            if (contentLayoutBinding.recyclerView.isRefreshing()) {
                                contentLayoutBinding.recyclerView.setRefreshing(false);
                            }
                            showEmptyView();
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

    private void updateTopicStatus(int topicStatus) {
        if (activity != null && !activity.isFinishing()) {
            ((TopicDetailsActivity) activity).updateTopicStatus(topicStatus);
        }
    }

    public void refreshData() {
        currentPageNumber = viewModel.getPageNumber();
        viewModel.setPageNumber(1);
        getTopicRelationPostList();
    }
}
