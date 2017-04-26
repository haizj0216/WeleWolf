package com.knowbox.teacher.widgets;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * @name 自定义控件：实现延迟滚动效果
 * @author Fanjb
 * @date 2015年5月27日
 */
public class CustomScrollView extends ScrollView {

	private int lastScrollY;

	public CustomScrollView(Context context) {
		this(context, null);
	}

	public CustomScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// 当用户的手在CustomScrollView上面的时候，直接将CustomScrollView滑动的Y方向距离回调给onScroll方法中，当用户抬起手的时候，
		// CustomScrollView可能还在滑动，所以当用户抬起手我们隔5毫秒给handler发送消息，在handler处理CustomScrollView滑动的距离

		if (onScrollListener != null) {
			onScrollListener.onScroll(lastScrollY = this.getScrollY());
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_UP:
			handler.sendMessageDelayed(handler.obtainMessage(), 5);
			break;
		}
		return super.onTouchEvent(ev);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int scrollY = CustomScrollView.this.getScrollY();
			// 此时的距离和记录下的距离不相等，在隔5毫秒给handler发送消息
			if (lastScrollY != scrollY) {
				lastScrollY = scrollY;
				handler.sendMessageDelayed(handler.obtainMessage(), 5);
			}
			if (onScrollListener != null) {
				onScrollListener.onScroll(scrollY);
			}
		};
	};

	private OnCustomScrollListener onScrollListener;

	public void setOnScrollListener(OnCustomScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	public interface OnCustomScrollListener {
		public void onScroll(int scrollY);
	}

}