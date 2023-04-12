package com.sortinghat.funny;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.OSSUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.facebook.stetho.Stetho;
import com.google.gson.JsonObject;
import com.jeffmony.videocache.VideoProxyCacheManager;
import com.jeffmony.videocache.utils.StorageUtils;
import com.qq.e.comm.managers.GDTAdSdk;
import com.sortinghat.common.base.RootApplication;
import com.sortinghat.common.gmoread.GMAdManagerHolder;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.NetworkChangeReceiver;
import com.sortinghat.common.utils.TTAdManagerHolder;
import com.sortinghat.common.utils.ad.ConfigAdUtil;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.push.HwPushUtil;
import com.sortinghat.funny.push.UmInitConfig;
import com.sortinghat.funny.service.CommonService;
import com.sortinghat.funny.thirdparty.alibaba.Config;
import com.sortinghat.funny.thirdparty.alibaba.http.OssService;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.CrashHandler;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.utils.ShareLibraryUM;
import com.sortinghat.funny.viewmodel.SplashModel;
import com.tencent.tauth.Tencent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.PushAgent;

import java.io.File;

/**
 * Created by wzy on 2021/6/15
 */
public class FunnyApplication extends RootApplication {

    @SuppressLint("StaticFieldLeak")
    private static FunnyApplication application;

    private static OssService uploadOssService, downloadOssService;

    public static Tencent mTencent;

    private int activityCount;

    private SplashModel splashModel;

    private CommonService commonService;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            commonService = ((CommonService.CommonServiceBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            commonService = null;
        }
    };

