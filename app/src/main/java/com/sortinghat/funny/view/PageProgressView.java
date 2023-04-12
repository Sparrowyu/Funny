package com.sortinghat.funny.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class PageProgressView extends View {

    /**
     * 分段颜色
     */
    private static final int[] SECTION_COLORS = {Color.parseColor("#FEBF00"), Color.parseColor("#FF9700"), Color.parseColor("#FE4B37")};
    /**
     * 进度条最大值
     */
    private float maxCount = 100;
    /**
     * 进度条当前值
     */
    private float currentCount;
    /**
     * 画笔
     */
    private Paint mPaint;
    private int mWidth, mHeight;

    public PageProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public PageProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PageProgressView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
//		int round = mHeight / 2;
        int round = 0;
//		mPaint.setColor(Color.rgb(71, 76, 80));
//		RectF rectBg = new RectF(0, 0, mWidth, mHeight);
//		canvas.drawRoundRect(rectBg, round, round, mPaint);

//		mPaint.setColor(Color.BLACK);
//		RectF rectBlackBg = new RectF(2, 2, mWidth - 2, mHeight - 2);
//		canvas.drawRoundRect(rectBlackBg, round, round, mPaint);

        float section = currentCount / maxCount;
        RectF rectProgressBg = new RectF(0, 1, mWidth * section, mHeight - 1);
        if (section <= 1.0f / 3.0f) {
            if (section != 0.0f) {
                mPaint.setColor(SECTION_COLORS[0]);
            } else {
                mPaint.setColor(Color.TRANSPARENT);
            }
        } else {
            int count = (section <= 1.0f / 3.0f * 2) ? 2 : 3;
            int[] colors = new int[count];
            System.arraycopy(SECTION_COLORS, 0, colors, 0, count);
            float[] positions = new float[count];
            if (count == 2) {
                positions[0] = 0.0f;
                positions[1] = 1.0f - positions[0];
            } else {
                positions[0] = 0.0f;
                positions[1] = (maxCount / 3) / currentCount;
                positions[2] = 1.0f - positions[0] * 2;
            }
            positions[positions.length - 1] = 1.0f;
            LinearGradient shader = new LinearGradient(3, 0, (mWidth - 3)
                    * section, mHeight, colors, null,
                    Shader.TileMode.MIRROR);
            mPaint.setShader(shader);
        }
        canvas.drawRoundRect(rectProgressBg, round, round, mPaint);
//		RectF rect = new RectF(mWidth * section, 0, mWidth* section+4,
//				mHeight);
//		Paint paint = new Paint();
//		paint.setColor(Color.parseColor("#FF9700"));
//		canvas.drawRoundRect(rect, round, round, paint);

//		if(threadStart&&currentCount>=30){
//			canvas.drawBitmap(lightBitmap, lightWidth, 1, lightPaint);
//		}
    }

    private int dipToPx(int dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /***
     * 设置最大的进度值
     *
     * @param maxCount
     */
    public void setMaxCount(float maxCount) {
        this.maxCount = maxCount;
    }

    /***
     * 设置当前的进度值
     *
     * @param currentCount
     */
    public void setCurrentCount(float currentCount) {
        this.currentCount = currentCount > maxCount ? maxCount : currentCount;
        invalidate();
    }

    public void setProgress(int newProgress) {
        if (!excutorRunning) {
            startProgress();
        }
        if (newProgress >= 100) {
            mprogress = 0;
            if (excutorRunning) {
//				executorService.shutdown();
                timer.cancel();
                excutorRunning = false;
            }
            setCurrentCount(newProgress);
            postInvalidate();
            newProgress = 0;
            setCurrentCount(0);
            postInvalidate();
        } else if (newProgress > mprogress) {// 当webview的进度更快时显示webview的进度
            setCurrentCount(newProgress);
            postInvalidate();
        }
    }

    public float getMaxCount() {
        return maxCount;
    }

    public float getCurrentCount() {
        return currentCount;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY
                || widthSpecMode == MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize;
        } else {
            mWidth = 0;
        }
        if (heightSpecMode == MeasureSpec.AT_MOST
                || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            mHeight = dipToPx(15);
        } else {
            mHeight = heightSpecSize;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    //	ScheduledExecutorService executorService;
    boolean excutorRunning;
    float mprogress = 0;

    private void startProgress() {
        if (!excutorRunning) {
            setCurrentCount(0);
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Message msg = new Message();
                    if (getCurrentCount() < 100) {
                        // 当真实进度大于虚拟进度时，显示真实进度
                        if (mprogress < getCurrentCount()) {
                            if (getCurrentCount() < 20) {
                                mprogress = getCurrentCount() + 0.1f;
                            } else if (getCurrentCount() < 90) {
                                mprogress = getCurrentCount() + 0.2f;
                            } else {
                                mprogress = getCurrentCount() + 0.05f;
                            }
                        } else {
                            if (mprogress < 20) {
                                mprogress = mprogress + 0.1f;
                            } else if (mprogress < 90) {
                                mprogress = mprogress + 0.2f;
                            } else {
                                mprogress = mprogress + 0.05f;
                            }
                            if (mprogress >= 98) {
                                mprogress = 98;
                            }
                            msg.what = 3;
                            msg.obj = mprogress;
                            handler.sendMessage(msg);
                        }
                    } else {
                    }
                }
            };

            timer.schedule(task, 0, 10);

//			executorService = Executors.newSingleThreadScheduledExecutor();
//			executorService.scheduleWithFixedDelay(new Runnable() {
//				@Override
//				public void run() {}
//			}, 0, 10, TimeUnit.MILLISECONDS);
            excutorRunning = true;
        }
    }

    public void resetProgress() {
        mprogress = 0;
        if (excutorRunning) {
            timer.cancel();
            excutorRunning = false;
        }
        setCurrentCount(100);
        postInvalidate();
        setCurrentCount(0);
        postInvalidate();
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 3:
                    Float currentCount = (Float) msg.obj;
                    if (mprogress == 0 && getCurrentCount() == 0) {
                        setCurrentCount(0);
                        postInvalidate();
                    } else if (getCurrentCount() < 100
                            && currentCount < 100) {
                        setCurrentCount(currentCount);
                        postInvalidate();
                    } else {
                        setCurrentCount(0);
                        postInvalidate();
                    }
                    break;
            }
        }
    };
    private Timer timer;
}
