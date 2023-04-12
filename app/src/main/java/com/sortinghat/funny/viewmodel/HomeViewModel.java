package com.sortinghat.funny.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sortinghat.common.base.BaseListViewModel;
import com.sortinghat.common.http.ProgressResponseBody;
import com.sortinghat.common.utils.FileUtil;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.ClientConfigBean;
import com.sortinghat.funny.bean.HomeVideoDisLikeBean;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.bean.HomeVideoLikeBean;
import com.sortinghat.funny.bean.MyOwnerUserInfoBean;
import com.sortinghat.funny.bean.PostReviewBean;
import com.sortinghat.funny.bean.TopicHomeChoseDialogBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.http.FileApi;
import com.sortinghat.funny.http.HttpClient;
import com.sortinghat.funny.http.LogApi;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.business.RequestParamUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class HomeViewModel extends FollowViewModel {

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    //    appuse ：后台进入 homebutton：点击home  refresh：下拉刷新的，outlogin：退出或者重新登录 up：正常上滑加载更多
    public MutableLiveData<BaseResultBean<HomeVideoImageListBean>> getHomeVideoList(Context mContext, int tab, List<Long> posIdList, String direction) {
        JsonArray jsonArray = new JsonArray();
        //只有帖子相同时才回传未看
        for (long i : posIdList) {
            jsonArray.add(i);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("tab", tab);
        jsonObject.addProperty("direction", direction);
        jsonObject.add("unexposed_pids", jsonArray);
        RequestParamUtil.addCommonRequestParam(jsonObject);
        RequestParamUtil.addCommonDeviceParam(jsonObject, mContext);

        final MutableLiveData<BaseResultBean<HomeVideoImageListBean>> homeVideoListLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getHomeVideoList(requestBody),
                resultBean -> homeVideoListLiveData.setValue(resultBean),
                throwable -> {
                    homeVideoListLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return homeVideoListLiveData;
    }

    @SuppressLint("CheckResult")
    public void downloadFile(String fileUrl, final String saveFilePath, ProgressResponseBody.ProgressListener progressListener, Consumer<Boolean> onNext, Consumer<? super Throwable> onError) {
        FileApi.Builder.getDownloadFileServer(progressListener)
                .downloadFile(fileUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(responseBody -> FileUtil.writeFileToDisk(saveFilePath, responseBody))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    /**
     * 点赞
     *
     * @param likeType 0 是没有，1 普通点赞  2 以后看，3 触动内心，4 感动哭了，5 特别爱看，6 笑出声，7 取消
     */
    public MutableLiveData<BaseResultBean<HomeVideoLikeBean>> setHomeVideoLike(int likeType, long postId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("postId", postId);
        jsonObject.addProperty("likeType", likeType);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<HomeVideoLikeBean>> setHomevideoLikeData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().setHomeVideoLike(requestBody),
                resultBean -> setHomevideoLikeData.setValue(resultBean),
                throwable -> {
                    setHomevideoLikeData.setValue(null);
                    disposeServerException(throwable);
                }));
        return setHomevideoLikeData;
    }

    /**
     * 点踩
     *
     * @param disLikeType 0 默认值，1 普通踩，2 不爱看，3 屏蔽作者，4 屏蔽xx话题，5 取消
     */
    public MutableLiveData<BaseResultBean<HomeVideoDisLikeBean>> setHomeVideoDisLike(int disLikeType, long postId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("postId", postId);
        jsonObject.addProperty("disLikeType", disLikeType);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<HomeVideoDisLikeBean>> setHomevideoDisLikeData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().setHomeVideoDisLike(requestBody),
                resultBean -> setHomevideoDisLikeData.setValue(resultBean),
                throwable -> {
                    setHomevideoDisLikeData.setValue(null);
                    disposeServerException(throwable);
                }));
        return setHomevideoDisLikeData;
    }

    /**
     * 点踩发送文字
     * idea:图片已经不能表达我的想法意见,示例值(看不顺眼)
     * complain:举报的其他原因,示例值(太血腥)
     * topics:屏蔽话题ID列表，逗号分隔,示例值(1,10004)
     * kind:举报id
     */
    public MutableLiveData<BaseResultBean<Object>> sendReportContent(long postId, String complain, String idea, int kind, String topics) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("complain", complain);
        jsonObject.addProperty("idea", idea);
        jsonObject.addProperty("kind", kind);
        jsonObject.addProperty("postId", postId);
        jsonObject.addProperty("topics", topics);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<Object>> reportData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().sendReportContent(requestBody),
                resultBean -> reportData.setValue(resultBean),
                throwable -> {
                    reportData.setValue(null);
                    disposeServerException(throwable);
                }));
        return reportData;
    }

    public MutableLiveData<BaseResultBean<List<PostReviewBean>>> getPostReview(String jsonData) {
        final MutableLiveData<BaseResultBean<List<PostReviewBean>>> postReviewLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getPostReview(requestBody),
                resultBean -> postReviewLiveData.setValue(resultBean),
                throwable -> {
                    postReviewLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return postReviewLiveData;
    }

    public MutableLiveData<BaseResultBean<Long>> publishReview(String jsonData) {
        final MutableLiveData<BaseResultBean<Long>> publishReviewLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().publishReview(requestBody),
                resultBean -> publishReviewLiveData.setValue(resultBean),
                throwable -> {
                    publishReviewLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return publishReviewLiveData;
    }

    public MutableLiveData<BaseResultBean<Long>> postReviewLikeOrUnlike(String jsonData) {
        final MutableLiveData<BaseResultBean<Long>> likeOrUnlikeLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().postReviewLikeOrUnlike(requestBody),
                resultBean -> likeOrUnlikeLiveData.setValue(resultBean),
                throwable -> {
                    likeOrUnlikeLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return likeOrUnlikeLiveData;
    }

    public MutableLiveData<BaseResultBean<Long>> deleteReview(String jsonData) {
        final MutableLiveData<BaseResultBean<Long>> deleteReviewLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        addDisposable(execute(HttpClient.Builder.getGeneralServer().deleteReview(requestBody),
                resultBean -> deleteReviewLiveData.setValue(resultBean),
                throwable -> {
                    deleteReviewLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return deleteReviewLiveData;
    }

    /**
     * tabType 0:我的发布 1:我的评论 2：我的喜欢
     */
    public MutableLiveData<BaseResultBean<List<HomeVideoImageListBean.ListBean>>> getWorksLikeList(int tabType, int pageNum, int likeType, long otherUserId, int pageSize) {

        JsonObject jsonObject = new JsonObject();
        if (tabType == 2) {
            jsonObject.addProperty("likeType", likeType);
        }
        jsonObject.addProperty("otherUserId", otherUserId == 0 ? SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id") : otherUserId);//查询别人的就传别人的id
        RequestParamUtil.addPagingParam(jsonObject, pageNum, pageSize);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<List<HomeVideoImageListBean.ListBean>>> fansListBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        if (tabType == 0) {
            addDisposable(execute(HttpClient.Builder.getGeneralServer().getWorksMyList(requestBody),
                    resultBean -> fansListBean.setValue(resultBean),
                    throwable -> {
                        fansListBean.setValue(null);
                        disposeServerException(throwable);
                    }));
        } else if (tabType == 1) {
            addDisposable(execute(HttpClient.Builder.getGeneralServer().getWorksCommentList(requestBody),
                    resultBean -> fansListBean.setValue(resultBean),
                    throwable -> {
                        fansListBean.setValue(null);
                        disposeServerException(throwable);
                    }));

        } else {
            addDisposable(execute(HttpClient.Builder.getGeneralServer().getWorksLikeList(requestBody),
                    resultBean -> fansListBean.setValue(resultBean),
                    throwable -> {
                        fansListBean.setValue(null);
                        disposeServerException(throwable);
                    }));

        }
        return fansListBean;
    }

    /**
     * 删除自己帖子
     *
     * @param postId
     */
    public MutableLiveData<BaseResultBean<Object>> getDeletePost(long postId) {

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("postId", postId);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<Object>> publishReviewLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getDeletePost(requestBody),
                resultBean -> publishReviewLiveData.setValue(resultBean),
                throwable -> {
                    publishReviewLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return publishReviewLiveData;
    }

    //登录类型：游客-0,一键登录-1,手机短信登录-2,微信登录-3,token登录-4,
    public MutableLiveData<BaseResultBean<MyOwnerUserInfoBean>> getLoginId(int loginType) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("loginType", loginType);
        jsonObject.addProperty("deviceId", DeviceUtils.getUniqueDeviceId());

        final MutableLiveData<BaseResultBean<MyOwnerUserInfoBean>> loginBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().setLogin(requestBody),
                resultBean -> loginBean.setValue(resultBean),
                throwable -> {
                    loginBean.setValue(null);
                    disposeServerException(throwable);
                }));
        return loginBean;
    }

    //配置 获取配置接口,不传参数的话全部返回
    public MutableLiveData<BaseResultBean<ClientConfigBean>> getClientConfig() {
        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<ClientConfigBean>> configBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getClientConfig(requestBody),
                resultBean -> {
                    configBean.setValue(resultBean);
                    ConstantUtil.setConfigSp(resultBean);
                },
                throwable -> {
                    configBean.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return configBean;
    }

    //收集用户弹框信息
    public MutableLiveData<BaseResultBean<Object>> getCollectionInformation(String age, String sex) {
        JsonObject jsonObject = new JsonObject();
        if (!TextUtils.isEmpty(age)) {
            jsonObject.addProperty("age", age);
        }
        if (!TextUtils.isEmpty(sex)) {
            jsonObject.addProperty("gender", sex);
        }

        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<Object>> loginBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getCollectionInformation(requestBody),
                resultBean -> {
                    loginBean.setValue(resultBean);
                },
                throwable -> {
                    loginBean.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return loginBean;
    }

    //获取弹框的话题列表
    public MutableLiveData<BaseResultBean<List<TopicHomeChoseDialogBean>>> getUserLikeTopic() {
        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<List<TopicHomeChoseDialogBean>>> listBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getUserLikeTopic(requestBody),
                resultBean -> {
                    listBean.setValue(resultBean);
                },
                throwable -> {
                    listBean.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return listBean;
    }

    //用户吐槽
    public MutableLiveData<BaseResultBean<Object>> getUserCriticizeApp(String content) {
        JsonObject jsonObject = new JsonObject();
        if (!TextUtils.isEmpty(content)) {
            jsonObject.addProperty("content", content);
        }

        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<Object>> loginBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getUserCriticizeApp(requestBody),
                resultBean -> {
                    loginBean.setValue(resultBean);
                },
                throwable -> {
                    loginBean.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return loginBean;
    }

    //话题-喜欢  fromPage collPage
    public MutableLiveData<BaseResultBean<Object>> likeOrShieldTopic(String topicIds) {
        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);
        jsonObject.addProperty("type", 1);
        jsonObject.addProperty("topicIds", topicIds);
        jsonObject.addProperty("fromPage", "collPage");


        final MutableLiveData<BaseResultBean<Object>> likeOrShieldTopicLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().likeOrShieldTopic(requestBody),
                likeOrShieldTopicLiveData::setValue,
                throwable -> {
                    likeOrShieldTopicLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return likeOrShieldTopicLiveData;
    }

    /**
     * 埋点统计 帖子日志，行为日志，前后台日志统一的
     *
     * @param jsonString jsonString
     * @param isOnPause  isOnPause //是否是退出app的，如果退出app则主需要发送本次就行,如果本次没有发送成功则丢失，避免和启动日志bug
     */
    public MutableLiveData<Object> setAppUnifyLog(Context context, String jsonString, boolean isOnPause) {
        JSONObject jsonObject = new JSONObject();
        RequestParamUtil.addLogkitCommonParam(jsonObject, context);

        if (!isOnPause) {
            String start_log_string = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString(Constant.START_LOG_STRING_NEW);
            SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put(Constant.START_LOG_STRING_NEW, "");
            if (!TextUtils.isEmpty(start_log_string)) {
                jsonString = start_log_string + ConstantUtil.LogListTag + jsonString;
            }
        }

        try {
            JSONArray jsonArrayData = new JSONArray();
            String[] spArrayString = jsonString.split(ConstantUtil.LogListTag);
            if (spArrayString.length > 0) {
                for (int i = 0; i < spArrayString.length; i++) {
                    JSONObject myjson = null;
                    myjson = new JSONObject(spArrayString[i]);
                    jsonArrayData.put(myjson);
                }
            }
            jsonObject.putOpt("data", jsonArrayData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("start-log--app-success", "homeviewModel:" + jsonString);
        final MutableLiveData<Object> setAppUnifyLog = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        String finalJsonString = jsonString;
        addDisposable(execute(LogApi.Builder.getUnifyLogServer().setAppUnifyLog(requestBody),
                resultBean -> {
                    setAppUnifyLog.setValue(resultBean);
                },
                throwable -> {
                    if (!isOnPause) {
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put(Constant.START_LOG_STRING_NEW, finalJsonString);
                    }
                    setAppUnifyLog.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return setAppUnifyLog;
    }
}
