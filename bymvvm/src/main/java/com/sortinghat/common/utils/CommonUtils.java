package com.sortinghat.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ToastUtils;
import com.sortinghat.common.R;
import com.sortinghat.common.base.RootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.Random;

/**
 * Created by jingbin on 2016/11/22
 * 获取原生资源
 */
public class CommonUtils {

    public static Resources getResoure() {
        return RootApplication.getContext().getResources();
    }

    public static String getString(int resid) {
        return getResoure().getString(resid);
    }

    public static String[] getStringArray(int resid) {
        return getResoure().getStringArray(resid);
    }

    public static int getColor(int resid) {
        return getResoure().getColor(resid);
    }

    public static Drawable getDrawable(int resid) {
        return ContextCompat.getDrawable(RootApplication.getContext(), resid);
    }

    public static float getDimens(int resId) {
        return getResoure().getDimension(resId);
    }

    public static int getDimensionPixelSize(int resId) {
        return getResoure().getDimensionPixelSize(resId);
    }

    public static void showShort(CharSequence text) {
        ToastUtils.getDefaultMaker().setBgResource(R.drawable.shape_black_bg_with_angle).setTextColor(getColor(R.color.white)).setGravity(Gravity.CENTER, 0, 0).setDurationIsLong(false).show(text);
    }

    public static void showLong(CharSequence text) {
        ToastUtils.getDefaultMaker().setBgResource(R.drawable.shape_black_bg_with_angle).setTextColor(getColor(R.color.white)).setGravity(Gravity.CENTER, 0, 0).setDurationIsLong(true).show(text);
    }

    public static boolean isJsonString(String json) {
        if (TextUtils.isEmpty(json)) {
            return false;
        } else if (json.length() < 2) {
            return false;
        } else {
            return json.startsWith("{") && json.endsWith("}");
        }
    }

    public static String getJsonStringFromFile(String fileName, Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 随机颜色
     */
    public static int randomColor() {
        Random random = new Random();
        int red = random.nextInt(150) + 50;//50-199
        int green = random.nextInt(150) + 50;//50-199
        int blue = random.nextInt(150) + 50;//50-199
        return Color.rgb(red, green, blue);
    }

    public static void removeSelfFromParent(View child) {
        if (child != null) {
            ViewParent parent = child.getParent();
            if (parent instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(child);
            }
        }
    }

    /**
     * 获得当前版本号
     */
    public static int getCurrentVersionCode(Context context) {
        String packageName = context.getPackageName();
        int version = 0;
        try {
            version = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获得当前版本name
     */
    public static String getCurrentVersionName(Context context) {
        String packageName = context.getPackageName();
        String version = "1.0.1";
        try {
            version = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获得友盟Channel
     */
    public static String getUmChannel(Context mContext) {
        String channel = "";
        try {
            ApplicationInfo appInfo = mContext.getPackageManager()
                    .getApplicationInfo(mContext.getPackageName(),
                            PackageManager.GET_META_DATA);
            channel = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel;
    }

    public static String userAgent = "";

    public static String getUserAgent() {
        try {
            if (TextUtils.isEmpty(userAgent)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    userAgent = WebSettings.getDefaultUserAgent(RootApplication.getContext());
                } else {
                    userAgent = new WebView(RootApplication.getContext()).getSettings().getUserAgentString();
                }
                if (userAgent == null)
                    userAgent = "";
                return userAgent;
            } else {
                return userAgent;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getMD5ForString(String content) {
        StringBuilder md5Buffer = new StringBuilder();
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] tempBytes = digest.digest(content.getBytes());
            int digital;
            for (int i = 0; i < tempBytes.length; i++) {
                digital = tempBytes[i];
                if (digital < 0) {
                    digital += 256;
                }
                if (digital < 16) {
                    md5Buffer.append("0");
                }
                md5Buffer.append(Integer.toHexString(digital));
            }
        } catch (Exception ignored) {
            return Integer.toString(content.hashCode());
        }
        return md5Buffer.toString();
    }

    //12:e3:7d:11:48:f8,去掉"："，换成大写Md5
    public static String getMacMd5(Context context, String mac) {
        if (TextUtils.isEmpty(mac)) {
            return "";
        }
        String macS = "";
        macS = mac.replaceAll(":", "");
        macS = macS.toUpperCase();
        return getMD5ForString(macS);
    }//e3f42a92d32423b273f549191ed166d4,49a8127d9d072ef44b4bb1542d5fda11

    //判断手机是否root
    public static boolean isRoot() {
        try {
            String binPath = "/system/bin/su";
            String xBinPath = "/system/xbin/su";
            if (new File(binPath).exists() && isCanExecute(binPath)) {
                return true;
            }
            if (new File(xBinPath).exists() && isCanExecute(xBinPath)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isCanExecute(String filePath) {
        java.lang.Process process = null;
        try {
            process = Runtime.getRuntime().exec("ls -l " + filePath);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str = in.readLine();
            if (str != null && str.length() >= 4) {
                char flag = str.charAt(3);
                if (flag == 's' || flag == 'x')
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    public static boolean isActivityDestroyed(Activity activity) {
        return activity == null || activity.isFinishing() ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 &&
                        activity.isDestroyed());
    }

    public static boolean isActivityDestroyed(Context context) {
        return (context instanceof Activity && isActivityDestroyed((Activity) context));
    }


    public static String OAID = "00000000-0000-0000-0000-000000000000";
}