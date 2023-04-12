package com.sortinghat.funny.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.sortinghat.common.base.BaseViewModel;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.TopicBean;
import com.sortinghat.funny.http.HttpClient;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by wzy on 2021/7/1
 */
public class PublishTopicViewModel extends BaseViewModel {

    public PublishTopicViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<BaseResultBean<List<TopicBean>>> getSystemTopicList(String jsonData) {
        final MutableLiveData<BaseResultBean<List<TopicBean>>> systemTopicListLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getSystemTopicList(requestBody),
                resultBean -> systemTopicListLiveData.setValue(resultBean),
                throwable -> {
                    systemTopicListLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return systemTopicListLiveData;
    }

    public MutableLiveData<BaseResultBean<List<TopicBean.SubTopicBean>>> getRecommendTopicList(String jsonData) {
        final MutableLiveData<BaseResultBean<List<TopicBean.SubTopicBean>>> recommendTopicListLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getRecommendTopicList(requestBody),
                resultBean -> recommendTopicListLiveData.setValue(resultBean),
                throwable -> {
                    recommendTopicListLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return recommendTopicListLiveData;
    }
}
