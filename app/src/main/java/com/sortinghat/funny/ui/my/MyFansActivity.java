package com.sortinghat.funny.ui.my;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ActivityUtils;
import com.sortinghat.common.adapter.FragmentPagerAdapter;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.base.NoViewModel;
import com.sortinghat.funny.R;
import com.sortinghat.funny.databinding.ActivityMyFansBinding;

import java.util.ArrayList;

public class MyFansActivity extends BaseActivity<NoViewModel, ActivityMyFansBinding> {

    private ArrayList<String> titleList = new ArrayList<>(2);
    private ArrayList<Fragment> fragmentList = new ArrayList<>(2);
    private int currentIndex = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_fans;
    }

    @Override
    protected void initViews() {
        if (null != getIntent()) {
            currentIndex = getIntent().getIntExtra("index", 0);
            initTitleBar(getIntent().getStringExtra("name"));
        }

        initFragmentList();
        initViewPagerAdapter();
    }

    @Override
    protected void initData() {

    }

    private void initFragmentList() {
        titleList.add("粉丝");
        titleList.add("关注");
        fragmentList.add(new MyFansFragment(0));
        fragmentList.add(new MyFansFragment(1));
    }

    private void initViewPagerAdapter() {
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titleList);
        contentLayoutBinding.viewPager.setAdapter(pagerAdapter);
        contentLayoutBinding.tabLayout.setupWithViewPager(contentLayoutBinding.viewPager);
        contentLayoutBinding.viewPager.setCurrentItem(currentIndex);
    }

    public static void starActivity(Context mContext, int index, String name) {
        Intent intent = new Intent(mContext, MyFansActivity.class);
        intent.putExtra("index", index);
        intent.putExtra("name", name);
        ActivityUtils.startActivity(intent);
    }

}

