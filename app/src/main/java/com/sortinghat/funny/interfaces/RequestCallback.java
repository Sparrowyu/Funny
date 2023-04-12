package com.sortinghat.funny.interfaces;

/**
 * Created by wzy on 2021/8/26
 */
public interface RequestCallback<T> {

    void updateProgress(int progress);

    void onSuccess(T t);

    void onFailure();
}
