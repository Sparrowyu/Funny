package com.sortinghat.funny.util;

import com.sortinghat.common.utils.FileUtil;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.bean.HomeVideoImageListBean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class EditUtils {
    public static final int LOGO_WIDTH = 201;
    public static final int LOGO_HEIGHT = 48;
    public static final int LOGO_PADDING_LEFT = 16;
    public static final int LOGO_PADDING_TOP = 16;


    public static void copyWaterMark() throws IOException {
        InputStream inputStream = FunnyApplication.getContext().getAssets().open("logo.png");
        FileUtil.writeFileToDisk(getWaterMarkTempPath(), inputStream);
        FileUtil.renameFile(new File(getWaterMarkTempPath()), getWaterMarkPath());
    }


    public static String getWaterMarkTempPath() {
        return FunnyApplication.getContext().getFilesDir() + File.separator +  "logo_tmp.png";
    }

    public static String getWaterMarkPath() {
        return FunnyApplication.getContext().getFilesDir() + File.separator +  "logo.png";
    }

    public interface EditListener {
        void onSuccess(HomeVideoImageListBean.ListBean videoInfo, String path);
        void onFail(HomeVideoImageListBean.ListBean videoInfo, Throwable e);
        void onProgress(float progress);
    }
}
