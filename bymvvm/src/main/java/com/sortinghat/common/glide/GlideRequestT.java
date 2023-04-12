package com.sortinghat.common.glide;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.CheckResult;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestListener;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Contains all public methods from {@link RequestBuilder<TranscodeType>}, all options from
 * {@link com.bumptech.glide.request.RequestOptions} and all generated options from
 * {@link com.bumptech.glide.annotation.GlideOption} in annotated methods in
 * {@link com.bumptech.glide.annotation.GlideExtension} annotated classes.
 *
 * <p>Generated code, do not modify.
 *
 * @see RequestBuilder<TranscodeType>
 * @see com.bumptech.glide.request.RequestOptions
 */
@SuppressWarnings({
        "unused",
        "deprecation"
})
public class GlideRequestT<TranscodeType> extends RequestBuilder<TranscodeType> implements Cloneable {
    GlideRequestT(@NonNull Class<TranscodeType> transcodeClass, @NonNull RequestBuilder<?> other) {
        super(transcodeClass, other);
    }

    GlideRequestT(@NonNull Glide glide, @NonNull RequestManager requestManager,
                  @NonNull Class<TranscodeType> transcodeClass, @NonNull Context context) {
        super(glide, requestManager ,transcodeClass, context);
    }

    @Override
    @CheckResult
    @NonNull
    protected GlideRequestT<File> getDownloadOnlyRequest() {
        return new GlideRequestT<>(File.class, this).apply(DOWNLOAD_ONLY_OPTIONS);
    }

    /**
     * @see GlideOptions#sizeMultiplier(float)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> sizeMultiplier(@FloatRange(from = 0.0, to = 1.0) float value) {
        return (GlideRequestT<TranscodeType>) super.sizeMultiplier(value);
    }

    /**
     * @see GlideOptions#useUnlimitedSourceGeneratorsPool(boolean)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> useUnlimitedSourceGeneratorsPool(boolean flag) {
        return (GlideRequestT<TranscodeType>) super.useUnlimitedSourceGeneratorsPool(flag);
    }

    /**
     * @see GlideOptions#useAnimationPool(boolean)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> useAnimationPool(boolean flag) {
        return (GlideRequestT<TranscodeType>) super.useAnimationPool(flag);
    }

    /**
     * @see GlideOptions#onlyRetrieveFromCache(boolean)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> onlyRetrieveFromCache(boolean flag) {
        return (GlideRequestT<TranscodeType>) super.onlyRetrieveFromCache(flag);
    }

    /**
     * @see GlideOptions#diskCacheStrategy(DiskCacheStrategy)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> diskCacheStrategy(@NonNull DiskCacheStrategy strategy) {
        return (GlideRequestT<TranscodeType>) super.diskCacheStrategy(strategy);
    }

    /**
     * @see GlideOptions#priority(Priority)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> priority(@NonNull Priority priority) {
        return (GlideRequestT<TranscodeType>) super.priority(priority);
    }

    /**
     * @see GlideOptions#placeholder(Drawable)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> placeholder(@Nullable Drawable drawable) {
        return (GlideRequestT<TranscodeType>) super.placeholder(drawable);
    }

    /**
     * @see GlideOptions#placeholder(int)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> placeholder(@DrawableRes int id) {
        return (GlideRequestT<TranscodeType>) super.placeholder(id);
    }

    /**
     * @see GlideOptions#fallback(Drawable)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> fallback(@Nullable Drawable drawable) {
        return (GlideRequestT<TranscodeType>) super.fallback(drawable);
    }

    /**
     * @see GlideOptions#fallback(int)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> fallback(@DrawableRes int id) {
        return (GlideRequestT<TranscodeType>) super.fallback(id);
    }

    /**
     * @see GlideOptions#error(Drawable)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> error(@Nullable Drawable drawable) {
        return (GlideRequestT<TranscodeType>) super.error(drawable);
    }

    /**
     * @see GlideOptions#error(int)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> error(@DrawableRes int id) {
        return (GlideRequestT<TranscodeType>) super.error(id);
    }

    /**
     * @see GlideOptions#theme(Resources.Theme)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> theme(@Nullable Resources.Theme theme) {
        return (GlideRequestT<TranscodeType>) super.theme(theme);
    }

    /**
     * @see GlideOptions#skipMemoryCache(boolean)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> skipMemoryCache(boolean skip) {
        return (GlideRequestT<TranscodeType>) super.skipMemoryCache(skip);
    }

    /**
     * @see GlideOptions#override(int, int)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> override(int width, int height) {
        return (GlideRequestT<TranscodeType>) super.override(width, height);
    }

    /**
     * @see GlideOptions#override(int)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> override(int size) {
        return (GlideRequestT<TranscodeType>) super.override(size);
    }

    /**
     * @see GlideOptions#signature(Key)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> signature(@NonNull Key key) {
        return (GlideRequestT<TranscodeType>) super.signature(key);
    }

    /**
     * @see GlideOptions#set(Option<Y>, Y)
     */
    @NonNull
    @CheckResult
    public <Y> GlideRequestT<TranscodeType> set(@NonNull Option<Y> option, @NonNull Y y) {
        return (GlideRequestT<TranscodeType>) super.set(option, y);
    }

