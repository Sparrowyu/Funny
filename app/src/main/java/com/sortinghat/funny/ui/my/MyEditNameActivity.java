package com.sortinghat.funny.ui.my;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;


import com.blankj.utilcode.util.ActivityUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivityMyEditNameBinding;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.viewmodel.MyEditInformationViewModel;

public class MyEditNameActivity extends BaseActivity<MyEditInformationViewModel, ActivityMyEditNameBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_edit_name;
    }

    @Override
    protected void initViews() {
        initTitleBar("昵称");
        if (null != getIntent()) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("nickName"))) {
                contentLayoutBinding.etName.setText(getIntent().getStringExtra("nickName"));
            }
        }
        contentLayoutBinding.tvSure.setOnClickListener(quickClickListener);
        contentLayoutBinding.ivDel.setOnClickListener(quickClickListener);
        contentLayoutBinding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (contentLayoutBinding.etName.getText().toString().trim().length() >= 1) {
                    contentLayoutBinding.ivDel.setEnabled(true);
                    contentLayoutBinding.ivDel.setVisibility(View.VISIBLE);
                    contentLayoutBinding.tvSure.setEnabled(true);
                    contentLayoutBinding.tvSure.setBackgroundResource(R.drawable.click_can_orange_bt_bg);
                    contentLayoutBinding.tvSure.setTextColor(getResources().getColor(R.color.white));
                } else {
                    contentLayoutBinding.ivDel.setEnabled(false);
                    contentLayoutBinding.ivDel.setVisibility(View.GONE);
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
                case R.id.iv_del:
                    contentLayoutBinding.etName.setText("");
                    break;
                case R.id.tv_sure:
                    String nickname = contentLayoutBinding.etName.getText().toString().trim();
                    if ((TextUtils.isEmpty(nickname))) {
                        CommonUtils.showShort("请输入昵称");
                        return;
                    }
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("nickname", nickname);
                    progressDialog = MaterialDialogUtil.showCustomProgressDialog(MyEditNameActivity.this);
                    viewModel.completeUserInfo(jsonObject).observe(MyEditNameActivity.this, resultBean -> {
                        closeProgressDialog();
                        if (resultBean != null) {
                            if (resultBean.getCode() == 0) {
                                resultBean.getData().setNickname(nickname);
                                RxBus.getDefault().post(RxCodeConstant.MY_COMPLETEMENT_INFO, resultBean.getData());
                                CommonUtils.showLong("信息审核中，请耐心等待");
                                finish();
                            }
                        }
                    });
                    break;
            }
        }
    };

    public static void starActivity(Context mContext, String nickName) {
        Intent intent = new Intent(mContext, MyEditNameActivity.class);
        intent.putExtra("nickName", nickName);
        ActivityUtils.startActivity(intent);
    }

}

