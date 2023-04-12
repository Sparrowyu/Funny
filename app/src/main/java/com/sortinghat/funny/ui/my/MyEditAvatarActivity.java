package com.sortinghat.funny.ui.my;

import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.funny.R;
import com.sortinghat.funny.databinding.ActivityMyAvatarBinding;
import com.sortinghat.funny.viewmodel.MyEditInformationViewModel;

public class MyEditAvatarActivity extends BaseActivity<MyEditInformationViewModel, ActivityMyAvatarBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_avatar;
    }

    @Override
    protected void initViews() {
        initTitleBar("图像");
    }

    @Override
    protected void initData() {

    }

}

