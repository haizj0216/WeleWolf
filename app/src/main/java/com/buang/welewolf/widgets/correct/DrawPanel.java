/**
 * Copyright (C) 2015 The PathCorrect Project
 */
package com.buang.welewolf.widgets.correct;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.buang.welewolf.widgets.correct.DrawContaienr.LayerInfo;
import com.buang.welewolf.widgets.correct.DrawContaienr.PathInfo;

import java.util.List;
import java.util.Stack;

/**
 * 绘图View
 * @author yangzc
 */
public class DrawPanel extends View {

	private static final int MODE_NONE = 1;
	private static final int MODE_DRAW = 2;
	
	private float mPreviousX, mPreviousY;
	private Path mPath;
	private Paint mPaint;
	
	private Matrix mDrawMatrix;
	private Path mDrawPath;
	private int mCurrentMode = MODE_NONE;
	private boolean mEditable = false;
	
	private Stack<LayerInfo> mLayerInfos;
	
	public DrawPanel(Context context) {
		super(context);
		init();
	}
	
	/**
	 * 是否可以编辑
	 * @param editable
	 */
	public void setEditable(boolean editable){
		this.mEditable = editable;
	}
	
	@SuppressLint("NewApi")
	private void init() {
		if(VERSION.SDK_INT >= 11)
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.STROKE);// 空心
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setAntiAlias(true);
		DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		if (width >= 2048) {
			mPaint.setStrokeWidth(4);
		} else {
			mPaint.setStrokeWidth(2);
		}
		mPaint.setPathEffect(new CornerPathEffect(80));
		mDrawPath = new Path();
		mDrawMatrix = new Matrix();
	}
	
	public Paint getDrawPaint(){
		return mPaint;
	}

	private float mDownX, mDownY;
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		{
			mDownX = event.getX();
			mDownY = event.getY();
			mCurrentMode = MODE_NONE;
			return true;
		}
		case MotionEvent.ACTION_UP:
		{
			if(mCurrentMode == MODE_NONE){
				if(mClickListener != null){
					mClickListener.onClick(this);
				}
			}
			mCurrentMode = MODE_NONE;
			return false;
		}
		case MotionEvent.ACTION_MOVE:
		{
			if((event.getX() - mDownX)*(event.getX() - mDownX) + 
					(event.getY() - mDownY)*(event.getY() - mDownY) > 25){
				mCurrentMode = MODE_DRAW;
			}
			return true;
		}
		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	
	public void onTouch(MotionEvent event) {
//		if(!mEditable)
//			return;
		
		int action = event.getActionMasked();
		switch (action) {
		case MotionEvent.ACTION_DOWN: 
		{
			mDownX = event.getX();
			mDownY = event.getY();
			mCurrentMode = MODE_NONE;
//			onTouchDown(event);
			break;
		}
		case MotionEvent.ACTION_MOVE: 
		{
			if(mCurrentMode == MODE_NONE && (event.getX() - mDownX)*(event.getX() - mDownX) + 
					(event.getY() - mDownY)*(event.getY() - mDownY) > 25){
				if(mEditable)
					onTouchDown(event);
				mCurrentMode = MODE_DRAW;
			}
			if(mCurrentMode == MODE_DRAW){
				if(mEditable)
					onTouchMove(event);
			}
			break;
		}
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL: 
		{
			onTouchUp(event);
			break;
		}
		default:
			break;
		}
	}
	
	private void onTouchDown(MotionEvent event) {
		LayerInfo layer = getLayerInfo();
		
		this.mPreviousX = event.getX() - layer.mMarginLeft;
		this.mPreviousY = event.getY() - layer.mMarginTop;
		if(layer.mPaths == null){
			layer.mPaths = new Stack<PathInfo>();
		}
		mPath = new Path();
		layer.mPaths.add(new PathInfo(mPath));
		mPath.moveTo(mPreviousX, mPreviousY);
	}

	private void onTouchMove(MotionEvent event) {
		LayerInfo layer = getLayerInfo();
		final float x = event.getX() - layer.mMarginLeft;
		final float y = event.getY() - layer.mMarginTop;
		
		final float dx = Math.abs(x - mPreviousX);
		final float dy = Math.abs(y - mPreviousY);
		if (dx >= 3 || dy >= 3) {
			mPath.lineTo(x, y);
			mPreviousX = x;
			mPreviousY = y;
		}
		invalidate();
	}
	
	private void onTouchUp(MotionEvent event){
		if(mCurrentMode == MODE_NONE){
			if(mClickListener != null){
				mClickListener.onClick(this);
			}
		}
		mCurrentMode = MODE_NONE;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		canvas.drawColor(0x3c3c3c3c);
		//绘制层次
//		if(mLayerInfos != null){
//			for (int i = 0; i < mLayerInfos.size(); i++) {
//				drawLayer(canvas, mLayerInfos.get(i), mLayerInfos.get(mLayerInfos.size() - 1), mInitScale);
//			}
//		}
		doDrawImpl(canvas, mLayerInfos, mInitScale);
	}
	
	public void doDrawImpl(Canvas canvas, Stack<LayerInfo> layInfos, float scale){
		if(layInfos != null){
			for (int i = 0; i < layInfos.size(); i++) {
				drawLayer(canvas, layInfos.get(i), layInfos.get(layInfos.size() - 1), scale);
			}
		}
	}
	
	/**
	 * 绘制层次信息
	 * @param canvas
	 * @param layer
	 */
	private void drawLayer(Canvas canvas, LayerInfo layer, LayerInfo topLayer, float scale){
		if(layer == null)
			return;
		
		/*
		 * Path:基于屏幕坐标系 没有任何放大或者缩小处理
		 * topValue:当前的放大比
		 * values:本层放大比
		 * 
		 * 首先把整个view放大到当前屏幕的大小，然后把path缩小到当前大小。
		 * 注意：这里的坐标系和基准点均以原图为基准，实际的path运算为topValue/values
		 */
		float topValues[] = new float[9];
		topLayer.mMatrix.getValues(topValues);
		
		float values[] = new float[9];
		layer.mMatrix.getValues(values);
		
		List<PathInfo> pathInfos = layer.mPaths;
		
		canvas.save();
		canvas.scale(topValues[0] * scale/topLayer.mInitScale, topValues[0] * scale/topLayer.mInitScale, 0, 0);
		if (pathInfos != null) {
			for (int i = 0; i < pathInfos.size(); i++) {
				PathInfo pathInfo = pathInfos.get(i);
				if(!pathInfo.isAvailable)
					continue;
				
				mDrawMatrix.reset();
				mDrawPath.reset();
				mDrawMatrix.postScale(1/values[0], 1/values[0], 0, 0);
				pathInfo.mPath.transform(mDrawMatrix, mDrawPath);
				if (!mDrawPath.isEmpty()) {
					canvas.drawPath(mDrawPath, mPaint);
				}
			}
		}
		canvas.restore();
	}
	
	
	
	/**
	 * 获得当前的层次信息
	 * @return
	 */
	private LayerInfo getLayerInfo(){
		if(mLayerInfos.isEmpty()){
			mLayerInfos.push(new LayerInfo(mInitScale));
		}
		return mLayerInfos.peek();
	}
	
	private float mInitScale = 1.0f;
	public void setInitScale(float initScale){
		this.mInitScale = initScale;
	}
	
	/**
	 * 设置层次信息
	 * @param stack
	 */
	public void setLayerInfos(Stack<LayerInfo> stack){
		this.mLayerInfos = stack;
		postInvalidate();
	}
	
	private OnClickListener mClickListener;
	@Override
	public void setOnClickListener(OnClickListener l) {
//		super.setOnClickListener(l);
		setClickable(true);
		this.mClickListener = l;
	}
	
}
