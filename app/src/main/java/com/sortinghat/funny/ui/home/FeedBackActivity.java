package com.sortinghat.funny.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.lifecycle.Observer;

import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.base.NoViewModel;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.databinding.ActivityFeedbackBinding;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.viewmodel.HomeViewModel;

public class FeedBackActivity extends BaseActivity<HomeViewModel, ActivityFeedbackBinding> {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void initViews() {
        initTitleBar("意见反馈");
        titleBarBinding.vDividerLine.setVisibility(View.VISIBLE);

        contentLayoutBinding.etSign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (contentLayoutBinding.etSign.getText().toString().trim().length() >= 1) {
                    contentLayoutBinding.tvSure.setEnabled(true);
                    contentLayoutBinding.tvSure.setBackgroundResource(R.drawable.click_can_orange_bt_bg);
                    contentLayoutBinding.tvSure.setTextColor(getResources().getColor(R.color.white));
                } else {
                    contentLayoutBinding.tvSure.setEnabled(false);
                    contentLayoutBinding.tvSure.setBackgroundResource(R.drawable.click_e7_corner4_bt_bg);
                    contentLayoutBinding.tvSure.setTextColor(getResources().getColor(R.color.color_333333));
                }
            }
        });
    }

    @Override
    protected void initData() {
        contentLayoutBinding.tvFeedbackWx.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvFeedbackWxCopy.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvFeedbackQq.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvFeedbackQqCopy.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvCancel.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvSure.setOnClickListener(quickClickListener);
    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.tv_feedback_wx:
                case R.id.tv_feedback_wx_copy:
                    ClipboardManager cmb1 = (ClipboardManager) FunnyApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb1.setText("GXXQ818");
                    CommonUtils.showShort("已复制微信号");
                    break;
                case R.id.tv_feedback_qq:
                case R.id.tv_feedback_qq_copy:
                    ClipboardManager cmb2 = (ClipboardManager) FunnyApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb2.setText("675082738");
                    CommonUtils.showShort("已复制QQ群号");
                    break;
                case R.id.tv_cancel:
                    ConstantUtil.hideSoftKeyboardFromActivity(FeedBackActivity.this, contentLayoutBinding.etSign);
                    finish();
                    break;
                case R.id.tv_sure:
                    String content = contentLayoutBinding.etSign.getText().toString().trim();
                    if ((TextUtils.isEmpty(content))) {
                        CommonUtils.showShort("请输入评价");
                        return;
                    }
                    contentLayoutBinding.etSign.setText("");
//                    ConstantUtil.createUmEvent("store_Evaluation_opinion_btn_submit");//引导评价弹窗-吐槽页-提交

//            用以前的帖子吐槽
                    viewModel.sendReportContent(0, "", content, 0, "").observe(FeedBackActivity.this, (Observer<BaseResultBean<Object>>) resultBean -> {
                        if (resultBean != null) {
                            ConstantUtil.hideSoftKeyboardFromActivity(FeedBackActivity.this, contentLayoutBinding.etSign);
                            if (resultBean.getCode() == 0) {
                                CommonUtils.showShort("感谢你的反馈");
                            }
                        }
                    });
                    break;
            }
        }
    };
}

