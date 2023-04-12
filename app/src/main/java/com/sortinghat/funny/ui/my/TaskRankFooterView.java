package com.sortinghat.funny.ui.my;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.databinding.ItemTaskRankFooterBinding;
import com.sortinghat.funny.util.ConstantUtil;

public class TaskRankFooterView extends RelativeLayout {

    private ItemTaskRankFooterBinding mBinding;
    private Context mContext;

    public TaskRankFooterView(Context context) {
        super(context);
        init(context);
    }

    public TaskRankFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TaskRankFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context mContext) {
        this.mContext = mContext;
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_task_rank_footer, null);
        mBinding = DataBindingUtil.bind(rootView);
        addView(rootView);
        initData();
        setListener();
    }

    public void initData() {

    }

    public void setListener() {
        mBinding.taskItemFootBt.setOnClickListener(quickClickListener);
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
                case R.id.task_item_foot_bt:
                    ConstantUtil.createUmEvent("task_top_click_money_btn_bottom");//任务-排行榜-下面攒星币按钮
                    TaskCentralActivity.starActivity(mContext, "0");
                    break;
            }
        }
    };

}
