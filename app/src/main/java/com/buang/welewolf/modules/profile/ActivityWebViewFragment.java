package com.buang.welewolf.modules.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.buang.welewolf.modules.base.BaseWebViewFragment;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.utils.UiThreadHandler;
import com.buang.welewolf.modules.webactivity.BaseWebView;

/**
 * Created by weilei on 15/7/27.
 */
public class ActivityWebViewFragment extends BaseWebViewFragment {

	private BaseWebView mWebView;
	private ImageView mBackView;
	private TextView mCloseView;
	private TextView mTitleView;
	private ImageView mMenuView;
	private TextView mRightView;
	private int menuId;
	private View.OnClickListener mMenuClick;
	private ProgressBar mProgress;
	private RelativeLayout mLayout;

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		setSlideable(true);
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		getUIFragmentHelper().getEmptyView().setEmptyMargin(50);
		View view = View.inflate(getActivity(),
				com.buang.welewolf.R.layout.layout_activity_webview, null);
		return view;
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		mLayout = (RelativeLayout) view.findViewById(com.buang.welewolf.R.id.activity_webview_layout);
		mProgress = (ProgressBar) view.findViewById(com.buang.welewolf.R.id.activity_webview_progress);
		mWebView = (BaseWebView) view.findViewById(com.buang.welewolf.R.id.activity_webview);
		setWebView(mWebView);
		if (!NetworkProvider.getNetworkProvider().getNetworkSensor()
				.isNetworkAvailable()) {
			getUIFragmentHelper().getEmptyView().showNoNetwork();
		}else{
			mWebView.loadUrl(getArguments().getString("url"));
		}

		mBackView = (ImageView) view.findViewById(com.buang.welewolf.R.id.activity_title_bar_back);
		mCloseView = (TextView) view.findViewById(com.buang.welewolf.R.id.activity_title_close);
		mRightView = (TextView) view.findViewById(com.buang.welewolf.R.id.activity_right_txt);
		mTitleView = (TextView) view
		.findViewById(com.buang.welewolf.R.id.activity_title_bar_title);
		mMenuView = (ImageView) view.findViewById(com.buang.welewolf.R.id.activity_menu);
		if (getArguments() != null && getArguments().containsKey("title"))
		mTitleView.setText(getArguments().getString("title"));
		else
		mTitleView.setText("活动");
		mBackView.setOnClickListener(mOnClickListener);
		mCloseView.setOnClickListener(mOnClickListener);
		updateMenuView();
	}

	View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
				case com.buang.welewolf.R.id.activity_title_bar_back:
					if (mWebView != null && mWebView.canGoBack()) {
						mWebView.goBack();
					} else {
						finish();
					}
					break;
				case com.buang.welewolf.R.id.activity_title_close:
					finish();
					break;
			}
		}
	};

	@Override
	public void onPageLoadError(WebView view, int errorCode,
			String description, String failingUrl) {
		super.onPageLoadError(view, errorCode, description, failingUrl);
		if (NetworkProvider.getNetworkProvider().getNetworkSensor()
				.isNetworkAvailable()) {
			getUIFragmentHelper().getEmptyView().showEmpty(
					com.buang.welewolf.R.drawable.icon_webview_empty, "点击重新加载",
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							mWebView.reload();
							showContent();
							isPageError = false;
						}
					});
		}

	}

	public void setTitle(String title) {
		super.setTitle(title);
		if (mTitleView != null) {
			mTitleView.setText(title);
		}
	}

	public void setMenuClick(int id, View.OnClickListener listener) {
		menuId = id;
		mMenuClick = listener;
	}

	public void updateMenuView() {
		if (mMenuView == null)
			return;
		if (menuId == 0 || mMenuClick == null) {
			mMenuView.setVisibility(View.GONE);
			return;
		}
		mMenuView.setVisibility(View.VISIBLE);
		mMenuView.setImageResource(menuId);
		mMenuView.setOnClickListener(mMenuClick);
	}

	private void updateCloseView() {
		if (mWebView.canGoBack()) {
			mCloseView.setVisibility(View.VISIBLE);
		} else {
			mCloseView.setVisibility(View.GONE);
		}
	}

	@Override
	public void setVisibleToUser(boolean visible) {
		super.setVisibleToUser(visible);
		if (visible) {
			if (mWebView != null) {
				mWebView.reload();
				isPageError = false;
			}
		}
	}

	@Override
	public void onPageProgressChanged(WebView view, final int newProgress) {
		super.onPageProgressChanged(view, newProgress);
		UiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				if (newProgress <= 100)
					mProgress.setProgress(newProgress);
			}
		});
	}

	@Override
	public void onPageLoadFinished() {
		super.onPageLoadFinished();
		updateCloseView();
		if (mProgress != null)
			mProgress.setVisibility(View.GONE);
	}

	@Override
	public void onPageLoadStart() {
		super.onPageLoadStart();
		if (mProgress != null)
			mProgress.setVisibility(View.VISIBLE);
	}

	@Override
	public void showRightMenu(String txt, final String jsCallBack) {
		super.showRightMenu(txt, jsCallBack);
		if (!TextUtils.isEmpty(txt)) {
			mRightView.setVisibility(View.VISIBLE);
			mRightView.setText(txt);
			mRightView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mWebView != null) {
						mWebView.runJs(jsCallBack);
					}
				}
			});
		}else {
			mRightView.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView != null && mWebView.canGoBack()) {
				mWebView.goBack();
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onDestroyImpl() {
		super.onDestroyImpl();
		if (mLayout != null && mWebView != null) {
			mLayout.removeView(mWebView);
			mWebView.destroy();
			mWebView = null;
		}
	}
}
