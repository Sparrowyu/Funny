package com.sortinghat.common.glide;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.sortinghat.common.utils.StorageUtil;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Glide图片框架的配置
 * Created by wzy on 2021/4/15
 */
public class GlideConfiguration extends AppGlideModule {

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void applyOptions(@NonNull @NotNull Context context, @NonNull @NotNull GlideBuilder builder) {
        builder.setDiskCache(new DiskLruCacheFactory(StorageUtil.getCachePath(), "glidecache", 300 * 1024 * 1024));
    }

    @Override
    public void registerComponents(@NonNull @NotNull Context context, @NonNull @NotNull Glide glide, @NonNull @NotNull Registry registry) {
        //设置长时间读取和断线重连
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.MINUTES)
                .readTimeout(15, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .build();

        registry.append(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
    }
}
