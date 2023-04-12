package com.sortinghat.funny.view;

import android.view.animation.AccelerateDecelerateInterpolator;

import com.sortinghat.funny.view.rollingview.strategy.CharOrder;
import com.sortinghat.funny.view.rollingview.strategy.MyStrategy;
import com.sortinghat.funny.view.rollingview.strategy.RollingTextView;

public class ConstantView {

    public static void initGoldAnim(RollingTextView textView, String gold) {
        textView.setAnimationDuration(300);
        textView.setCharStrategy(MyStrategy.CarryBitAnimation());
        textView.addCharOrder(CharOrder.Number);
        textView.setAnimationInterpolator(new AccelerateDecelerateInterpolator());
        textView.setText(gold);
    }
}