    /**
     * @see GlideOptions#decode(Class<?>)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> decode(@NonNull Class<?> clazz) {
        return (GlideRequestT<TranscodeType>) super.decode(clazz);
    }

    /**
     * @see GlideOptions#encodeFormat(Bitmap.CompressFormat)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> encodeFormat(@NonNull Bitmap.CompressFormat format) {
        return (GlideRequestT<TranscodeType>) super.encodeFormat(format);
    }

    /**
     * @see GlideOptions#encodeQuality(int)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> encodeQuality(@IntRange(from = 0, to = 100) int value) {
        return (GlideRequestT<TranscodeType>) super.encodeQuality(value);
    }

    /**
     * @see GlideOptions#frame(long)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> frame(@IntRange(from = 0) long value) {
        return (GlideRequestT<TranscodeType>) super.frame(value);
    }

    /**
     * @see GlideOptions#format(DecodeFormat)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> format(@NonNull DecodeFormat format) {
        return (GlideRequestT<TranscodeType>) super.format(format);
    }

    /**
     * @see GlideOptions#disallowHardwareConfig()
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> disallowHardwareConfig() {
        return (GlideRequestT<TranscodeType>) super.disallowHardwareConfig();
    }

    /**
     * @see GlideOptions#downsample(DownsampleStrategy)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> downsample(@NonNull DownsampleStrategy strategy) {
        return (GlideRequestT<TranscodeType>) super.downsample(strategy);
    }

    /**
     * @see GlideOptions#timeout(int)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> timeout(@IntRange(from = 0) int value) {
        return (GlideRequestT<TranscodeType>) super.timeout(value);
    }

    /**
     * @see GlideOptions#optionalCenterCrop()
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> optionalCenterCrop() {
        return (GlideRequestT<TranscodeType>) super.optionalCenterCrop();
    }

    /**
     * @see GlideOptions#centerCrop()
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> centerCrop() {
        return (GlideRequestT<TranscodeType>) super.centerCrop();
    }

    /**
     * @see GlideOptions#optionalFitCenter()
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> optionalFitCenter() {
        return (GlideRequestT<TranscodeType>) super.optionalFitCenter();
    }

    /**
     * @see GlideOptions#fitCenter()
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> fitCenter() {
        return (GlideRequestT<TranscodeType>) super.fitCenter();
    }

    /**
     * @see GlideOptions#optionalCenterInside()
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> optionalCenterInside() {
        return (GlideRequestT<TranscodeType>) super.optionalCenterInside();
    }

    /**
     * @see GlideOptions#centerInside()
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> centerInside() {
        return (GlideRequestT<TranscodeType>) super.centerInside();
    }

    /**
     * @see GlideOptions#optionalCircleCrop()
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> optionalCircleCrop() {
        return (GlideRequestT<TranscodeType>) super.optionalCircleCrop();
    }

    /**
     * @see GlideOptions#circleCrop()
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> circleCrop() {
        return (GlideRequestT<TranscodeType>) super.circleCrop();
    }

    /**
     * @see GlideOptions#transform(Transformation<Bitmap>)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> transform(@NonNull Transformation<Bitmap> transformation) {
        return (GlideRequestT<TranscodeType>) super.transform(transformation);
    }

    /**
     * @see GlideOptions#transform(Transformation<Bitmap>[])
     */
    @NonNull
    @CheckResult
    @SuppressWarnings({
            "unchecked",
            "varargs"
    })
    public GlideRequestT<TranscodeType> transform(@NonNull Transformation<Bitmap>... transformations) {
        return (GlideRequestT<TranscodeType>) super.transform(transformations);
    }

    /**
     * @see GlideOptions#transforms(Transformation<Bitmap>[])
     */
    @Deprecated
    @NonNull
    @CheckResult
    @SuppressWarnings({
            "unchecked",
            "varargs"
    })
    public GlideRequestT<TranscodeType> transforms(@NonNull Transformation<Bitmap>... transformations) {
        return (GlideRequestT<TranscodeType>) super.transforms(transformations);
    }

