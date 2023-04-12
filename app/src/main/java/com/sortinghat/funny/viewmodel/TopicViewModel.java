package com.sortinghat.funny.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.sortinghat.funny.bean.BannerBean;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.bean.TopicBean;
import com.sortinghat.funny.bean.TopicListBean;
import com.sortinghat.funny.http.HttpClient;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by wzy on 2021/7/1
 */
public class TopicViewModel extends HomeViewModel {

    public TopicViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<BaseResultBean<List<TopicListBean>>> getTopicList(String jsonData) {
        final MutableLiveData<BaseResultBean<List<TopicListBean>>> topicListLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getTopicList(requestBody),
                topicListLiveData::setValue,
                throwable -> {
                    topicListLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return topicListLiveData;
    }

    public MutableLiveData<BaseResultBean<List<BannerBean>>> getTopicBanners(String jsonData) {
        final MutableLiveData<BaseResultBean<List<BannerBean>>> bannersLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getTopicBanners(requestBody),
                bannersLiveData::setValue,
                throwable -> {
                    bannersLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return bannersLiveData;
    }

    public MutableLiveData<BaseResultBean<List<TopicBean>>> getAllTopicList(String jsonData) {
        final MutableLiveData<BaseResultBean<List<TopicBean>>> systemTopicListLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getAllTopicList(requestBody),
                systemTopicListLiveData::setValue,
                throwable -> {
                    systemTopicListLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return systemTopicListLiveData;
    }

    //0 热帖 1：最新
    public MutableLiveData<BaseResultBean<HomeVideoImageListBean>> getTopicRelationPostList(String jsonData, int topicTab) {
        final MutableLiveData<BaseResultBean<HomeVideoImageListBean>> topicRelationPostListLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        if (topicTab == 0) {
            addDisposable(execute(HttpClient.Builder.getGeneralServer().getTopicRelationPostList(requestBody),
                    topicRelationPostListLiveData::setValue,
                    throwable -> {
                        topicRelationPostListLiveData.setValue(null);
                        disposeServerException(throwable);
                    }));
        } else {
            addDisposable(execute(HttpClient.Builder.getGeneralServer().getTopicByCreateDayDescList(requestBody),
                    topicRelationPostListLiveData::setValue,
                    throwable -> {
                        topicRelationPostListLiveData.setValue(null);
                        disposeServerException(throwable);
                    }));
        }
        return topicRelationPostListLiveData;
    }

    public MutableLiveData<BaseResultBean<Object>> likeOrShieldTopic(String jsonData) {
        final MutableLiveData<BaseResultBean<Object>> likeOrShieldTopicLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().likeOrShieldTopic(requestBody),
                likeOrShieldTopicLiveData::setValue,
                throwable -> {
                    likeOrShieldTopicLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return likeOrShieldTopicLiveData;
    }
}
