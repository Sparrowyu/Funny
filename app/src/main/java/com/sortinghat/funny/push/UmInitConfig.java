package com.sortinghat.funny.push;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.R;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.util.CommonUtil;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.DateUtil;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.api.UPushRegisterCallback;
import com.umeng.message.entity.UMessage;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.vivo.VivoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

import static android.os.Looper.getMainLooper;

public class UmInitConfig {

    private static final String TAG = "app-push";
    public static final String UPDATE_STATUS_ACTION = "com.umeng.message.example.action.UPDATE_STATUS";
    private Handler handler;

    public void UMinit(Context context) {
        UMConfigure.init(context, context.getString(R.string.um_appkey), CommonUtils.getUmChannel(context), UMConfigure.DEVICE_TYPE_PHONE, context.getString(R.string.um_secret));
        //PushSDK初始化(如使用推送SDK，必须调用此方法)
        initUpush(context);
    }

    String iconPath = "";

    /**
     * 主进程和子进程channel都需要进行初始化和注册
     */
    private void initUpush(Context context) {
        PushAgent pushAgent = PushAgent.getInstance(context);
        handler = new Handler(getMainLooper());

        //sdk开启通知声音，raw下的声音umeng_push_notification_default_sound
//        pushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        // sdk关闭通知声音
        // mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
        // 通知声音由服务端控制
        pushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER);
        pushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SERVER);
        pushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);


        UmengMessageHandler messageHandler = new UmengMessageHandler() {

            /**
             * 通知的回调方法（通知送达时会回调）
             */
            @Override
            public void dealWithNotificationMessage(Context context, UMessage msg) {
                //调用super，会展示通知，不调用super，则不展示通知。应用内这样了
                Log.i(TAG, "supermsg：" + msg.text);
                if (null != msg.extra && null != msg.extra.get("thumbUrl") && !TextUtils.isEmpty(msg.extra.get("thumbUrl")) && (msg.extra.get("thumbUrl").contains("http"))) {
                    GlideUtils.loadImageToCacheFile(context, msg.extra.get("thumbUrl"), data -> iconPath = data);
                } else {
                    iconPath = "";
                }

                ThreadUtils.runOnUiThreadDelayed(() -> {
                    super.dealWithNotificationMessage(context, msg);
                    CommonUtil.wakeup(context);
                }, 800);

            }

            /**
             * 自定义消息的回调方法
             */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                Log.i(TAG, "Custommsg：" + msg.text);
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        Log.i(TAG, "viewmsg" + msg);
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(context).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(context).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }

            /**
             * 自定义通知栏样式的回调方法
             */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                Log.i(TAG, "builder_id：" + msg.builder_id);
                switch (msg.builder_id) {
                    case 0://默认
                        Notification.Builder builder;
                        if (Build.VERSION.SDK_INT >= 26) {
                            if (!UmengMessageHandler.isChannelSet) {
                                UmengMessageHandler.isChannelSet = true;
                                NotificationChannel chan = new NotificationChannel(UmengMessageHandler.PRIMARY_CHANNEL,
                                        PushAgent.getInstance(context).getNotificationChannelName(),
                                        NotificationManager.IMPORTANCE_DEFAULT);
                                NotificationManager manager = (NotificationManager) context.getSystemService(
                                        Context.NOTIFICATION_SERVICE);
                                if (manager != null) {
                                    manager.createNotificationChannel(chan);
                                }
                            }
                            builder = new Notification.Builder(context, UmengMessageHandler.PRIMARY_CHANNEL);
                        } else {
                            builder = new Notification.Builder(context);
                        }
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(),
                                ConstantUtil.isXiaomi() ? R.layout.notification_view : ConstantUtil.isVivo() ? R.layout.notification_view_vivo : R.layout.notification_view_huawei);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setTextViewText(R.id.notification_time, DateUtil.getMillToCurrentTime(System.currentTimeMillis()));
                        if (TextUtils.isEmpty(iconPath) || iconPath.length() < 10) {
//                            myNotificationView.setImageViewResource(R.id.notification_small_icon,
//                                    getSmallIconId(context, msg));
                        } else {
                            myNotificationView.setImageViewUri(R.id.notification_small_icon, Uri.parse(iconPath));
                        }
                        builder.setContent(myNotificationView)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);
                        return builder.getNotification();
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        pushAgent.setMessageHandler(messageHandler);
//        pushAgent.setDisplayNotificationNumber(10);//设置显示推送条数，最高为10

        /*
         * 自定义行为的回调处理，参考文档：高级功能-通知的展示及提醒-自定义通知打开动作
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
            }

            @Override
            public void openUrl(Context context, UMessage msg) {
                super.openUrl(context, msg);
            }

            @Override
            public void openActivity(Context context, UMessage msg) {
                Log.i(TAG, "appmsg1" + msg.extra);//应用在前台时打开应用，不过要考虑是否在首页和首页第一项
                if (null != msg.extra && null != msg.extra.get("postId") && !TextUtils.isEmpty(msg.extra.get("postId"))) {
                    RxBus.getDefault().post(RxCodeConstant.PUSH_FORE_TO_HOME, msg.extra.get("postId"));
                }
                super.openActivity(context, msg);
            }

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        //使用自定义的NotificationHandler,这个可以打开app或者指定页面
        pushAgent.setNotificationClickHandler(notificationClickHandler);

        //注册推送服务 每次调用register都会回调该接口
        pushAgent.register(new UPushRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //我的小米：AmnXvHBeSdZ0R5Yq0t9QjqpheW_-fNvrz8-Oyqzqmezt
                Log.i(TAG, "device token: " + deviceToken);
                ConstantUtil.pushToken = deviceToken;
                long userid = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id");
                if (userid > 0) {
                    ConstantUtil.setAlias(context, userid);
                }
//                context.sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.i(TAG, "register failed: " + s + " " + s1);
//                context.sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }
        });

        //使用完全自定义处理
//        pushAgent.setPushIntentServiceClass(UmengNotificationService.class);
        pushAgent.setResourcePackageName("com.sortinghat.funny");

        //小米通道。MiPushBroadcastReceiver
        MiPushRegistar.register(context, context.getString(R.string.mi_id), context.getString(R.string.mi_appkey));
//        //oppo通道，OppoPush
        OppoRegister.register(context, context.getString(R.string.oppo_appkey), context.getString(R.string.oppo_secret));
//        //华为通道
        HuaWeiRegister.register(FunnyApplication.getInstance());
        //vivo通道。PushMessageReceiver
        VivoRegister.register(context);
        //魅族通道
        //MeizuRegister.register(this, MEIZU_APPID, MEIZU_APPKEY);
    }
}
