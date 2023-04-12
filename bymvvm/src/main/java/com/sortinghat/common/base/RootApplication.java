package com.sortinghat.common.base;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.sortinghat.common.http.HttpUtils;

public class RootApplication extends MultiDexApplication {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        HttpUtils.getInstance().init(this);
    }

    public static Context getContext() {
        return mContext;
    }
}
