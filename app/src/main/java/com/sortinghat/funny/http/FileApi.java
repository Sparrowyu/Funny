package com.sortinghat.funny.http;

import com.sortinghat.common.http.HttpHelp;
import com.sortinghat.common.http.ProgressRequestBody;
import com.sortinghat.common.http.ProgressResponseBody;
import com.sortinghat.common.utils.BuildFactory;
import com.sortinghat.funny.bean.UploadVideoOrImageBean;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by wzy on 2020/4/20
 */
public interface FileApi {

    class Builder {
        public static FileApi getDownloadFileServer(ProgressResponseBody.ProgressListener responseProgressListener) {
            return BuildFactory.getInstance().create(FileApi.class, HttpHelp.funnyDownloadFileUrl, responseProgressListener, null);
        }

        public static FileApi getUploadFileServer(ProgressRequestBody.ProgressListener requestProgressListener) {
            return BuildFactory.getInstance().create(FileApi.class, HttpHelp.funnyUploadFileUrl, null, requestProgressListener);
        }
    }

    @Streaming
    @GET
    Flowable<ResponseBody> downloadFile(@Url String url);

    /**
     * 发布-上传帖子文件
     */
    @Multipart
    @POST("upload")
    Observable<UploadVideoOrImageBean> uploadPostFile(@Part MultipartBody.Part file);
//    Observable<UploadVideoOrImageBean> uploadPostFile(@Part MultipartBody.Part file, @Part("size") RequestBody fileSize, @Part("suffix") RequestBody fileExpandedName, @Part("key") RequestBody fileNameMd5, @Part("shardIndex") RequestBody shardIndex, @Part("shardTotal") RequestBody shardTotal, @Part("shardSize") RequestBody shardSize);
}