    /**
     * @see GlideOptions#optionalTransform(Transformation<Bitmap>)
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> optionalTransform(@NonNull Transformation<Bitmap> transformation) {
        return (GlideRequestT<TranscodeType>) super.optionalTransform(transformation);
    }

    /**
     * @see GlideOptions#optionalTransform(Class<Y>, Transformation<Y>)
     */
    @NonNull
    @CheckResult
    public <Y> GlideRequestT<TranscodeType> optionalTransform(@NonNull Class<Y> clazz,
                                                              @NonNull Transformation<Y> transformation) {
        return (GlideRequestT<TranscodeType>) super.optionalTransform(clazz, transformation);
    }

    /**
     * @see GlideOptions#transform(Class<Y>, Transformation<Y>)
     */
    @NonNull
    @CheckResult
    public <Y> GlideRequestT<TranscodeType> transform(@NonNull Class<Y> clazz,
                                                      @NonNull Transformation<Y> transformation) {
        return (GlideRequestT<TranscodeType>) super.transform(clazz, transformation);
    }

    /**
     * @see GlideOptions#dontTransform()
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> dontTransform() {
        return (GlideRequestT<TranscodeType>) super.dontTransform();
    }

    /**
     * @see GlideOptions#dontAnimate()
     */
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> dontAnimate() {
        return (GlideRequestT<TranscodeType>) super.dontAnimate();
    }

    /**
     * @see GlideOptions#lock()
     */
    @NonNull
    public GlideRequestT<TranscodeType> lock() {
        return (GlideRequestT<TranscodeType>) super.lock();
    }

    /**
     * @see GlideOptions#autoClone()
     */
    @NonNull
    public GlideRequestT<TranscodeType> autoClone() {
        return (GlideRequestT<TranscodeType>) super.autoClone();
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> apply(@NonNull BaseRequestOptions<?> options) {
        return (GlideRequestT<TranscodeType>) super.apply(options);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> transition(@NonNull TransitionOptions<?, ? super TranscodeType> options) {
        return (GlideRequestT<TranscodeType>) super.transition(options);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> listener(@Nullable RequestListener<TranscodeType> listener) {
        return (GlideRequestT<TranscodeType>) super.listener(listener);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> addListener(@Nullable RequestListener<TranscodeType> listener) {
        return (GlideRequestT<TranscodeType>) super.addListener(listener);
    }

    @Override
    @NonNull
    public GlideRequestT<TranscodeType> error(@Nullable RequestBuilder<TranscodeType> builder) {
        return (GlideRequestT<TranscodeType>) super.error(builder);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> error(Object o) {
        return (GlideRequestT<TranscodeType>) super.error(o);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> thumbnail(@Nullable RequestBuilder<TranscodeType> builder) {
        return (GlideRequestT<TranscodeType>) super.thumbnail(builder);
    }

    @Override
    @NonNull
    @CheckResult
    @SafeVarargs
    @SuppressWarnings("varargs")
    public final GlideRequestT<TranscodeType> thumbnail(@Nullable RequestBuilder<TranscodeType>... builders) {
        return (GlideRequestT<TranscodeType>) super.thumbnail(builders);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> thumbnail(@Nullable List<RequestBuilder<TranscodeType>> list) {
        return (GlideRequestT<TranscodeType>) super.thumbnail(list);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> thumbnail(float sizeMultiplier) {
        return (GlideRequestT<TranscodeType>) super.thumbnail(sizeMultiplier);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> load(@Nullable Object o) {
        return (GlideRequestT<TranscodeType>) super.load(o);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> load(@Nullable Bitmap bitmap) {
        return (GlideRequestT<TranscodeType>) super.load(bitmap);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> load(@Nullable Drawable drawable) {
        return (GlideRequestT<TranscodeType>) super.load(drawable);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> load(@Nullable String string) {
        return (GlideRequestT<TranscodeType>) super.load(string);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> load(@Nullable Uri uri) {
        return (GlideRequestT<TranscodeType>) super.load(uri);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> load(@Nullable File file) {
        return (GlideRequestT<TranscodeType>) super.load(file);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> load(@RawRes @DrawableRes @Nullable Integer id) {
        return (GlideRequestT<TranscodeType>) super.load(id);
    }

    @Override
    @Deprecated
    @CheckResult
    public GlideRequestT<TranscodeType> load(@Nullable URL url) {
        return (GlideRequestT<TranscodeType>) super.load(url);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<TranscodeType> load(@Nullable byte[] bytes) {
        return (GlideRequestT<TranscodeType>) super.load(bytes);
    }

    @Override
    @CheckResult
    public GlideRequestT<TranscodeType> clone() {
        return (GlideRequestT<TranscodeType>) super.clone();
    }
}

