package com.sortinghat.funny.util;

import com.sortinghat.funny.interfaces.HomeRemoveImgPostIdListener;
import com.sortinghat.funny.interfaces.HomeRemoveVideoPostIdListener;

public class ListenerUtils {
    private static ListenerUtils mListenerUtils;

    public static ListenerUtils getInstance() {
        if (mListenerUtils == null) {
            mListenerUtils = new ListenerUtils();
        }
        return mListenerUtils;
    }

    private HomeRemoveVideoPostIdListener homeRemoveVideoPostIdListener;
    private HomeRemoveImgPostIdListener homeRemoveImgPostIdListener;


    public HomeRemoveVideoPostIdListener getHomeRemoveVideoPostIdListener() {
        return homeRemoveVideoPostIdListener;
    }

    public void setHomeRemoveVideoPostIdListener(HomeRemoveVideoPostIdListener homeRemoveVideoPostIdListener) {
        this.homeRemoveVideoPostIdListener = homeRemoveVideoPostIdListener;
    }

    public HomeRemoveImgPostIdListener getHomeRemoveImgPostIdListener() {
        return homeRemoveImgPostIdListener;
    }

    public void setHomeRemoveImgPostIdListener(HomeRemoveImgPostIdListener homeRemoveImgPostIdListener) {
        this.homeRemoveImgPostIdListener = homeRemoveImgPostIdListener;
    }
}
