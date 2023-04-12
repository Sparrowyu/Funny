package com.sortinghat.common.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.LogUtils;
import com.sortinghat.common.R;
import com.sortinghat.common.utils.ClassUtil;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.statusbar.StatusBarFontColorUtil;
import com.sortinghat.common.utils.statusbar.StatusBarUtil;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 是没有title的Fragment
 * Created by wzy on 2020/3/2
 */
public abstract class BaseFragment<VM extends AndroidViewModel, SV extends ViewDataBinding> extends Fragment {

    protected Activity activity;
    protected View view;

    //加载中
    private View loadingView;
    //加载失败
    private View errorView;
    //空布局
    protected View emptyView;
    //动画
    private AnimationDrawable mAnimationDrawable;

    protected MaterialDialog progressDialog;

    //布局view
    protected SV contentLayoutBinding;
    //ViewModel
    protected VM viewModel;

    private CompositeDisposable mCompositeDisposable;

    private boolean isHasInitRootView = false, isInitData = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(getClass().getSimpleName() + "----------onCreate----------");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.fragment_base, null);
            isHasInitRootView = true;
            contentLayoutBinding = DataBindingUtil.inflate(activity.getLayoutInflater(), getLayoutId(), null, false);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            contentLayoutBinding.getRoot().setLayoutParams(params);
            RelativeLayout contentLayoutRootView = view.findViewById(R.id.content_layout_root_view);
            if (null != contentLayoutRootView) {
                contentLayoutRootView.addView(contentLayoutBinding.getRoot());
            }
            contentLayoutBinding.getRoot().setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isHasInitRootView) {
            isHasInitRootView = false;
            initViewModel();
            initViews();
            setListener();
            lazyLoadData();
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initViews();

    protected abstract void initData();

    protected void setListener() {
    }

    protected void setStatusBar() {
        setStatusBar(R.color.white, true);
    }

    protected void setStatusBar(int colorResource) {
        setStatusBar(colorResource, true);
    }

    protected void setStatusBar(int colorResource, boolean isLightMode) {
        //设置透明状态栏，兼容4.4
        StatusBarUtil.setColor(activity, CommonUtils.getColor(colorResource), 0);
        if (isLightMode) {
            StatusBarFontColorUtil.setLightMode(activity);
        } else {
            StatusBarFontColorUtil.setDarkMode(activity);
        }
    }

    protected void setStatusBarMode(boolean isLightMode) {
        //设置透明状态栏，兼容4.4
        if (isLightMode) {
            StatusBarFontColorUtil.setLightMode(activity);
        } else {
            StatusBarFontColorUtil.setDarkMode(activity);
        }
    }

    private void initViewModel() {
        Class<VM> viewModelClass = ClassUtil.getViewModel(this);
        if (viewModelClass != null) {
            this.viewModel = ViewModelProviders.of(this).get(viewModelClass);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        lazyLoadData();
    }

    private void lazyLoadData() {
        if (getUserVisibleHint() && !isInitData && view != null) {
            isInitData = true;
            initData();
        }
    }

    public boolean isInitData() {
        return isInitData;
    }

    protected void showContentView() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        contentLayoutBinding.getRoot().setVisibility(View.VISIBLE);
    }

    protected void showLoading() {
        contentLayoutBinding.getRoot().setVisibility(View.GONE);
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (loadingView == null) {
            loadingView = ((ViewStub) findViewById(R.id.vs_loading)).inflate();
            ImageView img = loadingView.findViewById(R.id.img_progress);
            mAnimationDrawable = (AnimationDrawable) img.getDrawable();
        } else {
            loadingView.setVisibility(View.VISIBLE);
        }
        if (mAnimationDrawable != null && !mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
    }

    protected void showError() {
        contentLayoutBinding.getRoot().setVisibility(View.GONE);
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (errorView == null) {
            errorView = ((ViewStub) findViewById(R.id.vs_error_refresh)).inflate();
            //点击加载失败布局
            errorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoading();
                    onReload();
                }
            });
        } else {
            errorView.setVisibility(View.VISIBLE);
        }
    }

    protected void showEmptyView() {
        showEmptyView(getString(R.string.by_string_empty), R.drawable.load_data_empty);
    }

    protected void showEmptyView(String text) {
        showEmptyView(text, R.drawable.load_data_empty);
    }

    protected void showEmptyView(int emptyDataImageResource) {
        showEmptyView(getString(R.string.by_string_empty), emptyDataImageResource);
    }

    protected void showEmptyView(String text, int emptyDataImageResource) {
        showEmptyView(text, emptyDataImageResource, false, null);
    }

    protected void showEmptyView(String text, int emptyDataImageResource, boolean isTop) {
        showEmptyView(text, emptyDataImageResource, isTop, null);
    }

    protected void showEmptyView(String text, int emptyDataImageResource, boolean isTop, QuickClickListener quickClickListener) {
        contentLayoutBinding.getRoot().setVisibility(View.VISIBLE);
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
//        if (emptyView == null) {
//            emptyView = ((ViewStub) findViewById(R.id.vs_empty)).inflate();
//        }
        getEmptyView(isTop);
        ((ImageView) emptyView.findViewById(R.id.iv_empty_content)).setImageResource(emptyDataImageResource);
        ((TextView) emptyView.findViewById(R.id.tv_tip_empty)).setText(text);
        if (null != quickClickListener) {
            (emptyView.findViewById(R.id.tv_tip_bt)).setVisibility(View.VISIBLE);
            (emptyView.findViewById(R.id.tv_tip_bt)).setOnClickListener(quickClickListener);
            (emptyView.findViewById(R.id.iv_empty_content)).setOnClickListener(quickClickListener);
        }
        emptyView.setVisibility(View.VISIBLE);
    }

    protected View getEmptyView() {
        return getEmptyView(false);
    }

    protected View getEmptyView(boolean isTop) {
        if (emptyView == null) {
            if (isTop) {
                emptyView = LayoutInflater.from(activity).inflate(R.layout.layout_loading_empty_top, null);
            } else {
                emptyView = LayoutInflater.from(activity).inflate(R.layout.layout_loading_empty, null);
            }
        }
        return emptyView;
    }


    /**
     * 失败后点击重新加载
     */
    protected void onReload() {
    }

    protected <T extends View> T findViewById(int id) {
        return (T) view.findViewById(id);
    }

    protected void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeProgressDialog();
        breakParent();
    }

    private void breakParent() {
        if (view == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
    }

    public void addSubscription(Disposable disposable) {
        if (this.mCompositeDisposable == null) {
            this.mCompositeDisposable = new CompositeDisposable();
        }
        this.mCompositeDisposable.add(disposable);
    }

    public void removeDisposable() {
        if (this.mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            this.mCompositeDisposable.dispose();
        }
    }

    @Override
    public void onDestroy() {
        LogUtils.i(getClass().getSimpleName() + "----------onDestroy----------");
        if (this.mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            this.mCompositeDisposable.clear();
        }
        super.onDestroy();
    }
}