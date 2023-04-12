package com.sortinghat.funny.util.business;

import android.widget.TextView;

import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.funny.R;

/**
 * Created by wzy on 2021/9/24
 */
public class BusinessUtils {

    public static void setFollowStatus(int followStatus, TextView textView) {
        int drawableResource = 0, textColorResource = 0;
        String followStatusText = "";
        switch (followStatus) {
            case 0:
                drawableResource = R.drawable.shape_orange_bg_with_corner;
                textColorResource = R.color.white;
                followStatusText = "关注";
                break;
            case 1:
                drawableResource = R.drawable.shape_white_bg_gray_border;
                textColorResource = R.color.color_333333;
                followStatusText = "互相关注";
                break;
            case 2:
                drawableResource = R.drawable.shape_white_bg_gray_border;
                textColorResource = R.color.color_333333;
                followStatusText = "已关注";
                break;
            case 3:
                drawableResource = R.drawable.shape_orange_bg_with_corner;
                textColorResource = R.color.white;
                followStatusText = "回关";
                break;
            default:
                break;
        }
        textView.setBackgroundResource(drawableResource);
        textView.setTextColor(CommonUtils.getColor(textColorResource));
        textView.setText(followStatusText);
    }
}
