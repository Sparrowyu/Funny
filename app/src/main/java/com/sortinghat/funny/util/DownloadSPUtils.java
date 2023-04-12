package com.sortinghat.funny.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DownloadSPUtils {
    public static final int MAX_COUNT = 40;
    public static final String name = "download";

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static void putDownloadCount(Context context, int count) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        getPreferences(context).edit().putInt(date, count).commit();
    }

    public static int getDownloadCount(Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        return getPreferences(context).getInt(date, 0);
    }
}