package com.sortinghat.funny.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.TopicHomeChoseDialogBean;
import com.sortinghat.funny.databinding.ItemHomeChoseObjectDialogBinding;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wzy on 2021/6/28
 */
public class HomeDialogChoseObjectAdapter extends BaseBindingAdapter<TopicHomeChoseDialogBean, ItemHomeChoseObjectDialogBinding> {
    private Context mContext;
    //用来记录所有checkbox的状态
    private Map<Integer, Boolean> hashMap = new HashMap<>();

    public HomeDialogChoseObjectAdapter(Context mContext, int listSize) {
        super(R.layout.item_home_chose_object_dialog);
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
    protected void bindView(BaseBindingHolder holder, TopicHomeChoseDialogBean info, ItemHomeChoseObjectDialogBinding binding, int position) {

        switch (position % 9) {
            case 0:
                binding.itemRl.setBackgroundColor(CommonUtils.getColor(R.color.object_dialog_bg0));
                break;
            case 1:
                binding.itemRl.setBackgroundColor(CommonUtils.getColor(R.color.object_dialog_bg1));
                break;
            case 2:
                binding.itemRl.setBackgroundColor(CommonUtils.getColor(R.color.object_dialog_bg2));
                break;
            case 3:
                binding.itemRl.setBackgroundColor(CommonUtils.getColor(R.color.object_dialog_bg3));
                break;
            case 4:
                binding.itemRl.setBackgroundColor(CommonUtils.getColor(R.color.object_dialog_bg4));
                break;
            case 5:
                binding.itemRl.setBackgroundColor(CommonUtils.getColor(R.color.object_dialog_bg5));
                break;
            case 6:
                binding.itemRl.setBackgroundColor(CommonUtils.getColor(R.color.object_dialog_bg6));
                break;
            case 7:
                binding.itemRl.setBackgroundColor(CommonUtils.getColor(R.color.object_dialog_bg7));
                break;
            case 8:
                binding.itemRl.setBackgroundColor(CommonUtils.getColor(R.color.object_dialog_bg8));
                break;
        }


        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.itemRl.getLayoutParams();
        int imageSize = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(112) - SizeUtils.dp2px(8)) / 3;
        params.width = imageSize;
        params.height = imageSize;
        binding.itemRl.setLayoutParams(params);
        binding.tvName.setText(info.getName());

        if (info != null) {

            binding.itemRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!hashMap.get(position)) {
                        hashMap.put(position, true);//每个position为唯一索引,进行存和取
                        binding.checkImg.setImageResource(R.mipmap.object_dialog_img_checked);
                        onItemClickListener.onClick(info.getId(), true);
                    } else {
                        hashMap.put(position, false);//每个position为唯一索引,进行存和取
                        binding.checkImg.setImageResource(R.mipmap.report_check_default);
                        onItemClickListener.onClick(info.getId(), false);
                    }
                }
            });

            if (!hashMap.get(position)) {
                binding.checkImg.setImageResource(R.mipmap.report_check_default);
            } else {
                binding.checkImg.setImageResource(R.mipmap.object_dialog_img_checked);
            }
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(String position, boolean isCheck);
    }
}
