package com.sortinghat.common.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.sortinghat.common.R;
import com.sortinghat.common.databinding.ActivityBaseBinding;
import com.sortinghat.common.databinding.TitleBarBinding;
import com.sortinghat.common.utils.ClassUtil;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.ConstantUtilsByMvvm;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.ad.ForegroundSplashAdUtils;
import com.sortinghat.common.utils.ad.SplashAdActivity;
import com.sortinghat.common.utils.statusbar.StatusBarFontColorUtil;
import com.sortinghat.common.utils.statusbar.StatusBarUtil;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by wzy on 2020/3/2
 */
public abstract class BaseActivity<VM extends AndroidViewModel, SV extends ViewDataBinding> extends AppCompatActivity {

    protected Context mContext;

    //加载中
    private View loadingView;
    //加载失败
    private View errorView;
    //空布局
    protected View emptyView;
    //动画
    private AnimationDrawable mAnimationDrawable;

    public MaterialDialog progressDialog;

    protected TitleBarBinding titleBarBinding;
    protected SV contentLayoutBinding;
    protected VM viewModel;

    private CompositeDisposable mCompositeDisposable;
    private boolean mStateEnable = true;//设置标志位，状态保存过后，不处理KEY事件

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (ActivityUtils.getTopActivity() != null) {
            LogUtils.i("入栈:" + ActivityUtils.getTopActivity().getClass().getSimpleName());
        }
        mContext = this;
        mStateEnable = true;
        setContentView();
        setStatusBar(false);
        initViewModel();
        initViews();
        setListener();
        initData();
    }

    public void setContentView() {
        ActivityBaseBinding mBaseBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_base, null, false);

        addTitleBarView(mBaseBinding);

        contentLayoutBinding = DataBindingUtil.inflate(getLayoutInflater(), getLayoutId(), null, false);
        contentLayoutBinding.getRoot().setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        RelativeLayout contentLayoutRootView = mBaseBinding.getRoot().findViewById(R.id.content_layout_root_view);
        contentLayoutRootView.addView(contentLayoutBinding.getRoot());

        getWindow().setContentView(mBaseBinding.getRoot());

