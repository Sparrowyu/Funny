package com.sortinghat.funny.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.StorageUtil;
import com.sortinghat.funny.BuildConfig;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.bean.HomeVideoImageListBean;

import java.io.File;
import java.io.FileOutputStream;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ImageEditUtils extends EditUtils {

    public static void addWaterMarkAsync(HomeVideoImageListBean.ListBean videoInfo, String path, String outPath, EditListener listener) {
        if (BuildConfig.DEBUG) {
            CommonUtils.showShort("image path: " + path);
        }
        Observable.fromCallable(() -> {
            if (!TextUtils.isEmpty(path) && path.endsWith(".gif")) {
                StorageUtil.notifyAlbum(FunnyApplication.getContext(), path, false, true);
            } else {
                addWaterMarkSync(videoInfo, path, outPath, listener);
                StorageUtil.notifyAlbum(FunnyApplication.getContext(), outPath, false, false);
            }
            return true;
        }).observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (listener != null) {
                        listener.onSuccess(videoInfo, path);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    if (listener != null) {
                        listener.onFail(videoInfo, throwable);
                    }
                });
    }

    public static void addWaterMarkSync(HomeVideoImageListBean.ListBean videoInfo, String path, String outPath, EditListener listener) throws Exception {
        Bitmap src = BitmapFactory.decodeFile(path);
        if (src == null) {
            listener.onFail(videoInfo, new Exception("path is invalid"));
        }

        int width = src.getWidth();
        int height = src.getHeight();
        //创建一个bitmap
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        //将该图片作为画布
        Canvas canvas = new Canvas(result);
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);
        //在画布上绘制水印图片
        String waterMarkPath = getWaterMarkPath();
        File waterMark = new File(waterMarkPath);
        if (!waterMark.exists()) {
            copyWaterMark();
        }

        Bitmap waterMarkBitmap = BitmapFactory.decodeFile(waterMarkPath);
        int outWidth = waterMarkBitmap.getWidth() * width / 720;
        int outHeight = waterMarkBitmap.getHeight() * width / 720;
        waterMarkBitmap = Bitmap.createScaledBitmap(waterMarkBitmap, outWidth, outHeight, true);
        if (BuildConfig.DEBUG) {
            LogUtils.d("src size: " + width + " * " + height + "; logo size: " + waterMarkBitmap.getWidth() + " * " + waterMarkBitmap.getHeight());
        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(waterMarkBitmap, LOGO_PADDING_LEFT, LOGO_PADDING_TOP, null);
        // 保存
        canvas.save();
        // 存储
        canvas.restore();
        FileOutputStream fos = new FileOutputStream(outPath);
        result.compress(Bitmap.CompressFormat.JPEG, 100, fos);

    }

}
