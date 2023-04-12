package com.sortinghat.common.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sortinghat.common.R;
import com.sortinghat.common.utils.CommonUtils;

/**
 * Created by wzy on 2020/12/7
 */
public class CommonEmptyView extends LinearLayout {

    private LayoutInflater inflater;

    private RelativeLayout mEmptyView;
    private FrameLayout mFrameImage;
    private ProgressBar mLoadingProgress;
    private ImageView mEmptyImage;
    private ImageView mEmptyCommentImage;
    private TextView mEmptyContent;

    private OnEmptyViewClickListener onClickListener;

    public void setOnClickListener(OnEmptyViewClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnEmptyViewClickListener {
        void onClickCallback();
    }

    public CommonEmptyView(Context context) {
        super(context);
        inflater = LayoutInflater.from(context);
        createView();
    }

    public CommonEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);
        createView();
    }

    public void createView() {
        View rootView = inflater.inflate(R.layout.layout_empty_view, this);
        initViews(rootView);
        setListener();
    }

    private void initViews(View rootView) {
        mEmptyView = rootView.findViewById(R.id.empty_view);
        mFrameImage = rootView.findViewById(R.id.frame_image);
        mLoadingProgress = rootView.findViewById(R.id.loading_progress);
        mEmptyImage = rootView.findViewById(R.id.empty_image);
        mEmptyCommentImage = rootView.findViewById(R.id.empty_comment_image);
        mEmptyContent = rootView.findViewById(R.id.empty_content);
    }

    private void setListener() {
        mEmptyView.setEnabled(false);
        mEmptyView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dealRequestData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (onClickListener != null) {
                            onClickListener.onClickCallback();
                        }
                    }
                }, 1000);
            }
        });
    }

    public void dealRequestData() {
        mEmptyView.setEnabled(false);
        if (mLoadingProgress.getVisibility() == View.VISIBLE) {
            return;
        }
        mLoadingProgress.setVisibility(View.VISIBLE);
        mEmptyImage.setVisibility(View.GONE);
        mEmptyContent.setText(R.string.by_string_loading);
    }

    public void dealRequestDataEmpty() {
        dealRequestDataEmpty(CommonUtils.getString(R.string.by_string_comment_empty));
    }

    public void dealRequestDataEmpty(String emptyContent) {
        mFrameImage.setVisibility(VISIBLE);
        mLoadingProgress.setVisibility(View.GONE);
        mEmptyImage.setVisibility(View.GONE);
        mEmptyCommentImage.setVisibility(View.VISIBLE);
        mEmptyContent.setText(emptyContent);
        mEmptyView.setEnabled(false);
    }

    public void dealRequestDataFail() {
        dealRequestDataFail(CommonUtils.getString(R.string.by_string_try_load));
    }

    public void dealRequestDataFail(String desc) {
        mLoadingProgress.setVisibility(View.GONE);
        mEmptyImage.setImageResource(R.drawable.load_error);
        mEmptyImage.setVisibility(View.VISIBLE);
        mEmptyContent.setText(desc);
        mEmptyView.setEnabled(true);
    }

    public void setContentViewCenter() {
        mEmptyView.setPadding(0, 0, 0, 0);
    }
}
