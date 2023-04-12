package com.sortinghat.funny.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.os.Process;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.JsonObject;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.viewmodel.SplashModel;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;

    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/CrashTest/log/";
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".log";

    private static CrashHandler sInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;
    private SplashModel splashModel;

    private CrashHandler() {
    }

    public static CrashHandler getsInstance() {
        return sInstance;
    }

    public void init(Context context, SplashModel splashModel) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
        if (splashModel != null) {
            this.splashModel = splashModel;
        }
    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     *
     * @param thread 为出现未捕获异常的线程
     * @param ex     为未捕获的异常，有了这个ex,我们就可以得到异常信息
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            String throwableString = Log.getStackTraceString(ex);
            if (!TextUtils.isEmpty(throwableString)) {
                long createTime = System.currentTimeMillis();
                String source = ConstantUtil.getLogActivity(ActivityUtils.getTopActivity());
                JsonObject startJsonObject = new JsonObject();
                startJsonObject.addProperty("create_time", createTime);//事件时间
                long forTime = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("foreground_time");
                if (forTime > 0) {
                    startJsonObject.addProperty("foreground_time", forTime);//开始时长
                    startJsonObject.addProperty("duration", createTime - forTime);//时长
                }

                RequestParamUtil.addLogkitErrParam(startJsonObject, throwableString);
                RequestParamUtil.addStartLogHeadParam(startJsonObject, "background", "app", source, "app");

                //这里可以上传异常信息到服务器，便于开发人员分析日志从而解决bug
                uploadExceptionToServer(startJsonObject.toString());
            }
            //导出异常信息到SD卡中
//            dumpExceptionToSDCard(ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ex.printStackTrace();
        //如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就由自己结束自己
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    private void dumpExceptionToSDCard(Throwable ex) throws IOException {
        //如果SD卡不存在或者无法使用，则无法把异常信息写入SD卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (DEBUG) {
                Log.w(TAG, "sdcard unmounted,skip dump exception");
                return;
            }
        }
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
        File file = new File(PATH + FILE_NAME + (time.replace(" ", "_")).replaceAll(":", "-") + FILE_NAME_SUFFIX);

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            ex.printStackTrace(pw);
            pw.close();
        } catch (Exception e) {
            Log.e(TAG, "dump crash info failed");
        }
    }

    private void dumpPhoneInfo(PrintWriter pw) {
        try {
            JSONObject jsonObject = new JSONObject();
            RequestParamUtil.addLogkitCommonParam(jsonObject, mContext);
            //common
            pw.print("common:");
            pw.println(jsonObject.toString());
        } catch (Exception e) {
            Log.e(TAG, "dump crash info failed");
        }
    }

    //崩溃时,可以保存一下退出记录，发送日志，如果犯错误，那就择机发送
    private void uploadExceptionToServer(String throwableString) {
        if (TextUtils.isEmpty(throwableString)) {
            return;
        }
        if (splashModel == null) {
            ConstantUtil.spStartLog(throwableString);
            return;
        }
        //observeForever不依赖activity
        splashModel.setAppUnifyLog(throwableString, mContext).observeForever(resultBean -> {
        });
    }
}