    public static FunnyApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            webviewSetPath(this);
        }
        if (isMainProcess()) {
            splashModel = new SplashModel(this);
            LogUtils.getConfig().setLogSwitch(BuildConfig.DEBUG);
            registerActivityLifecycleCallback();
            Stetho.initializeWithDefaults(this);
            initVideoProxyCacheManager();
            uploadOssService = initOSS(Config.UPLOAD_OSS_ENDPOINT, Config.BUCKET_NAME);
            downloadOssService = initOSS(Config.DOWNLOAD_OSS_ENDPOINT, Config.BUCKET_NAME);

            CrashHandler crashHandler = CrashHandler.getsInstance();
            crashHandler.init(this, splashModel);
            NetworkChangeReceiver.registerReceiver(this);
            NetworkChangeReceiver.registerObserver(new NetworkChangeReceiver.NetStateChangeObserver() {
                @Override
                public void onDisconnect() {
                    RxBus.getDefault().post(RxCodeConstant.NETWORK_CONNECT, false);
                }

                @Override
                public void onMobileConnect() {
                    RxBus.getDefault().post(RxCodeConstant.NETWORK_CONNECT, true);

                }

                @Override
                public void onWifiConnect() {
                    RxBus.getDefault().post(RxCodeConstant.NETWORK_CONNECT, true);
                }
            });
        }

        /**
         * 注意：
         * preInit预初始化函数耗时极少，不会影响App首次冷启动用户体验
         * 用户授权之后，才调用正式初始化函数UMConfigure.init()初始化统计SDK
         * 如果您已经在AndroidManifest.xml中配置过appkey和channel值，可以调用此版本初始化函数。
         */
        UMConfigure.setLogEnabled(true);
        //解决推送消息显示乱码的问题
        PushAgent.setup(this, getString(R.string.um_appkey), getString(R.string.um_secret));
        UMConfigure.preInit(getApplicationContext(), getString(R.string.um_appkey), CommonUtils.getUmChannel(this));
        if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getBoolean("user_accept_privacy_protocol")) {
            // 其中APP_ID是分配给第三方应用的appid，类型为String。
            if (mTencent == null) {
                mTencent = Tencent.createInstance(getString(R.string.qq_appid), this);
            }
            UmInitConfig umInitConfig = new UmInitConfig();
            umInitConfig.UMinit(getApplicationContext());
            // 必须getString，不能直接R.string
            ShareLibraryUM.setConfig(getString(R.string.weixin_appid), getString(R.string.weixin_appsecret), getString(R.string.qq_appid), getString(R.string.qq_key), "com.sortinghat.funny");
            //穿山甲SDK初始化
            //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
            TTAdManagerHolder.init(application);
            // 通过调用此方法初始化 SDK。如果需要在多个进程拉取广告，每个进程都需要初始化 SDK。
            GDTAdSdk.init(application, getString(R.string.gdtad_appid));
            ConfigAdUtil.initKSSDK(application);
            GMAdManagerHolder.init(this);
        }
    }

    //调用一定是在进程初始化的时候调用，比如Application中进行调用，并且这行代码需要在其他的SDK等等初始化之前就要调用，否则会报其他的错误。
    @RequiresApi(api = 28)
    public void webviewSetPath(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(context);
            if (!isMainProcess()) {//判断不等于默认进程名称
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }

    private String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }

    private boolean isMainProcess() {
        if (getApplicationInfo().packageName.equals(getProcessName(this))) {//判断不等于默认进程名称
            return true;
        } else {
            return false;
        }
    }

    private void registerActivityLifecycleCallback() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                activityCount++;
                if (activityCount == 1) {
                    startCommonService();
                    goStartLog("foreground", activity);
                    HwPushUtil.editCornerMarker(activity, 0);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                activityCount--;
                if (activityCount == 0) {
                    stopCommonService();
                    goStartLog("background", activity);
                    ConstantUtil.KfpLogSendLogValue = 0;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    @Override
    public void onTerminate() {
        uploadOssService = null;
        downloadOssService = null;
        NetworkChangeReceiver.unRegisterReceiver(this);
        super.onTerminate();
    }


    private void initVideoProxyCacheManager() {
        File saveFile = StorageUtils.getIndividualCacheDirectory(this);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }

        VideoProxyCacheManager.Builder builder = new VideoProxyCacheManager.Builder()
                .setFilePath(saveFile.getAbsolutePath())
                .setConnTimeOut(60 * 1000)
                .setReadTimeOut(60 * 1000)
                .setMaxCacheSize(1 * 1024 * 1024 * 1024);//1g存储上限

        VideoProxyCacheManager.getInstance().initProxyConfig(builder.build());
    }

    private OssService initOSS(String endpoint, String bucketName) {
        OSSCustomSignerCredentialProvider credentialProvider = new OSSCustomSignerCredentialProvider() {
            @Override
            public String signContent(String content) {
                return OSSUtils.sign(Config.OSS_ACCESS_KEY_ID, Config.OSS_ACCESS_KEY_SECRET, content);
            }
        };
        OSS oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);

        if (BuildConfig.DEBUG) {
            OSSLog.enableLog();
        } else {
            OSSLog.disableLog();
        }
        return new OssService(oss, bucketName);
    }

    public static OssService getUploadOssService() {
        return uploadOssService;
    }

    public static OssService getDownloadOssService() {
        return downloadOssService;
    }

    public void startCommonService() {
        if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
            return;
        }
        Intent intent = new Intent(this, CommonService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopCommonService() {
        if (commonService != null) {
            if (ServiceUtils.isServiceRunning(CommonService.class)) {
                unbindService(serviceConnection);
            }
        }
    }

    public CommonService getCommonService() {
        return commonService;
    }

    private long foreCreateTime = 0;//当在其他activity时退出后台时，微信点击回来会走一遍fore和back，所以1000ms内可以不发日志

    private void goStartLog(String type, Activity activity) {
        if (splashModel == null) {
            return;
        }
        try {
            String source = ConstantUtil.getLogActivity(activity);
            long createTime = System.currentTimeMillis();
            JsonObject startJsonObject = new JsonObject();
            startJsonObject.addProperty("create_time", createTime);//事件时间
            if (type.equals("background")) {
                long forTime = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("foreground_time");
                if (forTime > 0) {
                    startJsonObject.addProperty("foreground_time", forTime);//开始时长
                    startJsonObject.addProperty("duration", createTime - forTime);//时长
                }
            }

            long uid = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id");
            RequestParamUtil.addStartLogHeadParam(startJsonObject, type, "app", source, "app");
            if (type.equals("foreground")) {
                SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("foreground_time", createTime);
                foreCreateTime = createTime;
                //前台日志存到本地，退出时发送
                if (source.equals("splash") && uid < 1) {
                    //首次安装进入时，不发送埋点，在登录接口后调用
                    return;
                }
                ConstantUtil.spStartLog(startJsonObject.toString());
            } else {
//                if (Math.abs(foreCreateTime - createTime) < 1000) {
//                    foreCreateTime = 0;
//                    LogUtils.d("start-log--app", "-微信后台点进来,或者快速进出就不发送：" + type + "；source：" + source);
//                    return;
//                }
                if (TextUtils.isEmpty(startJsonObject.toString())) {
                    return;
                }
                //observeForever不依赖activity
                splashModel.setAppUnifyLog(startJsonObject.toString(), this).observeForever(resultBean -> {
                });
            }
        } catch (Exception e) {
            LogUtils.d("start-log--app", "-error：" + e.toString());
            e.printStackTrace();
        }
    }

}
