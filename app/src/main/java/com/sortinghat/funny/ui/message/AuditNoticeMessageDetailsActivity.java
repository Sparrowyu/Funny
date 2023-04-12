package com.sortinghat.funny.ui.message;

import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.base.BasePullDownRefreshActivity;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.AuditNoticeMessageDetailsAdapter;
import com.sortinghat.funny.bean.AuditNoticeMessageBean;
import com.sortinghat.funny.databinding.ActivityMessageDetailsBinding;
import com.sortinghat.funny.ui.my.MyEditInformationActivity;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.viewmodel.MessageViewModel;

import java.util.List;

/**
 * Created by wzy on 2021/7/19
 */
public class AuditNoticeMessageDetailsActivity extends BasePullDownRefreshActivity<MessageViewModel, ActivityMessageDetailsBinding> {

    private AuditNoticeMessageDetailsAdapter auditNoticeMessageDetailsAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_details;
    }

    @Override
    protected void initViews() {
        initTitleBar("审核通知");
        initAdapter();
    }

    private void initAdapter() {
        contentLayoutBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        auditNoticeMessageDetailsAdapter = new AuditNoticeMessageDetailsAdapter();
        contentLayoutBinding.recyclerView.setAdapter(auditNoticeMessageDetailsAdapter);
        contentLayoutBinding.recyclerView.setEmptyView(getEmptyView());
        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
    }

    @Override
    protected void setListener() {
        contentLayoutBinding.recyclerView.setOnItemClickListener((v, position) -> {
            AuditNoticeMessageBean auditNoticeMessageBean = auditNoticeMessageDetailsAdapter.getItemData(position);
            if (auditNoticeMessageBean == null || auditNoticeMessageBean.getContent() == null) {
                return;
            }
            switch (TextUtils.isEmpty(auditNoticeMessageBean.getContent().getType()) ? "" : auditNoticeMessageBean.getContent().getType()) {
                case "userIconApplyRefuse":
                case "userNicknameApplyRefuse":
                case "userSloganApplyRefuse":
                    MyEditInformationActivity.starActivity(this, 1);
                    break;
                default:
                    break;
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
            getAuditNoticeMessageDetailsData();
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
            getAuditNoticeMessageDetailsData();
        });
    }

    @Override
    protected void initData() {
        showLoading();
        getAuditNoticeMessageDetailsData();
    }

    @Override
    protected void onReload() {
        if (!NetworkUtils.isConnected()) {
            showError();
            CommonUtils.showShort(getString(R.string.network_connect_fail_prompt));
            return;
        }
        getAuditNoticeMessageDetailsData();
    }

    private void getAuditNoticeMessageDetailsData() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "applySys");
        RequestParamUtil.addCommonRequestParam(jsonObject);
        RequestParamUtil.addPagingParam(jsonObject, viewModel.getPageNumber(), viewModel.getPageSize());

        viewModel.getAuditNoticeMessageDetailsData(jsonObject.toString()).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    if (isLoadMoreFail) {
                        isLoadMoreFail = false;
                    }
                    List<AuditNoticeMessageBean> auditNoticeMessageList = resultBean.getData();
                    if (auditNoticeMessageList != null && !auditNoticeMessageList.isEmpty()) {
                        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
                        contentLayoutBinding.recyclerView.setLoadMoreEnabled(true);
                        if (viewModel.getPageNumber() == 1) {
                            auditNoticeMessageDetailsAdapter.setNewData(auditNoticeMessageList);
                            showContentView();
                        } else {
                            auditNoticeMessageDetailsAdapter.addData(auditNoticeMessageList);
                            contentLayoutBinding.recyclerView.loadMoreComplete();
                        }
                        if (auditNoticeMessageList.size() < viewModel.getPageSize()) {
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
