package com.buang.welewolf.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @name 可调整大小并支持回调的线性布局 <br>
 *       主要用于弹出输入法软键盘时调整布局中的一些控件
 * @author Fanjb
 * @date 2015-3-25
 */
public class ResizeLayout extends LinearLayout {
	private OnResizeListener mListener;

	public void setOnResizeListener(OnResizeListener l) {
		mListener = l;
	}

	public ResizeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mListener != null) {
			mListener.OnResize(w, h, oldw, oldh);
		}
	}

	public interface OnResizeListener {
		void OnResize(int w, int h, int oldw, int oldh);
	}

}
