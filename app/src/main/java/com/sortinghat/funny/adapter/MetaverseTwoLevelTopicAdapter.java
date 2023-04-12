package com.sortinghat.funny.adapter;

import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.TopicBean;
import com.sortinghat.funny.bean.event.TopicListEvent;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ItemMetaverseTwoLevelTopicBinding;
import com.sortinghat.funny.ui.my.LoginActivity;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.viewmodel.TopicViewModel;

/**
 * Created by wzy on 2021/6/28
 */
public class MetaverseTwoLevelTopicAdapter extends BaseBindingAdapter<TopicBean.SubTopicBean, ItemMetaverseTwoLevelTopicBinding> {

    private final LifecycleOwner lifecycleOwner;

    private final TopicViewModel viewModel;

    public MetaverseTwoLevelTopicAdapter(LifecycleOwner lifecycleOwner, TopicViewModel viewModel) {
        super(R.layout.item_metaverse_two_level_topic);
        this.lifecycleOwner = lifecycleOwner;
        this.viewModel = viewModel;
    }

    @Override
    protected void bindView(BaseBindingHolder holder, TopicBean.SubTopicBean subTopicBean, ItemMetaverseTwoLevelTopicBinding binding, int position) {
        if (subTopicBean != null) {
            setListener(subTopicBean, binding);
            setData(subTopicBean, binding);
        }
    }

    private void setListener(TopicBean.SubTopicBean subTopicBean, ItemMetaverseTwoLevelTopicBinding binding) {
        binding.tvLikeTopic.setOnClickListener(new QuickClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                    LoginActivity.starActivity(0);
                    return;
                }

                binding.tvLikeTopic.setEnabled(false);

                int isLike = subTopicBean.getLikeStatus();
                if (isLike == 1) {
                    isLike = 0;
                    setLikeStatus(isLike, binding);
                    likeTopic(2, isLike, subTopicBean, binding);
                } else {
                    isLike = 1;
                    setLikeStatus(isLike, binding);
                    likeTopic(1, isLike, subTopicBean, binding);
                }
            }
        });
    }

    private void setData(TopicBean.SubTopicBean subTopicBean, ItemMetaverseTwoLevelTopicBinding binding) {
        if (!TextUtils.isEmpty(subTopicBean.getThumb())) {
            GlideUtils.loadRoundImage(subTopicBean.getThumb(), R.mipmap.default_topic_top_post_cover_image, 5, binding.ivTopicTopPostCoverImage);
        } else if (!TextUtils.isEmpty(subTopicBean.getUrl())) {
            GlideUtils.loadRoundImage(subTopicBean.getUrl(), R.mipmap.default_topic_top_post_cover_image, 5, binding.ivTopicTopPostCoverImage);
        } else {
            binding.ivTopicTopPostCoverImage.setImageResource(R.mipmap.default_topic_top_post_cover_image);
        }
        binding.tvTwoLevelTopicName.setText(subTopicBean.getName());
        setLikeStatus(subTopicBean.getLikeStatus(), binding);
    }

    /**
     * @param operateType 1喜欢 2取消喜欢
     */
    private void likeTopic(int operateType, int isLike, TopicBean.SubTopicBean subTopicBean, ItemMetaverseTwoLevelTopicBinding binding) {
        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);
        jsonObject.addProperty("type", operateType);
        jsonObject.addProperty("topicIds", subTopicBean.getId());
        jsonObject.addProperty("topicNames", subTopicBean.getName());

        viewModel.likeOrShieldTopic(jsonObject.toString()).observe(lifecycleOwner, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    if (operateType == 1) {
                        CommonUtils.showShort("已喜欢");
                    } else {
                        CommonUtils.showShort("已取消喜欢");
                    }
                    subTopicBean.setLikeStatus(isLike);
                    RxBus.getDefault().post(RxCodeConstant.UPDATE_LIKE_OR_SHIELD_TOPIC_LIST, new TopicListEvent("AllTopicListFragment", String.valueOf(subTopicBean.getId()), subTopicBean.getName(), operateType));
                } else {
                    LogUtils.e(resultBean.getMsg());
                    setLikeStatus(subTopicBean.getLikeStatus(), binding);
                }
            } else {
                setLikeStatus(subTopicBean.getLikeStatus(), binding);
            }
            binding.tvLikeTopic.setEnabled(true);
        });
    }

    private void setLikeStatus(int isLike, ItemMetaverseTwoLevelTopicBinding binding) {
        if (isLike == 1) {
            binding.tvLikeTopic.setText("已喜欢");
            binding.tvLikeTopic.setTextColor(CommonUtils.getColor(R.color.color_666666));
            binding.tvLikeTopic.setBackground(CommonUtils.getDrawable(R.drawable.shape_white_bg_gray_border));
        } else {
            binding.tvLikeTopic.setText("喜欢");
            binding.tvLikeTopic.setTextColor(CommonUtils.getColor(R.color.light_orange));
            binding.tvLikeTopic.setBackground(CommonUtils.getDrawable(R.drawable.shape_white_bg_orange_border));
        }
    }
}
