/**
 * Copyright (C) 2015 The KnowBoxTeacher2.0 Project
 */
package com.knowbox.teacher.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 是否可以滑动的ViewPager
 * @author yangzc
 */
public class TouchableViewPager extends ViewPager {

	public TouchableViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		return false;
	}
}
