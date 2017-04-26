/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.knowbox.teacher.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgressView extends View {

	private Paint mProgressBgPaint;
	private int mProgress = 0;
	private RectF mRect = new RectF();
	
	public CircleProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
		mProgressBgPaint = new Paint();
		mProgressBgPaint.setAntiAlias(true);
		mProgressBgPaint.setStyle(Paint.Style.FILL);
		mProgressBgPaint.setStrokeCap(Paint.Cap.ROUND);
		mProgressBgPaint.setStrokeJoin(Paint.Join.ROUND);
		mProgressBgPaint.setColor(0x99ffffff);
	}
	
	/**
	 * 设置进度
	 * @param progress
	 */
	public void setProgress(int progress) {
		this.mProgress = progress;
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mRect.set(0, 0, getWidth(), getHeight());
		canvas.drawArc(mRect, -90, 360 - mProgress * 3.6f, true, mProgressBgPaint);
	}
}
