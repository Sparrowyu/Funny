package com.sortinghat.fymUpdate.updateapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.content.FileProvider;

import com.sortinghat.fymUpdate.R;
import com.sortinghat.fymUpdate.common.UpdateApp;
import com.sortinghat.fymUpdate.common.Utils;
import com.sortinghat.fymUpdate.utils.SPUtils;
import com.sortinghat.fymUpdate.utils.UrlUtils;
import com.sortinghat.fymUpdate.view.UpdateWifiPagDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class DownloadNewService extends Service {
    private static final int NOTIFY_ID = 99999;
    private int progress;
    private NotificationManager mNotificationManager;
    public static boolean canceled = true;
    // 返回的安装包url
    private String apkUrl = "";
    /* 下载包安装路径 */
    private static String savePath = "";
    private static String saveFileName;
    private UpdateInfoBean.DataBean info;
    private int opType;
    private Notification mNotification;
    private Context mContext = this;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @SuppressWarnings("deprecation")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    // 下载完毕 取消通知
                    mNotificationManager.cancel(NOTIFY_ID);
                    Activity currActivity = getGlobleActivity();
                    if (currActivity != null) {
                        UpdateApp.installApk(currActivity, opType);
                        SPUtils.putISDOWNLOADEND(mContext, 1);
                    }
                    break;
                case 1:
                    int rate = msg.arg1;
                    if (rate < 100) {
                        RemoteViews contentview = mNotification.contentView;
                        contentview.setProgressBar(R.id.cy_plugin_nofipb, 100, rate, false);
                        SPUtils.putISDOWNLOADEND(mContext, -1);
                    } else {
                        // 下载完毕后变换通知形式
                        Notification.Builder builder = new Notification.Builder(mContext);//新建Notification.Builder对象
                        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                        mNotification.contentView = null;
                        Intent intent = new Intent(mContext, DownloadNewService.this.getBaseContext().getClass());
                        // 告知已完成
                        intent.putExtra("completed", "yes");
                        // 更新参数,注意flags要使用FLAG_UPDATE_CURRENT
                        PendingIntent contentIntent = PendingIntent.getActivity(
                                mContext, 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        String tip = mContext.getResources().getString(R.string.cy_plugin_downloaded);
                        builder.setContentTitle(tip);//设置标题
                        builder.setContentText(tip);//设置内容
                        int icon = R.drawable.cy_plgin_downicon;
                        builder.setSmallIcon(icon);//设置图片
                        builder.setContentIntent(contentIntent);//执行intent
                        mNotification = builder.getNotification();
                        stopSelf();// 停掉服务自身
                        SPUtils.putISDOWNLOADEND(mContext, 1);
                    }
                    mNotificationManager.notify(NOTIFY_ID, mNotification);
                    break;
                case 2:
                    mNotificationManager.cancel(NOTIFY_ID);
                    break;
                case 3:
                    mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                    mNotification.contentView = null;
                    mNotificationManager.cancel(NOTIFY_ID);
                    stopSelf();
                    SPUtils.putISDOWNLOADEND(mContext, -1);
                    break;
                default:
                    mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                    stopSelf();// 停掉服务自身
                    SPUtils.putISDOWNLOADEND(mContext, -1);
                    break;
            }
        }
    };

    private RemoteViews contentView;
    private int lengthFile;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            createNotification();
            startForeground(NOTIFY_ID, mNotification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            info = (UpdateInfoBean.DataBean) intent.getSerializableExtra("upinfo");
            if (info != null) {
                opType = intent.getIntExtra("opType", 0);
                contentView = new RemoteViews(getPackageName(), R.layout.cy_plugin_update);
                savePath = Utils.getSingleSdcardPath(this) + "/" + "download_CyPlugin";
                saveFileName = UrlUtils.UpdateAddressUrl(this, info.getVersionName()); // 如果存在先删除
                File file = new File(saveFileName);
                if (file.exists()) {
                    file.delete();
                    SPUtils.putISDOWNLOADEND(mContext, -1);
                }

                apkUrl = info.getDownloadurl();
                progress = 0;
                setUpNotification(); // 设置通知栏
                startDownload();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startDownload() {
        canceled = false;
        downloadApk();
    }

    // 通知栏

    /**
     * 创建通知
     */
    private void setUpNotification() {
        int tvid = R.id.cy_plugin_notifititle;
        String text = getResources().getString(R.string.cy_plugin_update_noti_loading);
        contentView.setTextViewText(tvid, text);
        int imageId = R.id.cy_plugin_notifiimage;
        contentView.setImageViewResource(imageId, info.getRIcon());
        createNotification();
        mNotificationManager.notify(NOTIFY_ID, mNotification);
    }

    //
    /**
     * 下载apk
     *
     * @param url
     */
    private Thread downLoadThread;

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        UpdateWifiPagDialog dialog = new UpdateWifiPagDialog(mContext);
        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File apkfile = new File(saveFileName);
                if (!apkfile.exists()) {
                    return;
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//大于等于24
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(DownloadNewService.this, mContext.getPackageName() + ".fileprovider", apkfile);
                    i.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } else {
                    i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                            "application/vnd.android.package-archive");
                }
                mContext.startActivity(i);
            }
        });
        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int lastRate = 0;
    private Runnable mdownApkRunnable = new Runnable() {

        @Override
        public void run() {
            HttpURLConnection conn = null;
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                URL url = new URL(apkUrl);

                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(20000);
                conn.setReadTimeout(20000);
                conn.connect();
                lengthFile = conn.getContentLength();
                is = conn.getInputStream();

                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }

                if (file.exists()) {
                    if (file.length() >= lengthFile) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(0);
                        // 下载完了，cancelled也要设置
                        canceled = true;
                        return;
                    }
                }

                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];
                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / lengthFile) * 100);
                    // 更新进度
                    Message msg = mHandler.obtainMessage();

                    msg.what = 1;
                    msg.arg1 = progress;
                    if (progress >= lastRate + 1) {
                        mHandler.sendMessage(msg);
                        lastRate = progress;
                    }
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(0);
                        // 下载完了，cancelled也要设置
                        canceled = true;
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!canceled);// 点击取消就停止下载.
            } catch (Exception e) {
                // 下载失败
                canceled = true;
                Message msg = mHandler.obtainMessage();
                msg.what = 3;
                mHandler.sendMessage(msg);
            } finally {

                if (conn != null) {
                    conn.disconnect();
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {

                    }
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    public Activity getGlobleActivity() {
        Class<?> activityThreadClass;
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map<?, ?> activities = (Map<?, ?>) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class<? extends Object> activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createNotification() {
        int icon = R.drawable.cy_plgin_downicon;
        long when = System.currentTimeMillis();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //分组（可选）
            //groupId要唯一
            String groupId = mContext.getPackageName() + "update" + NOTIFY_ID;
            NotificationChannelGroup group = new NotificationChannelGroup(groupId, "更新");
            //创建group
            mNotificationManager.createNotificationChannelGroup(group);
            //channelId要唯一
            String channelId = mContext.getPackageName() + "channel" + NOTIFY_ID;
            NotificationChannel adChannel = new NotificationChannel(channelId,
                    "更新提示", NotificationManager.IMPORTANCE_DEFAULT);
            adChannel.enableVibration(false);
            adChannel.setVibrationPattern(new long[]{0});
            adChannel.setSound(null, null);
            //补充channel的含义（可选）
            adChannel.setDescription("更新提示");
            //将渠道添加进组（先创建组才能添加）
            adChannel.setGroup(groupId);
            //创建channel
            mNotificationManager.createNotificationChannel(adChannel);
            //创建通知时，标记你的渠道id

            Intent intent = new Intent(this, this.getBaseContext().getClass());
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            mNotification = new Notification.Builder(mContext, channelId).setCustomContentView(contentView)
                    .setContentIntent(contentIntent)
                    .setSmallIcon(icon)
                    .setAutoCancel(true)
                    .build();
        } else {
            Intent intent = new Intent(this, this.getBaseContext().getClass());
            mNotification = new Notification(icon, "", when);
            mNotification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotification.contentView = contentView;
            mNotification.contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }
}