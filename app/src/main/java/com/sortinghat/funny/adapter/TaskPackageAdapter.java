package com.sortinghat.funny.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.TaskPackageItemBean;
import com.sortinghat.funny.databinding.ItemTaskPackageBinding;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wzy on 2021/6/28
 */
public class TaskPackageAdapter extends BaseBindingAdapter<TaskPackageItemBean, ItemTaskPackageBinding> {
    private Context mContext;
    //用来记录所有checkbox的状态
    private Map<Integer, Boolean> hashMap = new HashMap<>();
    private int hadDressIndex = -1;

    public TaskPackageAdapter(Context mContext, int listSize) {
        super(R.layout.item_task_package);
        this.mContext = mContext;
        initMap(listSize);
    }

    /**
     * 初始化map集合,将所有checkbox默认设置为false
     */
    private void initMap(int listSize) {
        if (listSize > 0) {
            for (int i = 0; i < listSize; i++) {
                hashMap.put(i, false);
            }
        }
    }

    @Override
    protected void bindView(BaseBindingHolder holder, TaskPackageItemBean info, ItemTaskPackageBinding binding, int position) {

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.itemRl.getLayoutParams();
        int imageSize = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(16)) / 3;
        params.width = imageSize;
        params.height = (int) ((float) imageSize * 1.7);
        binding.itemRl.setLayoutParams(params);

        if (info != null) {

            binding.tvName.setText(info.getProductName());
            binding.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onItemClickListener)
                        onItemClickListener.onClick(position, hadDressIndex,0, pagStatus(info.getStatus()));
                }
            });
            binding.tvBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onItemClickListener)
                        onItemClickListener.onClick(position, hadDressIndex,1, pagStatus(info.getStatus()));
                }
            });
            if (info.isClicked()) {
                binding.deleteRl.setVisibility(View.VISIBLE);
            } else {
                binding.deleteRl.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(info.getExpireTime())) {
                binding.tvTime.setText(info.getExpireTime());
            }
            if (!TextUtils.isEmpty(info.getProductUrl())) {
                GlideUtils.loadImageNoPlaceholder(info.getProductUrl(), binding.ivBoxUserIcon);
            }
            switch (info.getStatus()) {
                case 0:
                    binding.tvBt.setBackgroundResource(R.drawable.click_can_orange_corner50_bt_bg);
                    binding.tvBt.setEnabled(true);
                    binding.tvBt.setText("装备");
                    binding.itemRl.setBackgroundColor(CommonUtils.getColor(R.color.color_FFF2DE));
                    break;
                case 1:
                    binding.tvBt.setBackgroundResource(R.drawable.click_can_orange_corner50_bt_bg);
                    binding.tvBt.setEnabled(true);
                    binding.tvBt.setText("卸下");
                    binding.itemRl.setBackgroundColor(CommonUtils.getColor(R.color.color_FFF2DE));
                    hadDressIndex = position;
                    break;
                case 2:
                    binding.tvBt.setBackgroundResource(R.drawable.click_d7d7d7_corner50_bt_bg);
                    binding.tvBt.setText("已过期");
                    binding.tvBt.setEnabled(false);
                    binding.itemRl.setBackgroundColor(CommonUtils.getColor(R.color.color_F5F4F3));
                    break;
            }
        }
    }

    //穿戴或者卸下装扮，1:穿戴 0:卸下,示例值(1)
    private int pagStatus(int status) {
        if (status == 0) {
            return 1;
        }
        return 0;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // clickType 0 :删除 1装备
    public interface OnItemClickListener {
        void onClick(int position,int hadDressIndex, int clickType, int status);
    }
}
