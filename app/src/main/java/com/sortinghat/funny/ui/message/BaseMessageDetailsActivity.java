package com.sortinghat.funny.ui.message;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.AndroidViewModel;

import com.sortinghat.common.base.BasePullDownRefreshActivity;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.viewmodel.MessageViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzy on 2021/9/26
 */
public class BaseMessageDetailsActivity<VM extends AndroidViewModel, SV extends ViewDataBinding> extends BasePullDownRefreshActivity<VM, SV> {

    protected void getPostInfo(MessageViewModel viewModel, long postId, String messageType) {
        viewModel.getPostInfo(postId).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    HomeVideoImageListBean.ListBean videoInfo = resultBean.getData();
                    if (videoInfo != null && videoInfo.getContent() != null) {
                        List<HomeVideoImageListBean.ListBean> homeVideoBeanList = new ArrayList<>();
                        homeVideoBeanList.add(videoInfo);
                    } else {
                        CommonUtils.showShort("抱歉，帖子已被删除");
                    }
                } else {
                    CommonUtils.showShort("抱歉，出现问题，请退出重试");
                }
            }
        });
    }
}
