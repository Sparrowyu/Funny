package com.sortinghat.common.base;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.JsonSyntaxException;
import com.sortinghat.common.utils.CommonUtils;
import com.umeng.analytics.MobclickAgent;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author jingbin
 * @data 2018/5/28
 */
public class BaseViewModel extends AndroidViewModel {

    private CompositeDisposable mCompositeDisposable;

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    protected <T> void execute(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    protected <T> Disposable execute(Observable<T> observable, Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    protected <T> void throttleFirstExecute(Observable<T> observable, Observer<T> observer) {
        observable.throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    protected <T> Disposable throttleFirstExecute(Observable<T> observable, Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        return observable.throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    protected void disposeServerException(Throwable throwable) {
        if (throwable instanceof UnknownHostException) {
            CommonUtils.showShort("请确认您的手机网络是否可用");
        } else if (throwable instanceof ConnectException) {
            CommonUtils.showShort("网络连接失败，请检查您的网络设置");
        } else if (throwable instanceof SocketTimeoutException) {
            CommonUtils.showShort("加载超时，请重试");
        } else if (throwable instanceof JsonSyntaxException) {
            CommonUtils.showShort("json数据格式异常");
        } else {
            CommonUtils.showShort("服务器异常");
        }
        String throwableString = Log.getStackTraceString(throwable);
        if (!TextUtils.isEmpty(throwableString)) {
            MobclickAgent.reportError(RootApplication.getContext(), throwableString);
        }
        LogUtils.e(throwableString);
    }

    protected void printServerExceptionLog(Throwable throwable) {
        LogUtils.e(Log.getStackTraceString(throwable));
    }

    protected void addDisposable(Disposable disposable) {
        if (this.mCompositeDisposable == null) {
            this.mCompositeDisposable = new CompositeDisposable();
        }
        this.mCompositeDisposable.add(disposable);
    }

    public void removeDisposable(Disposable disposable) {
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.remove(disposable);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (this.mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            this.mCompositeDisposable.clear();
        }
    }
}