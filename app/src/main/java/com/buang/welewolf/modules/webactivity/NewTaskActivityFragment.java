package com.buang.welewolf.modules.webactivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.buang.welewolf.modules.base.BaseWebViewFragment;
import com.hyena.framework.utils.UiThreadHandler;

/**
 * Created by weilei on 15/9/1.
 */
public class NewTaskActivityFragment extends BaseWebViewFragment {

    private BaseWebView mWebView;
    private ImageView mCloseView;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_activity_newtask, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mWebView = (BaseWebView) view.findViewById(com.buang.welewolf.R.id.activity_newtask_webview);
        mCloseView = (ImageView) view.findViewById(com.buang.welewolf.R.id.activity_newtask_close);
        mCloseView.setVisibility(View.GONE);
        setWebView(mWebView);

        mCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mWebView.getSettings().setUseWideViewPort(false);
        mWebView.getSettings().setLoadWithOverviewMode(false);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        UiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWebView.setVisibility(View.INVISIBLE);
                int width = mWebView.getWidth();
                float t = ((float) width / 640 * 100);
                int scale = (int) (t);
                if (scale != 0)
                    mWebView.setInitialScale(scale);
                mWebView.loadUrl(getArguments().getString("url"));
            }
        }, 200);

    }

    @Override
    public void onPageLoadFinished() {
        super.onPageLoadFinished();
        mCloseView.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishFragment() {
        super.finishFragment();
        finish();
    }

    @Override
    public void onPageLoadError(WebView view, int errorCode, String description, String failingUrl) {
        super.onPageLoadError(view, errorCode, description, failingUrl);
        finish();
    }

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
        if (mOnFragmentCloseListener != null) {
            mOnFragmentCloseListener.onFragmentClose();
        }
    }

    @Override
    public void openNewFragment(String title, String url) {
        super.openNewFragment(title, url);
        finish();
    }

    public OnFragmentCloseListener mOnFragmentCloseListener;

    public void setOnFragmentCloseListener(OnFragmentCloseListener listener) {
        mOnFragmentCloseListener = listener;
    }

    public interface OnFragmentCloseListener {
        public void onFragmentClose();
    }

}
