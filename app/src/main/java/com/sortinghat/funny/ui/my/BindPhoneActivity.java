package com.sortinghat.funny.ui.my;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.mob.secverify.SecVerify;
import com.mob.secverify.VerifyCallback;
import com.mob.secverify.common.exception.VerifyException;
import com.mob.secverify.datatype.VerifyResult;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CheckNetwork;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.ConstantWeb;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivityBindPhoneBinding;
import com.sortinghat.funny.databinding.ActivityLoginBinding;
import com.sortinghat.funny.interfaces.CommonConfirmDialogListener;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.viewmodel.LoginViewModel;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class BindPhoneActivity extends BaseActivity<LoginViewModel, ActivityBindPhoneBinding> {
    private int currentSecond;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (currentSecond > 0) {
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
        return R.layout.activity_bind_phone;
    }

    @Override
    protected void initViews() {
        titleBarBinding.ivBack.setImageResource(R.mipmap.title_close_x);
        initTitleBar("绑定手机号");

        contentLayoutBinding.tvGetSmsCode.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvLogin.setOnClickListener(quickClickListener);
        contentLayoutBinding.editPhoneClearImg.setOnClickListener(quickClickListener);

        String stringClick = "已阅读并同意《用户协议》和《隐私政策》";
        SpannableString msp = new SpannableString(stringClick);
        ConstantUtil.setClickSpanToWeb(mContext, msp, 6, 13, "用户协议", ConstantWeb.PRIVACY_POLICY);
        ConstantUtil.setClickSpanToWeb(mContext, msp, 13, 19, "隐私政策", ConstantWeb.PRIVACY_PROTOCOL);
        contentLayoutBinding.tvPrivacy.setText(msp);
        contentLayoutBinding.tvPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
        contentLayoutBinding.tvPrivacy.setHighlightColor(this.getResources().getColor(android.R.color.transparent));

        initSms();
        ConstantUtil.showSoftInputFromWindow(this, contentLayoutBinding.etPhone);
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
                    contentLayoutBinding.tvLogin.setEnabled(true);
                    contentLayoutBinding.tvLogin.setTextColor(getResources().getColor(R.color.white));
                    contentLayoutBinding.tvLogin.setBackgroundResource(R.drawable.click_can_orange_bt_bg);
                } else {
                    contentLayoutBinding.tvLogin.setEnabled(false);
                    contentLayoutBinding.tvLogin.setTextColor(getResources().getColor(R.color.color_666666));
                    contentLayoutBinding.tvLogin.setBackgroundResource(R.drawable.click_no_gray_bt_bg);
                }
            }
        });
    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.edit_phone_clear_img:
                    contentLayoutBinding.etPhone.setText("");
                    break;
                case R.id.tv_get_sms_code:
                    String phone = contentLayoutBinding.etPhone.getText().toString();
                    if (phone.length() == 11) {
                        if (!CheckNetwork.isNetworkConnected(BindPhoneActivity.this)) {
                            CommonUtils.showShort("请检查网络");
                            return;
                        }
                        CommonUtils.showShort("验证码发送成功");
                        SMSSDK.getVerificationCode(getString(R.string.sms_temp_code), "86", phone);
                        contentLayoutBinding.tvGetSmsCode.setEnabled(false);
                        contentLayoutBinding.tvGetSmsCode.setTextColor(getResources().getColor(R.color.color_d7d7d7));
                    }
                    break;
                case R.id.tv_login:
                    if (!contentLayoutBinding.loginCheckbox.isChecked()) {
                        CommonUtils.showShort(getString(R.string.login_agree_click));
                        return;
                    }
                    contentLayoutBinding.etPhone.getText().toString();
                    String verifyCode = contentLayoutBinding.etSmsCode.getText().toString();
                    phone = contentLayoutBinding.etPhone.getText().toString();
                    if (phone.length() == 11 && verifyCode.length() >= 4) {
                        login(phone, verifyCode);
                    } else {
                        CommonUtils.showShort("请输入正确的手机号或验证码");
                    }
                    break;
            }
        }
    };

    private void login(String mobile, String verifyCode) {
        progressDialog = MaterialDialogUtil.showCustomProgressDialog(this);
        viewModel.getBindPhone(mobile, verifyCode).observe(BindPhoneActivity.this, resultBean -> {
            closeProgressDialog();
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    //user_status 0:游客 1:注册成功 2:账户注销中 3:账户已经注销
                    CommonUtils.showShort("绑定成功");
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_bind_phone", 1);
                    finish();
                } else if (resultBean.getCode() == 301) {
                    CommonUtils.showShort("请输入正确的验证码");
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

