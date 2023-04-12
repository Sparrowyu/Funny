package com.sortinghat.common.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.CheckResult;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.manager.Lifecycle;
import com.bumptech.glide.manager.RequestManagerTreeNode;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.net.URL;

/**
 * Includes all additions from methods in {@link com.bumptech.glide.annotation.GlideExtension}s
 * annotated with {@link com.bumptech.glide.annotation.GlideType}
 *
 * <p>Generated code, do not modify
 */
@SuppressWarnings("deprecation")
public class GlideRequestsS extends RequestManager {
    public GlideRequestsS(@NonNull Glide glide, @NonNull Lifecycle lifecycle,
                          @NonNull RequestManagerTreeNode treeNode, @NonNull Context context) {
        super(glide, lifecycle, treeNode, context);
    }

    @Override
    @CheckResult
    @NonNull
    public <ResourceType> GlideRequestT<ResourceType> as(@NonNull Class<ResourceType> resourceClass) {
        return new GlideRequestT<>(glide, this, resourceClass, context);
    }

    @Override
    @NonNull
    public synchronized GlideRequestsS applyDefaultRequestOptions(@NonNull RequestOptions options) {
        return (GlideRequestsS) super.applyDefaultRequestOptions(options);
    }

    @Override
    @NonNull
    public synchronized GlideRequestsS setDefaultRequestOptions(@NonNull RequestOptions options) {
        return (GlideRequestsS) super.setDefaultRequestOptions(options);
    }

    @Override
    @NonNull
    public GlideRequestsS addDefaultRequestListener(RequestListener<Object> listener) {
        return (GlideRequestsS) super.addDefaultRequestListener(listener);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<Bitmap> asBitmap() {
        return (GlideRequestT<Bitmap>) super.asBitmap();
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<GifDrawable> asGif() {
        return (GlideRequestT<GifDrawable>) super.asGif();
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<Drawable> asDrawable() {
        return (GlideRequestT<Drawable>) super.asDrawable();
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<Drawable> load(@Nullable Bitmap bitmap) {
        return (GlideRequestT<Drawable>) super.load(bitmap);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<Drawable> load(@Nullable Drawable drawable) {
        return (GlideRequestT<Drawable>) super.load(drawable);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<Drawable> load(@Nullable String string) {
        return (GlideRequestT<Drawable>) super.load(string);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<Drawable> load(@Nullable Uri uri) {
        return (GlideRequestT<Drawable>) super.load(uri);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<Drawable> load(@Nullable File file) {
        return (GlideRequestT<Drawable>) super.load(file);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<Drawable> load(@RawRes @DrawableRes @Nullable Integer id) {
        return (GlideRequestT<Drawable>) super.load(id);
    }

    @Override
    @Deprecated
    @CheckResult
    public GlideRequestT<Drawable> load(@Nullable URL url) {
        return (GlideRequestT<Drawable>) super.load(url);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<Drawable> load(@Nullable byte[] bytes) {
        return (GlideRequestT<Drawable>) super.load(bytes);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<Drawable> load(@Nullable Object o) {
        return (GlideRequestT<Drawable>) super.load(o);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<File> downloadOnly() {
        return (GlideRequestT<File>) super.downloadOnly();
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<File> download(@Nullable Object o) {
        return (GlideRequestT<File>) super.download(o);
    }

    @Override
    @NonNull
    @CheckResult
    public GlideRequestT<File> asFile() {
        return (GlideRequestT<File>) super.asFile();
    }


    @Override
    protected synchronized void setRequestOptions(@NonNull RequestOptions toSet) {
        super.setRequestOptions(toSet);
    }
}

