/**
 * Copyright (C) 2015 The PathCorrect Project
 */
package com.knowbox.teacher.widgets.correct;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.hyena.framework.clientlog.LogUtil;

import java.util.Stack;

@SuppressLint({ "ClickableViewAccessibility", "FloatMath" })
public class DrawContaienr extends RelativeLayout {

	private static final int MODE_NONE = 0;
	private static final int MODE_MOVE = 1;
	private static final int MODE_DRAW = 2;
	
	private DrawPanel mDrawPanel;
	private ImagePanel mImagePanel;
	
	private Matrix mCurrentMatrix = new Matrix();
    private Matrix mSavedMatrix = new Matrix();
    private PointF mStart = new PointF();
    private PointF mid = new PointF();
    
    private float oldDist = 1f;
	private Bitmap mRawBitmap;
	private Bitmap mMarkBitmap;
	private float mInitX, mInitY;
	private float mInitScale = 1.0f;
	//当前模式
	private int mCurrentMode = MODE_NONE;
	//层次信息
	private Stack<LayerInfo> mLayerInfos;
	
	private boolean mTouchable = true;
	private boolean mEditable = false;
	private OnRevertListener mRevertListener;
	
	public DrawContaienr(Context context) {
		super(context);
		initViews();
	}
	
	public DrawContaienr(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}

	/**
	 * 初始化View
	 */
	@SuppressLint("NewApi")
	private void initViews(){
		if(VERSION.SDK_INT >= 11)
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mDrawPanel = new DrawPanel(getContext());
//		mDrawPanel.setDrawingCacheEnabled(true);
//		mDrawPanel.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		mImagePanel = new ImagePanel(getContext());
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(mImagePanel, params);
		addView(mDrawPanel, params);
		
		mLayerInfos = new Stack<LayerInfo>();
		mDrawPanel.setLayerInfos(mLayerInfos);
		mDrawPanel.setEditable(mEditable);
	}
	
	/**
	 * 是否接受触摸事件
	 * @param touchable
	 */
	public void setTouchable(boolean touchable){
		this.mTouchable = touchable;
	}
	
	/**
	 * 是否可以编辑
	 * @param editable
	 */
	public void setEditable(boolean editable){
		this.mEditable = editable;
		mDrawPanel.setEditable(editable);
	}
	
	/**
	 * 是否可以编辑
	 * @return
	 */
	public boolean editable(){
		return mEditable;
	}
	
	public void setLayerStack(Stack<LayerInfo> layerStack){
		this.mLayerInfos = layerStack;
		initScale();
		rebuildDrawView();
		mDrawPanel.setLayerInfos(mLayerInfos);
		if(mRevertListener != null){
			mRevertListener.onMarkedChange();
		}
	}
	
	/**
	 * 是否正在绘画
	 * @return
	 */
	public boolean isDrawing(){
		return mCurrentMode == MODE_DRAW;
	}
	
