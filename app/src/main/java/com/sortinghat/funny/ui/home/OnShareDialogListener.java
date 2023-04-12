package com.sortinghat.funny.ui.home;

public interface OnShareDialogListener {
    void onWechatShare();

    void onWechatCircleShare();

    void onQQShare();

    void onQQZoneShare();

    void onShareReport();

    void onShareDelete();

    default void onShareDownload() {}

}
