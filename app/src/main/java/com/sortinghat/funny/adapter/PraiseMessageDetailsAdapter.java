package com.sortinghat.funny.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.TimeUtils;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.PraiseMessageBean;
import com.sortinghat.funny.databinding.ItemMessagePraiseDetailsBinding;
import com.sortinghat.funny.ui.my.MyOtherUserInfoActivity;
import com.sortinghat.funny.util.ConstantUtil;

/**
 * Created by wzy on 2021/6/28
 */
public class PraiseMessageDetailsAdapter extends BaseBindingAdapter<PraiseMessageBean, ItemMessagePraiseDetailsBinding> {

    public PraiseMessageDetailsAdapter() {
        super(R.layout.item_message_praise_details);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindView(BaseBindingHolder holder, PraiseMessageBean praiseMessageBean, ItemMessagePraiseDetailsBinding binding, int position) {
        if (praiseMessageBean != null && praiseMessageBean.getContent() != null) {
            PraiseMessageBean.ContentBean contentBean = praiseMessageBean.getContent();
            setListener(contentBean, binding);
            setData(praiseMessageBean, binding, contentBean);
        }
    }

    private void setListener(PraiseMessageBean.ContentBean contentBean, ItemMessagePraiseDetailsBinding binding) {
        binding.rlUserIcon.setOnClickListener(new MyQuickClickListener(contentBean));
        binding.tvPraisePeopleNickName.setOnClickListener(new MyQuickClickListener(contentBean));
    }

    private void setData(PraiseMessageBean praiseMessageBean, ItemMessagePraiseDetailsBinding binding, PraiseMessageBean.ContentBean contentBean) {
        GlideUtils.loadImageNoPlaceholder(contentBean.getThumb(), binding.ivPraiseWorksCoverImage);
        binding.tvPraisePeopleNickName.setText(contentBean.getNickName());
        binding.tvPraiseYourWorks.setText(contentBean.getTips());
        binding.tvPraiseTime.setText(TimeUtils.millis2String(praiseMessageBean.getCrateAt(), "yyyy-MM-dd"));
        GlideUtils.loadImage(contentBean.getAvatar(), R.mipmap.user_icon_default, binding.ivUserIcon);
        ConstantUtil.glideLoadPendantUrl(contentBean.getPendantUrl(), binding.ivBoxUserIcon);
    }

    private class MyQuickClickListener extends QuickClickListener {

        private PraiseMessageBean.ContentBean contentBean;

        public MyQuickClickListener(PraiseMessageBean.ContentBean contentBean) {
            this.contentBean = contentBean;
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.rl_user_icon:
                case R.id.tv_praise_people_nick_name:
                    MyOtherUserInfoActivity.starActivity(v.getContext(), contentBean.getOtherUserId(), contentBean.getPostId());
                    break;
                default:
                    break;
            }
        }
    }
}
