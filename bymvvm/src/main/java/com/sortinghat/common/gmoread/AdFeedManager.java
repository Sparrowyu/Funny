package com.sortinghat.common.gmoread;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.bytedance.msdk.adapter.util.Logger;
import com.bytedance.msdk.api.GMAdEcpmInfo;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMUnifiedNativeAd;
import com.bytedance.msdk.api.v2.slot.GMAdOptionUtil;
import com.bytedance.msdk.api.v2.slot.GMAdSlotNative;
import com.bytedance.msdk.api.v2.slot.paltform.GMAdSlotGDTOption;
import com.sortinghat.common.utils.CommonUtils;

import java.util.List;

public class AdFeedManager {
    private static final String TAG = AppConst.TAG_PRE + AdFeedManager.class.getSimpleName();

    private GMUnifiedNativeAd mGMUnifiedNativeAd;
    private Activity mActivity;
    private GMNativeAdLoadCallback mGMNativeAdLoadCallback;
    private String mAdUnitId; //广告位
    private int mAdCount; //广告数量
    private int mStyleType; //模板类型，可以不传

    public AdFeedManager(Activity activity, GMNativeAdLoadCallback gmNativeAdLoadCallback) {
        mActivity = activity;
        mGMNativeAdLoadCallback = gmNativeAdLoadCallback;
    }

    public GMUnifiedNativeAd getGMUnifiedNativeAd() {
        return mGMUnifiedNativeAd;
    }


    /**
     *
     * @param adUnitId 广告位ID
     * @param adCount 广告数量
     *
     */
    /**
     * @param adUnitId  广告位ID
     * @param adCount   广告数量
     * @param styleType //模板类型，兜底使用，可以不传
     */
    public void loadAdWithCallback(final String adUnitId, int adCount, int styleType) {
        this.mAdUnitId = adUnitId;
        this.mAdCount = adCount;
        this.mStyleType = styleType;

        /**
         * 判断当前是否存在config 配置 ，如果存在直接加载广告 ，如果不存在则注册config加载回调
         */
        if (GMMediationAdSdk.configLoadSuccess()) {
            Log.e(TAG, "load ad 当前config配置存在，直接加载广告");
            loadAd(adUnitId, adCount, styleType);
        } else {
            Log.e(TAG, "load ad 当前config配置不存在，正在请求config配置....");
            GMMediationAdSdk.registerConfigCallback(mSettingConfigCallback); //不用使用内部类，否则在ondestory中无法移除该回调
        }
    }

    private float expressViewWidth = 1080;
    private float expressViewHeight = 1080;

