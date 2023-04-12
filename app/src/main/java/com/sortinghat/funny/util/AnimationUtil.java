package com.sortinghat.funny.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

public class AnimationUtil {
    /**
     * 放大缩小的动画
     */
    public static void showScaleAnimation(View view, long duration) {
        try {
            AnimationSet setAnimation = new AnimationSet(true);
            setAnimation.setRepeatMode(Animation.REVERSE);
            Animation scale1 = new ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scale1.setDuration(duration);
            scale1.setRepeatCount(Animation.INFINITE);
            setAnimation.addAnimation(scale1);
            setAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if (view != null)
                view.startAnimation(setAnimation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //放大缩小动画
    public static void startScaleAnimation(View view) {
        try {
            long duration = 600;//动画时间
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.7f, 1.0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.7f, 1.0f);
            set.play(scaleX).with(scaleY);

            scaleX.setDuration(duration);
            scaleY.setDuration(duration);

            scaleX.setRepeatMode(ValueAnimator.REVERSE);
            scaleY.setRepeatMode(ValueAnimator.REVERSE);

            scaleX.setRepeatCount(ValueAnimator.INFINITE);
            scaleY.setRepeatCount(ValueAnimator.INFINITE);
            set.setStartDelay(0);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                }
            });
            set.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
