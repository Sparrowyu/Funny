package com.sortinghat.funny.util;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.funny.R;

/**
 * Created by wzy on 2021/3/17
 */
public class SpannableUtil {

    public static void setTextSpan(String replaceText, String text, TextView textView, ClickableSpan clickableSpan) {
        int start = text.indexOf(replaceText);
        int end = start + replaceText.length();
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        textView.setHighlightColor(Color.TRANSPARENT);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static SpannableString getTextSpan(String text, TextView textView, ClickableSpan clickableSpan) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(clickableSpan == null ? new ForegroundColorSpan(CommonUtils.getColor(R.color.light_orange)) : clickableSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setHighlightColor(Color.TRANSPARENT);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setOnLongClickListener(v -> true);
        return spannableString;
    }
}
