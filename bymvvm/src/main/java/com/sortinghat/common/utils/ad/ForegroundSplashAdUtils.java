package com.sortinghat.common.utils.ad;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.MainThread;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.sortinghat.common.utils.TTAdManagerHolder;
import com.sortinghat.common.utils.statusbar.StatusBarUtil;
import com.umeng.analytics.MobclickAgent;

public class ForegroundSplashAdUtils {


    /**
     * 加载广告
     */
    public static void loadCSJSplashAD(final Context mContext) {
        final FrameLayout rootView = ((Activity) mContext).getWindow().getDecorView().findViewById(android.R.id.content);
        final FrameLayout container = new FrameLayout(mContext);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            params.topMargin = StatusBarUtil.getStatusBarHeight(mContext);
        } else {
            params.topMargin = 0;
        }
        rootView.addView(container, params);

        loadCSJAd(mContext, rootView, container);

    }

    /**
     * 加载广点通广告代码
     */
    private static void loadCSJAd(Context mContext, FrameLayout rootView, FrameLayout container) {
        TTAdNative mTTAdNative = TTAdManagerHolder.get().createAdNative(mContext);
        String splashAdId = "887646695";
        int AD_TIME_OUT = 3000;
        boolean isShowSplashAD = SPUtils.getInstance("config_info").getBoolean("ad_fore_splash_show", true);//是否显示
        if (!isShowSplashAD) {
            try {
                rootView.removeView(container);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            int expressViewWidth = SizeUtils.px2dp( ScreenUtils.getScreenWidth());
            int expressViewHeight =  SizeUtils.px2dp(ScreenUtils.getScreenHeight() - StatusBarUtil.getStatusBarHeight(mContext));
            if (expressViewWidth < 200) {
                expressViewWidth = 200;
            }
            if (expressViewHeight < 300) {
                expressViewHeight = 300;
            }

            //step2:创建TTAdNative对象
            //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(splashAdId)
                    .setSupportDeepLink(true)
                    .setExpressViewAcceptedSize((int) expressViewWidth, (int) expressViewHeight)
                    .build();
            //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
            mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
                @Override
                @MainThread
                public void onError(int code, String message) {
                    LogUtils.e("splashad--", "error:csj" + code + message.toString());
                    try {
                        rootView.removeView(container);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                @MainThread
                public void onTimeout() {
                    LogUtils.e("splashad--", "error:csj：onTimeout");
                    try {
                        rootView.removeView(container);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                @MainThread
                public void onSplashAdLoad(TTSplashAd ad) {

                    if (ad == null) {
                        try {
                            rootView.removeView(container);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    //获取SplashView
                    View view = ad.getSplashView();
                    if (view != null && container != null && !((Activity) mContext).isFinishing()) {
                        container.removeAllViews();
                        //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
                        container.addView(view);
                        //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                        //设置SplashView的交互监听器
                        ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                            @Override
                            public void onAdClicked(View view, int type) {
                                try {
                                    rootView.removeView(container);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onAdShow(View view, int type) {
                            }

                            @Override
                            public void onAdSkip() {
                                try {
                                    rootView.removeView(container);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onAdTimeOver() {
                                try {
                                    rootView.removeView(container);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        try {
                            rootView.removeView(container);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, AD_TIME_OUT);

        } catch (Exception e) {
            e.printStackTrace();
            MobclickAgent.reportError(mContext, e);
        }

    }


}
