package com.buang.welewolf.modules.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.webactivity.BaseWebView;

/**
 * Created by weilei on 15/8/31.
 */
public class BaseWebViewFragment extends BaseUIFragment<UIFragmentHelper>{
    private BaseWebView mWebView;
    public boolean isPageError;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        isPageError = false;
    }

    public void setWebView(BaseWebView webview) {
        mWebView = webview;
        mWebView.setFragment(this);
        mWebView.setOnWebViewListener(new BaseWebView.OnWebViewListener() {


            @Override
            public void onPageStart(WebView view, String url, Bitmap favicon) {
            	if (isPageError) {
            		return;
            	}
                onPageLoadStart();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            	if (isPageError) {
            		return;
            	}
                onPageLoadFinished();
            }

            @Override
            public void openFragment(String title, String url) {
                openNewFragment(title, url);
            }

            @Override
            public void onPageError(WebView view, int errorCode, String description, String failingUrl) {
                getUIFragmentHelper().getEmptyView().showEmpty("加载页面失败");
                onPageLoadError(view, errorCode, description, failingUrl);
                isPageError = true;
            }

            @Override
            public void onFinishFragment() {
                finishFragment();
            }

            @Override
            public void onPageProgress(WebView view, int newProgress) {
                onPageProgressChanged(view, newProgress);
            }

            @Override
            public void onShowRightMenu(String title, final String jsCallBack) {
                showRightMenu(title, jsCallBack);
            }

            @Override
            public void onReceiveTitle(WebView webView, String title) {
                if (!TextUtils.isEmpty(title)) {
                    setTitle(title);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView != null && mWebView.mJsHandleBack) {
            mWebView.runJs("onBackPressed");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setTitle(String title) {

    }

    /**
     * 打开新的页面
     * @param title
     * @param url
     */
    public void openNewFragment(String title, String url){

    }

    public void onPageLoadStart() {

    }

    public void onPageLoadFinished() {
    	
    }

    public void finishFragment() {

    }

    public void onPageLoadError(WebView view, int errorCode, String description, String failingUrl) {
    	
    }

    public void onPageProgressChanged(WebView view, int newProgress){};

    public void showRightMenu(String txt, String jsCallBack){};

}
