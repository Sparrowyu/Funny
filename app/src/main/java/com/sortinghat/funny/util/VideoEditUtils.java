package com.sortinghat.funny.util;

import android.content.Context;
import android.util.Log;

import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.FileUtil;
import com.sortinghat.common.utils.StorageUtil;
import com.sortinghat.funny.BuildConfig;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.video.EpDraw;
import com.sortinghat.funny.video.EpEditorEx;
import com.sortinghat.funny.video.EpVideo;
import com.sortinghat.funny.video.VideoUitls;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class VideoEditUtils extends EditUtils {
    public static void addWaterMarkAsync(HomeVideoImageListBean.ListBean videoInfo, String path, String outPath, EditListener listener) {
        addWaterMarkAsync(videoInfo, path, outPath, false, listener);
    }
    public static void addWaterMarkAsync(HomeVideoImageListBean.ListBean videoInfo, String path, String outPath, boolean addAdVideo, EditListener listener) {
        if (BuildConfig.DEBUG) {
            CommonUtils.showShort("video size: " + Arrays.toString(VideoUitls.getSize(path)));
        }
        Observable.fromCallable(() -> {
            addWaterMarkSync(videoInfo, path, outPath, addAdVideo, listener);
            return true;
        }).observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {}, throwable -> {
                    throwable.printStackTrace();
                    if (listener != null) {
                        listener.onFail(videoInfo, throwable);
                    }
                });
    }

    public static void addWaterMarkSync(HomeVideoImageListBean.ListBean videoInfo, String path, String outPath, boolean addAdVideo, EditListener listener) throws IOException {
        cancel();
        String waterMarkPath = getWaterMarkPath();
        File waterMark = new File(waterMarkPath);
        if (!waterMark.exists()) {
            copyWaterMark();
        }
        int size[] = VideoUitls.getSize(path);
        boolean landscape = size[0] > size[1];
        EpEditorEx.OutputOption outputOption = new EpEditorEx.OutputOption(outPath);
        int outWidth = size[0] != 0 ? size[0] : 720;
        int outHeight = size[1] != 0 ? size[1] : 1280;
        outputOption.setWidth(outWidth);
        outputOption.setHeight(outHeight);
        outputOption.frameRate = 24;

        if (!addAdVideo) {
            EpVideo video = new EpVideo(path);
            EpDraw epDraw = new EpDraw(waterMarkPath, LOGO_PADDING_LEFT, LOGO_PADDING_TOP, LOGO_WIDTH, LOGO_HEIGHT, false);
            video.addDraw(epDraw);
            try {
                Context context = FunnyApplication.getContext();
                EpEditorEx.exec(video, outputOption, new RxFFmpegInvoke.IFFmpegListener() {
                    @Override
                    public void onFinish() {
                        StorageUtil.notifyAlbum(context, new File(outPath), true, false);
                        if (listener != null) {
                            listener.onSuccess(videoInfo, outPath);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Log.e("VideoEditUtils", "onError: " + message);
                        if (listener != null) {
                            listener.onFail(videoInfo, new Exception(message));
                        }
                    }

                    @Override
                    public void onProgress(int progress, long progressTime) {
                        if (listener != null) {
                            listener.onProgress(progress * 1f / 100);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            } catch (Exception e) {
                if (listener != null) {
                    listener.onFail(videoInfo, e);
                }
            }
        } else {
            String adVideoPath = getAdVideoPath(landscape);
            File adVideo = new File(adVideoPath);
            if (!adVideo.exists()) {
                copyAdVideo(landscape);
            }
            ArrayList<EpVideo> epVideos = new ArrayList<>();
            EpVideo video = new EpVideo(path);
            EpDraw epDraw = new EpDraw(waterMarkPath, 16, 16, 201, 48, false);
            video.addDraw(epDraw);
            epVideos.add(video);

            EpVideo adEpVideo = new EpVideo(adVideoPath);
            int adVideoWidth = landscape ? 1280 : 720;
            int adVideoHeight = landscape ? 720 : 1280;
            //need crop,use 9:16 ad video
            if (adVideoWidth * outHeight != adVideoHeight * outWidth) {
                adEpVideo = new EpVideo(getAdVideoPath(false));
                adVideoWidth = 720;
                adVideoHeight = 1280;
                adEpVideo.crop(outWidth, outHeight, Math.max((adVideoWidth - outWidth) / 2, 0) ,
                        Math.max((adVideoHeight - outHeight) / 2, 0));
            }
            epVideos.add(adEpVideo);


            try {
                Context context = FunnyApplication.getContext();
                EpEditorEx.merge(epVideos, outputOption, new RxFFmpegInvoke.IFFmpegListener() {
                    @Override
                    public void onFinish() {
                        StorageUtil.notifyAlbum(context, new File(outPath), true, false);
                        if (listener != null) {
                            listener.onSuccess(videoInfo, outPath);
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Log.e("VideoEditUtils", "onError: " + message);
                        if (listener != null) {
                            listener.onFail(videoInfo, new Exception(message));
                        }
                    }


                    @Override
                    public void onProgress(int progress, long progressTime) {
                        if (listener != null) {
                            listener.onProgress(progress * 1f / 100);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            } catch (Exception e) {
                if (listener != null) {
                    listener.onFail(videoInfo, e);
                }
            }
        }
    }

    public static void cancel() {
        RxFFmpegInvoke.getInstance().onCancel();
        RxFFmpegInvoke.getInstance().exit();
        RxFFmpegInvoke.getInstance().onClean();

    }

    public static void copyWaterMark() throws IOException {
        InputStream inputStream = FunnyApplication.getContext().getAssets().open("logo.png");
        FileUtil.writeFileToDisk(getWaterMarkTempPath(), inputStream);
        FileUtil.renameFile(new File(getWaterMarkTempPath()), getWaterMarkPath());
    }

    public static void copyAdVideo(boolean landscape) throws IOException {
        InputStream inputStream = FunnyApplication.getContext().getAssets().open((landscape ? "ad_16_9.mp4" : "ad_9_16.mp4"));
        FileUtil.writeFileToDisk(getAdVideoTempPath(), inputStream);
        FileUtil.renameFile(new File(getAdVideoTempPath()), getAdVideoPath(landscape));
    }


    public static String getAdVideoPath(boolean landscape) {
        return FunnyApplication.getContext().getFilesDir() + File.separator + (landscape ? "ad_16_9.mp4" : "ad_9_16.mp4");
    }

    public static String getAdVideoTempPath() {
        return FunnyApplication.getContext().getFilesDir() + File.separator +  "ad_tmp.mp4";
    }

}
