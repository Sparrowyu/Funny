package com.sortinghat.common.utils.ad;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.MainThread;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.sortinghat.common.R;
import com.sortinghat.common.utils.ConstantUtilsByMvvm;
import com.sortinghat.common.utils.TTAdManagerHolder;
import com.sortinghat.common.utils.statusbar.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class SplashAdActivity extends Activity implements WeakHandler.IHandler {
    private TTAdNative mTTAdNative;
    private FrameLayout mSplashContainer;
    private WeakHandler mHandler = new WeakHandler(this);
    //开屏广告加载超时时间,建议大于1000,这里为了冷启动第一次加载到广告并且展示,示例设置了2000ms
    private static final int AD_TIME_OUT = 3000;
    private static final int MSG_GO_MAIN = 1;
    //开屏广告是否已经加载
    private boolean mHasLoaded;
    //从穿山甲点击广告回来，是否强制跳转到主页面
    private boolean mForceGoMain = false;
    private List<Integer> adOrderConfig = new ArrayList<>();//o代表穿山甲，1广点通

    @SuppressWarnings("RedundantCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_ad);
        mSplashContainer = (FrameLayout) findViewById(R.id.splash_container);
        //step2:创建TTAdNative对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);

        loadSplashAd();
    }

    //0穿山甲 1：广点通 -1不显示
    private void loadSplashAd() {
        int currentAd = ConfigAdUtil.getForeSplashAdType();
        switch (currentAd) {
            case 0:
                startMainActivity();
                break;
            case 1:
                loadTTSplashAd();
                break;
            case 2:
                loadTTSplashAd();
                break;
            default:
                loadTTSplashAd();
                break;
        }
    }

    private float expressViewWidth = 1080;
    private float expressViewHeight = 1920;

    /**
     * 加载穿山甲开屏广告
     */
    private void loadTTSplashAd() {

        try {
            expressViewWidth = SizeUtils.px2dp(ScreenUtils.getScreenWidth());
            expressViewHeight = SizeUtils.px2dp(ScreenUtils.getScreenHeight() - SizeUtils.dp2px(20) - 200);
            if (expressViewWidth < 200) {
                expressViewWidth = 200;
            }
            if (expressViewHeight < 300) {
                expressViewHeight = 300;
            }
            //定时，AD_TIME_OUT时间到时执行，如果开屏广告没有加载则跳转到主页面
            if (mHandler != null) {
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_GO_MAIN;
                mHandler.sendMessageDelayed(msg, AD_TIME_OUT + 500);//自己判断超时多加0.5秒，避免可能展示率的问题
            }
            String splashAdId = "887665767";
            //step2:创建TTAdNative对象
            mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
            //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
            AdSlot adSlot = new AdSlot.Builder()
                    .setCodeId(splashAdId)
                    .setSupportDeepLink(true)
//                    .setExpressViewAcceptedSize(1080, 1920)
                    .setExpressViewAcceptedSize((int) expressViewWidth, (int) expressViewHeight)
                    .build();
            //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
            mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
                @Override
                @MainThread
                public void onError(int code, String message) {
                    LogUtils.e("splashad--", "error:csj" + code + message.toString());
                    mHasLoaded = true;
                    startMainActivity();
                }

                @Override
                @MainThread
                public void onTimeout() {
                    LogUtils.e("splashad--", "error:csj：onTimeout");
                    mHasLoaded = true;
                    startMainActivity();
                }

                @Override
                @MainThread
                public void onSplashAdLoad(TTSplashAd ad) {
                    mHasLoaded = true;
                    if (mHandler != null) {
                        mHandler.removeCallbacksAndMessages(null);
                    }
                    if (ad == null) {
                        return;
                    }
                    //获取SplashView
                    View view = ad.getSplashView();
                    if (view != null && mSplashContainer != null && !SplashAdActivity.this.isFinishing()) {
                        mSplashContainer.removeAllViews();
                        //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
                        mSplashContainer.addView(view);
                        //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                        //设置SplashView的交互监听器
                        ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                            @Override
                            public void onAdClicked(View view, int type) {
                                mForceGoMain = true;
                            }

                            @Override
                            public void onAdShow(View view, int type) {
                            }

                            @Override
                            public void onAdSkip() {
                                startMainActivity();
                            }

                            @Override
                            public void onAdTimeOver() {
                                startMainActivity();
                            }
                        });
                    } else {
                        startMainActivity();
                    }
                }
            }, AD_TIME_OUT);

        } catch (Exception e) {
            e.printStackTrace();
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

    private void startMainActivity() {
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mForceGoMain = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConstantUtilsByMvvm.BACKGROUND_PAUSE_TIME = 0;
    }

    @Override
    protected void onResume() {
        StatusBarUtil.setStatusBarTransparent(this);
        super.onResume();
        //如果是点击完穿山甲的广告触发的onResume方法则直接跳转到首页
        if (mForceGoMain) {
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

}
