package com.sortinghat.fymUpdate.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {
    public static final String name = "config_new";
    public static final String ISDOWNLOADEND = "ISDOWNLOADEND";//首页 是否下载完成 1Y -1N

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static int getISDOWNLOADEND(Context context) {
        return getPreferences(context).getInt(ISDOWNLOADEND, -1);
    }

    public static void putISDOWNLOADEND(Context context, int value) {
        getPreferences(context).edit().putInt(ISDOWNLOADEND, value).commit();
    }
}