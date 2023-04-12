package com.sortinghat.funny.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.sortinghat.funny.R;

public class ShadowTextView extends AppCompatTextView {
    private int shadowInt = 3;

    public ShadowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onDraw(Canvas canvas) {
        //先绘制一个边框在调用父类的onDraw()方法，绘制正常文本
        drawText(canvas);
        super.onDraw(canvas);
    }

    private void drawText(Canvas canvas) {
        if (canvas == null || null == getText() || TextUtils.isEmpty(getText()) || null == getLayout()) {
            return;
        }
        //1.获取画笔对象，这里如果不用父类自己new的话要注意设置字体的大小，不然会使用默认大小会，字会很小
        Paint paint = getPaint();
        //2.获取文本
        String text = String.valueOf(getText());
        //3.定位字体位置
        //第一行左边距
        float startX = getLayout().getLineLeft(0);
        //第一行文字的底部边距
        float startY = getBaseline();
        //分别向左，向上，向下，向右一个像素绘制文本形成重叠效果
        paint.setColor(getResources().getColor(R.color.textShadowColor));
//        canvas.drawText(text, startX + shadowInt, startY, paint);
//        canvas.drawText(text, startX, startY - shadowInt, paint);
        canvas.drawText(text, startX, startY + shadowInt, paint);
//        canvas.drawText(text, startX - shadowInt, startY, paint);

    }


}
