package com.sortinghat.fymUpdate.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sortinghat.common.http.HttpUtils;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.fymUpdate.R;
import com.sortinghat.fymUpdate.retrofit.api.RentApi;
import com.sortinghat.fymUpdate.retrofit.api.interceptor.DownloadListener;
import com.sortinghat.fymUpdate.updateapp.DownloadNewService;
import com.sortinghat.fymUpdate.updateapp.UpdateInfoBean;
import com.sortinghat.fymUpdate.utils.SPUtils;
import com.sortinghat.fymUpdate.utils.UrlUtils;
import com.sortinghat.fymUpdate.view.UpdateMustUpdataDialog;
import com.sortinghat.fymUpdate.view.UpdateMustnoticeDialog;
import com.sortinghat.fymUpdate.view.UpdateWifiPagDialog;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * 版本更新
 */
public class UpdateApp {
    private Activity _context;
    private String _pkName;
    private int _versionCode;
    private static UpdateInfoBean.DataBean upInfoBean;
    private boolean isUpdate = false;
    private int _Ricon = 0; // 图标
    private int _from = 1;
    private String _dialogDate = "";
    private String _channel;
    private static String noticecontentLength = "7";
    private OnAlertDialogIsHave mIsHaveListener;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final int contentLength = (int) msg.obj;
            switch (msg.what) {
                case 0:
                    File file0 = new File(UrlUtils.UpdateAddressUrl(_context, versionnameData));
                    final long length = file0.length();
                    if (length >= contentLength) {
                        SPUtils.putISDOWNLOADEND(_context, 1);
                        installApk(_context, 0);
                    } else {
                        downloadApp(0);
                    }
                    break;
                case 1:
                    File file1 = new File(UrlUtils.UpdateAddressUrl(_context, versionnameData));
                    final long length1 = file1.length();
                    if (length1 > 0 && length1 >= contentLength) {
                        SPUtils.putISDOWNLOADEND(_context, 1);
                        installApk(_context, 0);
                        return;
                    }

                    if (upInfoBean != null) {
                        String title = _context.getString(R.string.cy_plugin_update) + "  " + "V" + upInfoBean.getVersionName();
                        MyAlertDialog.creatAlertDialog(_context, title,
                                "发现新版本，" + upInfoBean.getUpgradeDescription() + "，是否更新？");
                        MyAlertDialog.setOnAlertDialogOklistener(new OnAlertDialogOkListener() {
                            @Override
                            public void handleOkClick() {
                                if (length1 >= contentLength) {
                                    startInstallationAct(_context);
                                } else {
                                    downloadApp(1);
                                }
                            }

                            @Override
                            public void handleDismissClick() {
                            }
                        });
                    }
                case 2:
                    DecimalFormat df = new DecimalFormat("#.00");
                    noticecontentLength = df.format((double) contentLength / 1048576) + "MB";
                    break;
                case 3:
                    File file3 = new File(UrlUtils.UpdateAddressUrl(_context, versionnameData));
                    final long length3 = file3.length();
                    if (length3 > 0 && length3 >= contentLength) {
                        startInstallationAct(_context);
                        return;
                    } else {
                        startDownload();
                    }
                    break;
                case 4:
                    if (UpdateMustdialog != null) {
                        UpdateMustdialog.showDiaOrBt(0);
                        UpdateMustdialog.showProgress(contentLength);
                    }
                    break;
                case 5:
                    if (UpdateMustdialog != null) {
                        UpdateMustdialog.showDiaOrBt(1);
                        startInstallationAct(_context);
                    }
                    break;
            }
        }
    };
    private static String versionnameData;

    public UpdateApp(Activity context, int rValueIcon, int from, OnAlertDialogIsHave ishaveListener, String dialogDate) {
        _context = context;
        _Ricon = rValueIcon;
        _from = from;
        _dialogDate = dialogDate;
        mIsHaveListener = ishaveListener;
        try {
            _pkName = context.getPackageName();
            _versionCode = context.getPackageManager().getPackageInfo(_pkName,
                    0).versionCode;

            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(_pkName,
                            PackageManager.GET_META_DATA);
            _channel = appInfo.metaData.getString("UMENG_CHANNEL");

        } catch (Exception e) {
            _pkName = "";
            _versionCode = 0;
            _channel = "";
        }
        upInfoBean = new UpdateInfoBean().getData();
        if (mIsHaveListener != null) {
            mIsHaveListener.isHaveShow(false);
        }
    }

    public void checkUpdateNew() {
        if (!Utils.isNetworkConnected(_context)) {
            String text = _context.getResources().getString(R.string.cy_plugin_nonetworktip);
            CommonUtils.showShort(text);
            return;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("versionCode", _versionCode);

        String checkurl = HttpUtils.funnyBaseUrl + "appVer/getLastAppVersion";

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        RentApi.post(_context, checkurl, requestBody, new ICallBack() {
            @Override
            public void result(String str) {
                try {
                    Log.e("update---", _versionCode + "\nstr:" + str);
                    //{"success":"1", "apkid":"1", "apkurl":"http://dl.duantian.com/apk/update/com.moying.crazyreduce_105.apk", "versionname":"1.0.5", "isself":"true", "googleurl":"", "descripton":"优化代码，提升性能"}
                    String urlApk = "http:\\/\\/dl.duantian.com\\/apk\\/update\\/com.moying.crazyreduce_105.apk";
                    String sss = "{\n" +
                            "\"code\": 0,\n" +
                            "\"data\": {\n" +
                            "\"isUpgrade\": 1,\n" +
                            "\"isForceUpgrade\": 1,\n" +
                            "\"versionCode\": 1001,\n" +
                            "\"versionName\": \"1.0.5\",\n" +
                            "\"upgradeDescription\": \"upgradeDescription\",\n" +
                            "\"apkUrl\": \"" + urlApk + "\",\n" +
                            "\"createTime\": \"createTime\",\n" +
                            "\"downloadurl\": \"\"\n" +
                            "},\n" +
                            "\"msg\": \"\"\n" +
                            "}";
                    UpdateInfoBean updateAppBean = new Gson().fromJson(str, UpdateInfoBean.class);
                    if (updateAppBean.getCode() == 0) {
                        upInfoBean = updateAppBean.getData();
                        if (upInfoBean.getIsUpgrade() != 1 || _versionCode >= upInfoBean.getVersionCode()) {
                            if (_from == 2) {
                                //这个是设置页的 toast 
                                String text = _context.getResources().getString(R.string.cy_plugin_update_message);
                                CommonUtils.showShort(text);
                            }
                            return;
                        }
                        versionnameData = upInfoBean.getVersionName();
                        upInfoBean.setRIcon(_Ricon);

                        upInfoBean.setDownloadurl(upInfoBean.getApkUrl());
                        if (!TextUtils.isEmpty(upInfoBean.getApkUrl())) { // 有下载路径
                            if (!DownloadNewService.canceled) {
                                String text = _context.getResources().getString(R.string.cy_plugin_update_noti_loading);
                                CommonUtils.showShort(text);
                                return;
                            }
                            if (mIsHaveListener != null) {
                                mIsHaveListener.isHaveShow(true);
                            }
                            //这边修改一下，目前全部是弹框提示一下
                            JudgeFileSize(2);
                            updaMustApk(_context);
                            if (_from == 1 && !TextUtils.isEmpty(_dialogDate)) {
                                com.blankj.utilcode.util.SPUtils.getInstance("config_info").put("update_main_date", _dialogDate);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new ICallBack() {
            @Override
            public void result(String str) {

            }
        });
    }

    //下载apk
    public void installAPPAD(String url) {
        upInfoBean.setApkUrl(url);
        upInfoBean.setDownloadurl(url);
        _pkName = url.substring(url.lastIndexOf("apk/") + 4, url.length() - 4);
        upInfoBean.setVersionName(_pkName);
        versionnameData = _pkName;
        String urlData = UrlUtils.UpdateAddressUrl(_context, _pkName);
        File file = new File(urlData);
        if (!file.exists()) {
            downloadApp(1);
        } else {
            JudgeFileSize(3);
        }

    }

    private void JudgeFileSize(final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL downloadUrl = new URL(upInfoBean.getDownloadurl());
                    HttpURLConnection conn = (HttpURLConnection) downloadUrl.openConnection();
                    conn.connect();
                    int contentLength = conn.getContentLength();
                    conn.disconnect();
                    Message message = new Message();
                    message.what = type;
                    message.obj = contentLength;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendHandler(final int type, final int pro) {
        Message message = new Message();
        message.what = type;
        message.obj = pro;
        handler.sendMessage(message);
    }

    private void downloadApp(int opType) {
        Intent intent = new Intent(_context, DownloadNewService.class);
        intent.putExtra("upinfo", upInfoBean);
        intent.putExtra("_pkName", _pkName);
        intent.putExtra("opType", opType);
        if (Build.VERSION.SDK_INT >= 26) {
            _context.startForegroundService(intent);
        } else {
            _context.startService(intent);
        }
    }

    /**
     * 强制安装apk弹框loading显示
     */
    private void startDownload() {
        sendHandler(4, 0);
        RentApi.download(_context, upInfoBean.getDownloadurl(), UrlUtils.UpdateAddressUrl(_context, versionnameData), new DownloadListener() {
            @Override
            public void onStart() {
                sendHandler(4, 0);
            }

            @Override
            public void onProgress(int pro) {
                sendHandler(4, pro);
            }

            @Override
            public void onFinish(String path) {
                sendHandler(5, 100);
            }

            @Override
            public void onFail(String errorInfo) {
                CommonUtils.showShort("下载失败，请重试");
            }
        });
    }

    /*
     * 获取网络状态
     * true ：wifi
     * fase ：流量
     */
    public static Boolean getNetWorkSt(Context context) {
        Boolean flag = null;
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        // wifi网络
        if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
            flag = true;
        }
        if (flag == null) {
            try {
                State mobile = manager.getNetworkInfo(
                        ConnectivityManager.TYPE_MOBILE).getState();
                // 手机流量网络
                if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
                    flag = false;
                }
            } catch (Exception e) {
            }
        }
        return flag;
    }

    /**
     * 安装apk
     */
    public static void installApk(final Activity mContext, int opType) {
        if (opType == 0) {
            final UpdateWifiPagDialog dialog = new UpdateWifiPagDialog(mContext);
            dialog.setUpInfoBean(upInfoBean);
            dialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    startInstallationAct(mContext);
                }
            });
            dialog.show();
        } else {
            startInstallationAct(mContext);
        }
    }

    /**
     * 强制安装apk弹框
     */
    public static UpdateMustUpdataDialog UpdateMustdialog;

    public void updaMustApk(final Activity mContext) {
        UpdateMustdialog = new UpdateMustUpdataDialog(mContext);
        UpdateMustdialog.setUpInfoBean(upInfoBean);
        UpdateMustdialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String urlData = UrlUtils.UpdateAddressUrl(mContext, versionnameData);
                File file = new File(urlData);
                Boolean state = getNetWorkSt(mContext);
                if (!file.exists() && state != null && !state) {
                    final UpdateMustnoticeDialog noticeDialog = new UpdateMustnoticeDialog(mContext);
                    noticeDialog.setUpApkLength(noticecontentLength);
                    noticeDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            noticeDialog.dismiss();
                            startDownload();
                        }
                    });
                    noticeDialog.show();
                } else {
                    JudgeFileSize(3);
                }
            }
        });
        UpdateMustdialog.show();
    }

    private static void startInstallationAct(Context mContext) {
        File apkfile = new File(UrlUtils.UpdateAddressUrl(mContext, versionnameData));
        if (!apkfile.exists() || mContext == null) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//大于等于24
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", apkfile);
            i.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                    "application/vnd.android.package-archive");
        }
        mContext.startActivity(i);
    }
}