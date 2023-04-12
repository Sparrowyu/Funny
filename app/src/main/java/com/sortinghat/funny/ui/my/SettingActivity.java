package com.sortinghat.funny.ui.my;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.base.RootApplication;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.RequestInfo;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.R;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.ConstantWeb;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivitySettingBinding;
import com.sortinghat.funny.interfaces.CommonConfirmDialogListener;
import com.sortinghat.funny.ui.MainActivity;
import com.sortinghat.funny.ui.home.FeedBackActivity;
import com.sortinghat.funny.util.CommonUserInfo;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.DateUtil;
import com.sortinghat.funny.util.FileSizeUtil;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.viewmodel.LoginViewModel;
import com.sortinghat.fymUpdate.common.UpdateApp;

import java.io.File;

public class SettingActivity extends BaseActivity<LoginViewModel, ActivitySettingBinding> {
    private long userId = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }


    @Override
    protected void initViews() {
        initTitleBar("设置");
        contentLayoutBinding.settingUpdate.setOnClickListener(quickClickListener);
        contentLayoutBinding.settingClearCache.setOnClickListener(quickClickListener);
        contentLayoutBinding.settingBindPhone.setOnClickListener(quickClickListener);
        contentLayoutBinding.settingPolicy.setOnClickListener(quickClickListener);
        contentLayoutBinding.settingProtocol.setOnClickListener(quickClickListener);
        contentLayoutBinding.settingFeedback.setOnClickListener(quickClickListener);
        contentLayoutBinding.settingCancellation.setOnClickListener(quickClickListener);
        contentLayoutBinding.settingThirdSdk.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvOutLogin.setOnClickListener(quickClickListener);
        if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 1) {
            contentLayoutBinding.settingCancellation.setVisibility(View.VISIBLE);
            contentLayoutBinding.tvOutLogin.setVisibility(View.VISIBLE);
            if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_bind_phone", 0) == 0) {
                contentLayoutBinding.settingBindPhone.setVisibility(View.VISIBLE);
                contentLayoutBinding.viewBindPhone.setVisibility(View.VISIBLE);
            } else {
                contentLayoutBinding.settingBindPhone.setVisibility(View.GONE);
                contentLayoutBinding.viewBindPhone.setVisibility(View.GONE);
            }
        } else {
            contentLayoutBinding.settingCancellation.setVisibility(View.GONE);
            contentLayoutBinding.tvOutLogin.setVisibility(View.GONE);
            contentLayoutBinding.settingBindPhone.setVisibility(View.GONE);
            contentLayoutBinding.viewBindPhone.setVisibility(View.GONE);
        }

//        我的小米手机 deviceId：2ecacdbfd60b83124909f1becd9b430ff  userId：172315250432
//测试的oppo手机： deviceId：22c33c111a63439c2a70b507201da1edc
        String deviceId = DeviceUtils.getUniqueDeviceId();//小米
        String deviceId1 = "2ecacdbfd60b83124909f1becd9b430ff";//
        String deviceId2 = "22c33c111a63439c2a70b507201da1edc";//
        String deviceId3 = "2ee81324becfb3ac8b19fc9818288dc61";//anqing
        String deviceId4 = "249a8127d9d073ef48b4bb1542d5fda11";//leo
        userId = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id");
        contentLayoutBinding.tvId.setText(userId + "");
//        if (deviceId.equals(deviceId1) || deviceId.equals(deviceId2)) {
        contentLayoutBinding.editId.setOnClickListener(quickClickListener);
        contentLayoutBinding.editId.setVisibility(View.VISIBLE);
