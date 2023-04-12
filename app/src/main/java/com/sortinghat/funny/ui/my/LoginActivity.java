package com.sortinghat.funny.ui.my;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.mob.secverify.SecVerify;
import com.mob.secverify.VerifyCallback;
import com.mob.secverify.common.exception.VerifyException;
import com.mob.secverify.datatype.VerifyResult;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.http.HttpUtils;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CheckNetwork;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.MyOwnerUserInfoBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.ConstantWeb;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivityLoginBinding;
import com.sortinghat.funny.interfaces.CommonConfirmDialogListener;
import com.sortinghat.funny.ui.MainActivity;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.DateUtil;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.viewmodel.LoginViewModel;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class LoginActivity extends BaseActivity<LoginViewModel, ActivityLoginBinding> {
    private int currentSecond;
    private UMShareAPI umShareAPI;
    private long beforeUserId;
    private int isNeedSkipHome = 1;
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
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {
        preLogin();
        subscibeRxBus();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isNeedSkipHome = bundle.getInt("isNeedSkipHome");
        }
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        beforeUserId = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id");
        umShareAPI = UMShareAPI.get(LoginActivity.this);
        titleBarBinding.ivBack.setImageResource(R.mipmap.title_close_x);
        titleBarBinding.ivBack.setOnClickListener(view -> finish());

        contentLayoutBinding.editPhoneClearImg.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvGetSmsCode.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvLogin.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvPrivacy.setOnClickListener(quickClickListener);
        findViewById(R.id.iv_wechat_login).setOnClickListener(quickClickListener);
        findViewById(R.id.iv_qq_login).setOnClickListener(quickClickListener);
        findViewById(R.id.password_login).setOnClickListener(quickClickListener);

        String stringClick = "已阅读并同意《用户协议》和《隐私政策》";
        SpannableString msp = new SpannableString(stringClick);
        ConstantUtil.setClickSpanToWeb(mContext, msp, 6, 13, "用户协议", ConstantWeb.PRIVACY_POLICY);
        ConstantUtil.setClickSpanToWeb(mContext, msp, 13, 19, "隐私政策", ConstantWeb.PRIVACY_PROTOCOL);
        contentLayoutBinding.tvPrivacy.setText(msp);
        contentLayoutBinding.tvPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
        contentLayoutBinding.tvPrivacy.setHighlightColor(this.getResources().getColor(android.R.color.transparent));
        initSms();
    }

    private void preLogin() {
        progressDialog = MaterialDialogUtil.showCustomProgressDialog(LoginActivity.this);
        contentLayoutBinding.loginSms.setVisibility(View.INVISIBLE);
        //设置在1000-10000之内
        SecVerify.setTimeOut(1500);
        SecVerify.setAdapterClass(SecVerifyAdapter.class);
        SecVerify.autoFinishOAuthPage(false);
        SecVerify.verify(new VerifyCallback() {
            @Override
            public void onOtherLogin() {
                // 用户点击“其他登录方式”，处理自己的逻辑
                LogUtils.e("miaoyan---", "其他登录方式");
                SecVerify.finishOAuthPage();
                skipSmsView();
            }

            @Override
            public void onUserCanceled() {
                // 用户点击“关闭按钮”或“物理返回键”取消登录，处理自己的逻辑
                LogUtils.e("miaoyan---", "关闭按钮");
                SecVerify.finishOAuthPage();
                finish();
            }

            @Override
            public void onComplete(VerifyResult data) {
                String token = data.getToken();
                String opToken = data.getOpToken();
                String operator = data.getOperator();
                LogUtils.e("miaoyan---Optoken:", "" + opToken + "\ntoken" + token + "\noperator" + operator);
                login(1, "", "", opToken, token, operator, "", "");
            }

            @Override
            public void onFailure(VerifyException e) {
                //TODO处理失败的结果
                LogUtils.e("miaoyan---", "失败结果" + e.toString() + "\n厂商失败结果" + e.getCause());
                SecVerify.finishOAuthPage();
                skipSmsView();
            }
        });
    }

    private void skipSmsView() {
        closeProgressDialog();
        ConstantUtil.showSoftInputFromWindow(LoginActivity.this, contentLayoutBinding.etPhone);
        contentLayoutBinding.loginSms.setVisibility(View.VISIBLE);
    }

    private void subscibeRxBus() {
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.PRE_LOGIN_TO_LOGIN, Integer.class)
                .subscribe(integer -> {
                    skipSmsView();
                    if (integer == 0) {
                        finish();
                    }
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));
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
        contentLayoutBinding.etUserName.addTextChangedListener(new TextWatcher() {
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
        contentLayoutBinding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (contentLayoutBinding.etPassword.getText().toString().length() >= 4) {
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
                case R.id.tv_privacy:
                    contentLayoutBinding.loginCheckbox.setChecked(true);
                    break;
                case R.id.edit_phone_clear_img:
                    contentLayoutBinding.etPhone.setText("");
                    contentLayoutBinding.etUserName.setText("");
                    break;
                case R.id.tv_get_sms_code:
                    String phone = contentLayoutBinding.etPhone.getText().toString();
                    if (phone.length() == 11) {
                        if (!CheckNetwork.isNetworkConnected(LoginActivity.this)) {
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
                    String verifyCode = contentLayoutBinding.etSmsCode.getText().toString();
                    String password = contentLayoutBinding.etPassword.getText().toString();
                    phone = contentLayoutBinding.etPhone.getText().toString();
                    String username = contentLayoutBinding.etUserName.getText().toString();
                    if (contentLayoutBinding.rlSmsCode.getVisibility() == View.VISIBLE) {
                        //验证码登录
                        if (phone.length() == 11 && verifyCode.length() >= 4) {
                            login(2, phone, verifyCode, "", "", "", "", "");
                        } else {
                            CommonUtils.showShort("请输入正确的手机号或验证码");
                        }
                    } else {
                        //用户名密码
                        if (username.length() >= 4 && password.length() >= 4) {
                            login(5, "", verifyCode, "", "", "", username, password);
                        } else {
                            CommonUtils.showShort("请输入正确的账号或密码");
                        }
                    }
                    break;
                case R.id.iv_wechat_login:
                    if (umShareAPI.isInstall(LoginActivity.this, SHARE_MEDIA.WEIXIN)) {
                        umShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, umAuthListener);
                    } else {
                        CommonUtils.showShort("未安装微信");
                    }

                    break;
                case R.id.iv_qq_login:
//                    if (umShareAPI.isInstall(LoginActivity.this, SHARE_MEDIA.WEIXIN)) {
//                        umShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ, umAuthListener);
//                    } else {
//                        CommonUtils.showShort("未安装QQ");
//                    }
                    umShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ, umAuthListener);
                    break;
                case R.id.password_login:
                    if (contentLayoutBinding.rlSmsCode.getVisibility() == View.VISIBLE) {

                        contentLayoutBinding.etUserName.setVisibility(View.VISIBLE);
                        contentLayoutBinding.etPhone.setVisibility(View.GONE);

                        contentLayoutBinding.etUserName.setHint("请输入账号");
                        contentLayoutBinding.etUserName.setText("");
                        contentLayoutBinding.etPhone.setHint("请输入手机号");
                        contentLayoutBinding.etPhone.setText("");

                        contentLayoutBinding.passwordLogin.setText("短信登录");
                        contentLayoutBinding.etPassword.setVisibility(View.VISIBLE);
                        contentLayoutBinding.rlSmsCode.setVisibility(View.GONE);
                    } else {
                        contentLayoutBinding.etUserName.setVisibility(View.GONE);
                        contentLayoutBinding.etPhone.setVisibility(View.VISIBLE);

                        contentLayoutBinding.etUserName.setHint("请输入账号");
                        contentLayoutBinding.etUserName.setText("");
                        contentLayoutBinding.etPhone.setHint("请输入手机号");
                        contentLayoutBinding.etPhone.setText("");

                        contentLayoutBinding.passwordLogin.setText("密码登录");
                        contentLayoutBinding.etPassword.setVisibility(View.GONE);
                        contentLayoutBinding.rlSmsCode.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

    UMAuthListener umAuthListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
            CommonUtils.showShort("开始");
        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            CommonUtils.showShort("成功了");
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
//            CommonUtils.showShort("失败：" + t.getMessage());
            CommonUtils.showShort("失败");
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            CommonUtils.showShort("取消了");
        }
    };

    private void login(int loginType, String phone, String verifyCode, String opToken, String token, String operator, String account, String passwd) {
        progressDialog = MaterialDialogUtil.showCustomProgressDialog(this, "登录中", true);
        viewModel.getLoginId(loginType, phone, verifyCode, opToken, token, operator, account, passwd).observe(LoginActivity.this, resultBean -> {
            closeProgressDialog();
            SecVerify.finishOAuthPage();
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    //user_status 0:游客 1:注册成功 2:账户注销中 3:账户已经注销
                    if (resultBean.getData().getUserBase().getStatus() == 2) {
                        showCancellationNoti(resultBean.getData());
                    } else {
                        loginSucess(resultBean.getData(), false);
                    }
                } else if (resultBean.getCode() == 301) {
                    CommonUtils.showShort("请输入正确的验证码");
                } else {
                    if (resultBean.getMsg() != null && !TextUtils.isEmpty(resultBean.getMsg())) {
                        CommonUtils.showShort("" + resultBean.getMsg());
                    }
                }
            } else {
                contentLayoutBinding.loginSms.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showCancellationNoti(MyOwnerUserInfoBean loginBean) {
        String message = "当前账号正在注销审核，继续登录会放弃注销。";
        MaterialDialog logDialog = MaterialDialogUtil.showCustomCommonConfirmDialog(mContext, message, new CommonConfirmDialogListener() {
            @Override
            public void onDialogCancel() {
            }

            @Override
            public void onDialogSure() {
                viewModel.setCancelDelete(loginBean.getUserBase().getId()).observe(LoginActivity.this, resultBean -> {
                    if (resultBean != null) {
                        if (resultBean.getCode() == 0) {
                            loginSucess(loginBean, true);
                        } else {
                            if (resultBean.getMsg() != null && !TextUtils.isEmpty(resultBean.getMsg())) {
                                CommonUtils.showShort("" + resultBean.getMsg());
                            } else {
                                CommonUtils.showShort("操作失败请重试");
                            }
                        }
                    }
                });
            }
        });
    }

    private void loginSucess(MyOwnerUserInfoBean loginBean, boolean isCancel) {
        CommonUtils.showShort("登录成功");
        SPUtils.getInstance(Constant.SP_CONFIG_INFO).put("funny_base_url", HttpUtils.funnyBaseUrl);

        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("authToken", loginBean.getAuthToken());
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("longTermToken", loginBean.getLongTermToken());
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("token_days", loginBean.getDays());
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("token_date", DateUtil.getTodayDateStringToServer());

        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_id", loginBean.getUserBase().getId());
        ConstantUtil.setAlias(this, loginBean.getUserBase().getId());
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_status", isCancel ? 1 : loginBean.getUserBase().getStatus());
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_bind_phone", TextUtils.isEmpty(loginBean.getUserBase().getPhoneNumShow()) ? 0 : 1);
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_avatar", loginBean.getUserBase().getAvatar());
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_nike_name", loginBean.getUserBase().getNickname());
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_info", GsonUtils.toJson(loginBean));
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("home_video_request_list", "");
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("home_img_request_list", "");

        viewModel.getClientConfig().observe(this, resultBean1 -> {
        });

        RxBus.getDefault().post(RxCodeConstant.LOGIN_STATUS_CHANGE, true);
        RxBus.getDefault().post(RxCodeConstant.UPDATE_MYFRAGMENT, 1);
        //如果当userid变化了，则首页重新刷新
        if (beforeUserId == loginBean.getUserBase().getId()) {
            finish();
        } else {
            ConstantUtil.isLogOutRefreshVideo = true;
            ConstantUtil.isLogOutRefreshImg = true;
            RxBus.getDefault().post(RxCodeConstant.SET_LOGOUT_TO_HOME, isNeedSkipHome);
            if (isNeedSkipHome == 1) {
                ActivityUtils.startActivity(MainActivity.class);
            }
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public static void starActivity() {
        starActivity(1);
    }

    //:1跳到首页刷新 0：等回到首页再刷新，不用跳,比如元宇宙页面和个人页评论页面 2:不用跳也不用刷新，目前只有首页评论页面
    //首页评论页面如果当前在视频,视频不刷新，但是滑到图片了图片刷新
    public static void starActivity(int isNeedSkipHome) {
        ConstantUtil.isNeedSkipHome = isNeedSkipHome;

        Bundle bundle = new Bundle();
        bundle.putInt("isNeedSkipHome", isNeedSkipHome);
        ActivityUtils.startActivity(bundle, LoginActivity.class, R.anim.dialog_enter, 0);
        ConstantUtil.createUmEvent("login_pv");//登录页-查看
    }

}
