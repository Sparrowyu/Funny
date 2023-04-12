package com.sortinghat.funny.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.sortinghat.funny.BR;

public class BaseResultBean<T> extends BaseObservable {

    private int code;
    private String msg;
    private String redirect;
    private T data;

    @Bindable
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
        notifyPropertyChanged(BR.code);
    }

    @Bindable
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
        notifyPropertyChanged(BR.msg);
    }

    @Bindable
    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
        notifyPropertyChanged(BR.redirect);
    }

    @Bindable
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
        notifyPropertyChanged(BR.data);
    }
}