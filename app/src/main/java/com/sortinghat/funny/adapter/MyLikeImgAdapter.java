package com.sortinghat.funny.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.databinding.ItemMyLikeVideoImgBinding;
import com.sortinghat.funny.util.CommonUtil;
import com.sortinghat.funny.util.ConstantUtil;

/**
 * Created by wzy on 2021/6/28
 */
public class MyLikeImgAdapter extends BaseBindingAdapter<HomeVideoImageListBean.ListBean, ItemMyLikeVideoImgBinding> {
    private Context mContext;
    private boolean isMyUpload;//我的作品

    public MyLikeImgAdapter(Context mContext, boolean isMyUpload) {
        super(R.layout.item_my_like_video_img);
        this.mContext = mContext;
        this.isMyUpload = isMyUpload;
    }

    @Override
    protected void bindView(BaseBindingHolder holder, HomeVideoImageListBean.ListBean info, ItemMyLikeVideoImgBinding binding, int position) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.ivImage.getLayoutParams();
        int imageSize = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(4)) / 3;
        params.width = imageSize;
        params.height = imageSize;
        binding.ivImage.setLayoutParams(params);
        if (info != null) {
            setListener(binding, info);
            if (info.getContent().getPostType() == 1) {
                GlideUtils.loadImage(info.getContent().getThumb(), binding.ivImage);
            } else {
                if (TextUtils.isEmpty(info.getContent().getThumb())) {
                    String imgUrl = info.getContent().getUrl();
                    if (CommonUtil.urlIsGif(imgUrl)) {
                        GlideUtils.loadImage(imgUrl, binding.ivImage);
                    } else {
                        GlideUtils.loadImage(imgUrl, binding.ivImage);
                    }
                } else {
                    GlideUtils.loadImage(info.getContent().getThumb(), binding.ivImage);
                }
            }
            binding.myPostPlayNum.setVisibility(View.GONE);
            binding.myPostPlayNum.setText("");//只有我的作品页才显示播放量
            if (isMyUpload) {
                binding.myPostPlayNum.setVisibility(View.VISIBLE);
                binding.myPostPlayNum.setText(ConstantUtil.getLikeNumString(info.getContent().getRecCount(), "") + "");
                if (info.getContent().getApplyStatus() == 2) {
                    binding.tvVideoTag.setVisibility(View.VISIBLE);
                    binding.tvVideoTag.setText("审核不通过");
                    binding.myPostPlayNum.setVisibility(View.GONE);
                } else if (info.getContent().getApplyStatus() == 0) {
//                    binding.tvVideoTag.setVisibility(View.VISIBLE);
//                    binding.tvVideoTag.setText("审核中");
                    //改为和审核通过一样，不显示审核中
                    binding.tvVideoTag.setVisibility(View.GONE);
                } else {
                    binding.tvVideoTag.setVisibility(View.GONE);
                }
            }
        }
    }

    private void setListener(ItemMyLikeVideoImgBinding binding, HomeVideoImageListBean.ListBean info) {
        binding.itemView.setTag(info.getContent().getPostId());
//        binding.itemView.setOnClickListener(listener);
    }

    private QuickClickListener listener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
//            PostPreviewActivity.starActivity(mContext);
        }
    };

}