//        } else {
//            contentLayoutBinding.editId.setVisibility(View.GONE);
//        }
        if (deviceId.equals(deviceId1) || deviceId.equals(deviceId2) || deviceId.equals(deviceId3) || deviceId.equals(deviceId4)) {
            contentLayoutBinding.editChannel.setVisibility(View.VISIBLE);
            contentLayoutBinding.tvChannel.setText("" + CommonUtils.getUmChannel(mContext));
        }


        contentLayoutBinding.tvVersionCode.setText("V" + RequestInfo.getInstance(mContext).getVersionName());
        if ((DeviceUtils.getModel().contains("2101") && Build.BRAND.equalsIgnoreCase("xiaomi"))) {
            contentLayoutBinding.settingClearCache.setVisibility(View.GONE);
        }
        if (CommonUtils.getUmChannel(mContext).equals("vivo") || deviceId.equals(deviceId1)){
            contentLayoutBinding.settingRecManage.setVisibility(View.VISIBLE);
            contentLayoutBinding.viewRecManage.setVisibility(View.VISIBLE);
            contentLayoutBinding.settingRecManage.setOnClickListener(quickClickListener);
        }else {
            contentLayoutBinding.settingRecManage.setVisibility(View.GONE);
            contentLayoutBinding.viewRecManage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {

    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.setting_update:
                    //"https://oss-andriod.gaoxiaoxingqiu.com/postfile/app_up/funny-v1.5.1-2022-03-13-release_151_17_none_sign.apk",//放到服务端的
                    //http://hatdata.oss-cn-beijing.aliyuncs.com/postfile/app_up/funny-v1.5.1-2022-03-13-release_151_17_none_sign.apk
//                    ConstantUtil.launchAppDetail(mContext,mContext.getPackageName(),"");
                    new UpdateApp(SettingActivity.this, R.mipmap.icon, 2, null, "").checkUpdateNew();
//                     CommonUtils.showShort("已是最新版");
                    break;
                case R.id.setting_clear_cache:
                    clearCache();
                    break;
                case R.id.setting_bind_phone:
                    ActivityUtils.startActivity(BindPhoneActivity.class);
                    break;
                case R.id.setting_policy:
                    CommonWebActivity.starWebActivity(SettingActivity.this, "服务协议", ConstantWeb.PRIVACY_POLICY);
                    break;
                case R.id.setting_protocol:
                    CommonWebActivity.starWebActivity(SettingActivity.this, "隐私政策", ConstantWeb.PRIVACY_PROTOCOL);
                    break;
                case R.id.setting_third_sdk:
                    CommonWebActivity.starWebActivity(SettingActivity.this, "第三方SDK目录", ConstantWeb.PRIVACY_THIRD_SDK);
                    break;
                case R.id.tv_out_login:
                    if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 1) {
                        showLogOut();
                    }
                    break;
                case R.id.setting_feedback:
                    ActivityUtils.startActivity(FeedBackActivity.class);
                    break;
                case R.id.setting_cancellation:
                    if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 1) {
                        ActivityUtils.startActivity(MyCancellationActivity.class);
                    }
                    break;
                case R.id.edit_id:
                    ClipboardManager cmb = (ClipboardManager) FunnyApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText("" + userId);
                    CommonUtils.showShort("已复制ID");
                    break;
                case R.id.setting_rec_manage:
                    RecManageActivity.starActivity(mContext);
                    break;
            }
        }
    };


    private void showLogOut() {
        MaterialDialog logOutDialog = MaterialDialogUtil.showCustomCommonConfirmDialog(mContext, "确定退出登录吗？", new CommonConfirmDialogListener() {
            @Override
            public void onDialogCancel() {
            }

            @Override
            public void onDialogSure() {
                visitorsLogin();
            }
        });
    }

    private void visitorsLogin() {
        //图文和视频都需要传，这个参数的问题
        progressDialog = MaterialDialogUtil.showCustomProgressDialog(SettingActivity.this);
        viewModel.getLoginOut().observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    closeProgressDialog();
                    CommonUserInfo.userIconImg = "";
                    CommonUtils.showShort("退出登录");
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_id", resultBean.getData().getUserBase().getId());
                    ConstantUtil.setAlias(this, resultBean.getData().getUserBase().getId());
                    //user_status-0：游客 1：注册成功 2:账户已经注销
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_status", resultBean.getData().getUserBase().getStatus());
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("authToken", resultBean.getData().getAuthToken());
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("longTermToken", resultBean.getData().getLongTermToken());
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("token_days", resultBean.getData().getDays());
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("token_date", DateUtil.getTodayDateStringToServer());

                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_bind_phone", 0);
                    userId = resultBean.getData().getUserBase().getId();
                    contentLayoutBinding.tvId.setText(userId + "");

                    RxBus.getDefault().post(RxCodeConstant.LOGIN_STATUS_CHANGE, false);
                    RxBus.getDefault().post(RxCodeConstant.UPDATE_MYFRAGMENT, 1);

                    contentLayoutBinding.settingCancellation.setVisibility(View.GONE);
                    contentLayoutBinding.tvOutLogin.setVisibility(View.GONE);
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("home_video_request_list", "");
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("home_img_request_list", "");

                    ConstantUtil.isLogOutRefreshVideo = true;
                    ConstantUtil.isLogOutRefreshImg = true;
                    RxBus.getDefault().post(RxCodeConstant.SET_LOGOUT_TO_HOME, 1);
                    ActivityUtils.startActivity(MainActivity.class);
                    finish();
                }
            }
        });
    }

    private void clearCache() {
        MaterialDialog logOutDialog = MaterialDialogUtil.showCustomCommonConfirmDialog(mContext, "确定清除缓存吗？", new CommonConfirmDialogListener() {
            @Override
            public void onDialogCancel() {
            }

            @Override
            public void onDialogSure() {
                CommonUtils.showShort("已清除缓存");
                contentLayoutBinding.tvCache.setText("0MB");
            }
        });

    }
}

