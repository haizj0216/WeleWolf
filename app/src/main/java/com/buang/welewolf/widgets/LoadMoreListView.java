/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.buang.welewolf.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.buang.welewolf.widgets.pulltorefresh.PullToRefreshBase;
import com.hyena.framework.utils.UIUtils;

/**
 * 获取更多列表
 * @author yangzc
 */
public class LoadMoreListView extends ListView implements OnScrollListener {

	private boolean mLastItemVisible;
	private OnScrollListener mOnScrollListener;
	private PullToRefreshBase.OnLastItemVisibleListener mOnLastItemVisibleListener;

	private View mFootView;
	private View mLoadingView;
	private TextView mMakeHomeworkBtn;

	public LoadMoreListView(Context context) {
		super(context);
		super.setOnScrollListener(this);
	}

	public LoadMoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setOnScrollListener(this);
		initFooter();
	}
	
	public void initFooter(){
		mFootView = View.inflate(getContext(), com.buang.welewolf.R.layout.xlistview_footer, null);
		mLoadingView = mFootView.findViewById(com.buang.welewolf.R.id.xlistview_footer_loading);
		mMakeHomeworkBtn = (TextView) mFootView.findViewById(com.buang.welewolf.R.id.make_homework_btn);
		addFooterView(mFootView);
		setFootVisible(false);
	}

	@Override
	public final void onScroll(final AbsListView view,
			final int firstVisibleItem, final int visibleItemCount,
			final int totalItemCount) {
		if (null != mOnLastItemVisibleListener) {
			mLastItemVisible = (totalItemCount > 0)
					&& (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
		}
		if (null != mOnScrollListener) {
			mOnScrollListener.onScroll(view, firstVisibleItem,
					visibleItemCount, totalItemCount);
		}
	}
	
	public void setFootVisible(boolean isVisible){
		mFootView.setVisibility(isVisible ? View.VISIBLE: View.GONE);
		if(isVisible){
			mFootView.setPadding(0, UIUtils.dip2px(10), 0, UIUtils.dip2px(10));
		}else{
			mFootView.setPadding(0, -mFootView.getHeight(), 0, 0);
		}
	}

	public void setLoadingFootVisible(boolean isVisible) {
		mFootView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
		if (isVisible) {
			mFootView.setPadding(0, 0, 0, UIUtils.dip2px(10));
		} else {
			mFootView.setPadding(0, -mFootView.getHeight() - com.hyena.framework.utils.UIUtils.dip2px(10), 0, 0);
			mOnLastItemVisibleListener = null;
		}
	}

	public void setMakeWorkViewVisible(boolean isVisible) {
		mMakeHomeworkBtn.setVisibility(isVisible ? View.VISIBLE: View.GONE);
		mLoadingView.setVisibility(isVisible ? View.GONE: View.VISIBLE);
	}
	
	@Override
	public void onScrollStateChanged(final AbsListView view,
			final int state) {
		if (state == OnScrollListener.SCROLL_STATE_IDLE
				&& null != mOnLastItemVisibleListener && mLastItemVisible) {
			mOnLastItemVisibleListener.onLastItemVisible();
		}

		if (null != mOnScrollListener) {
			mOnScrollListener.onScrollStateChanged(view, state);
		}
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
	}

	public final void setOnLastItemVisibleListener(
			PullToRefreshBase.OnLastItemVisibleListener listener) {
		mOnLastItemVisibleListener = listener;
	}

	public final void setOnScrollListener(OnScrollListener listener) {
		mOnScrollListener = listener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
	}

}
