package com.sortinghat.funny.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.ad.ConfigAdUtil;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.BuildConfig;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.ClientConfigBean;
import com.sortinghat.funny.bean.ClientSystemConfigBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.databinding.DialogHomeRewardBinding;
import com.sortinghat.funny.databinding.DialogMyLikecountBinding;
import com.sortinghat.funny.ui.MainActivity;
import com.sortinghat.funny.ui.SplashActivity;
import com.sortinghat.funny.ui.my.CommonWebActivity;
import com.sortinghat.funny.ui.my.MyOtherUserInfoActivity;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.api.UPushAliasCallback;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstantUtil {

    public static String xiaomiDevicceId = "2ecacdbfd60b83124909f1becd9b430ff";
    public static boolean isUpdataMyFragmentList = false;
    public static boolean isUpdataMyFragmentHeader = false;
    public static boolean isLogOutRefreshVideo = false;//用户Id不一致的登录或者退出后需要刷新首页视频推荐的帖子
    public static boolean isLogOutRefreshImg = false;//用户Id不一致的登录或者退出后需要刷新首页图文推荐的帖子
    public static boolean isShareInShowPost = false;//是否用户从分享链接过来跳转视频或者帖子
    public static long isShareInShowPostId = 0;//是否用户从分享链接过来跳转视频或者帖子Id,分享和推送
    public static String isShareParamType = "share";//是否用户从分享链接过来跳转视频或者帖子Id
    public static int isNeedSkipHome = 1;
    public static int KfpLogSendMaxDefaultValue = 5;//最大日志包条数
    public static int KfpLogSendLogValue = 0;//日志包条数
    public static boolean homeVideoIsAd = false;//0不显示穿山甲弹框，11显示
    public static String appInstalledName = "";//已安装包的名字

    //红米note 10 测试手机
    public static boolean isInfoTest() {
        return DeviceUtils.getUniqueDeviceId().equals(xiaomiDevicceId) && BuildConfig.DEBUG;
    }

    //设置是否为Vip用户
    public static void setUserVip(boolean isVip) {
        long wxdmfId = 2465308681472l;//五香大米饭ID
        long yy3Id = 2580984123136l;//运营3ID
        long userId = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id");
        if (isVip || isInfoTest() || wxdmfId == userId || yy3Id == userId || (CommonUtils.getUmChannel(FunnyApplication.getContext()).toLowerCase().equals("oppo") && !ConstantUtil.isThroughReviewState())) {
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.USER_VIP_TAG, true);//是否是VIP用户
        } else {
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.USER_VIP_TAG, false);//是否是VIP用户
        }
    }

    //首页like的数字的显示
    public static String getLikeNumString(int likeCount, String s) {
        try {
            if (likeCount <= 0) {
                return s;//小于等于零时显示的字
            } else if (likeCount >= 10000) {
                return String.format("%.1f", likeCount / 10000d) + "W";//保留1位小数
            } else {
                return likeCount + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static ArrayList<Long> getSPList(String spString) {
        String result = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString(spString);
        ArrayList<Long> mList = new ArrayList<>();
        if (!TextUtils.isEmpty(result)) {
            mList = new Gson().fromJson(result, new TypeToken<ArrayList<Long>>() {
            }.getType());
        }
        return mList;
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(final Activity activity, final EditText edittext) {
        try {
            final Timer timer = new Timer(); //设置定时器
            timer.schedule(new TimerTask() {
                @Override
                public void run() { //弹出软键盘的代码
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timer.cancel();
                            edittext.setFocusable(true);
                            edittext.setFocusableInTouchMode(true);
                            edittext.requestFocus();
                            InputMethodManager imm = (InputMethodManager) FunnyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(edittext, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });
                }
            }, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏软键盘根据Activity
     */
    public static void hideSoftKeyboardFromActivity(Activity mActivity, View view) {
        if (mActivity == null)
            return;
        if (view == null) {
            view = mActivity.getWindow().peekDecorView();
        }
        if (view == null)
            return;

        View finalView = view;
        mActivity.runOnUiThread(() -> {
            try {
                ((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(finalView.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void setClickSpanToWeb(Context mContext, SpannableString msp, int firstIndex, int lastIndex, final String name, String url) {
        msp.setSpan(new StyleSpan(Typeface.NORMAL), firstIndex, lastIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置点击事件
        msp.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                CommonWebActivity.starWebActivity(mContext, name, url);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                //设置没有下划线
                ds.setUnderlineText(false);
                ds.setFakeBoldText(true);
                ds.setColor(mContext.getResources().getColor(R.color.blue));
            }
        }, firstIndex, lastIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    //日志点赞点踩埋点int改为string
    public static String changeLikeLogString(String type, int likeType) {
        String log = "like";
        if (type.equals("like")) {
            switch (likeType) {
                case 0:
                    log = "like_cancel";
                    break;
                case 1:
                    log = "like";
                    break;
                case 2:
                    log = "like_favor";
                    break;
                case 3:
                    log = "like_touchdeep";
                    break;
                case 4:
                    log = "like_movedcry";
                    break;
                case 5:
                    log = "like_wonderful";
                    break;
                case 6:
                    log = "like_giggle";
                    break;
                default:
                    log = "like";
                    break;


            }
        } else if (type.equals("dislike")) {
            switch (likeType) {
                case 0:
                    log = "dislike_cancel";
                    break;
                case 1:
                    log = "dislike";
                    break;
                case 2:
                    log = "dislike_content";
                    break;
                case 3:
                    log = "dislike_author";
                    break;
                case 4:
                    log = "dislike_topic";
                    break;
                default:
                    log = "dislike";
                    break;

            }
        } else if (type.equals("share")) {
            switch (likeType) {
                case 0:
                    log = "share_cancel";
                    break;
                case 1:
                    log = "share_wx";
                    break;
                case 2:
                    log = "share_wxmoment";
                    break;
                case 3:
                    log = "share_qq";
                    break;
                case 4:
                    log = "share_qqzone";
                    break;
                case 5:
                    log = "share_weibo";
                    break;
                default:
                    log = "share_cancel";
                    break;
            }
        }

        return log;

    }

    public static void setNavigationBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setNavigationBarColor(Color.BLACK);  //设置虚拟键的背景颜色
        }
    }

    public static void createUmEvent(String eventId) {
        if (FunnyApplication.getContext() == null) {
            return;
        }
        MobclickAgent.onEvent(FunnyApplication.getContext(), eventId);
    }

    public static void showNoOpenDialog(Context mContext, String title) {

        MaterialDialog dialog = MaterialDialogUtil.showCustomWithCloseButtonDialog(mContext, R.layout.dialog_home_reward);
        DialogHomeRewardBinding dialogHomeDislikeBinding = DataBindingUtil.bind(dialog.getCustomView());

        dialogHomeDislikeBinding.ivClose.setOnClickListener(view -> {
            dialog.dismiss();
        });
        if (TextUtils.isEmpty(title)) {
            title = "抱歉，该功能即将在未来开放";
        }
        dialogHomeDislikeBinding.dialogContent.setText(title);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialog.getCustomView().getLayoutParams();
        params.gravity = Gravity.CENTER;
        dialog.getCustomView().setLayoutParams(params);
        dialog.show();
        try {
            new Handler().postDelayed(() -> {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }, 7000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showLikeDialog(Context mContext, String title) {

        MaterialDialog dialog = MaterialDialogUtil.showCustomWithCloseButtonDialog(mContext, R.layout.dialog_my_likecount);
        DialogMyLikecountBinding dialogMyLikecountBinding = DataBindingUtil.bind(dialog.getCustomView());

        dialogMyLikecountBinding.txSure.setOnClickListener(view -> {
            dialog.dismiss();
        });
        if (TextUtils.isEmpty(title)) {
            title = "获得点赞数";
        }
        dialogMyLikecountBinding.dialogContent.setText(title);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialog.getCustomView().getLayoutParams();
        params.gravity = Gravity.CENTER;
        dialog.getCustomView().setLayoutParams(params);
        dialog.show();

    }

    /**
     * 禁止EditText输入空格和换行符
     * 此时maxlines和maxleng无效了，得在代码判断长度
     *
     * @param editText EditText输入框
     */
    public static void setEditTextInputSpace(EditText editText) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ") || source.toString().contentEquals("\n")) {
                    return "";
                } else {
                    return null;
                }
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    /**
     * 禁止EditText输入特殊字符
     *
     * @param editText EditText输入框
     */
    public static void setEditTextInputSpeChat(EditText editText) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String speChat = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if (matcher.find()) {
                    return "";
                } else {
                    return null;
                }
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    /**
     * 是否安装微信或者QQ
     */
    public static boolean isWxQQInstall(Context mContext, SHARE_MEDIA shareMedia, Tencent mTencent, String wx_appid) {
        String packageName = "";
        switch (shareMedia) {
            case QQ:
                packageName = PACKAGE_MOBILE_QQ;
                break;
            case QZONE:
                packageName = PACKAGE_MOBILE_QQ;
                break;
            case SINA:
                packageName = "";
                break;
            case WEIXIN:
                packageName = PACKAGE_WECHAT;
                break;
            case WEIXIN_CIRCLE:
                packageName = PACKAGE_WECHAT;
                break;
            default:
                break;
        }

        boolean isInstall = true;
        try {
            if (PACKAGE_MOBILE_QQ.equals(packageName)) {//如果是QQ
                if (mTencent == null) {
                    return true;
                }
                isInstall = mTencent.isQQInstalled(mContext);
                if (!isInstall) {
                    CommonUtils.showShort("QQ未安装");
                }

            } else if (PACKAGE_WECHAT.equals(packageName)) {
                IWXAPI wxApi = WXAPIFactory.createWXAPI(mContext, wx_appid);
                isInstall = wxApi.isWXAppInstalled();
                if (!isInstall) {
                    CommonUtils.showShort("微信未安装");
                }
            }
            return isInstall;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            final PackageManager packageManager = mContext.getPackageManager();
            List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
            if (pInfo != null) {
                for (int i = 0; i < pInfo.size(); i++) {
                    String pn = pInfo.get(i).packageName;
                    if (packageName.equals(pn)) {
                        return true;
                    }
                }
            }
            CommonUtils.showLong("QQ或者微信未安装");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        CommonUtils.showLong("QQ或者微信未安装");
        return false;

    }

    public static final String PACKAGE_WECHAT = "com.tencent.mm";
    private static final String PACKAGE_MOBILE_QQ = "com.tencent.mobileqq";

    public static String getLogActivity(Activity activity) {
        if (activity == null) {
            return "other";
        }

        if (activity instanceof SplashActivity) {
            return "splash";
        }
        if (activity instanceof MainActivity) {
            try {
                int pos = MainActivity.getInstance().getCurrentFragment();
                switch (pos) {
                    case 0:
                        return "index";
                    case 1:
                        return "other";
                    case 2:
                        return "other";
                    case 3:
                        return "mine";
                    case 4:
                        return "mine";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "index";
        }
        if (activity instanceof MyOtherUserInfoActivity) {
            return "author";
        }
        return "other";
    }

    public static String LogListTag = "#sortinghat#";
    public static String pushToken = "";

    public static void spStartLog(String jsonString) {
        if (TextUtils.isEmpty(jsonString) || jsonString.length() <= 1) {
            return;
        }
        String start_log_string = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString(Constant.START_LOG_STRING_NEW);
        if (!TextUtils.isEmpty(start_log_string)) {
            SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put(Constant.START_LOG_STRING_NEW, start_log_string + LogListTag + jsonString);
        } else {
            SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put(Constant.START_LOG_STRING_NEW, jsonString);
        }
    }

    public static void setAlias(Context context, long userID) {
        if (userID <= 0) {
            return;
        }
        try {
            //set换成add提高到达率
            PushAgent.getInstance(context).addAlias(userID + "", "android", new UPushAliasCallback() {
                @Override
                public void onMessage(boolean b, String s) {
                    Log.i("app-push-c", "Alias:" + b + "-" + s);
                }
            });
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    public static void setConfigSp(BaseResultBean<ClientConfigBean> configBean) {
        if (configBean.getCode() == 0) {
            LogUtils.d("app-config-sp", "isUserEvaluate：" + configBean.getData().isUserEvaluate());
            if (configBean.getData().getEmoticon().equals("panda")) {
                SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("home_like_animation_type", 0);
            } else {
                SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("home_like_animation_type", 1);
            }
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put("post_view_show_chose_age_had", configBean.getData().isUserInformation());
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put("post_view_show_chose_object_had", configBean.getData().isUserLike());
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.PUBLISH_POST_EDUCATION, configBean.getData().getCreatePostEducation());//-1不显示
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.HOME_AD_SHOW_SPACE_NUM, configBean.getData().getAdAndroid1());//
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.HOME_AD_IMG_SHOW_SPACE_NUM, configBean.getData().getAndroidImageTextAd());
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.AD_SPLASH_SHOW, configBean.getData().getAndroidOpenScreenAd() == 1);//是否显示
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.THROUGH_REVIEW_STATE, configBean.getData().isAndroid_through_review_state());//是否通过审核,默认true
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.HOME_VIDEO_AD_NEW_AB, configBean.getData().getAndroidFrequencyAd());//视频新版AB广告
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put("background_ad_pause_time", configBean.getData().getAndroidOpenScreenAdKeepAlive());//后台出广告时间毫秒值
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.ANDROID_TASK_SYS_AB, configBean.getData().isAndroidTasksys());//是否是任务
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.ANDROID_MOOD_REPORT, configBean.getData().getAndroidMoodReport());//是否显示报告

            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.PUBLISH_POST_GRADE, configBean.getData().isUserEvaluate());//-1不显示
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.HOME_TASK_SHOW, true);//-1不显示
            setUserVip(configBean.getData().getIsVip() == 1);
//            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.PUBLISH_POST_GRADE, true);//-1不显示

//            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.HOME_VIDEO_AD_NEW_AB, 2);//视频新版AB广告
//            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put("post_like_strategy", configBean.getData().getBottomEmotion());
//            RxBus.getDefault().post(RxCodeConstant.UPDATE_EXPERIMENT_STRATEGY, configBean.getData().getBottomEmotion());
        }
    }

    //系统配置
    public static void setSystemConfigSp(BaseResultBean<ClientSystemConfigBean> configBean) {

        SPUtils.getInstance("config_info").put("android_splash_ad_order", configBean.getData().getAdPlatformOpen());

        List<Integer> adHomeVideoConfig = new ArrayList<>();//1代表穿山甲，2广点通
        try {
            String spHome = configBean.getData().getAdPlatformHomePage();
            if (!TextUtils.isEmpty(spHome)) {
                String[] spArrayString = spHome.split(",");
                for (int i = 0; i < spArrayString.length; i++) {
                    adHomeVideoConfig.add(Integer.valueOf(spArrayString[i]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ConfigAdUtil.adHomeVideoOrderConfig = adHomeVideoConfig;

    }

    public static boolean isHuaWei() {
        boolean result = false;
        if (Build.BRAND.equalsIgnoreCase("huawei") || Build.BRAND.equalsIgnoreCase("honor")) {
            result = true;
        }
        return result;
    }

    public static boolean isVivo() {
        boolean result = false;
        if (Build.BRAND.equalsIgnoreCase("vivo") || Build.BRAND.equalsIgnoreCase("iqoo")) {
            result = true;
        }
        return result;
    }

    public static boolean isOppo() {
        boolean result = false;
        if (Build.BRAND.equalsIgnoreCase("oppo") || Build.BRAND.equalsIgnoreCase("realme") || Build.BRAND.equalsIgnoreCase("oneplus")) {
            result = true;
        }
        return result;
    }

    public static boolean isXiaomi() {
        boolean result = false;
        if (Build.BRAND.equalsIgnoreCase("xiaomi") || Build.BRAND.equalsIgnoreCase("redmi") || Build.BRAND.equalsIgnoreCase("blackshark")) {
            result = true;
        }
        return result;
    }

    public static boolean isXiaomiKa() {
        boolean result = false;
        //getModel("m2101k9c")小米11
        if ((DeviceUtils.getModel().contains("2101") && Build.BRAND.equalsIgnoreCase("xiaomi")) || isVivo()) {
            result = true;
        }
        return result;
    }

    //广告是否通过审核状态
    public static boolean isThroughReviewState() {
        return SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(Constant.THROUGH_REVIEW_STATE, false);
    }

    //版本广告是否通过审核
    public static boolean isHuaweiThroughReviewState(Context mContext) {
        if ((CommonUtils.getUmChannel(mContext).equals("huawei") || CommonUtils.getUmChannel(mContext).equals("vivo")) && !ConstantUtil.isThroughReviewState()) {
            return false;
        } else {
            return true;
        }
    }

    //加载图像框统一处理
    public static void glideLoadPendantUrl(String pendantUrl, ImageView imageView) {
        if (!TextUtils.isEmpty(pendantUrl)) {
            imageView.setVisibility(View.VISIBLE);
            GlideUtils.loadImageNoPlaceholder(pendantUrl, imageView);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    public static void launchAppDetail(Context mContext, String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg)) {
                return;
            }

            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