//        contentLayoutBinding.getRoot().setVisibility(View.GONE);
    }

    protected void addTitleBarView(ActivityBaseBinding mBaseBinding) {
        titleBarBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.title_bar, null, false);
        titleBarBinding.getRoot().setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(48)));
        RelativeLayout titleBarRootView = mBaseBinding.getRoot().findViewById(R.id.title_bar_root_view);
        titleBarRootView.addView(titleBarBinding.getRoot());
    }

    protected abstract int getLayoutId();

    protected abstract void initViews();

    protected abstract void initData();

    protected void setListener() {
    }

    protected void setStatusBar(boolean isBarTransparent) {
        if (isBarTransparent) {
            StatusBarUtil.setStatusBarTransparent(this);
        } else {
            setStatusBar(R.color.white, true);
        }
    }

    protected void setStatusBarMode(boolean isLightMode) {
        //设置透明状态栏，兼容4.4
        if (isLightMode) {
            StatusBarFontColorUtil.setLightMode(this);
        } else {
            StatusBarFontColorUtil.setDarkMode(this);
        }
    }

    protected void setStatusBar(int colorResource) {
        setStatusBar(colorResource, true);
    }

    protected void setStatusBar(int colorResource, boolean isLightMode) {
        //设置透明状态栏，兼容4.4
        StatusBarUtil.setColor(this, CommonUtils.getColor(colorResource), 0);
        if (isLightMode) {
            StatusBarFontColorUtil.setLightMode(this);
        } else {
            StatusBarFontColorUtil.setDarkMode(this);
        }
    }

    private void initViewModel() {
        Class<VM> viewModelClass = ClassUtil.getViewModel(this);
        if (viewModelClass != null) {
            this.viewModel = ViewModelProviders.of(this).get(viewModelClass);
        }
    }

    protected void initTitleBar(String title) {
        initTitleBar(title, "");
    }

    protected void initTitleBar(String title, String rightText) {
        if (titleBarBinding != null) {
            titleBarBinding.ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            if (!TextUtils.isEmpty(title)) {
                titleBarBinding.tvTitle.setText(title);
            }
            if (!TextUtils.isEmpty(rightText)) {
                titleBarBinding.tvRightText.setText(rightText);
                titleBarBinding.tvRightText.setVisibility(View.VISIBLE);
                titleBarBinding.tvRightText.setOnClickListener(new QuickClickListener() {
                    @Override
                    protected void onNoDoubleClick(View v) {
                        onRightTextClick();
                    }
                });
            }
        }
    }

    protected void onRightTextClick() {
    }

    protected void initTitleBarImage(String title) {
        if (titleBarBinding != null) {
            initTitleBar(title);
            titleBarBinding.ivSearch.setVisibility(View.VISIBLE);
            titleBarBinding.ivSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSearch();
                }
            });
        }
    }

    protected void onSearch() {
    }

    protected void showContentView() {
        View loadingView = getLoadingView();
        showContentView(getContentView(), loadingView, getAnimationDrawable(loadingView), getErrorView(), getEmptyView());
    }

    protected void showContentView(View contentView, View loadingView, AnimationDrawable mAnimationDrawable, View errorView, View emptyView) {
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
        contentView.setVisibility(View.VISIBLE);
    }

    protected void showLoading() {
        View loadingView = getLoadingView();
        showLoading(getContentView(), loadingView, getAnimationDrawable(loadingView), getErrorView(), getEmptyView());
    }

    protected void showLoading(View contentView, View loadingView, AnimationDrawable mAnimationDrawable, View errorView, View emptyView) {
        contentView.setVisibility(View.GONE);
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
        if (mAnimationDrawable != null && !mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
    }

    protected void showError() {
        View loadingView = getLoadingView();
        showError(getContentView(), loadingView, getAnimationDrawable(loadingView), getErrorView(), getEmptyView());
    }

    protected void showError(View contentView, View loadingView, AnimationDrawable mAnimationDrawable, View errorView, View emptyView) {
        contentView.setVisibility(View.GONE);
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (mAnimationDrawable != null && mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (errorView != null) {
            errorView.setVisibility(View.VISIBLE);
        }
    }

    protected void showEmptyView() {
        showEmptyView(getString(R.string.by_string_empty), R.drawable.load_data_empty);
    }

    protected void showEmptyView(String text) {
        showEmptyView(text, R.drawable.load_data_empty);
    }

    protected void showEmptyView(int drawableResource) {
        showEmptyView(getString(R.string.by_string_empty), drawableResource);
    }

    protected void showEmptyView(String text, int drawableResource) {
        View loadingView = getLoadingView();
        showEmptyView(text, drawableResource, getContentView(), loadingView, getAnimationDrawable(loadingView), getErrorView(), getEmptyView());
    }

    protected void showEmptyView(String text, int drawableResource, View contentView, View loadingView, AnimationDrawable mAnimationDrawable, View errorView, View emptyView) {
        contentView.setVisibility(View.VISIBLE);
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
            ((TextView) emptyView.findViewById(R.id.tv_tip_empty)).setText(text);
            ((ImageView) emptyView.findViewById(R.id.iv_empty_content)).setImageResource(drawableResource);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    protected View getContentView() {
        return contentLayoutBinding.getRoot();
    }

    protected View getLoadingView() {
        if (loadingView == null) {
            loadingView = ((ViewStub) findViewById(R.id.vs_loading)).inflate();
        }
        return loadingView;
    }

    protected AnimationDrawable getAnimationDrawable(View loadingView) {
        if (mAnimationDrawable == null) {
            ImageView img = loadingView.findViewById(R.id.img_progress);
            mAnimationDrawable = (AnimationDrawable) img.getDrawable();
        }
        return mAnimationDrawable;
    }

    protected View getErrorView() {
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
        }
        return errorView;
    }

    protected View getEmptyView() {
        if (emptyView == null) {
            emptyView = LayoutInflater.from(this).inflate(R.layout.layout_loading_empty, null);
        }
        return emptyView;
    }

    /**
     * 失败后点击重新加载
     */
    protected void onReload() {
    }

    protected <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }

    public void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.fontScale != 1) {
            getResources();
        }
    }

    /**
     * 禁止改变字体大小
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    public void addSubscription(Disposable s) {
        if (this.mCompositeDisposable == null) {
            this.mCompositeDisposable = new CompositeDisposable();
        }
        this.mCompositeDisposable.add(s);
    }

    public void removeDisposable() {
        if (this.mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            this.mCompositeDisposable.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        if (this.mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            //clear和dispose的区别是：disposed = true;
            this.mCompositeDisposable.clear();
        }
        ToastUtils.cancel();
        closeProgressDialog();
        super.onDestroy();
        if (!ActivityUtils.isActivityExistsInStack(this)) {
            LogUtils.i("出栈:" + this.getClass().getSimpleName());
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        // super.onStart();中将mStateSaved置为false
        mStateEnable = true;
    }

    @Override
    protected void onResume() {
        // onPause之后便可能调用onSaveInstanceState，因此onresume中也需要置true
        mStateEnable = true;
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // super.onSaveInstanceState();中将mStateSaved置为true
        mStateEnable = false;
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        // super.onStop();中将mStateSaved置为true
        mStateEnable = false;
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConstantUtilsByMvvm.BACKGROUND_PAUSE_TIME = System.currentTimeMillis();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!SPUtils.getInstance("config_info").getBoolean("user_vip_tag")) {
            long pauseTime = ConstantUtilsByMvvm.BACKGROUND_PAUSE_TIME;
            long configAdPauseTime = SPUtils.getInstance("config_info").getLong("background_ad_pause_time", 8 * 60 * 1000);
            if (!(ActivityUtils.getTopActivity() instanceof SplashAdActivity) && configAdPauseTime > 1 && pauseTime > 0 && (System.currentTimeMillis() - pauseTime) > configAdPauseTime) {
                ActivityUtils.startActivity(SplashAdActivity.class, R.anim.dialog_enter, R.anim.dialog_exit);
            }
        }
    }

    public boolean isStateEnable() {
        return mStateEnable;
    }
}