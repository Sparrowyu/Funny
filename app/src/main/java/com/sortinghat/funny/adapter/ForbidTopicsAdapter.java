package com.sortinghat.funny.adapter;

import android.content.Context;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.ForbidTopicsBean;
import com.sortinghat.funny.databinding.ItemForbidTopicsBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzy on 2021/6/28
 */
public class ForbidTopicsAdapter extends BaseBindingAdapter<ForbidTopicsBean, ItemForbidTopicsBinding> {
    private Context mContext;
    List<ForbidTopicsBean> beanList = new ArrayList<>();

    public ForbidTopicsAdapter(Context mContext, List<ForbidTopicsBean> beanList) {
        super(R.layout.item_forbid_topics);
        this.mContext = mContext;
        this.beanList = beanList;
    }

    @Override
    protected void bindView(BaseBindingHolder holder, ForbidTopicsBean infoBean, ItemForbidTopicsBinding binding, int position) {

        if (infoBean != null) {

            binding.checkbox.setText(infoBean.getTopics());

            binding.checkbox.setOnClickListener(view -> {
                if (beanList.get(position).isCheck()) {
                    beanList.get(position).setCheck(false);
                } else {
                    beanList.get(position).setCheck(true);
                }
//                    notifyItemChanged(position);
            });
        }
    }

}
