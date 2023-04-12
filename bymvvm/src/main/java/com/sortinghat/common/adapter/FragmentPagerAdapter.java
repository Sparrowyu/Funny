package com.sortinghat.common.adapter;

import android.text.Html;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by jingbin on 2016/12/6.
 */
public class FragmentPagerAdapter extends FragmentStatePagerAdapter {

    private List<?> fragmentList;
    private List<String> titleList;

    /**
     * 普通，主页使用
     */
    public FragmentPagerAdapter(FragmentManager fm, List<?> mFragment) {
        super(fm);
        this.fragmentList = mFragment;
    }

    /**
     * 接收首页传递的标题
     */
    public FragmentPagerAdapter(FragmentManager fm, List<?> mFragment, List<String> mTitleList) {
        super(fm);
        this.fragmentList = mFragment;
        this.titleList = mTitleList;
    }

    @Override
    public Fragment getItem(int position) {
        return (Fragment) fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    /**
     * 首页显示title，每日推荐等..
     * 若有问题，移到对应单独页面
     */
    @Override
    public CharSequence getPageTitle(int position) {
        if (titleList != null && position < titleList.size()) {
            return Html.fromHtml(titleList.get(position));
        } else {
            return "";
        }
    }

    public void addFragmentList(List<?> fragment) {
        this.fragmentList.clear();
        this.fragmentList = null;
        this.fragmentList = fragment;
        notifyDataSetChanged();
    }
}