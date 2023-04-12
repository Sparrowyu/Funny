package com.sortinghat.funny.push;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.SPUtils;
import com.sortinghat.common.http.HttpUtils;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.util.ConstantUtil;


public class HwPushUtil {

    private static final String TAG = "app-push-HwPushUtil";

    /**
     * 修改华为桌面角标
     * 传入0的时候显示不显示，1-99显示
     */
    public static void editCornerMarker(Context mContext, int badgenumber) {
        try {
            if (!ConstantUtil.isHuaWei()) {
                SPUtils.getInstance(Constant.SP_CONFIG_INFO).put("is_support_huawei_editCorner", false);
            }
            boolean isSupport = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean("is_support_huawei_editCorner", true);
            if (!isSupport) {
                return;
            }
            Log.e(TAG, "editCornerMarker: ");
            Bundle extra = new Bundle();
            extra.putString("package", "com.sortinghat.funny");
            extra.putString("class", "com.sortinghat.funny.ui.SplashActivity");
            extra.putInt("badgenumber", badgenumber);
            mContext.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, extra);
        } catch (Exception e) {
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put("is_support_huawei_editCorner", false);
            e.printStackTrace();
        }
    }
}
