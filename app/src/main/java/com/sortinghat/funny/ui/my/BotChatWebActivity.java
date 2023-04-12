package com.sortinghat.funny.ui.my;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.base.NoViewModel;
import com.sortinghat.common.utils.CheckNetwork;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.RequestInfo;
import com.sortinghat.funny.R;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.databinding.ActivityPrivacyBinding;
import com.sortinghat.funny.thirdparty.clipView.FileUtil;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.util.PermissionHandler;

import java.io.File;

public class BotChatWebActivity extends BaseActivity<NoViewModel, ActivityPrivacyBinding> {
    private String chatUrl = "https://bot.gaoxiaoxingqiu.com";
    private static String TAG = "BotChatWebActivity-";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_privacy;
    }

    @Override
    protected void initViews() {
        initTitleBar("搞笑星球小助手");
    }

    @Override
    protected void initData() {

//      String avatar =   SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("user_avatar");
//      String name =   SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("user_nike_name");

        StringBuilder chatUrlBuilder = new StringBuilder(chatUrl);
        chatUrlBuilder.append("?user_id=").append(Constant.getUserID())
                .append("&device_id=").append(DeviceUtils.getUniqueDeviceId())
                .append("&device_type=").append("Android")
                .append("&app_ver=").append(RequestInfo.getInstance(this).getVersionName() + "")
                .append("&net=").append(CheckNetwork.isWifiOr4G(this) + "")
                .append("&device_mode=").append(RequestInfo.getInstance(this).getModel() + "")
                .append("&ip=").append(CheckNetwork.getIpAddress(this) + "");
        initWebViewSetting(contentLayoutBinding.privacyWebview);
//        contentLayoutBinding.privacyWebview.loadUrl(chatUrl);/
        contentLayoutBinding.privacyWebview.loadUrl(chatUrlBuilder.toString());
    }

    @SuppressWarnings("deprecation")
    protected void initWebViewSetting(WebView w) {
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
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
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
                Log.e(TAG, "onProgressChanged");
                super.onProgressChanged(view, newProgress);
                contentLayoutBinding.pageProgress.setProgress(newProgress);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//                { icon:"photo-fill",title:"照片",uploadType:["album"] },
//                { icon:"camera-fill",title:"拍摄",uploadType:["camera"] },
                if (mUploadMsgForAndroidL != null) {
                    mUploadMsgForAndroidL.onReceiveValue(null);
                }
                mUploadMsgForAndroidL = filePathCallback;
                //前端H5接收的格式类型转为字符串,且后面不带分号
                String[] acceptTypes = fileChooserParams.getAcceptTypes();
                String acceptType = "*/*";//image/*;

                try {
                    if (fileChooserParams.isCaptureEnabled()) {
                        photoType = 0;
                        if (Build.VERSION.SDK_INT >= 23) {
                            checkPermission();
                        } else {
                            takePhoto();
                        }
                    } else {
                        photoType = 1;
                        if (Build.VERSION.SDK_INT >= 23) {
                            checkPermission();
                        } else {
                            selectGallery();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Exception:" + e.toString());
                    Toast.makeText(mContext,
                            "请去\"设置\"中开启本应用的相机和图片媒体访问权限",
                            Toast.LENGTH_SHORT).show();
                    restoreUploadMsg();
                }
                return true;
            }

        });

    }


    private void restoreUploadMsg() {
        if (mUploadMsg != null) {
            mUploadMsg.onReceiveValue(null);
            mUploadMsg = null;
        } else if (mUploadMsgForAndroidL != null) {
            mUploadMsgForAndroidL.onReceiveValue(null);
            mUploadMsgForAndroidL = null;
        }
    }

    private ValueCallback<Uri[]> mUploadMsgForAndroidL;
    private ValueCallback<Uri> mUploadMsg;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        mUploadMsgForAndroidL.onReceiveValue(new Uri[]{takePictureUri});
                    } else {
                        mUploadMsgForAndroidL.onReceiveValue(new Uri[]{Uri.fromFile(tempFile)});
                    }
                } else {
                    restoreUploadMsg();
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    mUploadMsgForAndroidL.onReceiveValue(new Uri[]{uri});
                } else {
                    restoreUploadMsg();
                }
                break;
        }

    }


    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;

    private File tempFile;
    private Uri takePictureUri;

    private void takePhoto() {
        try {
            //跳转到调用系统相机
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //创建拍照存储的图片文件
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //适配 Android Q
                String displayName = String.valueOf(System.currentTimeMillis());
                ContentValues values = new ContentValues(2);
                values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { //SD 卡是否可用，可用则用 SD 卡，否则用内部存储
                    takePictureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    takePictureUri = getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, takePictureUri);
            } else {
                tempFile = new File(FileUtil.checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"), System.currentTimeMillis() + ".jpg");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
                    intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(BotChatWebActivity.this, mContext.getPackageName() + ".fileprovider", tempFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                } else {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                }
            }
            startActivityForResult(intent, REQUEST_CAPTURE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectGallery() {
        //跳转到调用系统图库
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
    }


    private int photoType = 1;//0 相机 1相册

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionHandler.onRequestPermissionsResult(this, "", requestCode, permissions, grantResults, new PermissionHandler.OnHandlePermissionListener() {
            @Override
            public void granted() {
                if (photoType == 0) {
                    takePhoto();
                } else {
                    selectGallery();
                }
            }

            @Override
            public void denied() {
            }

            @Override
            public void deniedAndAskNoMore() {
                if (photoType == 0) {
                    if (!SPUtils.getInstance(Constant.SP_PERMISSION_INFO).getBoolean("camera_permission")) {
                        SPUtils.getInstance(Constant.SP_PERMISSION_INFO).put("camera_permission", true);
                        return;
                    }
                    MaterialDialogUtil.showAlert(BotChatWebActivity.this, "需要去权限设置页面打开相机权限", "去设置", "取消", (dialog, which) -> {
                        AppUtils.launchAppDetailsSettings(BotChatWebActivity.this, requestCode);
                    });
                } else {
                    if (!SPUtils.getInstance(Constant.SP_PERMISSION_INFO).getBoolean("storage_permission")) {
                        SPUtils.getInstance(Constant.SP_PERMISSION_INFO).put("storage_permission", true);
                        return;
                    }
                    MaterialDialogUtil.showAlert(BotChatWebActivity.this, "需要去权限设置页面打开存储权限", "去设置", "取消", (dialog, which) -> {
                        AppUtils.launchAppDetailsSettings(BotChatWebActivity.this, requestCode);
                    });
                }
            }
        });
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checkPermission() {
        if (photoType == 0) {
            if (PermissionHandler.isHandlePermission(this, Manifest.permission.CAMERA)) {
                takePhoto();
            }
        } else {
            if (PermissionHandler.isHandlePermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                selectGallery();
            }
        }
    }

    @Override
    protected void onDestroy() {
//        if ( contentLayoutBinding.privacyWebview != null) {
//            ((ViewGroup)  contentLayoutBinding.privacyWebview.getParent()).removeView( contentLayoutBinding.privacyWebview);
//            contentLayoutBinding.privacyWebview.destroy();
//            contentLayoutBinding.privacyWebview = null;
//        }
        super.onDestroy();
    }

    public static void starWebActivity(Context mContext) {
        Intent intent = new Intent(mContext, BotChatWebActivity.class);
        ActivityUtils.startActivity(intent);
    }
}

