package com.sortinghat.funny.view;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.sortinghat.common.base.RootApplication;
import com.umeng.analytics.MobclickAgent;

public class ImageViewFixed extends AppCompatImageView {
    public ImageViewFixed(@NonNull Context context) {
        super(context);
    }

    public ImageViewFixed(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (!TextUtils.isEmpty(ex.getMessage())) {
                MobclickAgent.reportError(RootApplication.getContext(), "ImageViewFixed:" + ex.getMessage());
                //MyImageView  -> onDraw() Canvas: trying to use a recycled bitmap
            }
        }
    }

}
