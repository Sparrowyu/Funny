package com.sortinghat.funny.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sortinghat.common.base.BaseListViewModel;
import com.sortinghat.funny.bean.AuditNoticeMessageBean;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.FansMessageBean;
import com.sortinghat.funny.bean.MessageCountBean;
import com.sortinghat.funny.bean.PraiseMessageBean;
import com.sortinghat.funny.bean.ReviewAndReplyMessageBean;
import com.sortinghat.funny.bean.event.SystemNoticeBean;
import com.sortinghat.funny.http.HttpClient;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by wzy on 2021/9/17
 */
public class MessageViewModel extends PostViewModel {

    public MessageViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<BaseResultBean<MessageCountBean>> getMessageCount(String jsonData) {
        final MutableLiveData<BaseResultBean<MessageCountBean>> messageCountLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getMessageCount(requestBody),
                resultBean -> messageCountLiveData.setValue(resultBean),
                throwable -> {
                    messageCountLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return messageCountLiveData;
    }

    public MutableLiveData<BaseResultBean<List<FansMessageBean>>> getFansMessageDetailsData(String jsonData) {
        final MutableLiveData<BaseResultBean<List<FansMessageBean>>> fansMessageLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getFansMessageDetailsData(requestBody),
                resultBean -> fansMessageLiveData.setValue(resultBean),
                throwable -> {
                    fansMessageLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return fansMessageLiveData;
    }

    public MutableLiveData<BaseResultBean<List<ReviewAndReplyMessageBean>>> getReviewAndReplyMessageDetailsData(String jsonData) {
        final MutableLiveData<BaseResultBean<List<ReviewAndReplyMessageBean>>> reviewAndReplyMessageLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getReviewAndReplyMessageDetailsData(requestBody),
                resultBean -> reviewAndReplyMessageLiveData.setValue(resultBean),
                throwable -> {
                    reviewAndReplyMessageLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return reviewAndReplyMessageLiveData;
    }

    public MutableLiveData<BaseResultBean<List<PraiseMessageBean>>> getPraiseMessageDetailsData(String jsonData) {
        final MutableLiveData<BaseResultBean<List<PraiseMessageBean>>> praiseMessageLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getPraiseMessageDetailsData(requestBody),
                resultBean -> praiseMessageLiveData.setValue(resultBean),
                throwable -> {
                    praiseMessageLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return praiseMessageLiveData;
    }

    public MutableLiveData<BaseResultBean<List<SystemNoticeBean>>> getSystemNoticeMessageDetailsData(String jsonData) {
        final MutableLiveData<BaseResultBean<List<SystemNoticeBean>>> systemNoticeMessageLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getSystemNoticeMessageDetailsData(requestBody),
                resultBean -> systemNoticeMessageLiveData.setValue(resultBean),
                throwable -> {
                    systemNoticeMessageLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return systemNoticeMessageLiveData;
    }

    public MutableLiveData<BaseResultBean<List<AuditNoticeMessageBean>>> getAuditNoticeMessageDetailsData(String jsonData) {
        final MutableLiveData<BaseResultBean<List<AuditNoticeMessageBean>>> auditNoticeMessageLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getAuditNoticeMessageDetailsData(requestBody),
                resultBean -> auditNoticeMessageLiveData.setValue(resultBean),
                throwable -> {
                    auditNoticeMessageLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return auditNoticeMessageLiveData;
    }
}
