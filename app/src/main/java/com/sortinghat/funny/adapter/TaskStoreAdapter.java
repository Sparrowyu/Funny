package com.sortinghat.funny.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.StoreTaskListBean;
import com.sortinghat.funny.databinding.ItemTaskStoreBinding;

/**
 * Created by wzy on 2021/6/28
 */
public class TaskStoreAdapter extends BaseBindingAdapter<StoreTaskListBean.ProductBean, ItemTaskStoreBinding> {
    private Context mContext;
    //用来记录所有checkbox的状态

    public TaskStoreAdapter(Context mContext, int listSize) {
        super(R.layout.item_task_store);
        this.mContext = mContext;
    }

    @Override
    protected void bindView(BaseBindingHolder holder, StoreTaskListBean.ProductBean info, ItemTaskStoreBinding binding, int position) {

        String urlTest = "http://oss-andriod.gaoxiaoxingqiu.com/postfile/20211213/19715479453a87fedf101df3ee07fca0.mp4?x-oss-process=video/snapshot,t_1,f_png,m_fast,ar_auto";
        if (info != null) {
            GlideUtils.loadImage(info.getProductUrl(), binding.itemImg);
//        binding.itemRl.setBackgroundColor(CommonUtils.getColor(R.color.object_dialog_bg0));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.itemRl.getLayoutParams();
            int imageSize = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(36)) / 2;//ui的是170 * 235（图片是170*170）
            params.width = imageSize;
            params.height = (int) ((float) imageSize + SizeUtils.dp2px(65));
            binding.itemRl.setLayoutParams(params);

            binding.itemImg.getLayoutParams().height = imageSize;
            binding.itemImg.getLayoutParams().width = imageSize;

            if (info.getIsLimitTimeTobuy() == 1) {
                binding.tvTag.setVisibility(View.VISIBLE);
            } else {
                binding.tvTag.setVisibility(View.GONE);
            }
            if (info.getStatus() == 1) {
                binding.tvBt.setEnabled(false);
                binding.tvBt.setBackgroundResource(R.drawable.click_d7d7d7_corner2_bt_bg);
                binding.tvBt.setText("已抢光");
            } else {
                binding.tvBt.setEnabled(true);
                binding.tvBt.setBackgroundResource(R.drawable.click_can_orange_bt_bg);
                binding.tvBt.setText("兑换");
            }

            binding.tvName.setText(info.getProductName());
            binding.tvGold.setText(info.getPrice() + "");

            binding.tvBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onItemClickListener)
                        onItemClickListener.onClick(position, true);

                }
            });
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(int position, boolean isCheck);
    }
}
