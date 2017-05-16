/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.buang.welewolf.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.hyena.framework.utils.UiThreadHandler;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 页面指示器
 * @author yangzc
 */
public class PagerIndicator extends HorizontalScrollView {

	private LinearLayout mTabLayout;
	private Runnable mTabSelector;
	
	public static final int ALIGN_LEFT = 1;
	public static final int ALIGN_CENTER = 2;
	private int mAlign = ALIGN_CENTER;
	private int mSelected = 0;

	public PagerIndicator(Context context) {
		this(context, null);
	}

	public PagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFillViewport(false);
		setHorizontalScrollBarEnabled(false);
		
		mTabLayout = new LinearLayout(context);
		addView(mTabLayout, new ViewGroup.LayoutParams(MATCH_PARENT,
				MATCH_PARENT));
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		boolean ret = super.onTouchEvent(ev);
		if(mIndicatorListener != null){
			int action = ev.getAction();
			if(action == MotionEvent.ACTION_UP
					|| action == MotionEvent.ACTION_CANCEL){
				UiThreadHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						int scrollX = getScrollX();
						if(mWidth > 0) {
							int position = (scrollX + mWidth/2)/mWidth;
							mIndicatorListener.onPageIndicatorChange(position);
						}
					}
				}, 100);
			}
		}
		return ret;
	}
	
	private int mWidth =  0;
	public void addTab(int index, int width, View tabView) {
		int padding = 0;
		this.mWidth = width;
		if(index == 0 && mAlign == ALIGN_CENTER) {
			padding = (getResources().getDisplayMetrics().widthPixels - width)/2;
			View view = new View(getContext());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(padding,
					MATCH_PARENT);
			mTabLayout.addView(view, params);
		}
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,
				LinearLayout.LayoutParams.MATCH_PARENT);
		mTabLayout.addView(tabView, params);
	}
	
	private boolean mIsShouldLayout = false;
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(mIsShouldLayout)
			super.onLayout(changed, l, t, r, b);
	}
	
	/**
	 * 设置对齐方式
	 * @param align
	 */
	public void setAlign(int align){
		this.mAlign = align;
	}
	
	/**
	 * 标记添加完毕
	 * @param width
	 */
	public void notifyAddFinish(int width){
		if(mAlign == ALIGN_CENTER){
			int padding = (getResources().getDisplayMetrics().widthPixels - width)/2;
			mTabLayout.addView(new View(getContext()), new LinearLayout.LayoutParams(padding,
					MATCH_PARENT));
		}
		mIsShouldLayout = true;
		requestLayout();
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mTabSelector != null) {
			post(mTabSelector);
		}
	}
	
	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mTabSelector != null) {
			removeCallbacks(mTabSelector);
		}
	}

	
	public void setCurrentItem(int item) {
        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);
            boolean isSelected = false;
            if(mAlign == ALIGN_CENTER){
            	isSelected = (i == (item + 1));
            }else{
            	isSelected = (i == item);
            }
            child.setSelected(isSelected);
            if (isSelected) {
            	mSelected = i;
                animateToTab(i);
            }
        }
    }
	
	/**
	 * 获得选中的Item
	 * @return
	 */
	public int getSelectedIndex(){
		return mSelected;
	}
	
	public LinearLayout getTabLayout(){
		return mTabLayout;
	}
	
	private void animateToTab(final int position) {
		final View tabView = mTabLayout.getChildAt(position);
		if (mTabSelector != null) {
			removeCallbacks(mTabSelector);
		}
		mTabSelector = new Runnable() {
			public void run() {
				final int scrollPos = tabView.getLeft()
						- (getWidth() - tabView.getWidth()) / 2;
				smoothScrollTo(scrollPos, 0);
//				scrollTo(scrollPos, 0);
				tabView.setSelected(true);
				mTabSelector = null;
			}
		};
		postDelayed(mTabSelector, 200);
	}
	
	public static interface PageIndicatorListener {
		public void onPageIndicatorChange(int position);
	}
	private PageIndicatorListener mIndicatorListener;
	public void setPageIndicatorListener(PageIndicatorListener listener){
		this.mIndicatorListener = listener;
	}
}
