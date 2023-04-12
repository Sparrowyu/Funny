package com.sortinghat.common.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 存储器工具类
 */
public class StorageUtil {

    //app在外部存储器上的根目录
    private static String PATH_ROOT = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + "com.sortinghat.funny" + "/";
    //缓存路径
    private static final String PATH_CACHE = PATH_ROOT + "cache/";
    //文件保存路径
    private static final String PATH_FILE = PATH_ROOT + "files/";
    //图片保存路径
    private static final String PATH_IMAGE = PATH_ROOT + "image/";

    //图片保存路径
    private static final String PATH_VIDEO = PATH_ROOT + "video/";

    //临时路径
    private static final String PATH_TEMP = PATH_ROOT + "temp/";

    static {
        File dir = new File(PATH_ROOT);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static String getCachePath() {
        File dir = new File(PATH_CACHE);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return PATH_CACHE;
    }

    public static String getFileSavePath() {
        File dir = new File(PATH_FILE);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return PATH_FILE;
    }

    public static String getImageSavePath() {
        File dir = new File(PATH_IMAGE);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return PATH_IMAGE;
    }

    public static String getVideoSavePath() {
        File dir = new File(PATH_VIDEO);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return PATH_VIDEO;
    }

    public static String getTempPath() {
        File dir = new File(PATH_TEMP);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return PATH_TEMP;
    }

    public static String generateVideoTempPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return getTempPath()  + "." + sdf.format(new Date()) +".mp4";
    }

    public static String generateVideoSavedPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return getVideoSavePath() + sdf.format(new Date()) +".mp4";
    }

    public static String generateImageTempPath(boolean isGif) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return getTempPath()  + "." + sdf.format(new Date()) + (!isGif ? ".jpg" : ".gif");
    }

    public static String generateImageSavedPath(boolean isGif) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return getImageSavePath() + sdf.format(new Date()) + (!isGif ? ".jpg" : ".gif");
    }

    public static void notifyAlbum(Context context, String path, boolean isVideo, boolean isGif) {
        notifyAlbum(context, new File(path), isVideo, isGif);
    }

    public static void notifyAlbum(Context context, File file, boolean isVideo, boolean isGif) {

        //更新图库
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
            values.put(MediaStore.MediaColumns.MIME_TYPE, isVideo ? "video/mp4" : (isGif ? "image/gif" : "image/jpeg"));
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = contentResolver.insert(isVideo ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    : MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                return;
            }
            try {
                OutputStream outputStream = contentResolver.openOutputStream(uri);
                FileInputStream fileInputStream = new FileInputStream(file);
                FileUtils.copy(fileInputStream, outputStream);
                fileInputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            MediaScannerConnection.scanFile(
                    context,
                    new String[]{file.getAbsolutePath()},
                    isVideo ? new String[]{"video/mp4"} : new String[]{(isGif ? "image/gif" : "image/jpeg")},
                    (path, uri) -> {
                        // Scan Completed
                    });
        }
    }

    public static void goToGallery(Context context) {
        if (context == null) {
            return;
        }
        try {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(mainIntent, 0);
            List<ResolveInfo> results = new ArrayList<>();

            for (ResolveInfo it : resolveInfos) {
                if (it.activityInfo.packageName.contains("gallery")) {
                    results.add(it);
                }
            }
            if (!results.isEmpty()) {
                Intent intent = context.getPackageManager()
                        .getLaunchIntentForPackage(results.get(0).activityInfo.packageName);
                if (!(context instanceof Activity)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
            }
        } catch (Exception e) {}

    }
}