package com.buang.welewolf.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by weilei on 16/5/18.
 */
public class BorderScrollView extends ScrollView {

    private View contentView;
    private OnBorderListener onBorderListener;

    public BorderScrollView(Context context) {
        super(context);
    }

    public BorderScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        doOnBorderListener();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    public void setOnBorderListener(OnBorderListener listener) {
        onBorderListener = listener;
    }

    private void doOnBorderListener() {
        if (contentView == null) {
            contentView = getChildAt(0);
        }
        if (contentView != null && contentView.getMeasuredHeight() <= getScrollY() + getHeight()) {
            if (onBorderListener != null) {
                onBorderListener.onBottom();
            }
        } else if (getScrollY() == 0) {
            if (onBorderListener != null) {
                onBorderListener.onTop();
            }
        }
    }

    public interface OnBorderListener {

        /**
         * Called when scroll to bottom
         */
        public void onBottom();

        /**
         * Called when scroll to top
         */
        public void onTop();
    }
}
