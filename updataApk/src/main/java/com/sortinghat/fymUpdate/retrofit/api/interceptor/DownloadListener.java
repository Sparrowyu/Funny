package com.sortinghat.fymUpdate.retrofit.api.interceptor;

/**
 * Created by  on 2019/11/15.
 */
public interface DownloadListener {
    void onStart();//下载开始

    void onProgress(int progress);//下载进度

    void onFinish(String path);//下载完成

    void onFail(String errorInfo);//下载失败
}
