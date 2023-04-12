package com.sortinghat.funny.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileUtils;
import com.sortinghat.common.base.BaseViewModel;
import com.sortinghat.common.http.ProgressRequestBody;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.UploadVideoOrImageBean;
import com.sortinghat.funny.http.FileApi;
import com.sortinghat.funny.http.HttpClient;
import com.sortinghat.funny.thirdparty.album.AlbumFile;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by wzy on 2021/7/1
 */
public class PublishViewModel extends PostViewModel {

    public PublishViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<UploadVideoOrImageBean> uploadPostFile(AlbumFile albumFile, ProgressRequestBody.ProgressListener progressListener) {
        final MutableLiveData<UploadVideoOrImageBean> postFileLiveData = new MutableLiveData<>();

        File file = new File(albumFile.getPath());
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse(albumFile.getMimeType()), file);
        MultipartBody.Part fileMultipartBody = MultipartBody.Part.createFormData("file", file.getName(), fileRequestBody);

        RequestBody fileSizeBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(albumFile.getSize()));
        RequestBody fileExpandedNameBody = RequestBody.create(MediaType.parse("text/plain"), FileUtils.getFileExtension(file.getAbsolutePath()));
        RequestBody fileNameMd5Body = RequestBody.create(MediaType.parse("text/plain"), EncryptUtils.encryptMD5ToString(file.getName()));
        RequestBody shardIndexBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(1));
        RequestBody shardTotalBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(1));
        RequestBody shardSizeBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(albumFile.getSize()));

        addDisposable(execute(FileApi.Builder.getUploadFileServer(progressListener).uploadPostFile(fileMultipartBody),
//        addDisposable(execute(FileApi.Builder.getUploadFileServer(progressListener).uploadPostFile(fileMultipartBody, fileSizeBody, fileExpandedNameBody, fileNameMd5Body, shardIndexBody, shardTotalBody, shardSizeBody),
                resultBean -> postFileLiveData.setValue(resultBean),
                throwable -> {
                    postFileLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return postFileLiveData;
    }

    public MutableLiveData<BaseResultBean<Long>> publishPost(String jsonData) {
        final MutableLiveData<BaseResultBean<Long>> publishPostLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().publishPost(requestBody),
                resultBean -> publishPostLiveData.setValue(resultBean),
                throwable -> {
                    publishPostLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return publishPostLiveData;
    }
}
