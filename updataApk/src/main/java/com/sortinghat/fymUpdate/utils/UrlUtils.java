package com.sortinghat.fymUpdate.utils;

import android.content.Context;

import com.sortinghat.fymUpdate.common.Utils;

public class UrlUtils {

    public static String UpdateAddressUrl(Context context, String versionname) {
        String savePath = Utils.getSingleSdcardPath(context) + "/" + "download_CyPlugin";
        return savePath + "/" + context.getPackageName() + versionname + ".apk";
    }
}
