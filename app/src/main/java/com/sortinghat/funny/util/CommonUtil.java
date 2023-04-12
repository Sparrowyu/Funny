package com.sortinghat.funny.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.PowerManager;
import android.text.TextUtils;

import com.blankj.utilcode.util.ThreadUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.sortinghat.funny.FunnyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by wzy on 2021/9/2
 */
public class CommonUtil {

    public static void clearCache() {
        ThreadUtils.executeBySingle(new ThreadUtils.SimpleTask<Void>() {
            @Override
            public Void doInBackground() {
//                GlideUtils.clearImageDiskCache(FunnyApplication.getContext());
                GSYVideoManager.instance().clearAllDefaultCache(FunnyApplication.getContext());
                return null;
            }

            @Override
            public void onSuccess(Void result) {
            }
        });
    }

    public static boolean urlIsGif(String url) {
        if (url == null || TextUtils.isEmpty(url)) {
            return false;
        }
        if (url.toLowerCase().contains(".gif")) {
            return true;
        }
        return false;
    }

    //是否为动图,或者webp动图
    public static boolean urlIsGifOrWebp(String url) {
        if (url == null || TextUtils.isEmpty(url)) {
            return false;
        }
        if (url.toLowerCase().contains(".gif") || url.toLowerCase().contains("gif2webp")) {
            return true;
        }
        return false;
    }

    /**
     * 唤醒手机屏幕并解锁
     */
    public static void wakeup(Context context) {
        //屏锁管理器
//        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
//        //解锁
//        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        if (null != wl) {
            //点亮屏幕
            wl.acquire(10000);
            //释放
            wl.release();
        }
    }

    /*
     * check the app is installed
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }
    public static String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
