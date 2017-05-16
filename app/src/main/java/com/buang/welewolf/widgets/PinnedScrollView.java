package com.buang.welewolf.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * Created by weilei on 17/2/18.
 */
public class PinnedScrollView extends ScrollView {


    private View mPinnedView;
    private RelativeLayout mPinnedViewParent;
    private View mPinnedViewRefView;
    private int[] mPinnedViewLocation;

    /**
     * @param context
     */
    public PinnedScrollView(Context context) {
        this(context, null);
    }

    /**
     * @param context
     * @param attrs
     */
    public PinnedScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.scrollViewStyle);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public PinnedScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (needComputePinnerLocation()) {
                    computePinnerLocation(mPinnedViewRefView);
                }
                int offY = getScrollY();
                int top = mPinnedViewLocation[0];
                int left = mPinnedViewLocation[1];
                pinned(mPinnedView, left, Math.max(offY, top));
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            View root = getChildAt(0);
            if (!(root instanceof RelativeLayout)) {
                throw new RuntimeException("first layout should be frameLayout.");
            }
            mPinnedViewParent = (RelativeLayout) root;
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (needComputePinnerLocation()) {
            computePinnerLocation(mPinnedViewRefView);
        }
        int top = mPinnedViewLocation[0];
        int left = mPinnedViewLocation[1];

        int offY = t;

        // Log.d(TAG, "scroll y:" + offY);

        if (oldt - t < 0) {
            // scroll up
            if (top <= offY) {
                pinned(mPinnedView, left, Math.max(offY, top));
                mPinnedView.setVisibility(View.VISIBLE);
            }
        } else if (oldt - t > 0) {
            // scroll down
            if (top >= offY) {
                pinned(mPinnedView, left, 0);
                mPinnedView.setVisibility(View.INVISIBLE);
            } else {
                pinned(mPinnedView, left, Math.max(offY, top));
                mPinnedView.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean needComputePinnerLocation() {
        return mPinnedViewRefView != null;
    }

    private void computePinnerLocation(View refView) {
        int top = refView.getTop();
        if (mPinnedViewLocation[0] != top) {
            mPinnedViewLocation[0] = top;
        }
        int left = refView.getLeft();
        if (mPinnedViewLocation[1] != left) {
            mPinnedViewLocation[1] = left;
        }
    }

    /**
     * pinned the view on screen's location.
     *
     * @param view a view to be pinned.
     * @param left left position, relative to parent
     * @param top  top position, relative to parent
     */
    private void pinned(View view, int left, int top) {
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        view.layout(left, top, left + width, top + height);

        // Log.d(TAG, "pinned location top:" + top);
        // Log.d(TAG, "pinned location left:" + left);
    }

    /**
     * get pinned view's parent. (should be first child of scroll view.)
     *
     * @return container
     */
    public RelativeLayout getPinnedViewContainer() {
        return mPinnedViewParent;
    }

    /**
     * attach a view to scroll view(actually, the first child of scroll view).
     * it will be pinned a location when you scroll screen.
     *
     * @param view a view that will be fixed a location.
     * @return this instance
     */
    public PinnedScrollView setPinnedView(View view) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPinnedViewParent.addView(view, params);
        view.setVisibility(View.INVISIBLE);
        mPinnedView = view;
        return this;
    }

    /**
     * mark the location that the view will be pinned, when you scroll the
     * screen.
     *
     * @param left a left position, relative to parent
     * @param top  a top position, relative to parent
     * @return this instance
     */
    public PinnedScrollView markPinnedViewStartLocation(int left, int top) {
        mPinnedViewLocation = new int[2];
        mPinnedViewLocation[0] = top;
        mPinnedViewLocation[1] = left;
        mPinnedViewRefView = null;
        return this;
    }

    /**
     * mark the location use a reference location of specific view.
     *
     * @return this instance
     */
    public PinnedScrollView markPinnedViewStartLocationRef(View view) {
        mPinnedViewRefView = view;
        mPinnedViewLocation = new int[2];
        return this;
    }
}
