package com.sortinghat.common.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.sortinghat.common.R;
import com.sortinghat.common.glide.ImageLoaderListener;
import com.sortinghat.common.utils.CommonUtils;

import java.io.File;

/**
 * 图片加载工具类
 * Created by wzy on 2021/4/15
 */
public class GlideUtils {

    private static int DEFAULT_IMAGE_NORMAL = R.drawable.image_defalut_big;

    public static void loadImageFromResource(int drawableResourceId, ImageView imageView) {
        loadImageFromResource(imageView.getContext(), drawableResourceId, imageView);
    }

    public static void loadImageNoPlaceholder(String url, ImageView imageView) {
        loadImage(imageView.getContext(), url, imageView, null);
    }

    public static void loadImageNoPlaceholder(String url, ImageView imageView, RequestListener<Drawable> requestListener) {
        loadImage(imageView.getContext(), url, imageView, requestListener);
    }

    public static void loadImageNoPlaceholder(String url, int width, int height, ImageView imageView) {
        loadImage(imageView.getContext(), url, width, height, imageView);
    }

    public static void loadImage(String url, ImageView imageView) {
        loadImage(imageView.getContext(), url, DEFAULT_IMAGE_NORMAL, imageView);
    }

    public static void loadImage(String url, int placeholder, ImageView imageView) {
        loadImage(imageView.getContext(), url, placeholder, imageView);
    }

    public static void loadImage(String url, int width, int height, ImageView imageView) {
        loadImage(imageView.getContext(), url, DEFAULT_IMAGE_NORMAL, width, height, imageView);
    }

    public static void loadImage(String url, int placeholder, int width, int height, ImageView imageView) {
        loadImage(imageView.getContext(), url, placeholder, width, height, imageView);
    }

    public static void loadCircleImage(String url, ImageView imageView) {
        loadCircleImage(imageView.getContext(), url, DEFAULT_IMAGE_NORMAL, imageView);
    }

    public static void loadCircleImage(String url, int placeholder, ImageView imageView) {
        loadCircleImage(imageView.getContext(), url, placeholder, imageView);
    }

    public static void loadCircleImage(String url, int width, int height, ImageView imageView) {
        loadCircleImage(imageView.getContext(), url, DEFAULT_IMAGE_NORMAL, width, height, imageView);
    }

    public static void loadCircleImage(String url, int placeholder, int width, int height, ImageView imageView) {
        loadCircleImage(imageView.getContext(), url, placeholder, width, height, imageView);
    }

    public static void loadRoundImage(String url, int radiusDpValue, ImageView imageView) {
        loadRoundImage(imageView.getContext(), url, DEFAULT_IMAGE_NORMAL, radiusDpValue, imageView);
    }

    public static void loadRoundImage(int url, int radiusDpValue, int width, int height, ImageView imageView) {
        loadRoundImage(imageView.getContext(), url, radiusDpValue, width, height, imageView);
    }

    public static void loadRoundImage(String url, int placeholder, int radiusDpValue, ImageView imageView) {
        loadRoundImage(imageView.getContext(), url, placeholder, radiusDpValue, imageView);
    }

    public static void loadRoundImage(String url, int radiusDpValue, int width, int height, ImageView imageView) {
        loadRoundImage(imageView.getContext(), url, DEFAULT_IMAGE_NORMAL, radiusDpValue, width, height, imageView);
    }

    public static void loadRoundImage(String url, int placeholder, int radiusDpValue, int width, int height, ImageView imageView) {
        loadRoundImage(imageView.getContext(), url, placeholder, radiusDpValue, width, height, imageView);
    }

    public static void loadGifImageFromResource(int drawableResourceId, ImageView imageView) {
        loadGifImageFromResource(imageView.getContext(), drawableResourceId, imageView);
    }

    public static void loadGifImage(String url, ImageView imageView) {
        loadGifImage(imageView.getContext(), url, DEFAULT_IMAGE_NORMAL, imageView, null);
    }

    public static void loadGifImage(String url, int placeholder, ImageView imageView) {
        loadGifImage(imageView.getContext(), url, placeholder, imageView, null);
    }

