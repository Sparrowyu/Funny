package com.sortinghat.funny.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class KeyboardListenerLayout extends RelativeLayout {

    public static final int KEYBOARD_STATE_SHOW = -3;
    public static final int KEYBOARD_STATE_HIDE = -2;
    public static final int KEYBOARD_STATE_INIT = -1;

    private boolean mHasInit = false;
    private int mHeight, keyboardLayoutbottom;

    private IOnKeyboardStateChangedListener onKeyboardStateChangedListener;

    public KeyboardListenerLayout(Context context) {
        super(context);
    }

    public KeyboardListenerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardListenerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnKeyboardStateChangedListener(IOnKeyboardStateChangedListener onKeyboardStateChangedListener) {
        this.onKeyboardStateChangedListener = onKeyboardStateChangedListener;
    }

    public interface IOnKeyboardStateChangedListener {
        void onKeyboardStateChanged(int state);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        keyboardLayoutbottom = b;
        if (!mHasInit) {
            mHeight = b;
            if (onKeyboardStateChangedListener != null) {
                mHasInit = true;
                onKeyboardStateChangedListener.onKeyboardStateChanged(KEYBOARD_STATE_INIT);
            }
        } else {
            mHeight = mHeight < b ? b : mHeight;
        }

        if (mHasInit && mHeight >= b) { // mHeight代表键盘的真实高度, b代表在窗口中的高度 mHeight>b, mHeight = b 说明已经弹出
            if (onKeyboardStateChangedListener != null) {
                if ((mHeight - b) > 200) {
                    onKeyboardStateChangedListener.onKeyboardStateChanged(KEYBOARD_STATE_SHOW);
                } else {
                    onKeyboardStateChangedListener.onKeyboardStateChanged(KEYBOARD_STATE_HIDE);
                }
            }
        }
    }

    public int getKeyboardLayoutbottom() {
        return keyboardLayoutbottom;
    }
}
