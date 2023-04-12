package com.sortinghat.common.gmoread;

import android.app.Activity;
import android.util.Log;

import com.bytedance.msdk.adapter.util.Logger;
import com.bytedance.msdk.api.GMAdEcpmInfo;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardAd;
import com.bytedance.msdk.api.v2.ad.reward.GMRewardedAdLoadCallback;
import com.bytedance.msdk.api.v2.slot.GMAdOptionUtil;
import com.bytedance.msdk.api.v2.slot.GMAdSlotRewardVideo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdRewardManager {
    private static final String TAG = AppConst.TAG_PRE;

    private GMRewardAd mGMRewardAd;
    private Activity mActivity;
    private GMRewardedAdLoadCallback mGMRewardedAdLoadCallback;
    private int mOrientation; //方向
    private String mAdUnitId; //广告位

    public AdRewardManager(Activity activity, GMRewardedAdLoadCallback gmRewardedAdLoadCallback) {
        mActivity = activity;
        mGMRewardedAdLoadCallback = gmRewardedAdLoadCallback;
    }

    public GMRewardAd getGMRewardAd() {
        return mGMRewardAd;
    }


    public void laodAdWithCallback(final String adUnitId, final int orientation) {
        this.mOrientation = orientation;
        this.mAdUnitId = adUnitId;

        /**
         * 判断当前是否存在config 配置 ，如果存在直接加载广告 ，如果不存在则注册config加载回调
         */
        if (GMMediationAdSdk.configLoadSuccess()) {
            Log.e(TAG, "load ad 当前config配置存在，直接加载广告");
            loadAd(adUnitId, orientation);
        } else {
            Log.e(TAG, "load ad 当前config配置不存在，正在请求config配置....");
            GMMediationAdSdk.registerConfigCallback(mSettingConfigCallback); //不用使用内部类，否则在ondestory中无法移除该回调
        }
    }

    private void loadAd(String unitId, int orientation) {
        /**
         * 注：每次加载激励视频广告的时候需要新建一个TTRewardAd，否则可能会出现广告填充问题
         * （ 例如：mttRewardAd = new GMRewardAd(this, adUnitId);）
         */
        mGMRewardAd = new GMRewardAd(mActivity, unitId);

        Map<String, String> customData = new HashMap<>();
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_PANGLE, "pangle media_extra");
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_GDT, "gdt custom data");
        customData.put(GMAdConstant.CUSTOM_DATA_KEY_KS, "ks custom data");
        // 其他需要透传给adn的数据。

        GMAdSlotRewardVideo adSlotRewardVideo = new GMAdSlotRewardVideo.Builder()
                .setMuted(true)//对所有SDK的激励广告生效，除需要在平台配置的SDK，如穿山甲SDK
                .setVolume(0f)//配合Admob的声音大小设置[0-1]
                .setGMAdSlotGDTOption(GMAdOptionUtil.getGMAdSlotGDTOption().build())
                .setGMAdSlotBaiduOption(GMAdOptionUtil.getGMAdSlotBaiduOption().build())
                .setCustomData(customData)
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(3)  //奖励的数量
                .setUserID("user123")//用户id,必传参数
                .setUseSurfaceView(false)
                .setOrientation(orientation)//必填参数，期望视频的播放方向：GMAdConstant.HORIZONTAL 或 GMAdConstant.VERTICAL
                .build();
        mGMRewardAd.loadAd(adSlotRewardVideo, mGMRewardedAdLoadCallback);
    }

    public void destroy() {
        if (mGMRewardAd != null) {
            mGMRewardAd.destroy();
        }
        mActivity = null;
        mGMRewardedAdLoadCallback = null;
        GMMediationAdSdk.unregisterConfigCallback(mSettingConfigCallback); //注销config回调
    }

    /**
     * config回调
     */
    private GMSettingConfigCallback mSettingConfigCallback = new GMSettingConfigCallback() {
        @Override
        public void configLoad() {
            Log.e(TAG, "load ad 在config 回调中加载广告");
            loadAd(mAdUnitId, mOrientation);
        }
    };


    //-----------以下方法非必须 ，按需使用--------------

    //打印已经加载广告的信息
    public void printLoadAdInfo() {
        if (mGMRewardAd == null) {
            return;
        }
        /**
         * 获取已经加载的clientBidding ，多阶底价广告的相关信息
         */
        List<GMAdEcpmInfo> gmAdEcpmInfos = mGMRewardAd.getMultiBiddingEcpm();
        if (gmAdEcpmInfos != null) {
            for (GMAdEcpmInfo info : gmAdEcpmInfos) {
                Log.e(TAG, "***多阶+client相关信息*** AdNetworkPlatformId" + info.getAdNetworkPlatformId()
                        + "  AdNetworkRitId:" + info.getAdNetworkRitId()
                        + "  ReqBiddingType:" + info.getReqBiddingType()
                        + "  PreEcpm:" + info.getPreEcpm()
                        + "  LevelTag:" + info.getLevelTag()
                        + "  ErrorMsg:" + info.getErrorMsg()
                        + "  request_id:" + info.getRequestId()
                        + "  SdkName:" + info.getAdNetworkPlatformName()
                        + "  CustomSdkName:" + info.getCustomAdNetworkPlatformName());
            }
        }

        /**
         * 获取实时填充/缓存池中价格最优的代码位信息即相关价格信息，每次只有一个信息
         */
        GMAdEcpmInfo gmAdEcpmInfo = mGMRewardAd.getBestEcpm();
        if (gmAdEcpmInfo != null) {
            Log.e(TAG, "***实时填充/缓存池中价格最优的代码位信息*** AdNetworkPlatformId" + gmAdEcpmInfo.getAdNetworkPlatformId()
                    + "  AdNetworkRitId:" + gmAdEcpmInfo.getAdNetworkRitId()
                    + "  ReqBiddingType:" + gmAdEcpmInfo.getReqBiddingType()
                    + "  PreEcpm:" + gmAdEcpmInfo.getPreEcpm()
                    + "  LevelTag:" + gmAdEcpmInfo.getLevelTag()
                    + "  ErrorMsg:" + gmAdEcpmInfo.getErrorMsg()
                    + "  request_id:" + gmAdEcpmInfo.getRequestId()
                    + "  SdkName:" + gmAdEcpmInfo.getAdNetworkPlatformName()
                    + "  CustomSdkName:" + gmAdEcpmInfo.getCustomAdNetworkPlatformName());
        }

        /**
         * 获取获取当前缓存池的全部信息
         */
        List<GMAdEcpmInfo> gmCacheInfos = mGMRewardAd.getCacheList();
        if (gmCacheInfos != null) {
            for (GMAdEcpmInfo info : gmCacheInfos) {
                Log.e(TAG, "***缓存池的全部信息*** AdNetworkPlatformId" + info.getAdNetworkPlatformId()
                        + "  AdNetworkRitId:" + info.getAdNetworkRitId()
                        + "  ReqBiddingType:" + info.getReqBiddingType()
                        + "  PreEcpm:" + info.getPreEcpm()
                        + "  LevelTag:" + info.getLevelTag()
                        + "  ErrorMsg:" + info.getErrorMsg()
                        + "  request_id:" + info.getRequestId()
                        + "  SdkName:" + info.getAdNetworkPlatformName()
                        + "  CustomSdkName:" + info.getCustomAdNetworkPlatformName());
            }
        }
    }

    //打印加载失败的adn错误信息
    public void printLoadFailAdnInfo() {
        if (mGMRewardAd == null) {
            return;
        }

        // 获取本次waterfall加载中，加载失败的adn错误信息。
        Log.d(TAG, "reward ad loadinfos: " + mGMRewardAd.getAdLoadInfoList());
    }

    //打印已经展示的广告信息
    public void printSHowAdInfo() {
        if (mGMRewardAd == null) {
            return;
        }

        GMAdEcpmInfo gmAdEcpmInfo = mGMRewardAd.getShowEcpm();
        if (gmAdEcpmInfo == null) {
            return;
        }
        Logger.e(TAG, "展示的广告信息 ：adNetworkPlatformName: " + gmAdEcpmInfo.getAdNetworkPlatformName()
                + "   CustomAdNetworkPlatformName: " + gmAdEcpmInfo.getCustomAdNetworkPlatformName()
                + "   adNetworkRitId: " + gmAdEcpmInfo.getAdNetworkRitId()
                + "   preEcpm: " + gmAdEcpmInfo.getPreEcpm());
    }

}