    public static void loadGifImage(String url, int placeholder, ImageView imageView, RequestListener<GifDrawable> requestListener) {
        loadGifImage(imageView.getContext(), url, placeholder, imageView, requestListener);
    }

    public static void loadGifImage(String url, int width, int height, ImageView imageView) {
        loadGifImage(imageView.getContext(), url, DEFAULT_IMAGE_NORMAL, width, height, imageView);
    }

    public static void loadGifImage(String url, int placeholder, int width, int height, ImageView imageView) {
        loadGifImage(imageView.getContext(), url, placeholder, width, height, imageView);
    }

    public static void loadImageWithCenterCrop(String url, ImageView imageView) {
        loadImageWithCenterCrop(imageView.getContext(), url, DEFAULT_IMAGE_NORMAL, imageView);
    }

    public static void loadImageWithCenterCrop(String url, int placeholder, ImageView imageView) {
        loadImageWithCenterCrop(imageView.getContext(), url, placeholder, imageView);
    }

    public static void loadImageWithCenterCrop(String url, int width, int height, ImageView imageView) {
        loadImageWithCenterCrop(imageView.getContext(), url, DEFAULT_IMAGE_NORMAL, width, height, imageView);
    }

    public static void loadImageWithCenterCrop(String url, int placeholder, int width, int height, ImageView imageView) {
        loadImageWithCenterCrop(imageView.getContext(), url, placeholder, width, height, imageView);
    }

    public static void loadRoundImageWithCenterCrop(String url, int radiusDpValue, ImageView imageView) {
        loadRoundImageWithCenterCrop(imageView.getContext(), url, DEFAULT_IMAGE_NORMAL, radiusDpValue, imageView);
    }

    public static void loadRoundImageWithCenterCrop(String url, int placeholder, int radiusDpValue, ImageView imageView) {
        loadRoundImageWithCenterCrop(imageView.getContext(), url, placeholder, radiusDpValue, imageView);
    }

    public static void loadRoundImageWithCenterCrop(String url, int radiusDpValue, int width, int height, ImageView imageView) {
        loadRoundImageWithCenterCrop(imageView.getContext(), url, DEFAULT_IMAGE_NORMAL, radiusDpValue, width, height, imageView);
    }

    public static void loadRoundImageWithCenterCrop(String url, int placeholder, int radiusDpValue, int width, int height, ImageView imageView) {
        loadRoundImageWithCenterCrop(imageView.getContext(), url, placeholder, radiusDpValue, width, height, imageView);
    }

    public static void loadVideoCoverImageWithUri(String path, ImageView imageView) {
        loadVideoCoverImageWithUri(imageView.getContext(), path, DEFAULT_IMAGE_NORMAL, imageView);
    }

    public static void loadImageWithPath(String path, ImageView imageView) {
        loadImageWithPath(imageView.getContext(), path, DEFAULT_IMAGE_NORMAL, imageView);
    }

    public static void loadImageWithPath(String path, int placeholder, ImageView imageView) {
        loadImageWithPath(imageView.getContext(), path, placeholder, imageView);
    }

    public static void loadImageWithPath(String path, int width, int height, ImageView imageView) {
        loadImageWithPath(imageView.getContext(), path, DEFAULT_IMAGE_NORMAL, width, height, imageView);
    }

    public static void loadImageWithPath(String path, int placeholder, int width, int height, ImageView imageView) {
        loadImageWithPath(imageView.getContext(), path, placeholder, width, height, imageView);
    }

    public static void loadImageWithPathAndCenterCrop(String path, ImageView imageView) {
        loadImageWithPathAndCenterCrop(imageView.getContext(), path, DEFAULT_IMAGE_NORMAL, imageView);
    }

    public static void loadImageWithPathAndCenterCrop(String path, int placeholder, ImageView imageView) {
        loadImageWithPathAndCenterCrop(imageView.getContext(), path, placeholder, imageView);
    }

    public static void loadImageWithPathAndCenterCrop(String path, int width, int height, ImageView imageView) {
        loadImageWithPathAndCenterCrop(imageView.getContext(), path, DEFAULT_IMAGE_NORMAL, width, height, imageView);
    }

