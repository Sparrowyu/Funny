package com.sortinghat.funny.viewmodel;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.SPUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.base.BaseListViewModel;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.PostReviewBean;
import com.sortinghat.funny.bean.StoreTaskListBean;
import com.sortinghat.funny.bean.StoreTaskMessageListBean;
import com.sortinghat.funny.bean.TaskCentralBean;
import com.sortinghat.funny.bean.TaskCommonToastBean;
import com.sortinghat.funny.bean.TaskPackageItemBean;
import com.sortinghat.funny.bean.TaskRankHeaderBean;
import com.sortinghat.funny.bean.TaskRankListBean;
import com.sortinghat.funny.bean.TaskTodayItemBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.http.HttpClient;
import com.sortinghat.funny.http.LogApi;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.business.RequestParamUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by wzy on 2021/9/26
 */
public class TaskCentralViewModel extends BaseListViewModel {

    public TaskCentralViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 任务中心数据
     */
    public MutableLiveData<BaseResultBean<TaskCentralBean>> getTaskCentralData() {
        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<TaskCentralBean>> publishReviewLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralTaskServer().getTaskCentralData(requestBody),
                resultBean -> publishReviewLiveData.setValue(resultBean),
                throwable -> {
                    publishReviewLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return publishReviewLiveData;
    }

    /**
     * 领取奖励
     * <p>
     * 0：每日任务，1：新手任务
     */
    public MutableLiveData<BaseResultBean<TaskCommonToastBean>> getTaskReceiveAward(int completeNum, int taskId, int taskType) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("completeNum", completeNum);
        jsonObject.addProperty("taskId", taskId);
        jsonObject.addProperty("taskType", taskType);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<TaskCommonToastBean>> publishReviewLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralTaskServer().getTaskReceiveAward(requestBody),
                resultBean -> publishReviewLiveData.setValue(resultBean),
                throwable -> {
                    publishReviewLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return publishReviewLiveData;
    }

    /**
     * 双倍奖励
     */
    public MutableLiveData<BaseResultBean<TaskCommonToastBean>> getTaskDoubleReceiveAward(int doubleType, int currentDay, int taskId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("currentDay", currentDay);
        jsonObject.addProperty("isDouble", 1);
        jsonObject.addProperty("doubleType", doubleType);
        jsonObject.addProperty("taskId", taskId);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<TaskCommonToastBean>> publishReviewLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralTaskServer().getTaskDoubleReceiveAward(requestBody),
                resultBean -> publishReviewLiveData.setValue(resultBean),
                throwable -> {
                    publishReviewLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return publishReviewLiveData;
    }

    /**
     * 输入邀请码
     */
    public MutableLiveData<BaseResultBean<Object>> getInviteFriendCode(String inviteCode) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code", inviteCode);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<Object>> publishReviewLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralTaskServer().getInviteFriendCode(requestBody),
                resultBean -> publishReviewLiveData.setValue(resultBean),
                throwable -> {
                    publishReviewLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return publishReviewLiveData;
    }

    /**
     * 兑换商品
     * <p>
     */
    public MutableLiveData<BaseResultBean<TaskCommonToastBean>> getExchangeStore(int taskId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", taskId);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<TaskCommonToastBean>> publishReviewLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralTaskServer().getExchangeStore(requestBody),
                resultBean -> publishReviewLiveData.setValue(resultBean),
                throwable -> {
                    publishReviewLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return publishReviewLiveData;
    }

    /**
     * 语音播报
     * <p>
     */
    public MutableLiveData<BaseResultBean<List<StoreTaskMessageListBean>>> getStoreBroadcastMsg() {
        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<List<StoreTaskMessageListBean>>> publishReviewLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralTaskServer().getStoreBroadcastMsg(requestBody),
                resultBean -> publishReviewLiveData.setValue(resultBean),
                throwable -> {
                    publishReviewLiveData.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return publishReviewLiveData;
    }

    /**
     * 签到
     */
    public MutableLiveData<BaseResultBean<TaskCommonToastBean>> getTaskDoSign() {
        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<TaskCommonToastBean>> publishReviewLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralTaskServer().getTaskDoSign(requestBody),
                resultBean -> publishReviewLiveData.setValue(resultBean),
                throwable -> {
                    publishReviewLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return publishReviewLiveData;
    }

    /**
     * 背包里面删除
     */
    public MutableLiveData<BaseResultBean<Object>> getDelPackageIcon(int proId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("proId", proId);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<Object>> publishReviewLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralTaskServer().getDelPackageIcon(requestBody),
                resultBean -> publishReviewLiveData.setValue(resultBean),
                throwable -> {
                    publishReviewLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return publishReviewLiveData;
    }

    /**
     * 背包里面装备
     * 穿戴或者卸下装扮，1:穿戴 0:卸下,示例值(1)
     */
    public MutableLiveData<BaseResultBean<TaskCommonToastBean>> setPackageIcon(int proId, int status) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("proId", proId);
        jsonObject.addProperty("status", status);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<TaskCommonToastBean>> publishReviewLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralTaskServer().setPackageIcon(requestBody),
                resultBean -> publishReviewLiveData.setValue(resultBean),
                throwable -> {
                    publishReviewLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return publishReviewLiveData;
    }


    /**
     * "completeNum": 0,
     * "isDouble": "1、加倍 0、不加倍",/**
     * "taskId": 0, *
     * "taskType": "0.日常任务，1.新手任务", *
     * "userId": 1,
     * "deviceId"
     */

    public MutableLiveData<BaseResultBean<List<TaskTodayItemBean>>> getTaskTodayList(JsonObject jsonObject) {

        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<List<TaskTodayItemBean>>> listBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralTaskServer().getTaskTodayList(requestBody),
                resultBean -> {
                    listBean.setValue(resultBean);
                },
                throwable -> {
                    listBean.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return listBean;
    }

    public MutableLiveData<BaseResultBean<StoreTaskListBean>> getStoreTaskList(int pageNum, int pageSize) {
        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);
        RequestParamUtil.addPagingParam(jsonObject, pageNum, pageSize);

        final MutableLiveData<BaseResultBean<StoreTaskListBean>> listBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralTaskServer().getStoreTaskList(requestBody),
                resultBean -> {
                    listBean.setValue(resultBean);
                },
                throwable -> {
                    listBean.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return listBean;
    }

    public MutableLiveData<BaseResultBean<List<TaskPackageItemBean>>> getPackageTaskList(int pageNum, int pageSize) {
        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);
        RequestParamUtil.addPagingParam(jsonObject, pageNum, pageSize);

        final MutableLiveData<BaseResultBean<List<TaskPackageItemBean>>> listBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralTaskServer().getPackageList(requestBody),
                resultBean -> {
                    listBean.setValue(resultBean);
                },
                throwable -> {
                    listBean.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return listBean;
    }


    /**
     * 埋点统计 帖子日志，行为日志，前后台日志统一的
     *
     * @param stype
     */
    public MutableLiveData<Object> setAppUnifyLog(String stype, String path, Context context) {

        long endTime = System.currentTimeMillis();
        //点击统计
        JsonObject postJsonObject = new JsonObject();
//        postJsonObject.addProperty("post_id", videoInfo.getContent().getPostId());
//        postJsonObject.addProperty("post_type", post_type);
        postJsonObject.addProperty("create_time", endTime);
        RequestParamUtil.addStartLogHeadParam(postJsonObject, "view", stype, path, "user_growth");

        String jsonString = postJsonObject.toString();

        JSONObject jsonObject = new JSONObject();
        RequestParamUtil.addLogkitCommonParam(jsonObject, context);

        String start_log_string = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString(Constant.START_LOG_STRING_NEW);
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put(Constant.START_LOG_STRING_NEW, "");
        if (!TextUtils.isEmpty(start_log_string)) {
            jsonString = start_log_string + ConstantUtil.LogListTag + jsonString;
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

        Log.d("start-log--app-success", "splashModel:" + jsonString);
        final MutableLiveData<Object> setAppUnifyLog = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        String finalJsonString = jsonString;
        addDisposable(execute(LogApi.Builder.getUnifyLogServer().setAppUnifyLog(requestBody),
                resultBean -> {
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put(Constant.START_LOG_STRING_NEW, "");
                    setAppUnifyLog.setValue(resultBean);
                },
                throwable -> {
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put(Constant.START_LOG_STRING_NEW, finalJsonString);
                    setAppUnifyLog.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return setAppUnifyLog;
    }

    /**
     * 我的排行
     * <p>
     */
    public MutableLiveData<BaseResultBean<TaskRankHeaderBean>> getMyRank() {
        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<TaskRankHeaderBean>> publishReviewLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralTaskServer().getMyrank(requestBody),
                resultBean -> publishReviewLiveData.setValue(resultBean),
                throwable -> {
                    publishReviewLiveData.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return publishReviewLiveData;
    }

    /**
     * 排行榜单
     * <p>
     */

    public MutableLiveData<BaseResultBean<List<TaskRankListBean>>> getRankList(int pageNum, int pageSize) {

        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);
        RequestParamUtil.addPagingParam(jsonObject, pageNum, pageSize);

        final MutableLiveData<BaseResultBean<List<TaskRankListBean>>> listBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralTaskServer().getRankList(requestBody),
                resultBean -> {
                    listBean.setValue(resultBean);
                },
                throwable -> {
                    listBean.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return listBean;
    }


}
