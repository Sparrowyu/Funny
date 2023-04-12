package com.sortinghat.funny.util;

import android.app.ActivityManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.WindowManager;

import com.sortinghat.funny.FunnyApplication;

public class SystemServiceUtil {
    /**
     * @return 获得剪贴板服务
     */
    public static ClipboardManager getClipboardService() {
        return (ClipboardManager) FunnyApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
    }

    /**
     * @param mActivity 上下文对象
     * @return 获得Activity的window对象，这里不能用全局mcontext 也不能用单例，因为每个activity不一样
     */
    public static WindowManager getWindowService(Context mActivity) {
        return (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * @return 获得Activity服务
     */
    public static ActivityManager getActivityService() {
        return (ActivityManager) FunnyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
    }

}
