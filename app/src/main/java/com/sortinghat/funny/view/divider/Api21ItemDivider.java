package com.sortinghat.funny.view.divider;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.RecyclerView;

/**
 * <p>The implementation of divider adds dividers around the list.</p>
 * Created by YanZhenjie on 2017/8/14.
 */
public class Api21ItemDivider extends Divider {

    private final int mWidth;
    private final int mHeight;
    private final Drawer mDrawer;

    /**
     * @param color divider line color.
     */
    public Api21ItemDivider(@ColorInt int color) {
        this(color, 4, 4);
    }

    /**
     * @param color  line color.
     * @param width  line width.
     * @param height line height.
     */
    public Api21ItemDivider(@ColorInt int color, int width, int height) {
        this.mWidth = Math.round(width / 2F);
        this.mHeight = Math.round(height / 2F);
        this.mDrawer = new ColorDrawer(color, mWidth, mHeight);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(mWidth, mHeight, mWidth, mHeight);
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        canvas.save();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int childCount = layoutManager.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View view = layoutManager.getChildAt(i);
            mDrawer.drawLeft(view, canvas);
            mDrawer.drawTop(view, canvas);
            mDrawer.drawRight(view, canvas);
            mDrawer.drawBottom(view, canvas);
        }
        canvas.restore();
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    @Override
    public int getWidth() {
        return mWidth;
    }
}