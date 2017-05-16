package com.buang.welewolf.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.knowbox.base.utils.UIUtils;

/**
 * @name 自定义控件：动画布局
 * @author Fanjb
 * @date 2015年5月27日
 */
public class OnlineTopLayout extends LinearLayout {
	Scroller mScroller;
	int mScrollY;

	int mFinalY;

	public OnlineTopLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mScroller = new Scroller(getContext());
	}

	public int getFinalY() {
		return mFinalY;
	}

	public void scroll2Bottom() {
		mScroller.abortAnimation();
		mScroller.startScroll(0, getScrollY(), 0, 0 - getScrollY(), 1000);
		mFinalY = 0;
		invalidate();
	}

	public void scroll2Top() {
		int y = UIUtils.dip2px(218);
		mScroller.abortAnimation();
		mFinalY = y;
		mScroller.startScroll(0, getScrollY(), 0, y - getScrollY(), 1000);
		invalidate();
	}

	public boolean hasdScorllFinished() {
		return mScroller.isFinished();
	}

	public void stopAutoScroll() {
		mScroller.abortAnimation();
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasWindowFocus);

	}

	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (!mScroller.isFinished()) {

			if (mScroller.computeScrollOffset()) {
				int oldX = getScrollX();
				int oldY = getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();
				mScrollY = y;

				if (oldX != x || oldY != y) {
					scrollTo(x, y);
				}
				invalidate();
			} else {
				clearChildrenCache();
			}
		} else {
			mFinalY = 0;
			clearChildrenCache();
		}

	}

	void enableChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(true);
		}
	}

	void clearChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(false);
		}
	}
}
