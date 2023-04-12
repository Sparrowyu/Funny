package com.sortinghat.funny.adapter;

import android.annotation.SuppressLint;

import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.AuditNoticeMessageBean;
import com.sortinghat.funny.databinding.ItemMessageAuditNoticeDetailsBinding;

/**
 * Created by wzy on 2021/6/28
 */
public class AuditNoticeMessageDetailsAdapter extends BaseBindingAdapter<AuditNoticeMessageBean, ItemMessageAuditNoticeDetailsBinding> {

    public AuditNoticeMessageDetailsAdapter() {
        super(R.layout.item_message_audit_notice_details);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindView(BaseBindingHolder holder, AuditNoticeMessageBean auditNoticeMessageBean, ItemMessageAuditNoticeDetailsBinding binding, int position) {
        if (auditNoticeMessageBean != null) {
            binding.setBean(auditNoticeMessageBean);
        }
    }
}
