package com.sortinghat.funny.ui.my;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.TaskRankingAdapter;
import com.sortinghat.funny.bean.TaskRankListBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.databinding.ActivityTaskRankBinding;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.viewmodel.TaskCentralViewModel;

import java.util.List;

public class RankingFunnyActivity extends BaseActivity<TaskCentralViewModel, ActivityTaskRankBinding> {
    TaskRankingAdapter taskAdapter;
    private TaskRankHeaderView rankHeaderView;
    private TaskRankFooterView rankFooterView;
    private int pageNum = 1;
    private int singleSize = 12;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_task_rank;
    }

    @Override
    protected void initViews() {
        initTitleBar("富豪榜");
        ConstantUtil.createUmEvent("task_top");//任务-排行榜-pv
        titleBarBinding.vDividerLine.setVisibility(View.GONE);
        initAdapter();
    }

    private void initAdapter() {
        contentLayoutBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskRankingAdapter(mContext);
        contentLayoutBinding.recyclerView.setAdapter(taskAdapter);

        rankHeaderView = new TaskRankHeaderView(this);
        rankFooterView = new TaskRankFooterView(this);

        rankHeaderView.setListener(quickClickListener);
        rankFooterView.setListener(quickClickListener);

        contentLayoutBinding.recyclerView.addHeaderView(rankHeaderView);
        contentLayoutBinding.recyclerView.addFooterView(rankFooterView);

        contentLayoutBinding.recyclerView.setEmptyView(getEmptyView());
        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
        contentLayoutBinding.recyclerView.setOnLoadMoreListener(() -> {
            pageNum++;
            getDataList();
        }, 100);
        taskAdapter.setOnItemClickListener((position, userId, clickType) -> {
            if (taskAdapter != null && taskAdapter.getData().size() > position) {

                if (userId == SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id")) {
                   finish();
                } else {
                    MyOtherUserInfoActivity.starActivity(mContext, userId, 0);
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
        //我的排行榜
        viewModel.getMyRank().observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    rankHeaderView.initData(resultBean.getData());
                }
            }
        });
//        //1:user_profile 2:store 两个进入方式
//        viewModel.setAppUnifyLog("entry_backpack_succ", path, this).observe(this, resultBean -> {
//        });
    }


    private void getDataList() {
        viewModel.getRankList(pageNum, singleSize).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    List<TaskRankListBean> beanList = resultBean.getData();
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

                    break;

            }
        }
    };

    public static void starActivity(Context mContext, String from) {
        Intent intent = new Intent(mContext, RankingFunnyActivity.class);
        intent.putExtra("from", from);
        ActivityUtils.startActivity(intent);
    }

}

