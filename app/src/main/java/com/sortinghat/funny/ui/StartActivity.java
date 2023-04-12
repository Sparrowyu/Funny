package com.sortinghat.funny.ui;

import static com.sortinghat.funny.FunnyApplication.mTencent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.gmoread.AdSplashManager;
import com.sortinghat.common.gmoread.AppConst;
import com.sortinghat.common.gmoread.GMAdManagerHolder;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.TTAdManagerHolder;
import com.sortinghat.common.utils.ad.ConfigAdUtil;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.R;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.ConstantWeb;
import com.sortinghat.funny.databinding.ActivitySplashBinding;
import com.sortinghat.funny.databinding.DialogSplashPrivacyProtocolBinding;
import com.sortinghat.funny.push.UmInitConfig;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.DateUtil;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.util.WeakHandler;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.utils.ShareLibraryUM;
import com.sortinghat.funny.viewmodel.SplashModel;
import com.tencent.tauth.Tencent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.PushAgent;


public class StartActivity extends BaseActivity<SplashModel, ActivitySplashBinding> implements WeakHandler.IHandler {
    //开屏广告加载超时时间,建议大于1000,这里为了冷启动第一次加载到广告并且展示,示例设置了2000ms
    //开屏广告加载发生超时但是SDK没有及时回调结果的时候，做的一层保护。
    private WeakHandler mHandler = new WeakHandler(this);
    //开屏广告加载超时时间,建议大于1000,这里为了冷启动第一次加载到广告并且展示,示例设置了2000ms
    private static final int AD_TIME_OUT = 3000;
    private static final int MSG_GO_MAIN = 1;
    //开屏广告是否已经加载
    private boolean mHasLoaded;
    //从穿山甲点击广告回来，是否强制跳转到主页面
    private boolean mForceGoMain = false;
    private boolean priDialogIsShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        标记Task不会重复启动，避免部分手机总是开屏页
        avoidLauncherAgain();
        super.onCreate(savedInstanceState);
    }

    private void avoidLauncherAgain() {
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                }
            }
        }
    }

    @Override
    protected int getLayoutId() {
        titleBarBinding.getRoot().setVisibility(View.GONE);
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initData() {
        if (!SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getBoolean("user_accept_privacy_protocol")) {
            initPrivacyProtocoDialog();
        } else {
            String oAid = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("umeng_oaid");
            if (TextUtils.isEmpty(oAid)) {
                //获取友盟oaid，广告标识：安卓Q以上替代IMEI
                UMConfigure.getOaid(mContext, s -> {

                    if (!TextUtils.isEmpty(s)) {
                        LogUtils.d("splash_id", s);
                        CommonUtils.OAID = s;
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("umeng_oaid", s);
                    }
                });
            }

            //推送平台多维度推送决策必须调用方法(需要同意隐私协议之后初始化完成调用)
            PushAgent.getInstance(this).onAppStart();
            startMainActivity();

        }

        if (TextUtils.isEmpty(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("authToken"))) {
            viewModel.getLoginId(0).observe(this, resultBean -> {
                if (resultBean != null) {
                    if (resultBean.getCode() == 0) {
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_id", resultBean.getData().getUserBase().getId());
                        if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getBoolean("user_accept_privacy_protocol")) {
                            ConstantUtil.setAlias(this, resultBean.getData().getUserBase().getId());
                        }
                        //user_status-0：游客 1：注册成功 2:账户已经注销
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_status", resultBean.getData().getUserBase().getStatus());
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("authToken", resultBean.getData().getAuthToken());
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("longTermToken", resultBean.getData().getLongTermToken());

                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("token_days", resultBean.getData().getDays());
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("token_date", DateUtil.getTodayDateStringToServer());

                        //首次安装
                        long createTime = System.currentTimeMillis();
                        JsonObject startJsonObject = new JsonObject();
                        startJsonObject.addProperty("create_time", createTime);//事件时间
                        RequestParamUtil.addStartLogHeadParam(startJsonObject, "foreground", "app", "index", "app");
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("foreground_time", createTime);

                        ConstantUtil.spStartLog(startJsonObject.toString());
                    }
                }
            });
        }

    }

    private void initPrivacyProtocoDialog() {
        priDialogIsShow = true;
        MaterialDialog dialog = MaterialDialogUtil.showCustomWithCloseButtonDialog(this, R.layout.dialog_splash_privacy_protocol);
        DialogSplashPrivacyProtocolBinding protocolBinding = DataBindingUtil.bind(dialog.getCustomView());
        String stringClick = this.getString(R.string.privacy_protocol_detail_click);
        SpannableString msp = new SpannableString(stringClick);
        ConstantUtil.setClickSpanToWeb(mContext, msp, 5, 11, "用户协议", ConstantWeb.PRIVACY_POLICY);
        ConstantUtil.setClickSpanToWeb(mContext, msp, 11, 17, "隐私政策", ConstantWeb.PRIVACY_PROTOCOL);
        protocolBinding.privacyProtocolDialogDetail.setText(this.getString(R.string.privacy_protocol_detail));
        protocolBinding.privacyProtocolDialogDetailClick.setText(msp);
        protocolBinding.privacyProtocolDialogDetailClick.setMovementMethod(LinkMovementMethod.getInstance());
        protocolBinding.privacyProtocolDialogDetailClick.setHighlightColor(this.getResources().getColor(android.R.color.transparent));
        protocolBinding.privacyProtocolDialogAgree.setOnClickListener(view -> {
            priDialogIsShow = false;
            ConstantUtil.createUmEvent("start_index_ok_button_click");//开屏-隐私协议点确定
            dialog.dismiss();
            agreeProtocol();
        });
        protocolBinding.privacyProtocolDialogDisagree.setOnClickListener(view -> {
            priDialogIsShow = false;
            ConstantUtil.createUmEvent("start_index_cancel_button_click");//开屏-隐私协议点取消
            dialog.dismiss();
            initPrivacyProtocoDisagreeDialog();
        });
        dialog.show();
    }

    private void initPrivacyProtocoDisagreeDialog() {
        MaterialDialog dialog = MaterialDialogUtil.showCustomWithCloseButtonDialog(this, R.layout.dialog_splash_privacy_protocol);
        DialogSplashPrivacyProtocolBinding protocolBinding = DataBindingUtil.bind(dialog.getCustomView());
        String stringClick = this.getString(R.string.privacy_protocol_dis_detail_click);
        SpannableString msp = new SpannableString(stringClick);
        ConstantUtil.setClickSpanToWeb(mContext, msp, 26, 32, "用户协议", ConstantWeb.PRIVACY_POLICY);
        ConstantUtil.setClickSpanToWeb(mContext, msp, 33, 39, "隐私政策", ConstantWeb.PRIVACY_PROTOCOL);
        protocolBinding.privacyProtocolDialogDetail.setText(this.getString(R.string.privacy_protocol_dis_detail));
        protocolBinding.privacyProtocolDialogDetailClick.setText(msp);
        protocolBinding.privacyProtocolDialogDetailClick.setMovementMethod(LinkMovementMethod.getInstance());
        protocolBinding.privacyProtocolDialogDetailClick.setHighlightColor(this.getResources().getColor(android.R.color.transparent));
        protocolBinding.privacyProtocolDialogAgree.setText("同意并继续");
        protocolBinding.privacyProtocolDialogAgree.setOnClickListener(view -> {
            dialog.dismiss();
            agreeProtocol();
        });
        protocolBinding.privacyProtocolDialogDisagree.setText("不同意并退出");
        protocolBinding.privacyProtocolDialogDisagree.setOnClickListener(view -> finish());
        dialog.show();
    }

    private void agreeProtocol() {
        // 其中APP_ID是分配给第三方应用的appid，类型为String。
        if (mTencent == null) {
            mTencent = Tencent.createInstance(getString(R.string.qq_appid), this);
        }
        TTAdManagerHolder.init(this);
        ConfigAdUtil.initKSSDK(FunnyApplication.getContext());
        GMAdManagerHolder.init(this);
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_accept_privacy_protocol", true);
        UmInitConfig umInitConfig = new UmInitConfig();
        umInitConfig.UMinit(getApplicationContext());

        // 必须getString，不能直接R.string
        ShareLibraryUM.setConfig(getString(R.string.weixin_appid), getString(R.string.weixin_appsecret), getString(R.string.qq_appid), getString(R.string.qq_key), "com.sortinghat.funny");
        String oAid = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("umeng_oaid");
        if (TextUtils.isEmpty(oAid)) {
            //获取友盟oaid，广告标识：安卓Q以上替代IMEI
            try {
                UMConfigure.getOaid(mContext, s -> {
                    if (!TextUtils.isEmpty(s)) {
                        LogUtils.d("splash_id", s);
                        CommonUtils.OAID = s;
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("umeng_oaid", s);
                    }
                    runOnUiThread(() -> startMainActivity());
                });
            } catch (Exception e) {
                runOnUiThread(() -> startMainActivity());
            }
        } else {
            startMainActivity();
        }
    }


    @Override
    public void handleMsg(Message msg) {
        if (msg.what == MSG_GO_MAIN) {
            if (!mHasLoaded) {
                startMainActivity();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mForceGoMain = true;
    }

    @Override
    protected void onResume() {
        setStatusBar(true);
        super.onResume();
        //如果是点击完穿山甲的广告触发的onResume方法则直接跳转到首页
        if (!priDialogIsShow && mForceGoMain) {
            startMainActivity();
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    /**
     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return super.onKeyDown(keyCode, event);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void startMainActivity() {
        viewModel.getClientConfig().observe(this, resultBeanConfig -> {
        });
        viewModel.getSystemClientConfig().observe(this, resultBeanConfig -> {
        });
        String date = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("token_date");
        int days = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("token_days") - 1;

        if (TextUtils.isEmpty(date) || days < 1 || DateUtil.getDateFromNetToProgressDay(date) < days) {
            ActivityUtils.startActivity(MainActivity.class);
            finish();
        } else {
            viewModel.getRefreshToken().observe(this, resultBean -> {
                if (resultBean != null) {
                    if (resultBean.getCode() == 0) {
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("authToken", resultBean.getData().getAuthToken());
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("token_days", resultBean.getData().getDays());
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("token_date", date);
                        ActivityUtils.startActivity(MainActivity.class);
                        finish();
                    } else {
                        CommonUtils.showLong("请重新请求");
                        ActivityUtils.startActivity(MainActivity.class);
                        finish();
                    }
                } else {
                    ActivityUtils.startActivity(MainActivity.class);
                    finish();
                }
            });
        }
    }
}
