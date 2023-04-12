package com.sortinghat.funny.ui.home;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.HomeReportAdapter;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.HomeReportBean;
import com.sortinghat.funny.databinding.ActivityReportBinding;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.viewmodel.ReportViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends BaseActivity<ReportViewModel, ActivityReportBinding> {

    private HomeReportAdapter homeReportAdapter;
    private List<HomeReportBean.ListBean> beanList = new ArrayList<>();
    private long postId, commentId;
    private String data = " { \"list\": [" +
            "{ \"dataString\":\"违规\",\"id\":10,\"isCheck\":false,\"isTitle\":true }," +//违规
            "{ \"dataString\":\"低俗色情\",\"id\":11,\"isCheck\":false,\"isTitle\":false }," +
            "{ \"dataString\":\"侮辱谩骂\",\"id\":12,\"isCheck\":false,\"isTitle\":false }," +
            "{ \"dataString\":\"违法行为\",\"id\":13,\"isCheck\":false,\"isTitle\":false }," +
            "{ \"dataString\":\"政治敏感\",\"id\":14,\"isCheck\":false,\"isTitle\":false }," +
            "{ \"dataString\":\"垃圾广告\",\"id\":15,\"isCheck\":false,\"isTitle\":false }," +
            "{ \"dataString\":\"未成年人有害行为\",\"id\":16,\"isCheck\":false,\"isTitle\":false }," +
            "{ \"dataString\":\"侵权\",\"id\":17,\"isCheck\":false,\"isTitle\":false }," +
            "{ \"dataString\":\"侵犯他人肖像\",\"id\":18,\"isCheck\":false,\"isTitle\":false }," +
            "{ \"dataString\":\"侵犯他人隐私\",\"id\":19,\"isCheck\":false,\"isTitle\":false }," +
            "{ \"dataString\":\"不友善\",\"id\":20,\"isCheck\":false,\"isTitle\":true }," +//不友善
            "{ \"dataString\":\"不尊重女性\",\"id\":21,\"isCheck\":false,\"isTitle\":false }," +
            "{ \"dataString\":\"引战、制造冲突\",\"id\":22,\"isCheck\":false,\"isTitle\":false }," +
            "{ \"dataString\":\"恶意诋毁\",\"id\":23,\"isCheck\":false,\"isTitle\":false }," +
            "{ \"dataString\":\"刷水/骗赞\",\"id\":24,\"isCheck\":false,\"isTitle\":false }," +
            "{ \"dataString\":\"感官不适\",\"id\":25,\"isCheck\":false,\"isTitle\":false }," +
            "{ \"dataString\":\"其他\",\"id\":30,\"isCheck\":false,\"isTitle\":true }" +//不友善
            "]}";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_report;
    }

    @Override
    protected void initViews() {
        if (getIntent() != null) {
            postId = getIntent().getLongExtra("postId", 0);
            commentId = getIntent().getLongExtra("commentId", 0);
        }
        initTitleBar("举报");
        titleBarBinding.tvRightText.setVisibility(View.VISIBLE);
        titleBarBinding.tvRightText.setText("完成");
        titleBarBinding.tvRightText.setTextColor(getResources().getColor(R.color.light_orange));
        titleBarBinding.tvRightText.setOnClickListener(quickClickListener);
        initViewPagerAdapter();
    }

    private void initViewPagerAdapter() {
        beanList = new Gson().fromJson(data, HomeReportBean.class).getList();
        Log.e("reportString", beanList.get(0).getDataString() + beanList.get(0).isCheck() + beanList.get(0).isTitle());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        contentLayoutBinding.recyclerview.setLayoutManager(gridLayoutManager);
        homeReportAdapter = new HomeReportAdapter(this, beanList);
        contentLayoutBinding.recyclerview.setAdapter(homeReportAdapter);
    }

    @Override
    protected void initData() {

    }

    private int getSendData() {
        int id = 0;
        for (HomeReportBean.ListBean bean : beanList) {
            if (!bean.isTitle() && bean.isCheck()) {
                id = bean.getId();
            }
        }
        return id;
    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.tv_right_text:
                    String message = contentLayoutBinding.reportEdit.getText().toString().trim();
                    if (getSendData() <= 0 && (TextUtils.isEmpty(message))) {
                        CommonUtils.showShort("请选择至少一项举报内容");
                        return;
                    }
                    progressDialog = MaterialDialogUtil.showCustomProgressDialog(ReportActivity.this);
                    sendReportContent(message);
                    LogUtils.d("report---", "kind：" + getSendData() + "-message:" + message);
                    break;
            }
        }
    };

    private void sendReportContent(String message) {
        if (postId != 0 && commentId == 0) {
            viewModel.sendReportContent(postId, message, "", getSendData()).observe(ReportActivity.this, (Observer<BaseResultBean<Object>>) resultBean -> {
                closeProgressDialog();
                if (resultBean != null) {
                    if (resultBean.getCode() == 0) {
                        finish();
                    }
                }
            });
        } else if (commentId != 0 && postId == 0) {
            viewModel.sendCommentReportContent(commentId, message, getSendData()).observe(ReportActivity.this, (Observer<BaseResultBean<Object>>) resultBean -> {
                closeProgressDialog();
                if (resultBean != null) {
                    if (resultBean.getCode() == 0) {
                        finish();
                    }
                }
            });
        }
    }

    public static void startActivity(Context mContext, long postId) {
        Intent intent = new Intent(mContext, ReportActivity.class);
        intent.putExtra("postId", postId);
        ActivityUtils.startActivity(intent);
    }
}

