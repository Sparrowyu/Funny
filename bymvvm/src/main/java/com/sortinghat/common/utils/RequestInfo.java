package com.sortinghat.common.utils;

import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ScreenUtils;

public class RequestInfo {
    private static RequestInfo mInstance;
    private static Context mContext;
    //    private String macAddressMd5;
    private String isRoot = "";
    private String versionName = "";
    private String isEmulator = "";
    private String UmChannel = "";
    private String model = "";
    private int screenWidth = 0;
    private int screenHeight = 0;

    private RequestInfo(Context context) {
        mContext = context;
//        macAddressMd5 = getMacAddressMd5();
        isRoot = getIsRoot();
        versionName = getVersionName();
        isEmulator = isEmulator();
        UmChannel = getUmChannel();
        model = getModel();
    }

    public static synchronized RequestInfo getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestInfo(context);
        }
        return mInstance;
    }

//    public String getMacAddressMd5() {
//        if (macAddressMd5 == null) {
//            macAddressMd5 = CommonUtils.getMacMd5(mContext, DeviceUtils.getMacAddress());
//        }
//        return macAddressMd5;
//    }

    public String getIsRoot() {
        if (TextUtils.isEmpty(isRoot)) {
            isRoot = CommonUtils.isRoot() + "";
        }
        return isRoot;
    }

    public String isEmulator() {
        if (TextUtils.isEmpty(isEmulator)) {
            isEmulator = DeviceUtils.isEmulator() + "";
        }
        return isEmulator;
    }

    public String getVersionName() {
        if (versionName == null || TextUtils.isEmpty(versionName)) {
            versionName = CommonUtils.getCurrentVersionName(mContext) + "";
        }
        return versionName;
    }

    public String getUmChannel() {
        if (UmChannel == null || TextUtils.isEmpty(UmChannel)) {
            UmChannel = CommonUtils.getUmChannel(mContext);
        }
        return UmChannel;
    }

    public String getModel() {
        if (model == null || TextUtils.isEmpty(model)) {
            model = DeviceUtils.getManufacturer() + "_" + DeviceUtils.getModel();
        }
        return model;
    }

    public int getScreenWidth() {
        if (screenWidth < 1) {
            screenWidth = ScreenUtils.getScreenWidth();
        }
        return screenWidth;
    }

    public int getScreenHeight() {
        if (screenHeight < 1) {
            screenHeight = ScreenUtils.getScreenHeight();
        }
        return screenHeight;
    }
}
