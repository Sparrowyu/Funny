package com.sortinghat.common.glide;

/**
 * 图片加载回调
 * Created by wzy on 2021/4/15
 */
public interface ImageLoaderListener<T> {

    void onLoadComplete(T data);
}
