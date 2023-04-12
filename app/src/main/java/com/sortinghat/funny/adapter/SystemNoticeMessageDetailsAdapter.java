package com.sortinghat.funny.adapter;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.TimeUtils;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.event.SystemNoticeBean;
import com.sortinghat.funny.databinding.ItemMessageSystemNoticeDetailsBinding;

/**
 * Created by wzy on 2021/6/28
 */
public class SystemNoticeMessageDetailsAdapter extends BaseBindingAdapter<SystemNoticeBean, ItemMessageSystemNoticeDetailsBinding> {

    public SystemNoticeMessageDetailsAdapter() {
        super(R.layout.item_message_system_notice_details);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindView(BaseBindingHolder holder, SystemNoticeBean systemNoticeBean, ItemMessageSystemNoticeDetailsBinding binding, int position) {
        if (systemNoticeBean != null) {
            setData(systemNoticeBean, binding, systemNoticeBean.getContent());
        }
    }

    private void setData(SystemNoticeBean systemNoticeBean, ItemMessageSystemNoticeDetailsBinding binding, SystemNoticeBean.ContentBean contentBean) {
        binding.tvAuditNoticeTitle.setText(contentBean.getTitle());
        binding.tvAuditNoticeContent.setText(contentBean.getContent());
        binding.tvAuditNoticeCreateDate.setText(TimeUtils.millis2String(systemNoticeBean.getCrateAt(), "yyyy-MM-dd"));
    }
}
