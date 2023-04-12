package com.sortinghat.funny.bean;

import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectResult;

/**
 * Created by wzy on 2021/9/10
 */
public class VideoFileInfo {

    public final String url;
    public final long length;
    public final String savePath;
    public final OSSAsyncTask<GetObjectResult> task;

    public VideoFileInfo(String url, long length, String savePath, OSSAsyncTask<GetObjectResult> task) {
        this.url = url;
        this.length = length;
        this.savePath = savePath;
        this.task = task;
    }
}
