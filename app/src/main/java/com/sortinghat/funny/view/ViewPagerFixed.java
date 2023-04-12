package com.sortinghat.funny.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.sortinghat.common.base.RootApplication;
import com.umeng.analytics.MobclickAgent;

//修复java.lang.IllegalArgumentException: pointerIndex out of range
public class ViewPagerFixed extends ViewPager {
    public ViewPagerFixed(@NonNull Context context) {
        super(context);
    }

    public ViewPagerFixed(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (!TextUtils.isEmpty(ex.getMessage())) {
                MobclickAgent.reportError(RootApplication.getContext(), "ViewPagerFixed:" + ex.getMessage());
                //MyImageView  -> onDraw() Canvas: trying to use a recycled bitmap
            }
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
