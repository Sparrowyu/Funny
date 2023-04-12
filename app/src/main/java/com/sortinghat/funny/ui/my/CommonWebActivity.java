package com.sortinghat.funny.ui.my;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.http.SslError;
import android.os.Build;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blankj.utilcode.util.ActivityUtils;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.base.NoViewModel;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.databinding.ActivityPrivacyBinding;
import com.sortinghat.funny.util.ConstantUtil;

public class CommonWebActivity extends BaseActivity<NoViewModel, ActivityPrivacyBinding> {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_privacy;
    }

    String[] privacy_info;

    @Override
    protected void initViews() {
        privacy_info = getIntent().getStringArrayExtra("web_info");
        if (privacy_info != null && privacy_info.length > 0) {
            initTitleBar(privacy_info[0]);
        }
    }

    @Override
    protected void initData() {
        initWebViewSettings(contentLayoutBinding.privacyWebview);
        if (privacy_info != null && privacy_info[1] != null) {
            contentLayoutBinding.privacyWebview.loadUrl(privacy_info[1]);
        }
    }

    @SuppressWarnings("deprecation")
    protected void initWebViewSettings(WebView w) {
        w.setScrollbarFadingEnabled(true);
        w.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        w.setHorizontalScrollBarEnabled(false);//水平不显示
//        w.setVerticalScrollBarEnabled(false); //垂直不显示
        w.setMapTrackballToArrowKeys(false); // use trackball directly
        final PackageManager pm = this.getPackageManager();
//        if (CommonUtil.getAndroidSDKVersion() >= 14) {
        boolean supportsMultiTouch = pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH) || pm.hasSystemFeature(PackageManager.FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT);
        w.getSettings().setDisplayZoomControls(!supportsMultiTouch);
//        }

        String dir = this.getDir("databases", Context.MODE_PRIVATE).getPath();
        String cacheDir = this.getDir("cache", Context.MODE_PRIVATE).getPath();
        String userAgent = CommonUtils.getUserAgent();
        WebSettings settings = w.getSettings();
        settings.setUserAgentString(userAgent);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setAppCacheEnabled(true);

        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setAllowFileAccess(true);
        //解决混合模式导致部分图片和视频无法播放的问题
        settings.setAppCachePath(cacheDir);
        settings.setGeolocationDatabasePath(dir);
        settings.setDatabasePath(dir);
        settings.setGeolocationEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //不加无法加载出来
        w.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
        w.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                contentLayoutBinding.pageProgress.setProgress(newProgress);
            }
        });
    }

    public static void starWebActivity(Context mContext, String titleName, String url) {
        String[] info = new String[]{titleName, url};
        Intent intent = new Intent(mContext, CommonWebActivity.class);
        intent.putExtra("web_info", info);
        ActivityUtils.startActivity(intent);
    }
}

