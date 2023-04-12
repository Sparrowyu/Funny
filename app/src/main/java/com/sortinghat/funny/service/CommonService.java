package com.sortinghat.funny.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.viewmodel.MessageViewModel;
import com.sortinghat.funny.viewmodel.MyFragmentViewModel;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wzy on 2021/9/17
 */
public class CommonService extends Service {

    private MessageViewModel messageViewModel;
    private MyFragmentViewModel myFragmentViewModel;

    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();
        messageViewModel = new MessageViewModel(FunnyApplication.getInstance());
        myFragmentViewModel = new MyFragmentViewModel(FunnyApplication.getInstance());
        createAndStartTimer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new CommonServiceBinder();
    }

    private void createAndStartTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 1) {
                    getMessageCount();
                }
                getTaskMessageCount();
            }
        }, 0, 60 * 1000);
    }

    public void getMessageCount() {
        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);

        ThreadUtils.runOnUiThread(() -> messageViewModel.getMessageCount(jsonObject.toString()).observeForever(resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    if (resultBean.getData() != null) {
                        RxBus.getDefault().postSticky(resultBean.getData());
                    }
                }
            }
        }));
    }

    public void getTaskMessageCount() {
        if (null == myFragmentViewModel || !SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(Constant.ANDROID_TASK_SYS_AB, false))//是否是任务
        {
            return;
        }
        ThreadUtils.runOnUiThread(() -> myFragmentViewModel.getTaskMessageCount().observeForever(resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    if (resultBean.getData() != null) {
                        RxBus.getDefault().postSticky(resultBean.getData());
                    }
                }
            }
        }));
    }

    public class CommonServiceBinder extends Binder {

        public CommonService getService() {
            return CommonService.this;
        }
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }
}
