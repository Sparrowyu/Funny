package com.sortinghat.funny.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.databinding.ItemMyLikeVideoImgBinding;

/**
 * Created by wzy on 2021/7/30
 */
public class TopicRelationPostAdapter extends BaseBindingAdapter<HomeVideoImageListBean.ListBean, ItemMyLikeVideoImgBinding> {

    public TopicRelationPostAdapter() {
        super(R.layout.item_my_like_video_img);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindView(BaseBindingHolder holder, HomeVideoImageListBean.ListBean listBean, ItemMyLikeVideoImgBinding binding, int position) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.ivImage.getLayoutParams();
        int imageSize = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(8)) / 3;
        params.width = imageSize;
        params.height = imageSize;
        binding.ivImage.setLayoutParams(params);

        if (listBean != null) {
            HomeVideoImageListBean.ListBean.ContentBean contentBean = listBean.getContent();
            if (contentBean != null) {
                if (contentBean.getPostType() == 1) {
                    GlideUtils.loadImage(contentBean.getThumb(), binding.ivImage);
                } else if (contentBean.getPostType() == 2) {
                    if (TextUtils.isEmpty(contentBean.getThumb())) {
                        GlideUtils.loadImage(contentBean.getUrl(), binding.ivImage);
                    }else {
                        GlideUtils.loadImage(contentBean.getThumb(), binding.ivImage);
                    }

                }
            }
        }
    }
}
