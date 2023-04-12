package com.sortinghat.funny.ui.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.reward.RewardItem;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdListener;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdLoadCallback;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.sortinghat.common.adapter.FragmentPagerAdapter;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.gmoread.AdRewardManager;
import com.sortinghat.common.gmoread.AppConst;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.TaskCentralBean;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivityTaskCentralBinding;
import com.sortinghat.funny.databinding.DialogTaskGoldSignBinding;
import com.sortinghat.funny.util.CommonUserInfo;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.viewmodel.TaskCentralViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.bytedance.sdk.openadsdk.TTAdLoadType.PRELOAD;

public class TaskCentralActivity extends BaseActivity<TaskCentralViewModel, ActivityTaskCentralBinding> {
    private ArrayList<String> titleList = new ArrayList<>(2);
    private ArrayList<Fragment> fragmentList = new ArrayList<>(2);
    private int currentIndex = 0;
    private TaskCentralBean taskCentralBean = new TaskCentralBean();
    private List<ImageView> imageViewList = new ArrayList<>();
    private List<View> tomorrowRlList = new ArrayList<>();
    private List<Integer> goldList = new ArrayList<>();
    private TTAdNative mTTAdNative;
    private int currentDay = 0;
    private long currentStarCount = 0;

    private boolean currentLoadIsTT = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_task_central;
    }

    @Override
    protected void initViews() {

        int adH = new Random().nextInt(2);
        if (adH / 2 == 0) {
            currentLoadIsTT = true;
        } else {
            currentLoadIsTT = false;
        }

        currentLoadIsTT = false;
        if (!ConstantUtil.isHuaweiThroughReviewState(this)) {
            currentLoadIsTT = true;
        }
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(this);
        initTitleBar("任务中心");
        initFragmentList();
        subscibeRxBus();
        initViewPagerAdapter();
        titleBarBinding.tvRightText.setVisibility(View.VISIBLE);
        titleBarBinding.tvRightText.setText("规则");
        titleBarBinding.vDividerLine.setVisibility(View.GONE);
        titleBarBinding.tvRightText.setOnClickListener(quickClickListener);
        imageViewList.add(contentLayoutBinding.imgDay1);
        imageViewList.add(contentLayoutBinding.imgDay2);
        imageViewList.add(contentLayoutBinding.imgDay3);
        imageViewList.add(contentLayoutBinding.imgDay4);
        imageViewList.add(contentLayoutBinding.imgDay5);
        imageViewList.add(contentLayoutBinding.imgDay6);
        imageViewList.add(contentLayoutBinding.imgDay7);
        tomorrowRlList.add(contentLayoutBinding.taskTomorrow1);
        tomorrowRlList.add(contentLayoutBinding.taskTomorrow2);
        tomorrowRlList.add(contentLayoutBinding.taskTomorrow3);
        tomorrowRlList.add(contentLayoutBinding.taskTomorrow4);
        tomorrowRlList.add(contentLayoutBinding.taskTomorrow5);
        tomorrowRlList.add(contentLayoutBinding.taskTomorrow6);
        tomorrowRlList.add(contentLayoutBinding.taskTomorrow7);
        goldList.add(60);
        goldList.add(30);
        goldList.add(40);
        goldList.add(30);
        goldList.add(40);
        goldList.add(50);
        goldList.add(50);

        if (null != getIntent()) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("hint"))) {
            }
        }
        contentLayoutBinding.tvTaskSignDay.setOnClickListener(quickClickListener);
        contentLayoutBinding.signBt.setOnClickListener(quickClickListener);

    }

    private void initFragmentList() {
        titleList.clear();
        fragmentList.clear();
        titleList.add("今日任务");
        titleList.add("新手任务");
        fragmentList.add(new TaskTodayFragment(0));
        fragmentList.add(new TaskTodayFragment(1));

    }

    private void initViewPagerAdapter() {
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titleList);
        contentLayoutBinding.viewPager.setAdapter(pagerAdapter);
        contentLayoutBinding.tabLayout.setupWithViewPager(contentLayoutBinding.viewPager);
        contentLayoutBinding.viewPager.setCurrentItem(currentIndex);
        contentLayoutBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position < fragmentList.size() && fragmentList.get(position) != null) {
//                    ((MyLikeImgFragment) fragmentList.get(position)).refreshData();
                }
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initData() {
        viewModel.getTaskCentralData().observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    taskCentralBean = resultBean.getData();
                    updateView(taskCentralBean);
                }
            }
        });
        viewModel.setAppUnifyLog("entry_taskcenter_succ", "user_profile", this).observe(this, resultBean -> {
        });
    }

    private void updateView(TaskCentralBean taskCentralBean) {
        if (taskCentralBean == null) {
            return;
        }
        currentDay = taskCentralBean.getCurrentDay();
        String pendantUrl = taskCentralBean.getPendantUrl();
        updateUserIcon(pendantUrl);

        if (!TextUtils.isEmpty(CommonUserInfo.userIconImg)) {
            GlideUtils.loadCircleImage(CommonUserInfo.userIconImg, contentLayoutBinding.ivUserIcon);
        }
        currentStarCount = taskCentralBean.getStarNoteCount();
        contentLayoutBinding.tvStarCount.setText(currentStarCount + "");
//        ConstantView.initGoldAnim(contentLayoutBinding.tvStarCount, currentStarCount + "");
        contentLayoutBinding.tvTaskSignDay.setText("" + taskCentralBean.getSignDays());

        if (imageViewList.size() == 7 && taskCentralBean.getSignInfoList() != null && taskCentralBean.getSignInfoList().size() == 7) {

            goldList = taskCentralBean.getSignAward();
            if (goldList.size() == 7) {
                contentLayoutBinding.tvGold1.setText("+" + goldList.get(0));
                contentLayoutBinding.tvGold2.setText("+" + goldList.get(1));
                contentLayoutBinding.tvGold3.setText("+" + goldList.get(2));
                contentLayoutBinding.tvGold4.setText("+" + goldList.get(3));
                contentLayoutBinding.tvGold5.setText("+" + goldList.get(4));
                contentLayoutBinding.tvGold6.setText("+" + goldList.get(5));
                contentLayoutBinding.tvGold7.setText("+" + goldList.get(6));
            }

            for (int i = 0; i < taskCentralBean.getSignInfoList().size(); i++) {
                if (i > currentDay - 1) {
                    imageViewList.get(i).setImageResource(R.mipmap.task_gold);
                } else if (taskCentralBean.getSignInfoList().get(i)) {
                    imageViewList.get(i).setImageResource(R.mipmap.task_gold_had);
                } else {
                    imageViewList.get(i).setImageResource(R.mipmap.task_gold_gray);
                }
            }

            //当天如果是true，代表已经签到
            if (taskCentralBean.getSignInfoList().get(currentDay - 1)) {
                contentLayoutBinding.signBt.setBackgroundResource(R.drawable.click_f5f5f5_corner50_bt_bg);
                contentLayoutBinding.signBt.setTextColor(CommonUtils.getColor(R.color.color_CACACA));
                contentLayoutBinding.signBt.setEnabled(true);
                contentLayoutBinding.signBt.setText("今日已签到");
                if (currentDay < 7 && tomorrowRlList.size() == 7) {
                    tomorrowRlList.get(currentDay).setVisibility(View.VISIBLE);
                }
            } else {
                contentLayoutBinding.signBt.setBackgroundResource(R.drawable.task_sign_sure_bt);
                contentLayoutBinding.signBt.setTextColor(CommonUtils.getColor(R.color.white));
                contentLayoutBinding.signBt.setEnabled(true);
                contentLayoutBinding.signBt.setText("立即签到");
                imageViewList.get(currentDay - 1).setImageResource(R.mipmap.task_gold);
            }


        }
    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.tv_right_text:
                    if (ConstantUtil.isInfoTest()) {
                        showSignGoldDialog(1, 0, 1122, 30);
                    } else {
                        CommonWebActivity.starWebActivity(TaskCentralActivity.this, "规则", "https://share.gaoxiaoxingqiu.com/TaskRules.html");
                    }
                    LogUtils.d("ForbidTopics---", "-message:");
                    break;
                case R.id.sign_bt:
                    viewModel.getTaskDoSign().observe(TaskCentralActivity.this, resultBean -> {
                        if (resultBean != null) {
                            if (resultBean.getCode() == 0) {
                                if (null != resultBean.getData() && !TextUtils.isEmpty(resultBean.getData().toString())) {
                                    int gold = resultBean.getData().getStarNote();
                                    viewModel.setAppUnifyLog("sign_in_succ", "taskCenter", TaskCentralActivity.this).observe(TaskCentralActivity.this, resultBean1 -> {
                                    });
//                                    CommonUtils.showShort(resultBean.getData().getToast());
                                }
                                contentLayoutBinding.signBt.setEnabled(false);
//                                initData();
                                int currentDay = taskCentralBean.getCurrentDay() - 1;
                                if ((currentDay) < goldList.size()) {
                                    showSignGoldDialog(0, currentDay + 1, 0, goldList.get(currentDay));
                                }

                            }
                        }
                    });
                    break;
            }
        }
    };

    /**
     * doubleType 0:签到  1任务
     */
    private void showSignGoldDialog(int doubleType, int day, int taskId, int gold) {
        MaterialDialog dialog = MaterialDialogUtil.showCustomWithCloseButtonDialog(mContext, R.layout.dialog_task_gold_sign);
        DialogTaskGoldSignBinding taskBinding = DataBindingUtil.bind(dialog.getCustomView());

        if (doubleType == 0) {
            taskBinding.tvTitle.setText("签到成功");
        } else {
            taskBinding.tvTitle.setText("领取成功");
        }

        taskBinding.tvGold.setText("+" + gold + "星币");
        taskBinding.dialogCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        initRewardAd(doubleType, day, taskId);

        //奖励激励视频
        taskBinding.dialogDoubleSure.setOnClickListener(view -> {
            initData();
//            ConstantView.initGoldAnim(contentLayoutBinding.tvStarCount, currentStarCount + "");
            if (currentLoadIsTT) {
                if (mttRewardVideoAd != null) {
                    toShowRewardVideo();
                    dialog.dismiss();
                } else {
                    progressDialog = MaterialDialogUtil.showCustomProgressDialog(mContext);
                    ThreadUtils.runOnUiThreadDelayed(() -> {
                        dialog.dismiss();
                        closeProgressDialog();
                        toShowRewardVideo();
                    }, 3000);
                }
            } else {
                if (mLoadSuccess && mAdRewardManager != null) {
                    dialog.dismiss();
                    showGMoreRewardAd();
                }else {
                    CommonUtils.showShort("请先加载广告" + mLoadSuccess);
                }
            }
        });
        taskBinding.dialogSure.setOnClickListener(view -> {
            dialog.dismiss();
            initData();
//            ConstantView.initGoldAnim(contentLayoutBinding.tvStarCount, currentStarCount + "");
        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialog.getCustomView().getLayoutParams();
        params.gravity = Gravity.CENTER;
        dialog.getCustomView().setLayoutParams(params);
        dialog.show();

    }

    private void updateUserIcon(String pendantUrl) {
        CommonUserInfo.userIconImgBox = pendantUrl;
        if (!TextUtils.isEmpty(pendantUrl)) {
            contentLayoutBinding.ivBoxUserIcon.setVisibility(View.VISIBLE);
            GlideUtils.loadImageNoPlaceholder(pendantUrl, contentLayoutBinding.ivBoxUserIcon);
        } else {
            contentLayoutBinding.ivBoxUserIcon.setVisibility(View.GONE);
        }
    }

    private void subscibeRxBus() {
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.TASK_TO_LOAD_REWARD_VIDEO, String.class)
                .subscribe(sting -> {
                    String[] array = sting.split(",");
                    if (array.length != 2) {
                        return;
                    }
                    int reward = Integer.valueOf(array[1]);
                    currentStarCount = currentStarCount + reward;
//                    ConstantView.initGoldAnim(contentLayoutBinding.tvStarCount, currentStarCount + "");
                    showSignGoldDialog(1, 0, Integer.valueOf(array[0]), reward);
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.TASK_TO_UPDATE_CENTRAL_USER_BOX, String.class)
                .subscribe(pendantUrl -> {
                    updateUserIcon(pendantUrl);
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));
    }

    private void toShowRewardVideo() {
        if (mttRewardVideoAd != null) {
            //step6:在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
            //该方法直接展示广告
            mttRewardVideoAd.showRewardVideoAd(TaskCentralActivity.this);
            //展示广告，并传入广告展示的场景
//                mttRewardVideoAd.showRewardVideoAd(TaskCentralActivity.this, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
            mttRewardVideoAd = null;
        } else {
            CommonUtils.showShort("加载视频失败，请重试");
        }

    }


    private void initRewardAd(int doubleType, int day, int taskId) {
        this.doubleType = doubleType;
        this.doubleDay = day;
        this.doubleTaskId = taskId;

        if (currentLoadIsTT) {
            initTTRewardAd();
        } else {
            initGMoreRewardAd();
        }

    }

    /**
     * 加载穿山甲的激励视频
     *
     * @param playNow 是否立刻播放
     * *
     * 加载穿山甲激励广告
     */
    private final String csjAdloadTag = "csjAdloadTag";
    private boolean mIsLoaded;

    private boolean isLoadTTRewardAd = true;
    private TTRewardVideoAd mttRewardVideoAd;

    private void initTTRewardAd() {

        if (!isLoadTTRewardAd || mttRewardVideoAd != null) {
            return;
        }
        isLoadTTRewardAd = false;
        String codeId = this.getResources().getString(R.string.ttad_task_reward_video_id);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                //模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可
//        且仅是模板渲染的代码位ID使用，非模板渲染代码位切勿使用
                .setExpressViewAcceptedSize(500, 500)
                .setUserID("tag123")//tag_id
                .setMediaExtra("media_extra") //附加参数
//                .setDownloadType(ConstantUtil.csjAdShowPop)//应用每次下载都需要触发弹窗披露应用信息，目前暂不需要
                .setOrientation(TTAdConstant.VERTICAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .setAdLoadType(PRELOAD)//推荐使用，用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，方便后续为开发者优化相关策略
                .build();

        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e(csjAdloadTag, "Callback --> onError: " + code + ", " + String.valueOf(message));
                isLoadTTRewardAd = true;
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                Log.e(csjAdloadTag, "Callback --> onRewardVideoCached");
                mIsLoaded = true;
            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ad) {
                Log.e(csjAdloadTag, "Callback --> onRewardVideoCached");
                mIsLoaded = true;
//                ad.showRewardVideoAd(TaskCentralActivity.this, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
            }


            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                mIsLoaded = false;
                mttRewardVideoAd = ad;
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
                    @Override
                    public void onAdShow() {

                    }

                    @Override
                    public void onAdVideoBarClick() {

                    }

                    @Override
                    public void onAdClose() {
                        isLoadTTRewardAd = true;
                    }

                    @Override
                    public void onVideoComplete() {
                        isLoadTTRewardAd = true;
                        String logString = "" + "onVideoComplete";
                        Log.e(csjAdloadTag, "Callback --> " + logString);
                    }

                    @Override
                    public void onVideoError() {

                    }

                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errorMsg) {
                        String logString = "verify:" + rewardVerify + " amount:" + rewardAmount +
                                " name:" + rewardName + " errorCode:" + errorCode + " errorMsg:" + errorMsg;
                        Log.e(csjAdloadTag, "Callback --> " + logString);
                        getTaskDoubleReceiveAward();
                    }

                    @Override
                    public void onRewardArrived(boolean b, int i, Bundle bundle) {

                    }

                    @Override
                    public void onSkippedVideo() {
                        isLoadTTRewardAd = true;
                    }
                });
            }
        });
    }

    private AdRewardManager mAdRewardManager; //聚合激励视频管理类

    private boolean mLoadSuccess; //是否加载成功
    private boolean mIsLoadedAndShow;//广告加载成功并展示
    private GMRewardedAdListener mGMRewardedAdListener;
    private boolean isLoadGMoreRewardAd = true;

    private void initGMoreRewardAd() {
        if (!isLoadGMoreRewardAd) {
            return;
        }
        isLoadGMoreRewardAd = false;
        mGMRewardedAdListener = new GMRewardedAdListener() {
            @Override
            public void onRewardedAdShow() {

            }

            @Override
            public void onRewardedAdShowFail(@NonNull AdError adError) {
                isLoadGMoreRewardAd = true;
                if (adError == null) {
                    return;
                }
            }

            @Override
            public void onRewardClick() {

            }

            @Override
            public void onRewardedAdClosed() {
                isLoadGMoreRewardAd = true;
            }

            @Override
            public void onVideoComplete() {
                isLoadGMoreRewardAd = true;
            }

            @Override
            public void onVideoError() {
                isLoadGMoreRewardAd = true;
            }

            @Override
            public void onRewardVerify(@NonNull RewardItem rewardItem) {
                getTaskDoubleReceiveAward();
            }

            @Override
            public void onSkippedVideo() {
                isLoadGMoreRewardAd = true;
            }
        };

        mLoadSuccess = false;
        mIsLoadedAndShow = false;

        mAdRewardManager = new AdRewardManager(this, new GMRewardedAdLoadCallback() {
            @Override
            public void onRewardVideoLoadFail(@NonNull AdError adError) {
                mLoadSuccess = false;
                isLoadGMoreRewardAd = true;
                currentLoadIsTT = true;
                initTTRewardAd();
            }

            @Override
            public void onRewardVideoAdLoad() {
                mLoadSuccess = true;
            }

            @Override
            public void onRewardVideoCached() {
                mLoadSuccess = true;
                if (mIsLoadedAndShow) { //加载并展示
                    showGMoreRewardAd();
                }
            }
        });


        mAdRewardManager.laodAdWithCallback(AppConst.gMoreTaskRewardVideoId, GMAdConstant.VERTICAL);
    }


    /**
     * 展示广告
     */
    private void showGMoreRewardAd() {
        if (mLoadSuccess && mAdRewardManager != null) {

            if (mAdRewardManager.getGMRewardAd() != null && mAdRewardManager.getGMRewardAd().isReady()) {
                //在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
                //该方法直接展示广告，如果展示失败了（如过期），会回调onVideoError()
                //展示广告，并传入广告展示的场景
                mAdRewardManager.getGMRewardAd().setRewardAdListener(mGMRewardedAdListener);
                mAdRewardManager.getGMRewardAd().showRewardAd(this);
                mLoadSuccess = false;
            } else {
                mLoadSuccess = false;
                mIsLoadedAndShow = true;
                mAdRewardManager.laodAdWithCallback(AppConst.gMoreTaskRewardVideoId, GMAdConstant.VERTICAL);
//                CommonUtils.showShort( "请先加载广告");
            }
        } else {
            CommonUtils.showShort("请先加载广告" + mLoadSuccess);
        }
    }

    private int doubleType;
    private int doubleDay;
    private int doubleTaskId;

    private void getTaskDoubleReceiveAward() {
        progressDialog = MaterialDialogUtil.showCustomProgressDialog(mContext);
        viewModel.getTaskDoubleReceiveAward(doubleType, doubleDay, doubleTaskId).observe(this, resultBean -> {
            closeProgressDialog();
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    currentStarCount = currentStarCount + resultBean.getData().getAwardStarNote();
                    contentLayoutBinding.tvStarCount.setText(currentStarCount + "");
//                    ConstantView.initGoldAnim(contentLayoutBinding.tvStarCount, currentStarCount + "");
//                    initData();
                    //任务中心显示toast“观看视频成功奖励已翻倍”
                }
                if (null != resultBean.getData() && !TextUtils.isEmpty(resultBean.getData().toString())) {
                    CommonUtils.showShort(resultBean.getData().getToast());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTTAdNative = null;
        if (mAdRewardManager != null) {
            mAdRewardManager.destroy();
        }
    }

    public static void starActivity(Context mContext, String hint) {
        Intent intent = new Intent(mContext, TaskCentralActivity.class);
        intent.putExtra("hint", hint);
        ActivityUtils.startActivity(intent);
    }


}

