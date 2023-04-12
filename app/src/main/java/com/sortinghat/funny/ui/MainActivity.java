package com.sortinghat.funny.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mob.MobSDK;
import com.mob.secverify.PreVerifyCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.common.exception.VerifyException;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.bean.MessageCountBean;
import com.sortinghat.funny.bean.TaskMessageBean;
import com.sortinghat.funny.bean.UserInstallAppListBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivityMainBinding;
import com.sortinghat.funny.push.HwPushUtil;
import com.sortinghat.funny.service.CommonService;
import com.sortinghat.funny.thirdparty.album.AlbumFile;
import com.sortinghat.funny.ui.home.HomeFragment;
import com.sortinghat.funny.ui.home.HomeImageTextFragment;
import com.sortinghat.funny.ui.home.HomeVideoFragment;
import com.sortinghat.funny.ui.message.MessageFragment;
import com.sortinghat.funny.ui.my.LoginActivity;
import com.sortinghat.funny.ui.my.MyFragment;
import com.sortinghat.funny.ui.publish.SelectVideoOrImageActivity;
import com.sortinghat.funny.ui.topic.TopicFragment;
import com.sortinghat.funny.util.CommonUtil;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.DateUtil;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.util.OnMultiClickUtils;
import com.sortinghat.funny.util.PermissionHandler;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.viewmodel.SplashModel;
import com.sortinghat.fymUpdate.common.UpdateApp;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.umlink.MobclickLink;
import com.umeng.umlink.UMLinkListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wzy on 2021/6/14
 */
public class MainActivity extends BaseActivity<SplashModel, ActivityMainBinding> {

    private TextBadgeItem textBadgeItem, textBadgeItemMine;

    private HomeFragment homeFragment;
    private TopicFragment topicFragment;
    private MessageFragment messageFragment;
    private MyFragment myFragment;

    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    private int currentFragmentPos = 0;

    public int getCurrentFragment() {
        return currentFragmentPos;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatusBars(getCurrentFragment());
    }

    @Override
    protected int getLayoutId() {
        titleBarBinding.getRoot().setVisibility(View.GONE);
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        instance = this;
        //推送平台多维度推送决策必须调用方法(需要同意隐私协议之后初始化完成调用)
        PushAgent.getInstance(this).onAppStart();
//        PlayerFactory.setPlayManager(Exo2PlayerManager.class);//默认是IJKplayer
        initBottomNavigationBar();
        subscibeRxBus();
        switchFragment(0);
        ConstantUtil.setNavigationBarColor(this);
        skipShareContent();
        checkInstallApp();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 此处要调用，否则App在后台运行时，会无法截获
        Uri data = intent.getData();
        if (data != null) {
            MobclickLink.handleUMLinkURI(this, data, umlinkAdapter);
        }
    }

    private void skipShareContent() {
        //友盟正常打开之后的跳转
        Uri data = getIntent().getData();
        if (data != null) {
            MobclickLink.handleUMLinkURI(this, data, umlinkAdapter);
        }
        //微信正常打开之后的跳转
        if (null != getIntent()) {
            String shareWxExtInfo = getIntent().getStringExtra("shareWxExtInfo");
            getDataFromWx(shareWxExtInfo);
            String pushPostId = getIntent().getStringExtra("pushPostId");
            Log.i("app-push", "appmsg142:" + pushPostId);
            getDataFromPush(pushPostId);
        }

        //友盟的首次安装后的跳转
        boolean hasGetInstallParamsUmeng = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getBoolean("key_Has_Get_InstallParams", false);
        if (!hasGetInstallParamsUmeng) {
            // 用户在首次安装时，App调取安装参数接口时，如果将剪切板功能打开，那么SDK就会去读取用户剪切板
            MobclickLink.getInstallParams(mContext, umlinkAdapter);
            //在9.3.6以后版本中，不再检查SDK是否初始化成功，可以不用延迟
        }
    }