    private void loadAd(String adUnitId, int adCount, int styleType) {
        expressViewWidth = SizeUtils.px2dp(ScreenUtils.getScreenWidth());
        expressViewHeight = SizeUtils.px2dp(ScreenUtils.getScreenHeight() - SizeUtils.dp2px(20) - 200);

        mGMUnifiedNativeAd = new GMUnifiedNativeAd(mActivity, adUnitId);//模板视频

        // 针对Gdt Native自渲染广告，可以自定义gdt logo的布局参数。该参数可选,非必须。
        FrameLayout.LayoutParams gdtNativeAdLogoParams =
                new FrameLayout.LayoutParams(
                        SizeUtils.dp2px(40),
                        SizeUtils.dp2px(13),
                        Gravity.RIGHT | Gravity.TOP); // 例如，放在右上角


        GMAdSlotGDTOption.Builder adSlotNativeBuilder = GMAdOptionUtil.getGMAdSlotGDTOption()
                .setNativeAdLogoParams(gdtNativeAdLogoParams);

        /**
         * 创建feed广告请求类型参数GMAdSlotNative,具体参数含义参考文档
         * 备注
         * 1: 如果是信息流自渲染广告，设置广告图片期望的图片宽高 ，不能为0
         * 2:如果是信息流模板广告，宽度设置为希望的宽度，高度设置为0(0为高度选择自适应参数)
         */


        GMAdSlotNative adSlotNative = new GMAdSlotNative.Builder()
                .setGMAdSlotBaiduOption(GMAdOptionUtil.getGMAdSlotBaiduOption().build())//百度相关的配置
                .setGMAdSlotGDTOption(adSlotNativeBuilder.build())//gdt相关的配置
                .setAdmobNativeAdOptions(GMAdOptionUtil.getAdmobNativeAdOptions())//admob相关配置
                .setAdStyleType(styleType)//必传，表示请求的模板广告还是原生广告，AdSlot.TYPE_EXPRESS_AD：模板广告 ； AdSlot.TYPE_NATIVE_AD：原生广告
                // 备注
                // 1:如果是信息流自渲染广告，设置广告图片期望的图片宽高 ，不能为0
                // 2:如果是信息流模板广告，宽度设置为希望的宽度，高度设置为0(0为高度选择自适应参数)
                .setImageAdSize((int) expressViewWidth, (int) expressViewHeight)// 必选参数 单位dp ，详情见上面备注解释
                .setAdCount(adCount)//请求广告数量为1到3条
                .build();

        //请求广告，调用feed广告异步请求接口，加载到广告后，拿到广告素材自定义渲染
        /**
         * 注：每次加载信息流广告的时候需要新建一个GMUnifiedNativeAd，否则可能会出现广告填充问题
         * (例如：mTTAdNative = new GMUnifiedNativeAd(this, mAdUnitId);）
         */
        mGMUnifiedNativeAd.loadAd(adSlotNative, mGMNativeAdLoadCallback);
    }

    public void destroy() {
        if (mGMUnifiedNativeAd != null) {
            mGMUnifiedNativeAd.destroy();
        }
        mActivity = null;
        mGMNativeAdLoadCallback = null;
        GMMediationAdSdk.unregisterConfigCallback(mSettingConfigCallback); //注销config回调
    }

    /**
     * config回调
     */
    private GMSettingConfigCallback mSettingConfigCallback = new GMSettingConfigCallback() {
        @Override
        public void configLoad() {
            Log.e(TAG, "load ad 在config 回调中加载广告");
            loadAd(mAdUnitId, mAdCount, mStyleType);
        }
    };


    //-----------以下方法非必须 ，按需使用--------------

    //打印已经加载广告的信息
    public void printLoadAdInfo() {
        if (mGMUnifiedNativeAd == null) {
            return;
        }
        /**
         * 获取已经加载的clientBidding ，多阶底价广告的相关信息
         */
        List<GMAdEcpmInfo> gmAdEcpmInfos = mGMUnifiedNativeAd.getMultiBiddingEcpm();
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
         * 获取获取当前缓存池的全部信息
         */
        List<GMAdEcpmInfo> gmCacheInfos = mGMUnifiedNativeAd.getCacheList();
        if (gmCacheInfos != null) {
            for (GMAdEcpmInfo info : gmCacheInfos) {
                Log.e(AppConst.TAG, "   ");
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
        if (mGMUnifiedNativeAd == null) {
            return;
        }

        // 获取本次waterfall加载中，加载失败的adn错误信息。
        Log.d(TAG, "reward ad loadinfos: " + mGMUnifiedNativeAd.getAdLoadInfoList());
    }

    //打印已经展示的广告信息
    public void printShowAdInfo(GMNativeAd gmNativeAd) {
        if (gmNativeAd == null) {
            return;
        }
        GMAdEcpmInfo gmAdEcpmInfo = gmNativeAd.getShowEcpm();
        if (gmAdEcpmInfo == null) {
            return;
        }

        Logger.e(TAG, "展示的广告信息 ：adNetworkPlatformName: " + gmAdEcpmInfo.getAdNetworkPlatformName()
                + "   CustomAdNetworkPlatformName: " + gmAdEcpmInfo.getCustomAdNetworkPlatformName()
                + "   adNetworkRitId: " + gmAdEcpmInfo.getAdNetworkRitId()
                + "   preEcpm: " + gmAdEcpmInfo.getPreEcpm());
    }

}
