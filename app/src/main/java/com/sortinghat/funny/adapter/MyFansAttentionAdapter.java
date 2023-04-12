package com.sortinghat.funny.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.LogUtils;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.MyFansAttentionListBean;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ItemMyFansAttentionBinding;
import com.sortinghat.funny.ui.BottomSheetDialog.DelAttentionDialog;
import com.sortinghat.funny.ui.my.MyOtherUserInfoActivity;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.DateUtil;
import com.sortinghat.funny.viewmodel.MyFansAttentionViewModel;

/**
 * Created by wzy on 2021/6/28
 */
public class MyFansAttentionAdapter extends BaseBindingAdapter<MyFansAttentionListBean, ItemMyFansAttentionBinding> {
    private FragmentManager childFragmentManager;
    private Context mContext;
    private MyFansAttentionViewModel viewModel;

    public void setViewModel(MyFansAttentionViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public MyFansAttentionAdapter(Context mContext, FragmentManager childFragmentManager) {
        super(R.layout.item_my_fans_attention);
        this.mContext = mContext;
        this.childFragmentManager = childFragmentManager;
    }

    @Override
    protected void bindView(BaseBindingHolder holder, MyFansAttentionListBean infoBean, ItemMyFansAttentionBinding binding, int position) {

        if (infoBean != null) {
            if (infoBean.getLastPubPostTime() > 0) {
                binding.tvUserTime.setText("发布了新作品" + DateUtil.getMillToTimeDay(infoBean.getLastPubPostTime()));
            } else {
                binding.tvUserTime.setText("未发布作品");
            }

            binding.tvUserName.setText(infoBean.getNickname());
            if (infoBean.getMutualFollow() == 1) {
                binding.sure.setBackgroundResource(R.drawable.shape_white_bg_gray_border);
                binding.sure.setTextColor(CommonUtils.getColor(R.color.color_333333));
                binding.sure.setText("互相关注");
            } else if (infoBean.getMutualFollow() == 2) {
                binding.sure.setTextColor(CommonUtils.getColor(R.color.color_333333));
                binding.sure.setText("已关注");
                binding.sure.setBackgroundResource(R.drawable.shape_white_bg_gray_border);
            } else {
                binding.sure.setBackgroundResource(R.drawable.shape_orange_bg_with_corner);
                binding.sure.setText("关注");
                binding.sure.setTextColor(CommonUtils.getColor(R.color.white));
            }
//            infoBean.setPendantUrl("http://oss-andriod.gaoxiaoxingqiu.com/funny_store/pendant/haowurenxing.png");
            GlideUtils.loadCircleImage(infoBean.getAvatar(), R.mipmap.user_icon_default, binding.ivUserIcon);
            ConstantUtil.glideLoadPendantUrl(infoBean.getPendantUrl(), binding.ivBoxUserIcon);
            setListener(binding, infoBean, position);
        }

    }

    private void setListener(ItemMyFansAttentionBinding binding, MyFansAttentionListBean infoBean, int position) {
        binding.layoutUser.setOnClickListener(new QuickClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                MyOtherUserInfoActivity.starActivity(mContext, infoBean.getUserId(), 0);
            }
        });
        binding.sure.setOnClickListener(new QuickClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (infoBean.getMutualFollow() == 0 || infoBean.getMutualFollow() == 3) {
                    getAttentionHttp(binding, infoBean, position);
                } else {
                    showSureDialog(binding, infoBean, position);
                }
            }
        });
    }

    DelAttentionDialog delAttentionDialog;

    private void showSureDialog(ItemMyFansAttentionBinding binding, MyFansAttentionListBean listBean, int position) {
        if (delAttentionDialog != null && delAttentionDialog.isVisible()) {
            LogUtils.d("shareDialog", "isShowing");
            return;
        }

        delAttentionDialog = new DelAttentionDialog(listBean.getMutualFollow());
        delAttentionDialog.show(childFragmentManager, "" + position);
        delAttentionDialog.setOnDialogSureListener(() -> {
            if (viewModel == null) {
                return;
            }
            getAttentionHttp(binding, listBean, position);
         /*   viewModel.getUserFollowList(listBean.getUserId(), (listBean.getMutualFollow() == 2 || listBean.getMutualFollow() == 1) ? 0 : 1).observe((LifecycleOwner) mContext, resultBean -> {
                delAttentionDialog.dismiss();
                if (resultBean != null) {
                    if (resultBean.getCode() == 0) {
                        if (listBean.getMutualFollow() == 0) {
                            listBean.setMutualFollow(2);
                            binding.sure.setTextColor(CommonUtils.getColor(R.color.color_333333));
                            binding.sure.setBackgroundResource(R.drawable.shape_white_bg_gray_border);
                            binding.sure.setText("已关注");
                            CommonUtils.showShort("关注成功");
                        } else if (listBean.getMutualFollow() == 1) {
                            listBean.setMutualFollow(3);
                            binding.sure.setTextColor(CommonUtils.getColor(R.color.white));
                            binding.sure.setBackgroundResource(R.drawable.shape_orange_bg_with_corner);
                            binding.sure.setText("关注");
                            CommonUtils.showShort("取消成功");
                        } else if (listBean.getMutualFollow() == 2) {
                            listBean.setMutualFollow(0);
                            binding.sure.setBackgroundResource(R.drawable.shape_orange_bg_with_corner);
                            binding.sure.setTextColor(CommonUtils.getColor(R.color.white));
                            binding.sure.setText("关注");
                            CommonUtils.showShort("取消成功");
                        } else {
                            listBean.setMutualFollow(1);
                            binding.sure.setTextColor(CommonUtils.getColor(R.color.color_333333));
                            binding.sure.setBackgroundResource(R.drawable.shape_white_bg_gray_border);
                            binding.sure.setText("互相关注");
                            CommonUtils.showShort("关注成功");
                        }

                    }
                }
            });*/

        });
    }

    private void getAttentionHttp(ItemMyFansAttentionBinding binding, MyFansAttentionListBean listBean, int position) {
        if (viewModel == null) {
            return;
        }
        viewModel.getUserFollowList(listBean.getUserId(), (listBean.getMutualFollow() == 2 || listBean.getMutualFollow() == 1) ? 0 : 1).observe((LifecycleOwner) mContext, resultBean -> {
            if (delAttentionDialog != null) {
                delAttentionDialog.dismiss();
            }
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    if (listBean.getMutualFollow() == 0) {
                        listBean.setMutualFollow(2);
                        binding.sure.setTextColor(CommonUtils.getColor(R.color.color_333333));
                        binding.sure.setBackgroundResource(R.drawable.shape_white_bg_gray_border);
                        binding.sure.setText("已关注");
                        CommonUtils.showShort("关注成功");
                    } else if (listBean.getMutualFollow() == 1) {
                        listBean.setMutualFollow(3);
                        binding.sure.setTextColor(CommonUtils.getColor(R.color.white));
                        binding.sure.setBackgroundResource(R.drawable.shape_orange_bg_with_corner);
                        binding.sure.setText("回关");
                        CommonUtils.showShort("取消成功");
                    } else if (listBean.getMutualFollow() == 2) {
                        listBean.setMutualFollow(0);
                        binding.sure.setBackgroundResource(R.drawable.shape_orange_bg_with_corner);
                        binding.sure.setTextColor(CommonUtils.getColor(R.color.white));
                        binding.sure.setText("关注");
                        CommonUtils.showShort("取消成功");
                    } else {
                        listBean.setMutualFollow(1);
                        binding.sure.setTextColor(CommonUtils.getColor(R.color.color_333333));
                        binding.sure.setBackgroundResource(R.drawable.shape_white_bg_gray_border);
                        binding.sure.setText("互相关注");
                        CommonUtils.showShort("关注成功");
                    }
                    RxBus.getDefault().post(RxCodeConstant.HOME_VIDEO_UPDATA_AUTHOR_FOLLOW, listBean.getUserId() + "," + listBean.getMutualFollow());
                }
            }
        });
    }
}
