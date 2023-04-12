package com.sortinghat.funny.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.TimeUtils;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.FansMessageBean;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ItemMessageFansDetailsBinding;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.business.BusinessUtils;
import com.sortinghat.funny.viewmodel.MessageViewModel;

/**
 * Created by wzy on 2021/6/28
 */
public class FansMessageDetailsAdapter extends BaseBindingAdapter<FansMessageBean, ItemMessageFansDetailsBinding> {

    private final LifecycleOwner lifecycleOwner;
    private final MessageViewModel viewModel;

    public FansMessageDetailsAdapter(LifecycleOwner lifecycleOwner, MessageViewModel viewModel) {
        super(R.layout.item_message_fans_details);
        this.lifecycleOwner = lifecycleOwner;
        this.viewModel = viewModel;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindView(BaseBindingHolder holder, FansMessageBean fansMessageBean, ItemMessageFansDetailsBinding binding, int position) {
        if (fansMessageBean != null && fansMessageBean.getContent() != null) {
            FansMessageBean.ContentBean contentBean = fansMessageBean.getContent();
            setListener(contentBean, binding);
            setData(fansMessageBean, binding, contentBean);
        }
    }

    private void setListener(FansMessageBean.ContentBean contentBean, ItemMessageFansDetailsBinding binding) {
        binding.tvFollowStatus.setOnClickListener(new MyQuickClickListener(contentBean, binding));
    }

    private void setData(FansMessageBean fansMessageBean, ItemMessageFansDetailsBinding binding, FansMessageBean.ContentBean contentBean) {
        GlideUtils.loadImage(contentBean.getAvatar(), R.mipmap.user_icon_default, binding.ivUserIcon);
        binding.tvFansNickName.setText(contentBean.getNickName());
        binding.tvFollowYou.setText(contentBean.getTips());
        binding.tvFollowDate.setText(TimeUtils.millis2String(fansMessageBean.getCrateAt(), "yyyy-MM-dd"));
        BusinessUtils.setFollowStatus(contentBean.getRelation(), binding.tvFollowStatus);
        ConstantUtil.glideLoadPendantUrl(contentBean.getPendantUrl(), binding.ivBoxUserIcon);
    }

    private class MyQuickClickListener extends QuickClickListener {

        private FansMessageBean.ContentBean contentBean;
        private ItemMessageFansDetailsBinding binding;

        public MyQuickClickListener(FansMessageBean.ContentBean contentBean, ItemMessageFansDetailsBinding binding) {
            this.contentBean = contentBean;
            this.binding = binding;
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {
            if (contentBean == null) {
                return;
            }
            if (v.getId() == R.id.tv_follow_status) {
                binding.tvFollowStatus.setEnabled(false);

                int followStatus = contentBean.getRelation();
                if (followStatus == 0) {
                    followStatus = 2;
                } else if (followStatus == 1) {
                    followStatus = 3;
                } else if (followStatus == 2) {
                    followStatus = 0;
                } else if (followStatus == 3) {
                    followStatus = 1;
                }
                BusinessUtils.setFollowStatus(followStatus, binding.tvFollowStatus);

                addFollow(contentBean.getOtherUserId(), followStatus);
            }
        }

        private void addFollow(long authorId, int followStatus) {
            viewModel.getUserFollowList(authorId, (followStatus == 1 || followStatus == 2) ? 1 : 0).observe(lifecycleOwner, resultBean -> {
                if (resultBean != null) {
                    if (resultBean.getCode() == 0) {
                        contentBean.setRelation(followStatus);
                        RxBus.getDefault().post(RxCodeConstant.HOME_VIDEO_UPDATA_AUTHOR_FOLLOW, authorId + "," + followStatus);
                    } else {
                        BusinessUtils.setFollowStatus(contentBean.getRelation(), binding.tvFollowStatus);
                    }
                } else {
                    BusinessUtils.setFollowStatus(contentBean.getRelation(), binding.tvFollowStatus);
                }
                binding.tvFollowStatus.setEnabled(true);
            });
        }
    }
}