	public Bitmap getDrawingBitmap(){
		try {
			if(mRawBitmap == null)
				return null;
			
			Bitmap bitmap = Bitmap.createBitmap(mRawBitmap.getWidth(), mRawBitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			Rect dst = new Rect(0, 0, mRawBitmap.getWidth(), mRawBitmap.getHeight());
			if(mMarkBitmap != null)
				canvas.drawBitmap(mMarkBitmap, null, dst, null);
//			canvas.drawBitmap(mDrawPanel.getDrawingCache(), null, dst, null);
			mDrawPanel.doDrawImpl(canvas, mLayerInfos, 1);
			return bitmap;
		} catch (Throwable e) {
			LogUtil.e("", e);
		}
		return null;
	}
	
	/**
	 * 设置原始图
	 * @param bitmap
	 */
	public void setRawBitmap(Bitmap bitmap){		
		this.mRawBitmap = bitmap;
		mCurrentMatrix.reset();
		mSavedMatrix.reset();
		initScale();
		if(mImagePanel != null)
			mImagePanel.setRawBitmap(bitmap);
		//重新构造绘制图层
		rebuildDrawView();
	}
	
	/**
	 * 设置标记过的Bitmap
	 * @param bitmap
	 */
	public void setMarkBitmap(Bitmap bitmap){
		this.mMarkBitmap = bitmap;
		if(mImagePanel != null)
			mImagePanel.setMarkBitmap(bitmap);
		if(mRevertListener != null){
			mRevertListener.onMarkedChange();
		}
	}
	
	public Bitmap getMarkBitmap(){
		return mMarkBitmap;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = ev.getActionMasked();
		if(!mTouchable){
			return super.onInterceptTouchEvent(ev);
		}
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		{
            mCurrentMode = MODE_DRAW;
			break;
		}
		case MotionEvent.ACTION_UP:
		{
			mCurrentMode = MODE_NONE;
			break;
		}
		case MotionEvent.ACTION_POINTER_DOWN:
		{
			oldDist = spacing(ev);
            if (oldDist > 10f) {
                mSavedMatrix.set(mCurrentMatrix);
                midPoint(mid, ev);
                mCurrentMode = MODE_MOVE;
            }
			break;
		}
		case MotionEvent.ACTION_POINTER_UP:
		{
			mCurrentMode = MODE_DRAW;
			break;
		}
		default:
			break;
		}
		return mCurrentMode != MODE_NONE;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!mTouchable || mRawBitmap == null) {
			return super.onTouchEvent(event);
		}
		int action = event.getActionMasked();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		{
			mCurrentMode = MODE_DRAW;
			mSavedMatrix.set(mCurrentMatrix);
            mStart.set(event.getX(), event.getY());
            createLayer();
			rebuildDrawView();
            if(mDrawPanel != null){
            	mDrawPanel.onTouch(event);
            }
			break;
		}
		case MotionEvent.ACTION_UP:
		{
			mCurrentMode = MODE_NONE;
			actionUp();
			mDrawPanel.onTouch(event);
			checkLayer();
			
			createLayer();
			break;
		}
		case MotionEvent.ACTION_POINTER_UP:
		{
			mCurrentMode = MODE_NONE;
			mSavedMatrix.set(mCurrentMatrix);
			//重新构造绘图View
			rebuildDrawView();
//			saveLayer();
			break;
		}
		case MotionEvent.ACTION_POINTER_DOWN:
		{
			oldDist = spacing(event);
            if (oldDist > 10f) {
                mSavedMatrix.set(mCurrentMatrix);
                midPoint(mid, event);
                mCurrentMode = MODE_MOVE;
            }
			break;
		}
		case MotionEvent.ACTION_MOVE:
		{
            float dx = event.getX() - mStart.x;
            float dy = event.getY() - mStart.y;
            if(mCurrentMode == MODE_NONE)
            	return false;
            
			if(mCurrentMode == MODE_MOVE || !mEditable){
				//dx>0向右滑动
				if(dx > 0){
					if(isTouchRight()){
						getParent().requestDisallowInterceptTouchEvent(true);
					}else{
						getParent().requestDisallowInterceptTouchEvent(false);
					}
				}else{
					if(isTouchLeft()){
						getParent().requestDisallowInterceptTouchEvent(true);
					}else{
						getParent().requestDisallowInterceptTouchEvent(false);
					}
				}
				
				//拖动模式
				mCurrentMatrix.set(mSavedMatrix);
				mCurrentMatrix.postTranslate(dx, dy);
				float newDist = spacing(event);
                if (newDist > 10f) {
                    float scale = (newDist / oldDist);
                    mCurrentMatrix.postScale(scale, scale, mid.x, mid.y);
                }
                
                /**
                 * 显示绘制范围
                 */
                float scale = 1;
        		float tranx = 0;
        		float trany = 0;
        		if(mCurrentMatrix != null){
        			float values[] = new float[9];
        			mCurrentMatrix.getValues(values);
        			scale = values[Matrix.MSCALE_X];
        			tranx = values[Matrix.MTRANS_X];
        			trany = values[Matrix.MTRANS_Y];
        		}
        		int width = (int) (mRawBitmap.getWidth() * mInitScale * scale);
        		int height = (int) (mRawBitmap.getHeight() * mInitScale * scale);
                if (tranx < -(width - getWidth())) {
                	mCurrentMatrix.postTranslate(-(tranx + width - getWidth()), 0);
                }
                if (tranx > 0) {
                	mCurrentMatrix.postTranslate(-tranx, 0);
                }
                if (trany < -(height - getHeight())) {
                	mCurrentMatrix.postTranslate(0, -(trany + height - getHeight()));
                }
                if (trany > 0) {
                	mCurrentMatrix.postTranslate(0, -trany);
                }
                
                if (width <= getWidth() || height <= getHeight()) {
                	mCurrentMatrix.reset();
                	mSavedMatrix.reset();
                }
                
                if(!mEditable){
                	mDrawPanel.onTouch(event);
                }
                
                //重新构造绘图范围
                rebuildDrawView();
			}else if(mCurrentMode == MODE_DRAW){
				//绘图模式
            	mDrawPanel.onTouch(event);
			}
			break;
		}
		default:
			break;
		}
		return mCurrentMode != MODE_NONE;
	}
	
	private void actionUp(){
		if(mRawBitmap == null)
			return;
		LogUtil.v("yangzc", "actionUp");
		float scale = 1;
		float tranx = 0;
		float trany = 0;
		if(mCurrentMatrix != null){
			float values[] = new float[9];
			mCurrentMatrix.getValues(values);
			scale = values[Matrix.MSCALE_X];
			tranx = values[Matrix.MTRANS_X];
			trany = values[Matrix.MTRANS_Y];
		}
		Log.v("yangzc", "scale: " + scale + ", tranx: " + tranx + ", trany: " + trany);
//		int width = (int) (mRawBitmap.getWidth() * mInitScale * scale);
//		int height = (int) (mRawBitmap.getHeight() * mInitScale * scale);
		if(scale < 1){
			mCurrentMatrix.reset();
			mSavedMatrix.reset();
		}
		rebuildDrawView();
	}
	
	/**
	 * 重新构造绘图View
	 */
	private void rebuildDrawView(){
		if(mRawBitmap == null)
			return;
		
		float scale = 1;
		float tranx = 0;
		float trany = 0;
		if(mCurrentMatrix != null){
			float values[] = new float[9];
			mCurrentMatrix.getValues(values);
			scale = values[Matrix.MSCALE_X];
			tranx = values[Matrix.MTRANS_X];
			trany = values[Matrix.MTRANS_Y];
		}
		Log.v("yangzc", "scale: " + scale + ", tranx: " + tranx + ", trany: " + trany);
		int width = (int) (mRawBitmap.getWidth() * mInitScale * scale);
		int height = (int) (mRawBitmap.getHeight() * mInitScale * scale);
		
		RelativeLayout.LayoutParams params = (LayoutParams) mDrawPanel.getLayoutParams();
		params.leftMargin = (int) (mInitX + tranx);
		params.topMargin = (int) (mInitY + trany);
		params.width = width;
		params.height = height;
		mImagePanel.updateSize(width, height);
		mDrawPanel.setLayoutParams(params);
		mImagePanel.setLayoutParams(params);
		
		LayerInfo layer = getLayerInfo();
		layer.mMarginLeft = params.leftMargin;
		layer.mMarginTop = params.topMargin;
		layer.setMatrix(mCurrentMatrix);
		
		mDrawPanel.postInvalidate();
	}
	
	public boolean isTouchLeft(){
		RelativeLayout.LayoutParams params = (LayoutParams) mDrawPanel.getLayoutParams();
		if(params.leftMargin >= 0){
			return true;
		}
		return false;
	}
	
	public boolean isTouchRight(){
		RelativeLayout.LayoutParams params = (LayoutParams) mDrawPanel.getLayoutParams();
		if(params.width + params.leftMargin < getWidth()){
			return true;
		}
		return false;
	}
	
	/**
	 * 创建层信息
	 */
	private void createLayer(){
		if(mLayerInfos != null && !mLayerInfos.isEmpty()){
			LayerInfo layerInfo = mLayerInfos.peek();
			if(layerInfo.mPaths == null || layerInfo.mPaths.isEmpty()){
				//删除层
				mLayerInfos.pop();
			}
		}
		LayerInfo newLayer = new LayerInfo(mInitScale);
		newLayer.setMatrix(mCurrentMatrix);
		mLayerInfos.push(newLayer);
	}
	
	/**
	 * 获得当前的层次信息
	 * @return
	 */
	private LayerInfo getLayerInfo(){
		if(mLayerInfos.isEmpty()){
			mLayerInfos.push(new LayerInfo(mCurrentMatrix, mInitScale));
		}
		return mLayerInfos.peek();
	}
	
	/**
	 * 回退
	 */
	public void revert(){
		if(mLayerInfos != null && !mLayerInfos.isEmpty()){
			mark:for (int i = mLayerInfos.size() - 1; i >= 0; i--) {
				LayerInfo layerInfo = mLayerInfos.get(i);
				for (int j = layerInfo.mPaths.size() - 1; j >= 0; j--) {
					PathInfo pathInfo = layerInfo.mPaths.get(j);
					if(pathInfo.isAvailable){
						pathInfo.isAvailable = false;
						break mark;
					}
				}
			}
			rebuildDrawView();
		}
		if(mRevertListener != null){
			mRevertListener.onMarkedChange();
		}
	}
	
	/**
	 * 前进
	 */
	public void forward(){
		if(mLayerInfos != null && !mLayerInfos.isEmpty()){
			mark:for (int i = 0; i < mLayerInfos.size(); i++) {
				LayerInfo layerInfo = mLayerInfos.get(i);
				for (int j = 0; j < layerInfo.mPaths.size(); j++) {
					PathInfo pathInfo = layerInfo.mPaths.get(j);
					if(!pathInfo.isAvailable){
						pathInfo.isAvailable = true;
						break mark;
					}
				}
			}
			rebuildDrawView();
		}
		if(mRevertListener != null){
			mRevertListener.onMarkedChange();
		}
	}
	
	/**
	 * 清空
	 */
	public void clear(){
		if(mLayerInfos != null && !mLayerInfos.isEmpty()){
			for (int i = mLayerInfos.size() - 1; i >= 0; i--) {
				LayerInfo layerInfo = mLayerInfos.get(i);
				for (int j = layerInfo.mPaths.size() - 1; j >= 0; j--) {
					PathInfo pathInfo = layerInfo.mPaths.get(j);
					if(pathInfo.isAvailable){
						pathInfo.isAvailable = false;
					}
				}
			}
			rebuildDrawView();
		}
		setMarkBitmap(null);
	}
	
	public void release() {
		if(mRawBitmap != null && !mRawBitmap.isRecycled()) {
			mRawBitmap = null;
		}
		if(mMarkBitmap != null && !mMarkBitmap.isRecycled()) {
			mMarkBitmap = null;
		}
		
		if(mDrawPanel != null) {
			mDrawPanel.setLayerInfos(null);
			mDrawPanel = null;
		}
		
		if(mImagePanel != null) {
			mImagePanel.setRawBitmap(null);
			mImagePanel.setMarkBitmap(null);
			mImagePanel = null;
		}
		
	}
	
	/**
	 * 是否可以前进
	 * @return
	 */
	public boolean isForward(){
		if(mLayerInfos != null && !mLayerInfos.isEmpty()){
			for (int i = mLayerInfos.size() - 1; i >= 0; i--) {
				LayerInfo layerInfo = mLayerInfos.get(i);
				for (int j = layerInfo.mPaths.size() - 1; j >= 0; j--) {
					PathInfo pathInfo = layerInfo.mPaths.get(j);
					if(!pathInfo.isAvailable){
						return true;
					}
				}
			}
		}
		return false;
	
	}
	
	/**
	 * 是否是空
	 * @return
	 */
	public boolean isEmpty(){
		if(mLayerInfos != null && !mLayerInfos.isEmpty()){
			for (int i = mLayerInfos.size() - 1; i >= 0; i--) {
				LayerInfo layerInfo = mLayerInfos.get(i);
				for (int j = layerInfo.mPaths.size() - 1; j >= 0; j--) {
					PathInfo pathInfo = layerInfo.mPaths.get(j);
					if(pathInfo.isAvailable){
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 检查图层
	 */
	public void checkLayer(){
		if(mLayerInfos != null && !mLayerInfos.isEmpty()){
			LayerInfo layerInfo = mLayerInfos.peek();
			if(layerInfo.mPaths == null || layerInfo.mPaths.isEmpty()){
				//删除层
//				mLayerInfos.pop();
			}else{
				if(mRevertListener != null && !mLayerInfos.isEmpty()){
					mRevertListener.onMarkedChange();
				}
			}
		}
	}
	
	/**
	 * 初始化缩放比
	 */
	private void initScale(){
		if(mRawBitmap == null)
			return;
		
		int dwidth = mRawBitmap.getWidth();
		int dheight = mRawBitmap.getHeight();
		int vwidth = getMeasuredWidth();
		int vheight = getMeasuredHeight();
		float scale = 1;
		if(vwidth > 0 && vheight > 0){
			if (dwidth * vheight < vwidth * dheight) {
	            scale = (float) vheight / (float) dheight; 
	        } else {
	            scale = (float) vwidth / (float) dwidth;
	        }
		}
		this.mInitScale = scale;
		this.mInitX = ((getWidth() - mRawBitmap.getWidth() * scale)/2);
		this.mInitY = ((getHeight() - mRawBitmap.getHeight() * scale)/2);
		if(mDrawPanel != null)
			mDrawPanel.setInitScale(mInitScale);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if(w != oldw && h != oldh){
			setRawBitmap(mRawBitmap);
		}
	}
	
	private float spacing(MotionEvent event) {
		if(event.getPointerCount() == 1)
			return -1;
		
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
	
	private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
	
	public static class LayerInfo {
		
		public Matrix mMatrix = new Matrix();
		public Stack<PathInfo> mPaths = new Stack<PathInfo>();
		public int mMarginLeft, mMarginTop;
		public float mInitScale = 1.0f;
		
		public LayerInfo(float initScale){
			this.mInitScale = initScale;
		}
		public LayerInfo(Matrix matrix, float initScale){
			setMatrix(matrix);
			this.mInitScale = initScale;
		}
		
		public void setMatrix(Matrix matrix){
			mMatrix.set(matrix);
		}
	}
	
	public static class PathInfo {
		
		public PathInfo(){}
		public PathInfo(Path path){
			this.mPath = path;
		}
		
		public boolean isAvailable = true;
		public Path mPath = new Path();;
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {
//		super.setOnClickListener(l);
		mDrawPanel.setOnClickListener(l);
	}
	
	/**
	 * 设置后退监听器
	 * @param listener
	 */
	public void setRevertListener(OnRevertListener listener){
		this.mRevertListener = listener;
	}
	
	public static interface OnRevertListener{
		
		/**
		 * 发生批改
		 */
		public void onMarkedChange();
	}
}