    private HashMap<String, String> mInstall_params;
    UMLinkListener umlinkAdapter = new UMLinkListener() {
        @Override
        public void onLink(String path, HashMap<String, String> query_params) {
            //只要发送了日志，说明友盟已经获取到了链接参数
            if (query_params != null) {
                judgeUserIsLogin(query_params, "share");
            }
        }

        @Override
        public void onInstall(HashMap<String, String> install_params, Uri uri) {
            if (install_params.isEmpty() && uri.toString().isEmpty()) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                builder.setMessage("没有匹配到安装参数");//没有匹配到安装参数
//                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                    }
//                });
//                builder.show();
            } else {
                isFirstInstall = 1;
                if (!install_params.isEmpty()) {
                    mInstall_params = install_params;
                }
                if (!uri.toString().isEmpty()) {
                    MobclickLink.handleUMLinkURI(mContext, uri, umlinkAdapter);
                }
            }
            SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("key_Has_Get_InstallParams", true);
        }

        @Override
        public void onError(String error) {
            android.util.Log.i("mob", error);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(error);
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            builder.show();
        }
    };

    private void getShareHashData(HashMap<String, String> query_params, String paramType) {
        try {
            logShare(query_params, paramType);
            for (String key : query_params.keySet()) {
                if (key.equals("postId")) {
                    if (!TextUtils.isEmpty(query_params.get(key))) {
                        getIntentFromShareUrl(Long.parseLong(query_params.get(key)), paramType);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //当userid为-1时，重新调一下登录接口,fromType "share","push"
    private void judgeUserIsLogin(HashMap<String, String> query_params, String paramType) {
        if (!query_params.isEmpty()) {
            if (TextUtils.isEmpty(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("authToken")) || SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id") <= 0) {
                viewModel.getLoginId(0).observe(this, resultBean -> {
                    if (resultBean != null) {
                        if (resultBean.getCode() == 0) {
                            SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_id", resultBean.getData().getUserBase().getId());
                            //user_status-0：游客 1：注册成功 2:账户已经注销
                            ConstantUtil.setAlias(this, resultBean.getData().getUserBase().getId());
                            SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_status", resultBean.getData().getUserBase().getStatus());
                            SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("authToken", resultBean.getData().getAuthToken());
                            SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("longTermToken", resultBean.getData().getLongTermToken());

                            SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("token_days", resultBean.getData().getDays());
                            SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("token_date", DateUtil.getTodayDateStringToServer());

                            getShareHashData(query_params, paramType);
                        }
                    }
                });
            } else {
                getShareHashData(query_params, paramType);
            }
        }
    }

    private void getIntentFromShareUrl(long postId, String paramType) {
//        Log.e("share--1", "182postId:" + postId);
        if (postId != 0) {
            ConstantUtil.isShareInShowPostId = postId;
            ConstantUtil.isShareParamType = paramType;
            if (ConstantUtil.isShareInShowPostId > 0) {
                ConstantUtil.isShareInShowPost = true;
                contentLayoutBinding.bottomNavigationBar.selectTab(0);
                if (homeFragment != null) {
                    homeFragment.showSharePost(ConstantUtil.isShareInShowPostId, paramType);
                }
            }
        }
    }

    private int isFirstInstall = 0;

    private void logShare(HashMap<String, String> query_params, String paramType) {
        try {
            long createTime = System.currentTimeMillis();
            JsonObject startJsonObject = new JsonObject();
            startJsonObject.addProperty("create_time", createTime);//事件时间
            startJsonObject.addProperty("install", isFirstInstall);//是否是首次
            isFirstInstall = 0;
            for (String key : query_params.keySet()) {
                if (key.equals("shareTime")) {
                    if (!TextUtils.isEmpty(query_params.get(key))) {
                        startJsonObject.addProperty("share_time", query_params.get(key));//用户分享时的时间
                    }
                }
                if (key.equals("shareUserId")) {
                    if (!TextUtils.isEmpty(query_params.get(key))) {
                        startJsonObject.addProperty("share_uid", query_params.get(key));//分享的id
                    }
                }
                if (key.equals("shareType")) {
                    if (!TextUtils.isEmpty(query_params.get(key))) {
                        startJsonObject.addProperty("share_type", query_params.get(key));//用户分享时的类型
                    }
                }

                if (key.equals("postId")) {
                    if (!TextUtils.isEmpty(query_params.get(key))) {
                        startJsonObject.addProperty("post_id", query_params.get(key));//用户点击的帖子
                    }
                }
                if (key.equals("sharePostId")) {
                    if (!TextUtils.isEmpty(query_params.get(key))) {
                        startJsonObject.addProperty("share_post_id", query_params.get(key));//用户分享时的类型
                    }
                }
            }

            RequestParamUtil.addStartLogHeadParam(startJsonObject, "start", paramType, "index", "app");
            SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("foreground_time", createTime);
            ConstantUtil.spStartLog(startJsonObject.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBottomNavigationBar() {
        contentLayoutBinding.bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        contentLayoutBinding.bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);

        textBadgeItem = new TextBadgeItem().setBackgroundColorResource(R.color.red).hide();
        textBadgeItemMine = new TextBadgeItem().setBackgroundColorResource(R.color.red).hide();

        contentLayoutBinding.bottomNavigationBar
                .addItem(new BottomNavigationItem(R.mipmap.navigation_ic_home_select, "首页").setInactiveIconResource(R.mipmap.navigation_ic_home_normal)).setFirstActiveColor(R.color.home_bottom_tx)
                .addItem(new BottomNavigationItem(R.mipmap.navigation_ic_metaverse_select, "兴趣").setInactiveIconResource(R.mipmap.navigation_ic_metaverse_normal))
                .addItem(new BottomNavigationItem(R.mipmap.navigation_ic_publish, "").setInactiveIconResource(R.mipmap.navigation_ic_publish))
                .addItem(new BottomNavigationItem(R.mipmap.navigation_ic_message_select, "消息").setInactiveIconResource(R.mipmap.navigation_ic_message_normal).setBadgeItem(textBadgeItem))
                .addItem(new BottomNavigationItem(R.mipmap.navigation_ic_my_select, "我").setInactiveIconResource(R.mipmap.navigation_ic_my_normal).setBadgeItem(textBadgeItemMine))
                .setFirstActiveColor(R.color.home_bottom_tx).setActiveColor(R.color.home_bottom_other_tx).setInActiveColor(R.color.color_999999)
                .setmActiveTextIsBold(true)
                .setFirstSelectedPosition(0)
                .initialise();
    }

    @Override
    protected void setListener() {
        contentLayoutBinding.bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                switchFragment(position);
                if (position != 2) {
                    //当点击发布时，不用赋值当前的位置，按上一个位置的值
                    currentFragmentPos = position;
                }
                if (homeFragment != null) {
                    if (position == 0 && homeFragment.getViewPager().getCurrentItem() == 0) {
                        HomeVideoFragment.videoStartTime = System.currentTimeMillis();
                        HomeVideoFragment.postVideoPlayDurationTime = System.currentTimeMillis();
                        HomeVideoFragment.isCurrentQuit = true;
                        HomeVideoFragment.startPlayTime = System.currentTimeMillis();

                    } else {
                        if (position == 0 || position == 1 || position == 4 || (position == 3 && SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 1)) {
                            HomeVideoFragment.isCurrentQuit = false;
                        }
                    }

                    if (position == 0 && homeFragment.getViewPager().getCurrentItem() == 1) {
                        HomeImageTextFragment.imgStartTime = System.currentTimeMillis();
                        HomeImageTextFragment.isCurrentQuit = true;
                    } else {
                        HomeImageTextFragment.isCurrentQuit = false;
                    }
                    if (position != 0) {
                        homeFragment.updateLog();
                        homeFragment.removeHandlerMessage();
                    }
                }
            }

            @Override
            public void onTabUnselected(int position) {
                int currentSelectedPosition = contentLayoutBinding.bottomNavigationBar.getCurrentSelectedPosition();
                if (currentSelectedPosition == 2) {
                    contentLayoutBinding.bottomNavigationBar.selectTab(position, false);
                } else if (currentSelectedPosition == 3) {
                    if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                        contentLayoutBinding.bottomNavigationBar.selectTab(position, false);
                    }
                }
            }

            @Override
            public void onTabReselected(int position) {
                if (homeFragment != null && position == 0) {
                    //点击底部首页键,2s只执行一次
                    if (OnMultiClickUtils.isMultiClickClick(2000)) {
                        homeFragment.refreshData();
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        initSecVerify();
        //observeForever不依赖activity
        viewModel.getClientConfig().observe(this, resultBean -> {
        });
        upDataApp();
    }

    private void upDataApp() {
        //首页一天只会弹一次
        String date = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getString("update_main_date");
        String dateSp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (dateSp.equals(date)) {
            return;
        }
        new UpdateApp(MainActivity.this, R.mipmap.icon, 1, null, dateSp).checkUpdateNew();
    }

    private void initSecVerify() {
        //秒验
        MobSDK.submitPolicyGrantResult(true, null);
        if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
            //建议提前调用预登录接口，可以加快免密登录过程，提高用户体验
            SecVerify.preVerify(new PreVerifyCallback() {
                @Override
                public void onComplete(Void aVoid) {
                    Log.e("mosdk--:", "预登录成功");
                }

                @Override
                public void onFailure(VerifyException e) {
                    Log.e("mosdk--:", "预登录失败" + e.getMessage().toString());
                }
            });
        }
    }

    private void switchFragment(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        setStatusBars(position);
        switch (position) {
            case 0:
                contentLayoutBinding.bottomNavigationBar.setBarBackgroundColor(R.color.bottom_navigation_bar_bg).setFirstSelectedPosition(0).initialise();
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.content_container, homeFragment);
                } else {
                    transaction.show(homeFragment);
                }
                transaction.commitAllowingStateLoss();
                ConstantUtil.createUmEvent("home_icon_click");//首页-pv
                break;
            case 1:
                contentLayoutBinding.bottomNavigationBar.setBarBackgroundColor(R.color.white).setFirstSelectedPosition(1).initialise();
                if (topicFragment == null) {
                    topicFragment = new TopicFragment();
                    transaction.add(R.id.content_container, topicFragment);
                } else {
                    transaction.show(topicFragment);
                }
                transaction.commitAllowingStateLoss();
                ConstantUtil.createUmEvent("metaverse_icon_click");//元宇宙-pv
                break;
            case 2:
                toPublishBt();
                ConstantUtil.createUmEvent("publish_icon_click");//发布-pv
                break;
            case 3:
                if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                    LoginActivity.starActivity();
                    return;
                }
                contentLayoutBinding.bottomNavigationBar.setBarBackgroundColor(R.color.white).setFirstSelectedPosition(3).initialise();
                if (messageFragment == null) {
                    messageFragment = new MessageFragment();
                    transaction.add(R.id.content_container, messageFragment);
                } else {
                    transaction.show(messageFragment);
                }
                transaction.commitAllowingStateLoss();
                ConstantUtil.createUmEvent("message_icon_click");//消息-pv
                break;
            case 4:
                contentLayoutBinding.bottomNavigationBar.setBarBackgroundColor(R.color.white).setFirstSelectedPosition(4).initialise();
                if (myFragment == null) {
                    myFragment = new MyFragment();
                    transaction.add(R.id.content_container, myFragment);
                } else {
                    transaction.show(myFragment);
                }
                initSecVerify();
                transaction.commitAllowingStateLoss();
                ConstantUtil.createUmEvent("my_icon_click");//我-pv
                break;
            default:
                break;
        }
    }

    private void setStatusBars(int position) {
        setStatusBar(true);
        setStatusBarMode(false);
        switch (position) {
            case 0:
//                setStatusBar(R.color.black, false);
                break;
            case 1:
                break;
            case 3:
                if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 1) {
                    setStatusBarMode(true);
                }
                break;
            case 4:
                if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                    setStatusBarMode(true);
                } else {
                    setStatusBarMode(false);
                }
                break;
            default:
                break;
        }
    }

    private void hideAllFragment(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (topicFragment != null) {
            transaction.hide(topicFragment);
        }
        if (messageFragment != null) {
            transaction.hide(messageFragment);
        }
        if (myFragment != null) {
            transaction.hide(myFragment);
        }
    }

    private void jumpSelectVideoOrImageActivity() {
        ActivityUtils.startActivity(SelectVideoOrImageActivity.class);
    }

    private void subscibeRxBus() {
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.LOGIN_STATUS_CHANGE, Boolean.class)
                .subscribe(isLogin -> {
                    if (topicFragment != null && topicFragment.isInitData()) {
                        topicFragment.refreshData();
                    }
                    if (isLogin) {
                        FunnyApplication.getInstance().startCommonService();
                    } else {
                        if (FunnyApplication.getInstance().getCommonService() != null) {
                            FunnyApplication.getInstance().getCommonService().getMessageCount();
                        }
//                        FunnyApplication.getInstance().stopCommonService();
                    }
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.PUBLISH_POST, AlbumFile.class)
                .subscribe(albumFile -> {
                    contentLayoutBinding.bottomNavigationBar.selectTab(0);
                    if (homeFragment != null) {
                        SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.PUBLISH_POST_EDUCATION, "-1");//-1不显示
                        homeFragment.uploadPostFile(albumFile);
                    }
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.UPDATE_EXPERIMENT_STRATEGY, String.class)
                .subscribe(bottomEmotion -> {
                    if (homeFragment != null) {
                        homeFragment.updateExperimentStrategy(bottomEmotion);
                    }
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        //更新帖子的点赞或点踩状态或评论数
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.UPDATE_POST_LIKE_OR_UNLIKE_OR_REVIEW, HomeVideoImageListBean.ListBean.ContentBean.class)
                .subscribe(videoOrImageContent -> {
                    if (homeFragment != null) {
                        homeFragment.updatePostLikeOrUnlikeOrReview(videoOrImageContent);
                    }
                    if (myFragment != null) {
                        myFragment.updatePostLikeOrUnlikeOrReview(videoOrImageContent);
                    }
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.SET_LOGOUT_TO_HOME, Integer.class)
                .subscribe(integer -> {
                    if (homeFragment != null) {
                        homeFragment.clearNoLookPostIdList();
                    }
                    if (integer == 1) {
                        contentLayoutBinding.bottomNavigationBar.selectTab(0);
                        if (homeFragment != null) {
                            homeFragment.logOutOrInRefreshVideo();
                        }
                    }

                    if (integer == 2) {
//                        首页评论页面如果当前在视频,视频不刷新，但是滑到图片了图片刷新
                        if (homeFragment != null) {
                            if (homeFragment.getViewPager().getCurrentItem() == 0) {
                                ConstantUtil.isLogOutRefreshVideo = false;
                            } else {
                                ConstantUtil.isLogOutRefreshImg = false;
                            }
                        } else {
                            ConstantUtil.isLogOutRefreshVideo = true;
                            ConstantUtil.isLogOutRefreshImg = true;
                        }
                    }
                    viewModel.getClientConfig().observe(this, resultBean -> {
                    });

                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        addSubscription(RxBus.getDefault().toObservableSticky(MessageCountBean.class).subscribe(messageCountBean -> {
            if (messageCountBean != null) {
                int totalMessageCount = messageCountBean.getUserFollow() + messageCountBean.getPostComment() + messageCountBean.getPostLike() + messageCountBean.getCommentLike() + messageCountBean.getApplySys() + messageCountBean.getSysBroadcast();
                if (textBadgeItem != null) {
                    if (totalMessageCount > 0) {
                        if (totalMessageCount > 99) {
                            textBadgeItem.setText("99+");
                        } else {
                            textBadgeItem.setText(String.valueOf(totalMessageCount));
                        }
                        textBadgeItem.show(false);
                    } else {
                        textBadgeItem.setText("");
                        textBadgeItem.hide(false);
                    }
                }
            }
        }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        addSubscription(RxBus.getDefault().toObservableSticky(TaskMessageBean.class).subscribe(taskMessageBean -> {
            if (taskMessageBean != null) {

                int totalMessageCount = taskMessageBean.getTaskRed() + taskMessageBean.getStoreRed() + taskMessageBean.getRankRed();
                if (textBadgeItemMine != null) {
                    if (totalMessageCount > 0) {
                        textBadgeItemMine.setBorderWidth(SizeUtils.dp2px(5));
                        textBadgeItemMine.setText("");
                        textBadgeItemMine.show(false);
                    } else {
                        textBadgeItemMine.setText("");
                        textBadgeItemMine.hide(false);
                    }
                }
            }
        }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.SHARE_WEIXIN_TO_HOME, String.class)
                .subscribe(shareWxExtInfo -> {
//                    "postId=1030369066752&shareTime=1631765449856&shareUserId=652072612864&shareType=weixin"
                    getDataFromWx(shareWxExtInfo);
//                    contentLayoutBinding.bottomNavigationBar.selectTab(0);
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.PUSH_FORE_TO_HOME, String.class)
                .subscribe(pushPostId -> getDataFromPush(pushPostId), throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.PUBLISH_VIDEO_OR_IMG, Integer.class)
                .subscribe(integer ->
                                ThreadUtils.runOnUiThreadDelayed(() -> {
                                    toPublishBt();
                                }, 600)
                        , throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.HOME_CHOSE_DIALOG_TO_OBJECT_MORE, Integer.class)
                .subscribe(integer -> {
                    contentLayoutBinding.bottomNavigationBar.selectTab(1);
                    ThreadUtils.runOnUiThreadDelayed(() -> {
                        if (topicFragment != null) {
                            topicFragment.selectIndex(1);
                        }
                    }, 600);

                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));


        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.OTHER_ACTIVITY_TO_HOME, Integer.class)
                .subscribe(selectTab -> {
                    ThreadUtils.runOnUiThreadDelayed(() -> {
                        contentLayoutBinding.bottomNavigationBar.selectTab(selectTab);
                    }, 500);
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));
    }

    private void getDataFromWx(String shareWxExtInfo) {
        if (!TextUtils.isEmpty(shareWxExtInfo)) {
            try {
                HashMap<String, String> query_params = new HashMap<>();
                JSONObject json = null;

                json = new JSONObject(shareWxExtInfo);

                String postId = json.getString("postId");
                if (!TextUtils.isEmpty(postId)) {
                    query_params.put("postId", postId);
                }
                String shareTime = json.getString("shareTime");
                if (!TextUtils.isEmpty(shareTime)) {
                    query_params.put("shareTime", shareTime);
                }
                String shareUserId = json.getString("shareUserId");
                if (!TextUtils.isEmpty(shareUserId)) {
                    query_params.put("shareUserId", shareUserId);
                }
                String shareType = json.getString("shareType");
                if (!TextUtils.isEmpty(shareType)) {
                    query_params.put("shareType", shareType);
                }

                String sharePostId = json.getString("sharePostId");
                if (!TextUtils.isEmpty(shareType)) {
                    query_params.put("sharePostId", sharePostId);
                }
                judgeUserIsLogin(query_params, "share");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getDataFromPush(String pushPostId) {
        HwPushUtil.editCornerMarker(mContext, 0);
        if (!TextUtils.isEmpty(pushPostId)) {
            HashMap<String, String> query_params = new HashMap<>();
            query_params.put("postId", pushPostId);
            judgeUserIsLogin(query_params, "push");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionHandler.onRequestPermissionsResult(this, "", requestCode, permissions, grantResults, new PermissionHandler.OnHandlePermissionListener() {
            @Override
            public void granted() {
                jumpSelectVideoOrImageActivity();
            }

            @Override
            public void denied() {
            }

            @Override
            public void deniedAndAskNoMore() {
                if (!SPUtils.getInstance(Constant.SP_PERMISSION_INFO).getBoolean("storage_permission")) {
                    SPUtils.getInstance(Constant.SP_PERMISSION_INFO).put("storage_permission", true);
                    return;
                }
                MaterialDialogUtil.showAlert(MainActivity.this, "需要去权限设置页面打开存储权限", "去设置", "取消", (dialog, which) -> AppUtils.launchAppDetailsSettings(MainActivity.this, requestCode));
            }
        });
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void toPublishBt() {
        if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
            LoginActivity.starActivity();
            return;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        } else {
            jumpSelectVideoOrImageActivity();
        }
    }

    private void checkPermission() {
        if (PermissionHandler.isHandlePermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            jumpSelectVideoOrImageActivity();
        }
    }

    private void checkInstallApp() {

        String date = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getString(Constant.INSTALLED_AD_COLLECT_DAY, "2022-03-01");//15天采集一次
        //15天后
        if (DateUtil.getDateFromNetToProgressDay(date) < 15) {
            return;
        }
        String dateSp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.INSTALLED_AD_COLLECT_DAY, dateSp);

        String path = "userInstallApp.json";
        String appData = CommonUtil.getJson(mContext, path);
        Gson gson = new Gson();
        UserInstallAppListBean listBean = gson.fromJson(appData, UserInstallAppListBean.class);
        try {
            if (listBean != null) {
                if (listBean.getCode() == 0) {
                    List<UserInstallAppListBean.dataBean> beanList = listBean.getData();
                    JSONArray jsonArrayData = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    int size = beanList.size();
                    if (size > 0) {
                        try {
                            for (int i = 0; i < size; i++) {

                                String appName = beanList.get(i).getAppName();
                                String appPackageName = beanList.get(i).getAppPackageName();

                                if (CommonUtil.isAppInstalled(mContext, beanList.get(i).getAppPackageName())) {

                                    JsonObject postJsonObject = new JsonObject();
                                    postJsonObject.addProperty("app_id", beanList.get(i).getAppId());
                                    postJsonObject.addProperty("app_name", beanList.get(i).getAppName());
                                    postJsonObject.addProperty("app_packagename", beanList.get(i).getAppPackageName());
                                    postJsonObject.addProperty("app_tag", beanList.get(i).getAppTag());

                                    JSONObject myjson = new JSONObject(postJsonObject.toString());
                                    jsonArrayData.put(myjson);

//                                    Log.d("install-app2", "已安装：" + appName);
                                    ConstantUtil.appInstalledName = appPackageName + "," + ConstantUtil.appInstalledName;
                                }
                            }
                            jsonObject.putOpt("installed_apps", jsonArrayData);
//                            Log.d("install-app3", "已安装：" + jsonObject.toString());
                            viewModel.setAppUnifyLogNew(jsonObject, this).observe(this, resultBean -> {
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //3s内按两次退出
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && OnMultiClickUtils.isMultiClickClick(3000)) {
            CommonUtils.showShort("再按一次退出应用");
            return true;
        } else {
            MobclickAgent.onKillProcess(mContext);//杀死进程时保存友盟的数据
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, CommonService.class));
        RxBus.getDefault().removeAllStickyEvents();
        UMShareAPI.get(this).release();
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
