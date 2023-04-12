package com.sortinghat.funny.ui.home;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.ForbidTopicsAdapter;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.ForbidTopicsBean;
import com.sortinghat.funny.databinding.ActivityForbidTopicsBinding;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class ForbidTopicsActivity extends BaseActivity<HomeViewModel, ActivityForbidTopicsBinding> {
    private String topicIds;
    private String topics;
    private long postId;
    private ForbidTopicsAdapter forbidTopicsAdapter;
    List<ForbidTopicsBean> beanList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forbid_topics;
    }

    @Override
    protected void initViews() {
        initTitleBar("屏蔽话题");
        titleBarBinding.tvRightText.setVisibility(View.VISIBLE);
        titleBarBinding.tvRightText.setText("完成");
        titleBarBinding.vDividerLine.setVisibility(View.VISIBLE);
        titleBarBinding.tvRightText.setTextColor(getResources().getColor(R.color.light_orange));
        titleBarBinding.tvRightText.setOnClickListener(quickClickListener);
        if (getIntent() != null) {
            postId = getIntent().getLongExtra("postId", 0);
            topicIds = getIntent().getStringExtra("topicIds");
            topics = getIntent().getStringExtra("topics");
        }

        initViewPagerAdapter();
    }

    private void initViewPagerAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        contentLayoutBinding.recyclerview.setLayoutManager(linearLayoutManager);
        forbidTopicsAdapter = new ForbidTopicsAdapter(this, beanList);
        contentLayoutBinding.recyclerview.setAdapter(forbidTopicsAdapter);

    }


    @Override
    protected void initData() {

        if (TextUtils.isEmpty(topicIds) || TextUtils.isEmpty(topics) || topicIds.equals("0") || topicIds.equals(",") || topics.equals(",") || topics.equals("#")) {
            return;
        } else {
            try {
                topics = (topics.length() > 0 && topics.startsWith("#")) ? topics.substring(1) : topics;
                String[] array1 = topics.split("#");
                String[] array = topicIds.split(",");
                if (array1.length != array.length) {
                    return;
                }
                for (int i = 0; i < array1.length; i++) {
                    ForbidTopicsBean bean = new ForbidTopicsBean();
                    bean.setTopics(array1[i]);
                    bean.setTopicsId(array[i]);
                    beanList.add(bean);
                }
                if (beanList.size() > 0) {
                    forbidTopicsAdapter.setNewData(beanList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.tv_right_text:
                    String sendTopics = getSendData();

                    if (TextUtils.isEmpty(sendTopics)) {
                        CommonUtils.showShort("请选择至少一项屏蔽内容");
                        return;
                    }
                    progressDialog = MaterialDialogUtil.showCustomProgressDialog(ForbidTopicsActivity.this);
                    viewModel.sendReportContent(postId, "", "", 0, sendTopics).observe(ForbidTopicsActivity.this, (Observer<BaseResultBean<Object>>) resultBean -> {
                        closeProgressDialog();
                        if (resultBean != null) {
                            if (resultBean.getCode() == 0) {
                                finish();
                            }
                        }
                    });
                    LogUtils.d("ForbidTopics---", "kind：" + sendTopics + "-message:");
                    break;
            }
        }
    };

    private String getSendData() {
        String id = "";
        for (ForbidTopicsBean bean : beanList) {
            if (!TextUtils.isEmpty(bean.getTopics()) && bean.isCheck()) {
                id += bean.getTopicsId() + ",";
            }
        }
        return id;
    }

    public static void startActivity(Context mContext, long postId, String topicIds, String topics) {
        Intent intent = new Intent(mContext, ForbidTopicsActivity.class);
        intent.putExtra("postId", postId);
        intent.putExtra("topicIds", topicIds);
        intent.putExtra("topics", topics);
        ActivityUtils.startActivity(intent);
    }
}

