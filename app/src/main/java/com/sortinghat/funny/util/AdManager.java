package com.sortinghat.funny.util;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ThreadUtils;
import com.bytedance.msdk.api.reward.RewardItem;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdListener;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdLoadCallback;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.sortinghat.common.gmoread.AdRewardManager;
import com.sortinghat.common.gmoread.AppConst;
import com.sortinghat.funny.R;

import static com.bytedance.sdk.openadsdk.TTAdLoadType.PRELOAD;

public class AdManager {

    public static final int AD_TYPE_TT = 0;
    public static final int AD_TYPE_GM = 1;
    private TTRewardVideoAd mttRewardVideoAd;
    private boolean isLoadTTRewardAd = true;
    private TTAdNative mTTAdNative;
    private Activity mActivity;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private AdListener listener;
    private AdManager() {}

    private static class Holder {
        private static AdManager instance = new AdManager();

    }

    public static AdManager getInstance() {
        return Holder.instance;
    }


    public void setAdListener(AdListener listener) {
        this.listener = listener;
    }
    public void init(Activity activity) {
        mActivity = activity;
        initGMoreRewardAd();
    }

    public void showAd(int adType, AdListener listener) {

        if (adType == 0) {
            if (mttRewardVideoAd != null) {
                toShowRewardVideo(listener);
            } else {
                ThreadUtils.runOnUiThreadDelayed(() -> {
                    toShowRewardVideo(listener);
                }, 3000);
            }
        } else {
            initGMoreRewardAd();
            showGMoreRewardAd(listener);
        }
    }

    private void toShowRewardVideo(AdListener listener) {
        if (mttRewardVideoAd != null) {
            //step6:在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
            //该方法直接展示广告
            mttRewardVideoAd.showRewardVideoAd(mActivity);
            //展示广告，并传入广告展示的场景
//                mttRewardVideoAd.showRewardVideoAd(TaskCentralActivity.this, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
            mttRewardVideoAd = null;

        } else {
           if (listener != null) {
               listener.onFail(false);
           }
        }

    }

    private void initTTRewardAd(AdListener listener) {

        if (!isLoadTTRewardAd || mttRewardVideoAd != null) {
            return;
        }
        isLoadTTRewardAd = false;
        String codeId = mActivity.getResources().getString(R.string.ttad_task_reward_video_id);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                //模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可
//        且仅是模板渲染的代码位ID使用，非模板渲染代码位切勿使用
                .setExpressViewAcceptedSize(500, 500)
                .setUserID("tag123")//tag_id
                .setMediaExtra("media_extra") //附加参数
                .setOrientation(TTAdConstant.VERTICAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .setAdLoadType(PRELOAD)//推荐使用，用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，方便后续为开发者优化相关策略
                .build();
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(mActivity);
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {

                isLoadTTRewardAd = true;
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {

            }

            @Override
            public void onRewardVideoCached(TTRewardVideoAd ad) {

            }


            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
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
                        mHandler.post(() -> {
                            if (listener != null) {
                                listener.onSuccess();
                            }
                        });
                    }

                    @Override
                    public void onVideoComplete() {
                        isLoadTTRewardAd = true;


                    }

                    @Override
                    public void onVideoError() {

                    }

                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errorMsg) {



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
            public void onRewardedAdShowFail(@NonNull com.bytedance.msdk.api.AdError adError) {
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
                mHandler.post(() -> {
                    if (listener != null) {
                        listener.onSuccess();
                    }
                });
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

            }

            @Override
            public void onSkippedVideo() {
                isLoadGMoreRewardAd = true;
            }
        };
        mAdRewardManager = new AdRewardManager(mActivity, new GMRewardedAdLoadCallback() {
            @Override
            public void onRewardVideoLoadFail(@NonNull com.bytedance.msdk.api.AdError adError) {
                mLoadSuccess = false;
                isLoadGMoreRewardAd = true;
                initTTRewardAd(listener);
            }

            @Override
            public void onRewardVideoAdLoad() {
                mLoadSuccess = true;
            }

            @Override
            public void onRewardVideoCached() {
                mLoadSuccess = true;
                if (mIsLoadedAndShow) { //加载并展示
                    showGMoreRewardAd(listener);
                }
            }
        });

        mLoadSuccess = false;
        mIsLoadedAndShow = false;
        mAdRewardManager.laodAdWithCallback(AppConst.gMoreTaskRewardVideoId, GMAdConstant.VERTICAL);
    }


    /**
     * 展示广告
     */
    private void showGMoreRewardAd(AdListener listener) {
        if (mLoadSuccess && mAdRewardManager != null) {
            if (mAdRewardManager.getGMRewardAd() != null && mAdRewardManager.getGMRewardAd().isReady()) {
                //在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
                //该方法直接展示广告，如果展示失败了（如过期），会回调onVideoError()
                //展示广告，并传入广告展示的场景
                mAdRewardManager.getGMRewardAd().setRewardAdListener(mGMRewardedAdListener);
                mAdRewardManager.getGMRewardAd().showRewardAd(mActivity);
                mLoadSuccess = false;
            } else {
                mLoadSuccess = false;
                mIsLoadedAndShow = true;
                mAdRewardManager.laodAdWithCallback(AppConst.gMoreTaskRewardVideoId, GMAdConstant.VERTICAL);
                if (listener != null) {
                    listener.onFail(false);
                }
            }
        } else {
            if (listener != null) {
                listener.onFail(false);
            }
        }
    }

    public void destroy() {
        mActivity = null;
        mTTAdNative = null;
        mAdRewardManager = null;
        mHandler = null;
    }

    public interface AdListener {
        void onSuccess();
        void onFail(boolean loaded);
    }
}
