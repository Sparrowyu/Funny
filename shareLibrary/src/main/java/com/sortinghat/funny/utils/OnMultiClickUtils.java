package com.sortinghat.funny.utils;


import android.content.Context;

public class OnMultiClickUtils {
    private static long lastClickTime;
    private static final int MIN_CLICK_DELAY_TIME = 1000;

    public static boolean isMultiClickClick(Context context) {
        boolean flag = false;
        try {
            long curClickTime = System.currentTimeMillis();
            if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME || (curClickTime - lastClickTime) < 0) {
                flag = true;
                lastClickTime = curClickTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }

    //一定时间只执行一次
    public static boolean isMultiClickClick(int deley_time) {
        boolean flag = false;
        try {
            long curClickTime = System.currentTimeMillis();
            if ((curClickTime - lastClickTime) >= deley_time || (curClickTime - lastClickTime) < 0) {
                flag = true;
                lastClickTime = curClickTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }


}
