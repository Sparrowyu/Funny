package com.sortinghat.funny.ui.message;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.base.BasePullDownRefreshActivity;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.SystemNoticeMessageDetailsAdapter;
import com.sortinghat.funny.bean.ReviewAndReplyMessageBean;
import com.sortinghat.funny.bean.event.SystemNoticeBean;
import com.sortinghat.funny.databinding.ActivityMessageDetailsBinding;
import com.sortinghat.funny.ui.my.MoodReportActivity;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.viewmodel.MessageViewModel;

import java.util.List;

/**
 * Created by wzy on 2021/7/19
 */
public class SystemNoticeMessageDetailsActivity extends BasePullDownRefreshActivity<MessageViewModel, ActivityMessageDetailsBinding> {

    private SystemNoticeMessageDetailsAdapter systemNoticeMessageDetailsAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_details;
    }

    @Override
    protected void initViews() {
        initTitleBar("系统通知");
        initAdapter();
    }

    private void initAdapter() {
        contentLayoutBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        systemNoticeMessageDetailsAdapter = new SystemNoticeMessageDetailsAdapter();
        contentLayoutBinding.recyclerView.setAdapter(systemNoticeMessageDetailsAdapter);
        contentLayoutBinding.recyclerView.setEmptyView(getEmptyView());
        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
    }

    @Override
    protected void setListener() {
        contentLayoutBinding.recyclerView.setOnItemClickListener((v, position) -> {
            SystemNoticeBean systemNoticeBean = systemNoticeMessageDetailsAdapter.getItemData(position);

            if (systemNoticeBean != null && systemNoticeBean.getContent() != null) {
                if (systemNoticeBean.getContent().getSubType().equals("achievementLaugh")){
                    MoodReportActivity.starActivity(mContext, "message");
                }
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
            getSystemNoticeMessageDetailsData();
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
            getSystemNoticeMessageDetailsData();
        });
    }

    @Override
    protected void initData() {
        showLoading();
        getSystemNoticeMessageDetailsData();
    }

    @Override
    protected void onReload() {
        if (!NetworkUtils.isConnected()) {
            showError();
            CommonUtils.showShort(getString(R.string.network_connect_fail_prompt));
            return;
        }
        getSystemNoticeMessageDetailsData();
    }

    private void getSystemNoticeMessageDetailsData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "sysBroadcast");
        RequestParamUtil.addCommonRequestParam(jsonObject);
        RequestParamUtil.addPagingParam(jsonObject, viewModel.getPageNumber(), viewModel.getPageSize());

        viewModel.getSystemNoticeMessageDetailsData(jsonObject.toString()).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    if (isLoadMoreFail) {
                        isLoadMoreFail = false;
                    }
                    List<SystemNoticeBean> systemNoticeMessageList = resultBean.getData();
                    //"content": {
                    //			"applyReason": "很遗憾，你发布的作品【test111#搞笑段子】不符合社区规范，未通过审核，如有异议，可以通过微信申诉:GXXQ818。",
                    //			"postId": 3265514656768,
                    //			"title": "审核提醒",
                    //			"type": "postApplyRefuse"
                    //		},
                    //		"crateAt": 1637844051402,
                    //		"isRead": true,
                    //		"type": "applySys",
                    //		"userId": 2580984123136

                 String details =  "新增下载功能，用户可以点击分享按钮，保存视频或者图文到本地相册";

                    if (systemNoticeMessageList != null && systemNoticeMessageList.isEmpty()) {
                        SystemNoticeBean systemNoticeBean = new SystemNoticeBean();
                        SystemNoticeBean.ContentBean contentBean = new SystemNoticeBean.ContentBean();
                        systemNoticeBean.setCrateAt(1649994380402l);//163 784 4051 402
                        contentBean.setTitle("下载功能通知");
                        contentBean.setContent(details);
                        systemNoticeBean.setContent(contentBean);
                        systemNoticeMessageList.add(systemNoticeBean);
                    }

                    if (systemNoticeMessageList != null && !systemNoticeMessageList.isEmpty()) {
                        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
                        contentLayoutBinding.recyclerView.setLoadMoreEnabled(true);
                        if (viewModel.getPageNumber() == 1) {
                            systemNoticeMessageDetailsAdapter.setNewData(systemNoticeMessageList);
                            showContentView();
                        } else {
                            systemNoticeMessageDetailsAdapter.addData(systemNoticeMessageList);
                            contentLayoutBinding.recyclerView.loadMoreComplete();
                        }
                        if (systemNoticeMessageList.size() < viewModel.getPageSize()) {
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
