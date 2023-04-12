package com.sortinghat.common.glide;

import android.content.Context;
import android.widget.AbsListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

public class GlideScrollListener implements AbsListView.OnScrollListener {

    private final Context context;

    public GlideScrollListener(Context context) {
        this.context = context;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        RequestManager requestManager = Glide.with(context);
        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            requestManager.resumeRequests();
        } else {
            requestManager.pauseRequests();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }
}