    public static void loadImageWithPathAndCenterCrop(String path, int placeholder, int width, int height, ImageView imageView) {
        loadImageWithPathAndCenterCrop(imageView.getContext(), path, placeholder, width, height, imageView);
    }

    private static void loadImageFromResource(Context context, int drawableResourceId, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load(drawableResourceId)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }

    private static void loadImage(Context context, String url, ImageView imageView, RequestListener<Drawable> requestListener) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load(url)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .listener(requestListener == null ? requestImageListener : requestListener)
                .into(imageView);
    }

    private static void loadImage(Context context, String url, int width, int height, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load(url)
                .dontAnimate()
                .override(width, height)
                .listener(requestImageListener)
                .into(imageView);
    }

    private static void loadImage(Context context, String url, int placeholder, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load(url)
                .dontAnimate()
                .placeholder(placeholder)
                .error(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .listener(requestImageListener)
                .into(imageView);
    }

    private static void loadImage(Context context, String url, int placeholder, int width, int height, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load(url)
                .dontAnimate()
                .placeholder(placeholder)
                .error(placeholder)
                .override(width, height)
                .listener(requestImageListener)
                .into(imageView);
    }

    private static void loadImageWithCenterCrop(Context context, String url, int placeholder, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load(url)
                .dontAnimate()
                .placeholder(placeholder)
                .error(placeholder)
                .centerCrop()
                .listener(requestImageListener)
                .into(imageView);
    }

    private static void loadImageWithCenterCrop(Context context, String url, int placeholder, int width, int height, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load(url)
                .dontAnimate()
                .placeholder(placeholder)
                .error(placeholder)
                .override(width, height)
                .centerCrop()
                .listener(requestImageListener)
                .into(imageView);
    }

    private static void loadCircleImage(Context context, String url, int placeholder, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load(url)
                .dontAnimate()
                .placeholder(placeholder)
                .error(placeholder)
                .circleCrop()
                .listener(requestImageListener)
                .into(imageView);
    }

    private static void loadCircleImage(Context context, String url, int placeholder, int width, int height, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load(url)
                .dontAnimate()
                .placeholder(placeholder)
                .error(placeholder)
                .circleCrop()
                .override(width, height)
                .listener(requestImageListener)
                .into(imageView);
    }

    private static void loadRoundImage(Context context, String url, int placeholder, int radiusDpValue, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load(url)
                .dontAnimate()
                .placeholder(placeholder)
                .error(placeholder)
                .apply(new RequestOptions().transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(SizeUtils.dp2px(radiusDpValue)))))
                .listener(requestImageListener)
                .into(imageView);
    }

    private static void loadRoundImage(Context context, String url, int placeholder, int radiusDpValue, int width, int height, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load(url)
                .dontAnimate()
                .placeholder(placeholder)
                .error(placeholder)
                .transform(new RoundedCorners(SizeUtils.dp2px(radiusDpValue)))
                .override(width, height)
                .listener(requestImageListener)
                .into(imageView);
    }

    private static void loadRoundImage(Context context, int url, int radiusDpValue, int width, int height, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load(url)
                .dontAnimate()
                .transform(new RoundedCorners(SizeUtils.dp2px(radiusDpValue)))
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(imageView);
    }

    private static void loadRoundImageWithCenterCrop(Context context, String url, int placeholder, int radiusDpValue, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load(url)
                .dontAnimate()
                .placeholder(placeholder)
                .error(placeholder)
                .transform(new RoundedCorners(SizeUtils.dp2px(radiusDpValue)))
                .centerCrop()
                .listener(requestImageListener)
                .into(imageView);
    }

    private static void loadRoundImageWithCenterCrop(Context context, String url, int placeholder, int radiusDpValue, int width, int height, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load(url)
                .dontAnimate()
                .placeholder(placeholder)
                .error(placeholder)
                .transform(new RoundedCorners(SizeUtils.dp2px(radiusDpValue)))
                .override(width, height)
                .centerCrop()
                .listener(requestImageListener)
                .into(imageView);
    }

    private static void loadGifImageFromResource(Context context, int drawableResourceId, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).asGif().load(drawableResourceId)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }

    private static void loadGifImage(Context context, String url, int placeholder, ImageView imageView, RequestListener<GifDrawable> requestListener) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).asGif().load(url)
                .placeholder(placeholder)
                .error(placeholder)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .listener(requestListener == null ? requestGifImageListener : requestListener)
                .into(imageView);
    }

    private static void loadGifImage(Context context, String url, int placeholder, int width, int height, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).asGif().load(url)
                .placeholder(placeholder)
                .error(placeholder)
                .override(width, height)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .listener(requestGifImageListener)
                .into(imageView);
    }

    private static void loadVideoCoverImageWithUri(Context context, String filePath, int placeholder, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load(Uri.fromFile(new File(filePath)))
                .dontAnimate()
                .placeholder(placeholder)
                .error(placeholder)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .listener(requestImageListener)
                .into(imageView);
    }

    private static void loadImageWithPath(Context context, String filePath, int placeholder, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load("file:///" + filePath)
                .dontAnimate()
                .placeholder(placeholder)
                .error(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(requestImageListener)
                .into(imageView);
    }

    private static void loadImageWithPath(Context context, String filePath, int placeholder, int width, int height, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load("file:///" + filePath)
                .dontAnimate()
                .placeholder(placeholder)
                .error(placeholder)
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(requestImageListener)
                .into(imageView);
    }

    private static void loadImageWithPathAndCenterCrop(Context context, String filePath, int placeholder, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load("file:///" + filePath)
                .dontAnimate()
                .placeholder(placeholder)
                .error(placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(requestImageListener)
                .into(imageView);
    }

    private static void loadImageWithPathAndCenterCrop(Context context, String filePath, int placeholder, int width, int height, ImageView imageView) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context).load("file:///" + filePath)
                .dontAnimate()
                .placeholder(placeholder)
                .error(placeholder)
                .override(width, height)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(requestImageListener)
                .into(imageView);
    }

    public static void loadImageWithTarget(Context context, String url, SimpleTarget<Bitmap> target) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        Glide.with(context)
                .asBitmap()
                .load(url)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(target);
    }

    public static void loadImageToBitmap(final Context context, final String url, final ImageLoaderListener<Bitmap> listener) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        loadImageToBitmap(context, url, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL, listener);
    }

    public static void loadImageToBitmap(final Context context, final String url, final int width, final int height, final ImageLoaderListener<Bitmap> listener) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        new AsyncTask<String, Integer, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bitmap = null;
                try {
                    bitmap = Glide.with(context)
                            .asBitmap()
                            .load(url)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .submit(width, height)
                            .get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                listener.onLoadComplete(bitmap);
            }
        }.execute();
    }

    public static void loadImageToCacheFile(final Context context, final String url, final ImageLoaderListener<String> listener) {
        if (CommonUtils.isActivityDestroyed(context)) {
            return;
        }
        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... params) {
                File cacheFile = null;
                try {
                    cacheFile = Glide.with(context).asFile().load(url)
                            .submit()
                            .get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cacheFile == null || !cacheFile.exists()) {
                    return "";
                }
                return cacheFile.getAbsolutePath();
            }

            @Override
            protected void onPostExecute(String cachePath) {
                if (listener != null) {
                    listener.onLoadComplete(cachePath);
                }
            }
        }.execute();
    }

    private static RequestListener<Drawable> requestImageListener = new RequestListener<Drawable>() {

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            LogUtils.e(GlideUtils.class.getSimpleName() + ":" + model + (e == null ? "" : e.toString()));
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            return false;
        }
    };

    private static RequestListener<GifDrawable> requestGifImageListener = new RequestListener<GifDrawable>() {

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
            LogUtils.e(GlideUtils.class.getSimpleName() + ":" + model + (e == null ? "" : e.toString()));
            return false;
        }

        @Override
        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
            return false;
        }
    };

    public static void clearImageMemoryCache(Context context) { //只能在主线程执行
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Glide.get(context.getApplicationContext()).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearImageDiskCache(final Context context) { //只能在子线程执行
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        Glide.get(context.getApplicationContext()).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(context.getApplicationContext()).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
