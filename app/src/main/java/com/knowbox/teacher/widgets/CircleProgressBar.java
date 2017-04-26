/**
 * Copyright (C) 2014 The KnowboxTeacher Project
 */
package com.knowbox.teacher.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.knowbox.teacher.R;

/**
 * 自适应圆形进度条
 *
 * 2016.11.24日增加圆弧进度条by LiuYu
 *
 * @author yangzc
 */
public class CircleProgressBar extends View {

    private Paint mProgressBgPaint;
    private Paint mProgressPaint;
//    private Paint mPercentPaint;

    private RectF mProgressRectF;

    private float mDensity;
    private float mProgress;
    private float mPadding;
    //缺口，如果画的是圆环，设置为零（默认为零），如果为圆弧，setBreachRadian()设置，参数需大于且小于358
    private int breachRadian;

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleProgressBar(Context context) {
        super(context);
        init();
    }

    private void init() {
        mDensity = getResources().getDisplayMetrics().density;
        mProgressRectF = new RectF();
        mProgressBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressBgPaint.setAntiAlias(true);
        mProgressBgPaint.setStrokeWidth(1.5f * mDensity);
        mPadding = mProgressBgPaint.getStrokeWidth() / 2 + 1.5f;
        mProgressBgPaint.setStyle(Paint.Style.STROKE);
        mProgressBgPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressBgPaint.setStrokeJoin(Paint.Join.ROUND);
        mProgressBgPaint.setColor(0xffeeeeee);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStrokeWidth(1.5f * mDensity);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setStrokeJoin(Paint.Join.ROUND);
        mProgressPaint.setColor(getResources().getColor(R.color.color_main_app));

//		mPercentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		mPercentPaint.setColor(0xff3dcab1);
//		mPercentPaint.setTextSize(18 * mDensity);
    }

    public void setLineColor(int bgColor, int color) {
        mProgressBgPaint.setColor(bgColor);
        mProgressPaint.setColor(color);
        invalidate();
    }

    /**
     * 这个方法在setBgStrokeWidth()方法调用之后调用，已避免风险
     *
     * @param padding
     */
    public void setPadding(float padding) {
        if (mPadding < padding) {
            mPadding = padding;
        }
    }

    public void setBgLineColor(int color) {
        mProgressBgPaint.setColor(color);
        invalidate();
    }

    public void setLineColor(int color) {
        mProgressPaint.setColor(color);
        invalidate();
    }

    public void setBgStrokeWidth(int width) {
        mProgressBgPaint.setStrokeWidth(width);
        mPadding = mProgressBgPaint.getStrokeWidth() / 2 + 1.5f;
        invalidate();
    }

    public void setStrokeWidth(int width) {
        mProgressPaint.setStrokeWidth(width);
        invalidate();
    }

    public void setProgress(int progress) {
        if (progress > 100) {
            this.mProgress = 100;
        } else {
            this.mProgress = progress;
        }
        invalidate();
    }

    public void setBreachRadian(int breachRadian) {
        if (breachRadian >= 2 && breachRadian <= 358) {
            this.breachRadian = breachRadian;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        refreshRectF();
        drawProgress(canvas);
        // drawProgressTxt(canvas);
    }

    // private void drawProgressTxt(Canvas canvas) {
    // String text = mProgress + "%";
    // float width = mPercentPaint.measureText(text);
    // int x = (int) ((mProgressRectF.width() - width) / 2 +
    // mProgressRectF.left);
    // int y = (int) ((mProgressRectF.height() + mPercentPaint
    // .measureText("H")) / 2 + mProgressRectF.top);
    // canvas.drawText(text, x, y, mPercentPaint);
    // }

    private void drawProgress(Canvas canvas) {
        float radian = mProgress * (360 - breachRadian) / 100;

        if (breachRadian == 0) {
            canvas.drawArc(mProgressRectF, 0, 360, false, mProgressBgPaint);

            canvas.drawArc(mProgressRectF, -90, mProgress * 3.6f, false, mProgressPaint);

        }else if (breachRadian <= 180) {
            canvas.drawArc(mProgressRectF, 90 - (breachRadian / 2), breachRadian - 360, false, mProgressBgPaint);

            canvas.drawArc(mProgressRectF, breachRadian + ((180 - breachRadian) / 2), radian, false, mProgressPaint);

        } else {
            canvas.drawArc(mProgressRectF, (180 - breachRadian) / 2, breachRadian - 360, false, mProgressBgPaint);

            canvas.drawArc(mProgressRectF, (breachRadian - 180) / 2 + 180, radian, false, mProgressPaint);
        }

    }

    private void refreshRectF() {
        int d = getWidth();
        if (getHeight() < d) {
            d = getHeight();
        }
        mProgressRectF.set(mPadding + (getWidth() - d) / 2, mPadding
                + (getHeight() - d) / 2, d - mPadding + (getWidth() - d) / 2, d
                - mPadding + (getHeight() - d) / 2);
    }
}
