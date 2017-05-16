package com.buang.welewolf.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @name 禁止滑动的ViewPager
 * @author Fanjb
 * @date 2015-3-13
 */
public class ForbidSlideViewPager extends ViewPager {

	private boolean mScrollable = true;

	public ForbidSlideViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScrollable(boolean isCanScroll) {
		this.mScrollable = isCanScroll;
	}

	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {
		if (!mScrollable) {
			return false;
		} else {
			return super.onTouchEvent(motionEvent);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!mScrollable) {
			return false;
		} else {
			return super.onInterceptTouchEvent(ev);
		}
	}
}
