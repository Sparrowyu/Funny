package com.sortinghat.common.utils;

import com.sortinghat.common.http.HttpHelp;
import com.sortinghat.common.http.HttpUtils;
import com.sortinghat.common.http.ProgressRequestBody;
import com.sortinghat.common.http.ProgressResponseBody;

/**
 * Created by wzy on 2021/3/10
 */
public class BuildFactory {

    private static BuildFactory instance;
    private Object requestApi;
    private Object requestLogApi;
    private Object dataWarehouseApi;
    private Object downloadFileApi;
    private Object uploadFileApi;
    private Object requestTaskApi;

    public static BuildFactory getInstance() {
        if (instance == null) {
            synchronized (BuildFactory.class) {
                if (instance == null) {
                    instance = new BuildFactory();
                }
            }
        }
        return instance;
    }

    public <T> T create(Class<T> a, String type) {
        if (HttpUtils.funnyBaseUrl.equals(type)) {
            if (requestApi == null) {
                synchronized (BuildFactory.class) {
                    if (requestApi == null) {
                        requestApi = HttpUtils.getInstance().getBuilder(type).build().create(a);
                    }
                }
            }
            return (T) requestApi;
        } else if (HttpUtils.funnyUnifyLogUrl.equals(type)) {
            if (dataWarehouseApi == null) {
                synchronized (BuildFactory.class) {
                    if (dataWarehouseApi == null) {
                        dataWarehouseApi = HttpUtils.getInstance().getBuilder(type).build().create(a);
                    }
                }
            }
            return (T) dataWarehouseApi;
        } else if (HttpUtils.funnyTaskBaseUrl.equals(type)) {
            if (requestTaskApi == null) {
                synchronized (BuildFactory.class) {
                    if (requestTaskApi == null) {
                        requestTaskApi = HttpUtils.getInstance().getBuilder(type).build().create(a);
                    }
                }
            }
            return (T) requestTaskApi;
        }
        return null;
    }

    public <T> T create(Class<T> a, String type, ProgressResponseBody.ProgressListener responseProgressListener, ProgressRequestBody.ProgressListener requestProgressListener) {
        if (HttpHelp.funnyDownloadFileUrl.equals(type)) {
            if (downloadFileApi == null) {
                synchronized (BuildFactory.class) {
                    if (downloadFileApi == null) {
                        downloadFileApi = HttpHelp.getInstance().getBuilder(type, responseProgressListener, null).build().create(a);
                    }
                }
            }
            return (T) downloadFileApi;
        } else if (HttpHelp.funnyUploadFileUrl.equals(type)) {
            if (uploadFileApi == null) {
                synchronized (BuildFactory.class) {
                    if (uploadFileApi == null) {
                        uploadFileApi = HttpHelp.getInstance().getBuilder(type, null, requestProgressListener).build().create(a);
                    }
                }
            }
            return (T) uploadFileApi;
        }
        return null;
    }
}
