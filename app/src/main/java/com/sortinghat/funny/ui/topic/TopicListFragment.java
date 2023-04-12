package com.sortinghat.funny.ui.topic;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.base.BasePullDownRefreshFragment;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.TopicContentAdapter;
import com.sortinghat.funny.bean.TopicListBean;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.FragmentTopicContentBinding;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.viewmodel.TopicViewModel;

import java.util.List;

import me.jingbin.library.decoration.GridSpaceItemDecoration;

/**
 * Created by wzy on 2021/7/16
 */
public class TopicListFragment extends BasePullDownRefreshFragment<TopicViewModel, FragmentTopicContentBinding> {

    private TextView tvMetaverseMoreTopic;

    private TopicContentAdapter topicContentAdapter;

    private int topicListType;

    public static TopicListFragment newInstance(int topicListType) {
        TopicListFragment topicListFragment = new TopicListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("TOPIC_LIST_TYPE", topicListType);
        topicListFragment.setArguments(bundle);
        return topicListFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_topic_content;
    }

    @Override
    protected void initViews() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            topicListType = bundle.getInt("TOPIC_LIST_TYPE");
        }
        initAdapter();
    }

    private void initAdapter() {
        contentLayoutBinding.recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
        contentLayoutBinding.recyclerView.addItemDecoration(new GridSpaceItemDecoration(SizeUtils.dp2px(5), false));

        if (topicListType == 1) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(48));
            tvMetaverseMoreTopic = new TextView(activity);
            tvMetaverseMoreTopic.setLayoutParams(layoutParams);
            tvMetaverseMoreTopic.setGravity(Gravity.CENTER);
            tvMetaverseMoreTopic.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvMetaverseMoreTopic.setTextSize(14);
            tvMetaverseMoreTopic.setTextColor(CommonUtils.getColor(R.color.color_999999));
            tvMetaverseMoreTopic.setText(getString(R.string.more));
            tvMetaverseMoreTopic.setVisibility(View.GONE);

            contentLayoutBinding.recyclerView.addFooterView(tvMetaverseMoreTopic);
        }

        topicContentAdapter = new TopicContentAdapter(topicListType);
        contentLayoutBinding.recyclerView.setAdapter(topicContentAdapter);
        contentLayoutBinding.recyclerView.setEmptyView(getEmptyView());
        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
    }

    @Override
    protected void setListener() {
        if (topicListType == 1) {
            tvMetaverseMoreTopic.setOnClickListener(new QuickClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    RxBus.getDefault().post(RxCodeConstant.SWITCH_METAVERSE_TAB, 1);
                }
            });
        }

        contentLayoutBinding.recyclerView.setOnItemClickListener((v, position) -> {
            TopicListBean topicBean = topicContentAdapter.getItemData(position);
            if (topicBean != null) {
                TopicDetailsActivity.starActivity(String.valueOf(topicBean.getId()), topicBean.getName());
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
            getTopicList();
        });

        if (topicListType != 1) {
            contentLayoutBinding.recyclerView.setOnLoadMoreListener(() -> {
                if (!NetworkUtils.isConnected()) {
                    contentLayoutBinding.recyclerView.loadMoreFail();
                    ToastUtils.showLong(R.string.network_connect_fail_prompt);
                    return;
                }
                if (!isLoadMoreFail) {
                    viewModel.setPageNumber(viewModel.getPageNumber() + 1);
                }
                getTopicList();
            });
        }
    }

    @Override
    protected void initData() {
        showLoading();
        getTopicList();
    }

    @Override
    protected void onReload() {
        if (!NetworkUtils.isConnected()) {
            showError();
            CommonUtils.showShort(getString(R.string.network_connect_fail_prompt));
            return;
        }
        getTopicList();
    }

    private void getTopicList() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", topicListType);
        RequestParamUtil.addCommonRequestParam(jsonObject);
        RequestParamUtil.addPagingParam(jsonObject, viewModel.getPageNumber(), viewModel.getPageSize());

        viewModel.getTopicList(jsonObject.toString()).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    if (isLoadMoreFail) {
                        isLoadMoreFail = false;
                    }

                    List<TopicListBean> topicList = resultBean.getData();
                    if (topicList != null && !topicList.isEmpty()) {
                        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
                        contentLayoutBinding.recyclerView.setLoadMoreEnabled(true);
                        if (viewModel.getPageNumber() == 1) {
                            topicContentAdapter.setNewData(topicList);
                            showContentView();
                        } else {
                            topicContentAdapter.addData(topicList);
                            contentLayoutBinding.recyclerView.loadMoreComplete();
                        }
                        if (topicListType == 1) {
                            tvMetaverseMoreTopic.setVisibility(View.VISIBLE);
                        } else {
                            if (topicList.size() < viewModel.getPageSize()) {
                                contentLayoutBinding.recyclerView.loadMoreEnd();
                            }
                        }
                    } else {
                        if (viewModel.getPageNumber() == 1) {
                            if (contentLayoutBinding.recyclerView.isRefreshing()) {
                                contentLayoutBinding.recyclerView.setRefreshing(false);
                            }

                            switch (topicListType) {
                                case 1:
                                    showEmptyView();
                                    break;
                                case 2:
                                    showEmptyView("看到喜欢的兴趣话题点击【喜欢】\n以后搞笑星球会推荐给你相关内容", R.mipmap.empty_bg_my_topic_like);
                                    break;
                                case 3:
                                    showEmptyView("可以把不喜欢的兴趣话题【屏蔽】\n以后就不会收到相关内容了", R.mipmap.empty_bg_my_topic_reject);
                                    break;
                                default:
                                    break;
                            }

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

    public void refreshData() {
        currentPageNumber = viewModel.getPageNumber();
        viewModel.setPageNumber(1);
        getTopicList();
    }
}
