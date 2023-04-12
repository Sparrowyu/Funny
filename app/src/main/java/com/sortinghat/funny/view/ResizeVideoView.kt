package com.sortinghat.funny.view

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

/**
 * Created by  on 2020/10/23.
 */
class ResizeVideoView : VideoView {
    private var mVideoViewWidth: Int = 0
    private var mVideoViewHeight: Int = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    //代码的重点在此，主要是根据外部传入的高度和宽度进行resize，这里默认处理的前提是要明确指定mode为EXACTLY，即在使用该空间的父viewgroup中明确指定宽高或者指定为match_parent。当然可以扩展支持wrap_content等特性。
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = if (mVideoViewWidth == 0) MeasureSpec.getSize(widthMeasureSpec) else mVideoViewWidth
        val heightSize = if (mVideoViewHeight == 0) MeasureSpec.getSize(heightMeasureSpec) else mVideoViewHeight
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY
                && MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, heightSize)
        }
    }


    fun resizeVideoView(width: Int, height: Int) {
        mVideoViewWidth = width
        mVideoViewHeight = height
        invalidate()
    }

}