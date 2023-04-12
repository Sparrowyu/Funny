package com.sortinghat.funny.ui.my;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.view.GridSpacingItemDecoration;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.TaskPackageAdapter;
import com.sortinghat.funny.bean.TaskPackageItemBean;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivityStorePackageBinding;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.viewmodel.TaskCentralViewModel;

import java.util.List;

public class PackageFunnyActivity extends BaseActivity<TaskCentralViewModel, ActivityStorePackageBinding> {
    TaskPackageAdapter taskAdapter;
    private int pageNum = 1;
    private int singleSize = 12;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_store_package;
    }

    @Override
    protected void initViews() {
        initTitleBar("背包");
        titleBarBinding.tvRightText.setVisibility(View.VISIBLE);
        titleBarBinding.tvRightText.setText("编辑");
        titleBarBinding.vDividerLine.setVisibility(View.GONE);
//        titleBarBinding.tvRightText.setTextColor(getResources().getColor(R.color.light_orange));
        titleBarBinding.tvRightText.setOnClickListener(quickClickListener);

//初始化recyclerView
        contentLayoutBinding.recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        contentLayoutBinding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, SizeUtils.dp2px(8), false));
        taskAdapter = new TaskPackageAdapter(mContext, 100);
        contentLayoutBinding.recyclerView.setAdapter(taskAdapter);
        contentLayoutBinding.recyclerView.setEmptyView(getEmptyView());
        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
        contentLayoutBinding.recyclerView.setOnLoadMoreListener(() -> {
            pageNum++;
            getDataList();
        }, 100);
        taskAdapter.setOnItemClickListener((position, hadDressIndex, clickType, status) -> {
            if (taskAdapter != null && taskAdapter.getData().size() > position) {
                if (clickType == 0) {
                    getDelPackageIcon(taskAdapter.getData().get(position).getProId(), position);
                } else {
                    setPackageIcon(taskAdapter.getData().get(position).getProId(), position, status, hadDressIndex);
                }
            } else {
                CommonUtils.showShort("请退出重试");
            }
        });

    }

    @Override
    protected void initData() {
        getDataList();

        String path = "user_profile";
        if (null != getIntent()) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("from"))) {
                path = getIntent().getStringExtra("from");
            }
        }
        //1:user_profile 2:store 两个进入方式
        viewModel.setAppUnifyLog("entry_backpack_succ", path, this).observe(this, resultBean -> {
        });
    }

    private void getDelPackageIcon(int proId, int position) {
        progressDialog = MaterialDialogUtil.showCustomProgressDialog(PackageFunnyActivity.this);
        viewModel.getDelPackageIcon(proId).observe(this, resultBean -> {
            closeProgressDialog();
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    taskAdapter.removeData(position);
                    if (null != resultBean.getData() && !TextUtils.isEmpty(resultBean.getData().toString())) {
                        CommonUtils.showShort(resultBean.getData().toString());
                    }
                }
            }
        });
    }

    private void setPackageIcon(int proId, int position, int status, int hadDressIndex) {
        progressDialog = MaterialDialogUtil.showCustomProgressDialog(PackageFunnyActivity.this);
        viewModel.setPackageIcon(proId, status).observe(this, resultBean -> {
            closeProgressDialog();

            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    String pendantUrl = "";
                    if (null != resultBean.getData().getPendantUrl() && !TextUtils.isEmpty(resultBean.getData().getPendantUrl())) {
                        pendantUrl = resultBean.getData().getPendantUrl();
                    }

                    RxBus.getDefault().post(RxCodeConstant.TASK_TO_UPDATE_STORE_USER_BOX, pendantUrl);
                    RxBus.getDefault().post(RxCodeConstant.TASK_TO_UPDATE_MINE_USER_BOX, pendantUrl);
                    pageNum = 1;
//                    taskAdapter.clear();
//                    getDataList();

                    if (hadDressIndex < taskAdapter.getData().size() && hadDressIndex != -1) {
                        if (taskAdapter.getData().get(hadDressIndex).getStatus() == 1) {
                            taskAdapter.getData().get(hadDressIndex).setStatus(0);
                            taskAdapter.notifyItemChanged(hadDressIndex);
                        }
                    }

                    if (position < taskAdapter.getData().size()) {
                        taskAdapter.getData().get(position).setStatus(status);
                        taskAdapter.notifyItemChanged(position);
                    }

                    if (!TextUtils.isEmpty(resultBean.getData().getToast())) {
                        CommonUtils.showShort(resultBean.getData().getToast());
                    }
                }
            }
        });
    }

    private void getDataList() {
        viewModel.getPackageTaskList(pageNum, singleSize).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    List<TaskPackageItemBean> beanList = resultBean.getData();
                    if (beanList != null && !beanList.isEmpty() && beanList.size() > 0) {
                        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
                        showContentView();
                        taskAdapter.addData(beanList);
                        contentLayoutBinding.recyclerView.loadMoreComplete();
                        if (beanList.size() < singleSize) {
                            contentLayoutBinding.recyclerView.loadMoreEnd();
                        }
                    } else {
                        contentLayoutBinding.recyclerView.loadMoreEnd();
                        if (pageNum == 1 && beanList.size() == 0) {
                            showEmptyView("还没有获得道具，快去搞笑商城看看吧！");
                            contentLayoutBinding.recyclerView.setEmptyViewEnabled(true);
                            contentLayoutBinding.recyclerView.setLoadMoreEnabled(false);
                        }
                    }
                }
            }
        });
    }

    private boolean currentIsDel = false;//当前编辑状态

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.tv_right_text://编辑

                    List<TaskPackageItemBean> beanList = taskAdapter.getData();
                    if (null != beanList && beanList.size() > 0) {
                        boolean setState = currentIsDel;
                        if (setState) {
                            titleBarBinding.tvRightText.setText("编辑");
                        } else {
                            titleBarBinding.tvRightText.setText("取消");
                        }
                        for (int i = 0; i < beanList.size(); i++) {
                            beanList.get(i).setClicked(!setState);
                        }
                        currentIsDel = !setState;
                        taskAdapter.setNewData(beanList);
                        LogUtils.d("ForbidTopics---", "-message:");
                    }
                    break;

            }
        }
    };

    public static void starActivity(Context mContext, String from) {
        Intent intent = new Intent(mContext, PackageFunnyActivity.class);
        intent.putExtra("from", from);
        ActivityUtils.startActivity(intent);
    }

}

