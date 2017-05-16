/**
 * Copyright (C) 2015 The PathCorrect Project
 */
package com.buang.welewolf.widgets.correct;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.View;

/**
 * 绘制图片
 * @author yangzc
 */
public class ImagePanel extends View {

	private Bitmap mRawBitmap;
	private Bitmap mMarkedBitmap;
	private RectF mRectF = new RectF();
	private int mWidth, mHeight;
	
	public ImagePanel(Context context) {
		super(context);
	}

	/**
	 * 设置原始图
	 * @param scale
	 * @param rawBitmap
	 */
	public void setRawBitmap(Bitmap rawBitmap){
		this.mRawBitmap = rawBitmap;
		postInvalidate();
	}
	
	/**
	 * 设置批改过的痕迹
	 * @param markBitmap
	 */
	public void setMarkBitmap(Bitmap markBitmap){
		this.mMarkedBitmap = markBitmap;
		postInvalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawImpl(canvas);
	}
	
	public RectF getDrawRectF() {
		mRectF.set(0, 0, mWidth, mHeight);
		return mRectF;
	}

	private void drawImpl(Canvas canvas) {
		if (mRawBitmap != null && !mRawBitmap.isRecycled()) {
			canvas.drawBitmap(mRawBitmap, null, getDrawRectF(), null);
		}
		if(mMarkedBitmap != null && !mMarkedBitmap.isRecycled()){
			canvas.drawBitmap(mMarkedBitmap, null, getDrawRectF(), null);
		}
	}
	
	public void updateSize(int width, int height){
		this.mWidth = width;
		this.mHeight = height;
		postInvalidate();
	}
}
