package com.sortinghat.funny.ui.my;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.TaskRankHeaderBean;
import com.sortinghat.funny.databinding.ItemTaskRankHeaderBinding;
import com.sortinghat.funny.util.ConstantUtil;

public class TaskRankHeaderView extends RelativeLayout {

    private ItemTaskRankHeaderBinding mBinding;
    private Context mContext;

    public TaskRankHeaderView(Context context) {
        super(context);
        init(context);
    }

    public TaskRankHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TaskRankHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context mContext) {
        this.mContext = mContext;
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_task_rank_header, null);
        mBinding = DataBindingUtil.bind(rootView);
        addView(rootView);
        setTopCoverImage();

        setListener();

    }

    public void initData(TaskRankHeaderBean headerBean) {
        if (headerBean != null) {
            if (headerBean.getMyRank() != null) {
                mBinding.taskGoldNum.setText(headerBean.getMyRank().getStarNoteCount() + "");
                String name = headerBean.getMyRank().getNickname();
                if (!TextUtils.isEmpty(name) && name.length() > 4) {
                    name = name.substring(0, 4) + "...";
                }
                mBinding.taskItemName.setText(name);
                mBinding.taskItemRank.setText(headerBean.getMyRank().getRank() + "");

                if (!TextUtils.isEmpty(headerBean.getMyRank().getAvatar())) {
                    GlideUtils.loadCircleImage(headerBean.getMyRank().getAvatar(), R.mipmap.user_icon_default, mBinding.ivUserIcon);
                }
                if (!TextUtils.isEmpty(headerBean.getMyRank().getPendantUrl())) {
                    ConstantUtil.glideLoadPendantUrl(headerBean.getMyRank().getPendantUrl(), mBinding.ivBoxUserIcon);
                }

            }
            if (headerBean.getFirstRank() != null) {
                mBinding.taskItemNameTop.setText(headerBean.getFirstRank().getNickname() + "");
                if (!TextUtils.isEmpty(headerBean.getFirstRank().getAvatar())) {
                    GlideUtils.loadCircleImage(headerBean.getFirstRank().getAvatar(), R.mipmap.user_icon_default, mBinding.ivUserIconTop);
                }
                if (!TextUtils.isEmpty(headerBean.getFirstRank().getPendantUrl())) {
                    ConstantUtil.glideLoadPendantUrl(headerBean.getFirstRank().getPendantUrl(), mBinding.ivBoxUserIconTop);
                }
            }


        }

    }

    public void setListener() {
        mBinding.taskItemCentralBt.setOnClickListener(quickClickListener);
    }

    private void setTopCoverImage() {
//        LayoutParams layoutParams = (LayoutParams) mBinding.ivTopBg.getLayoutParams();
//        layoutParams.width = ScreenUtils.getScreenWidth();
//        layoutParams.height = ScreenUtils.getScreenWidth() * 450 / 1125;
//        mBinding.ivTopBg.setLayoutParams(layoutParams);

    }


    public void setListener(QuickClickListener quickClickListener) {
        if (quickClickListener != null) {
            this.quickClickListener = quickClickListener;
        }
    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.task_item_central_bt:
                    ConstantUtil.createUmEvent("task_top_click_money_btn_top");//任务-排行榜-上面攒星币按钮
                    TaskCentralActivity.starActivity(mContext, "0");
                    break;
            }
        }
    };

}
