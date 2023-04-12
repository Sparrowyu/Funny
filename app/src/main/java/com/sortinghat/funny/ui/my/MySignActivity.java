package com.sortinghat.funny.ui.my;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivityMySignBinding;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.viewmodel.MyEditInformationViewModel;

public class MySignActivity extends BaseActivity<MyEditInformationViewModel, ActivityMySignBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_sign;
    }

    @Override
    protected void initViews() {
        initTitleBar("签名");
        if (null != getIntent()) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("hint"))) {
                contentLayoutBinding.etSign.setText(getIntent().getStringExtra("hint"));
            }
        }
        contentLayoutBinding.tvSure.setOnClickListener(quickClickListener);
        ConstantUtil.showSoftInputFromWindow(this, contentLayoutBinding.etSign);
        ConstantUtil.setEditTextInputSpace(contentLayoutBinding.etSign);
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
                    contentLayoutBinding.tvSure.setBackgroundResource(R.drawable.click_no_gray_bt_bg);
                    contentLayoutBinding.tvSure.setTextColor(getResources().getColor(R.color.color_666666));
                }
            }
        });

    }

    @Override
    protected void initData() {

    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.tv_sure:
                    String slogan = contentLayoutBinding.etSign.getText().toString().trim();
                    if ((TextUtils.isEmpty(slogan))) {
                        CommonUtils.showShort("请输入签名");
                        return;
                    }
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("slogan", slogan);
                    progressDialog = MaterialDialogUtil.showCustomProgressDialog(MySignActivity.this);
                    viewModel.completeUserInfo(jsonObject).observe(MySignActivity.this, resultBean -> {
                        closeProgressDialog();
                        if (resultBean != null) {
                            if (resultBean.getCode() == 0) {
                                resultBean.getData().setSlogan(slogan);
                                RxBus.getDefault().post(RxCodeConstant.MY_COMPLETEMENT_INFO, resultBean.getData());
                                CommonUtils.showLong("信息审核中，请耐心等待");
                                finish();
                            }
                        }
                    });
                    LogUtils.d("myeditname---", "-message:" + slogan);
                    break;
            }
        }
    };

    public static void starActivity(Context mContext, String hint) {
        Intent intent = new Intent(mContext, MySignActivity.class);
        intent.putExtra("hint", hint);
        ActivityUtils.startActivity(intent);
    }

}

