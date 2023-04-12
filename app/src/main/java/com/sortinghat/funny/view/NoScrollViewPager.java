package com.sortinghat.funny.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * 不可滑动的viewPager
 * Created by wzy on 2019/8/12
 */
public class NoScrollViewPager extends ViewPager {

    private final boolean noScroll = true;

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (noScroll) {
            return false;
        } else {
            return super.onInterceptTouchEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (noScroll) {
            return false;
        } else {
            return super.onTouchEvent(event);
        }
    }
}
