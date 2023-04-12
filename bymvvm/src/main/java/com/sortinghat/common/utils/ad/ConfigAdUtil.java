package com.sortinghat.common.utils.ad;

import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.kwad.sdk.api.KsAdSDK;
import com.kwad.sdk.api.SdkConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConfigAdUtil {

    //1穿山甲 2：广点通 0不显示
    public static int getSplashAdType() {
        List<Integer> adOrderConfig = new ArrayList<>();//1代表穿山甲，2广点通
        boolean isShowSplashAD = SPUtils.getInstance("config_info").getBoolean("ad_splash_show", true);//是否显示
        if (!isShowSplashAD) {
            return 0;
        }
        try {
            String spSplash = SPUtils.getInstance("config_info").getString("android_splash_ad_order");
            if (!TextUtils.isEmpty(spSplash)) {
                String[] spArrayString = spSplash.split(",");
                for (int i = 0; i < spArrayString.length; i++) {
                    adOrderConfig.add(Integer.valueOf(spArrayString[i]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int currentNum = SPUtils.getInstance("config_info").getInt("android_splash_ad_num", 0);//是否显示;//当前是第几次
        SPUtils.getInstance("config_info").put("android_splash_ad_num", currentNum + 1);

        //加载开屏广告
        int currentAd = 1;
        if (adOrderConfig.size() > 0 && (currentNum % adOrderConfig.size() < adOrderConfig.size())) {
            currentAd = adOrderConfig.get(currentNum % adOrderConfig.size());
        }

        return currentAd;
    }

    public static int getForeSplashAdType() {
        List<Integer> adOrderConfig = new ArrayList<>();//1代表穿山甲，2广点通
        boolean isShowSplashAD = SPUtils.getInstance("config_info").getBoolean("ad_fore_splash_show", true);//是否显示
        if (!isShowSplashAD) {
            return 0;
        }
        adOrderConfig.add(1);
        adOrderConfig.add(2);
        int currentNum = SPUtils.getInstance("config_info").getInt("android_fore_splash_ad_num", 0);//是否显示;//当前是第几次
        SPUtils.getInstance("config_info").put("android_fore_splash_ad_num", currentNum + 1);

        //加载开屏广告
        int currentAd = 1;
        if (adOrderConfig.size() > 0 && (currentNum % adOrderConfig.size() < adOrderConfig.size())) {
            currentAd = adOrderConfig.get(currentNum % adOrderConfig.size());
        }

        return currentAd;
    }

    private static int homeVideoNum = -1;
    public static List<Integer> adHomeVideoOrderConfig = new ArrayList<>();//1代表穿山甲，2广点通

    public static int getHomeVideoAdType() {

        if (adHomeVideoOrderConfig.size() == 0) {
            adHomeVideoOrderConfig.add(1);
            adHomeVideoOrderConfig.add(2);
        }

        if (adHomeVideoOrderConfig.size() == 1) {
            return adHomeVideoOrderConfig.get(0);
        } else if (homeVideoNum == -1) {
            homeVideoNum = 0;
            int adH = new Random().nextInt(adHomeVideoOrderConfig.size());
            if (adH < adHomeVideoOrderConfig.size()) {
                return adHomeVideoOrderConfig.get(adH);
            }
            return 1;
        }

        int currentNum = homeVideoNum;//是否显示;//当前是第几次
        homeVideoNum = currentNum + 1;

        //加载开屏广告
        int currentAd = 1;
        if (adHomeVideoOrderConfig.size() > 0 && (currentNum % adHomeVideoOrderConfig.size() < adHomeVideoOrderConfig.size())) {
            currentAd = adHomeVideoOrderConfig.get(currentNum % adHomeVideoOrderConfig.size());
        }

        return currentAd;
    }

    public static int getHomeImgAdType(int num) {
        return 0;
    }

    public static void initKSSDK(Context appContext) {
        KsAdSDK.init(appContext, new SdkConfig.Builder()
                .appId("844100001") // 测试aapId，请联系快⼿平台申请正式AppId，必填
                .appName("搞笑星球") // 测试appName，请填写您应⽤的名称，
                .showNotification(true) // 是否展示下载通知栏
                .debug(true) // 是否开启sdk 调试⽇志 可选
                .build());
    }
}
