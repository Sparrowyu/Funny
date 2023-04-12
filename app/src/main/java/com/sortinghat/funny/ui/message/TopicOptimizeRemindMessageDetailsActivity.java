package com.sortinghat.funny.ui.message;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.base.NoViewModel;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.SystemNoticeMessageDetailsAdapter;
import com.sortinghat.funny.databinding.ActivityMessageDetailsBinding;

/**
 * Created by wzy on 2021/7/19
 */
public class TopicOptimizeRemindMessageDetailsActivity extends BaseActivity<NoViewModel, ActivityMessageDetailsBinding> {

    private SystemNoticeMessageDetailsAdapter systemNoticeMessageDetailsAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_details;
    }

    @Override
    protected void initViews() {
        initTitleBar("话题优化提醒");
//        initAdapter();
    }

    private void initAdapter() {
        contentLayoutBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        systemNoticeMessageDetailsAdapter = new SystemNoticeMessageDetailsAdapter();
        contentLayoutBinding.recyclerView.setAdapter(systemNoticeMessageDetailsAdapter);
    }

    @Override
    protected void initData() {
    }
}
