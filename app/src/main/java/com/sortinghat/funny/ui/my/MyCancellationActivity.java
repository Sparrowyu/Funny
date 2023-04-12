package com.sortinghat.funny.ui.my;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.CompoundButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CheckNetwork;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.ConstantWeb;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivityMyCancellationBinding;
import com.sortinghat.funny.interfaces.CommonConfirmDialogListener;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.DateUtil;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.viewmodel.LoginViewModel;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class MyCancellationActivity extends BaseActivity<LoginViewModel, ActivityMyCancellationBinding> {
    private int currentSecond;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (currentSecond > 0) {
                        ConstantUtil.showSoftInputFromWindow(MyCancellationActivity.this, contentLayoutBinding.etSmsCode);
                        contentLayoutBinding.tvGetSmsCode.setText("获取验证码" + " (" + currentSecond + "s)");
                        contentLayoutBinding.tvGetSmsCode.setEnabled(false);
                        contentLayoutBinding.tvGetSmsCode.setTextColor(getResources().getColor(R.color.color_d7d7d7));
                        currentSecond--;
                        handler.sendEmptyMessageDelayed(0, 1000);
                    } else {
                        contentLayoutBinding.tvGetSmsCode.setText("获取验证码");
                        contentLayoutBinding.tvGetSmsCode.setEnabled(true);
                        contentLayoutBinding.tvGetSmsCode.setTextColor(getResources().getColor(R.color.tx_blue));
                    }
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_cancellation;
    }

    @Override
    protected void initViews() {
        initTitleBar("注销账号");
        initListener();
        //注销须知
        String stringClick = this.getString(R.string.privacy_cancellation_detail_click);
        SpannableString msp = new SpannableString(stringClick);
        ConstantUtil.setClickSpanToWeb(mContext, msp, 7, msp.length(), "注销协议", ConstantWeb.PRIVACY_CANCELLATION);
        contentLayoutBinding.tvFirstNotify.setText(this.getString(R.string.privacy_protocol_dis_detail));
        contentLayoutBinding.tvFirstNotify.setText(msp);
        contentLayoutBinding.tvFirstNotify.setMovementMethod(LinkMovementMethod.getInstance());
        contentLayoutBinding.tvFirstNotify.setHighlightColor(this.getResources().getColor(android.R.color.transparent));
    }

    private void initListener() {
        initSms();
        contentLayoutBinding.tvFirstNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    contentLayoutBinding.tvFirst.setEnabled(true);
                    contentLayoutBinding.tvFirst.setBackgroundResource(R.drawable.click_can_orange_bt_bg);
                    contentLayoutBinding.tvFirst.setTextColor(getResources().getColor(R.color.white));
                } else {
                    contentLayoutBinding.tvFirst.setEnabled(false);
                    contentLayoutBinding.tvFirst.setBackgroundResource(R.drawable.click_no_gray_bt_bg);
                    contentLayoutBinding.tvFirst.setTextColor(getResources().getColor(R.color.color_666666));

                }
            }
        });
        contentLayoutBinding.tvFirst.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvCancelSure.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvGetSmsCode.setOnClickListener(quickClickListener);
        contentLayoutBinding.editPhoneClearImg.setOnClickListener(quickClickListener);

    }

    private void initSms() {
        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    LogUtils.e("loginAc:", "回调完成");
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        LogUtils.e("loginAc:", "提交验证码成功" + data.toString());
                        //提交验证码成功
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        LogUtils.e("loginAc:", "获取验证码成功" + data.toString());
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            currentSecond = 60;
                            handler.sendEmptyMessage(0);
                        }
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {
                    LogUtils.e("loginAc:", "回调异常" + data.toString());
                    ((Throwable) data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }


    @Override
    protected void initData() {
        contentLayoutBinding.etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (count > 0) {
                    contentLayoutBinding.editPhoneClearImg.setVisibility(View.VISIBLE);
                } else {
                    contentLayoutBinding.editPhoneClearImg.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (contentLayoutBinding.etPhone.getText().toString().length() == 11) {
                    contentLayoutBinding.tvGetSmsCode.setEnabled(true);
                    contentLayoutBinding.tvGetSmsCode.setTextColor(getResources().getColor(R.color.tx_blue));
                } else {
                    contentLayoutBinding.tvGetSmsCode.setEnabled(false);
                    contentLayoutBinding.tvGetSmsCode.setTextColor(getResources().getColor(R.color.color_d7d7d7));
                }
            }
        });
        contentLayoutBinding.etSmsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (contentLayoutBinding.etSmsCode.getText().toString().length() >= 4) {
                    contentLayoutBinding.tvCancelSure.setEnabled(true);
                    contentLayoutBinding.tvCancelSure.setTextColor(getResources().getColor(R.color.white));
                    contentLayoutBinding.tvCancelSure.setBackgroundResource(R.drawable.click_can_orange_bt_bg);
                } else {
                    contentLayoutBinding.tvCancelSure.setEnabled(false);
                    contentLayoutBinding.tvCancelSure.setTextColor(getResources().getColor(R.color.color_666666));
                    contentLayoutBinding.tvCancelSure.setBackgroundResource(R.drawable.click_no_gray_bt_bg);
                }
            }
        });
    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.tv_first:
                    showCancellationNoti();
                    break;
                case R.id.tv_get_sms_code:
                    String phone = contentLayoutBinding.etPhone.getText().toString();
                    if (phone.length() == 11) {
                        if (!CheckNetwork.isNetworkConnected(MyCancellationActivity.this)) {
                            CommonUtils.showShort("请检查网络");
                            return;
                        }
                        CommonUtils.showShort("验证码发送成功");
                        SMSSDK.getVerificationCode(getString(R.string.sms_temp_code), "86", phone);
                        contentLayoutBinding.tvGetSmsCode.setEnabled(false);
                        contentLayoutBinding.tvGetSmsCode.setTextColor(getResources().getColor(R.color.color_d7d7d7));
                    }
                    break;
                case R.id.tv_cancel_sure://注销
                    String mobile = contentLayoutBinding.etPhone.getText().toString();
                    String verifyCode = contentLayoutBinding.etSmsCode.getText().toString();
                    if (mobile.length() == 11 && verifyCode.length() >= 4) {
                        viewModel.setUserDelete(mobile, verifyCode).observe(MyCancellationActivity.this, resultBean -> {
                            if (resultBean != null) {
                                if (resultBean.getCode() == 0) {
                                    visitorsLogin();
                                } else {
                                    if (resultBean.getMsg() != null && !TextUtils.isEmpty(resultBean.getMsg())) {
                                        CommonUtils.showShort("" + resultBean.getMsg());
                                    } else {
                                        CommonUtils.showShort("操作失败请重试");
                                    }

                                }
                            }
                        });
                    } else {
                        CommonUtils.showShort("请输入正确的手机号或验证码");
                    }
                    break;
            }
        }
    };

    private void showCancellationNoti() {
        String message = "账号注销后不可找回，若审核通过，将在15天后注销，如需放弃，15天内再次登录可根据提示放弃注销。";
        MaterialDialog logOutDialog = MaterialDialogUtil.showCustomCommonConfirmDialog(mContext, message, new CommonConfirmDialogListener() {
            @Override
            public void onDialogCancel() {
            }

            @Override
            public void onDialogSure() {
//                 CommonUtils.showShort("确定");
                contentLayoutBinding.cancelFirstPage.setVisibility(View.GONE);
                contentLayoutBinding.cancelSecondGetcodePage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void visitorsLogin() {
        progressDialog = MaterialDialogUtil.showCustomProgressDialog(MyCancellationActivity.this);
        viewModel.getLoginId(0).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    closeProgressDialog();
                    CommonUtils.showShort("已注销");
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_id", resultBean.getData().getUserBase().getId());
                    ConstantUtil.setAlias(this, resultBean.getData().getUserBase().getId());
                    //user_status-0：游客 1：注册成功 2:账户已经注销
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_status", resultBean.getData().getUserBase().getStatus());

                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("authToken", resultBean.getData().getAuthToken());
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("longTermToken", resultBean.getData().getLongTermToken());
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("token_days", resultBean.getData().getDays());
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("token_date", DateUtil.getTodayDateStringToServer());

                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_bind_phone", 0);

                    contentLayoutBinding.cancelSecondGetcodePage.setVisibility(View.GONE);
                    contentLayoutBinding.cancelThirdPage.setVisibility(View.VISIBLE);
                    RxBus.getDefault().post(RxCodeConstant.UPDATE_MYFRAGMENT, 1);

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

}

