package com.sortinghat.funny.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.LogUtils;
import com.sortinghat.common.utils.CommonUtils;

import java.util.ArrayList;

/**
 * @author jingbin
 * @data 2018/7/18
 * @Description 权限处理
 * // 存储权限
 * Manifest.permission.WRITE_EXTERNAL_STORAGE
 * // 相机
 * Manifest.permission.CAMERA
 * // 获得经纬度1
 * Manifest.permission.ACCESS_FINE_LOCATION
 * // 获得经纬度2
 * Manifest.permission.ACCESS_COARSE_LOCATION
 * // 获取手机权限 为了获得uuid：
 * Manifest.permission.READ_PHONE_STATE
 * // 应用列表
 * Manifest.permission.GET_PACKAGE_SIZE
 */

public class PermissionHandler {

    public static final int PERMISSION_CODE = 10010;
    public static final int PERMISSION_CODE_ONE = 10011;

    /**
     * 检查是否有权限，没有请求权限
     */
    public static boolean isHandlePermission(Activity activity, String permission, String permission2) {
        if (isPermission(activity, permission, permission2)) {
            // 有权限
            return true;
        } else {
            int permissionCheck = ContextCompat.checkSelfPermission(activity, permission);
            int permissionCheck2 = ContextCompat.checkSelfPermission(activity, permission2);
            // 没有权限
            ArrayList<String> list = new ArrayList<>();
            // 没有授权
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                list.add(permission);
            }
            if (permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
                list.add(permission2);
            }
            if (list.size() > 0) {
                String[] strings = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    strings[i] = list.get(i);
                }
                ActivityCompat.requestPermissions(activity, strings, PERMISSION_CODE);
            }
            return false;
        }
    }

    /**
     * 检查只有一个权限，现只用于相册权限
     */
    public static boolean isHandlePermission(Activity activity, String permission) {
        if (isPermission(activity, permission)) {
            // 有权限
            return true;
        } else {
            int permissionCheck = ContextCompat.checkSelfPermission(activity, permission);
            // 没有授权
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, PERMISSION_CODE_ONE);
            }
            return false;
        }
    }

    /**
     * 检测是否有单个权限
     */
    private static boolean isPermission(Activity activity, String permission) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, permission);
        // 有一个没有授权就作为 没有权限处理
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 检测是否全部有权限
     */
    private static boolean isPermission(Activity activity, String permission, String permission2) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, permission);
        int permissionCheck2 = ContextCompat.checkSelfPermission(activity, permission2);
        // 有一个没有授权就作为 没有权限处理
        return permissionCheck == PackageManager.PERMISSION_GRANTED && permissionCheck2 == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * vivo不走回调
     */
    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults, OnHandlePermissionListener listener) {
        onRequestPermissionsResult(activity, null, null, requestCode, permissions, grantResults, listener);
    }

    /**
     * 两个权限时的提示
     */
    public static void onRequestPermissionsResult(Activity activity, String toastTextTwo, int requestCode, String[] permissions, int[] grantResults, OnHandlePermissionListener listener) {
        onRequestPermissionsResult(activity, toastTextTwo, null, requestCode, permissions, grantResults, listener);
    }

    /**
     * @param toastTextTwo 两个权限时的提示
     * @param toastTextOne 一个权限时的提示
     */
    public static void onRequestPermissionsResult(Activity activity, String toastTextTwo, String toastTextOne, int requestCode, String[] permissions, int[] grantResults, OnHandlePermissionListener listener) {
        if (grantResults != null) {
            for (int i : grantResults) {
                LogUtils.i("-----grantResults:" + i);
            }
            for (String p : permissions) {
                LogUtils.i("-----permissions:" + p);
            }

            switch (requestCode) {
                case PERMISSION_CODE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (listener != null) {
                            listener.granted();
                        }
                    } else {
                        if (!TextUtils.isEmpty(toastTextTwo)) {
                            CommonUtils.showShort(toastTextTwo);
                        }

                        //判断权限是否被拒绝且不再询问
                        if (permissions.length > 0) {
                            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) {
                                if (listener != null) {
                                    listener.deniedAndAskNoMore();
                                }
                            } else {
                                if (listener != null) {
                                    listener.denied();
                                }
                            }
                        }
                    }
                    break;
                case PERMISSION_CODE_ONE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (listener != null) {
                            listener.granted();
                        }
                    } else {
                        if (!TextUtils.isEmpty(toastTextOne)) {
                            CommonUtils.showShort(toastTextOne);
                        }

                        //判断权限是否被拒绝且不再询问
                        if (permissions.length > 0) {
                            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) {
                                if (listener != null) {
                                    listener.deniedAndAskNoMore();
                                }
                            } else {
                                if (listener != null) {
                                    listener.denied();
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public interface OnHandlePermissionListener {
        /**
         * 同意
         */
        void granted();

        void denied();

        void deniedAndAskNoMore();
    }
}
