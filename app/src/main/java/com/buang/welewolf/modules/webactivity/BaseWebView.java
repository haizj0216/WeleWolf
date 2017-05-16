package com.buang.welewolf.modules.webactivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.buang.welewolf.modules.classes.AddGradeClassFragment;
import com.buang.welewolf.modules.profile.ActivityWebViewFragment;
import com.buang.welewolf.modules.profile.UserAuthNotyetFragment;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.service.share.ShareContent;
import com.knowbox.base.service.share.ShareListener;
import com.knowbox.base.service.share.ShareService;
import com.buang.welewolf.modules.message.utils.MessagePushUtils;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.ToastUtils;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Hashtable;

import cn.sharesdk.framework.Platform;

/**
 * 活动webview
 * Created by weilei on 15/9/9.
 */
public class BaseWebView extends WebView{

    public boolean mShowLoadingWhenLoadPage = true;
    public boolean mJsHandleBack = false;
    private ShareService mShareService;
    private ClipboardManager mClipboardManager;
    private Dialog mCurrentDialog;

    private BaseUIFragment mFragment;

    public BaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mShareService = (ShareService) context.getSystemService(ShareService.SERVICE_NAME);
        mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    public void setFragment(BaseUIFragment fragment) {
        mFragment = fragment;
    }

    public void initView() {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setUseWideViewPort(true);
        getSettings().setAllowFileAccess(true);
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setDomStorageEnabled(true);
        getSettings().setAppCacheEnabled(true);
        getSettings().setDatabaseEnabled(true);
        getSettings().setUserAgentString(ConstantsUtils.WEB_USER_AGENT);
        setWebViewClient(mWebViewClient);
        setWebChromeClient(webChromeClient);
    }

    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (mOnWebViewListener != null) {
                mOnWebViewListener.onPageStart(view, url, favicon);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mOnWebViewListener != null) {
                mOnWebViewListener.onPageFinished(view, url);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("hybird://method/")) {
                handleUrlLoading(url);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (mOnWebViewListener != null) {
                mOnWebViewListener.onPageError(view, errorCode, description, failingUrl);
            }
        }

    };

    private WebChromeClient webChromeClient = new WebChromeClient(){

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (mOnWebViewListener != null) {
                mOnWebViewListener.onPageProgress(view, newProgress);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (mOnWebViewListener != null) {
                mOnWebViewListener.onReceiveTitle(view, title);
            }
        }
    };

    /**
     * 处理HandlerURL
     *
     * @param url
     */
    private void handleUrlLoading(String url) {
        try {
            LogUtil.d("BaseWebViewFragment", "handleUrlLoading:" + url);
            String body = url.replace("hybird://method/", "");
            if (body.indexOf("?") != -1) {
                String method = body.substring(0, body.indexOf("?"));
                String query = body.replace(method + "?", "");
                String paramsArray[] = query.split("&");
                Hashtable<String, String> valueMap = new Hashtable<String, String>();
                for (int i = 0; i < paramsArray.length; i++) {
                    String params[] = paramsArray[i].split("=");
                    String key = URLDecoder.decode(params[0], HTTP.UTF_8);
                    String value = URLDecoder.decode(params[1], HTTP.UTF_8);
                    valueMap.put(key, value);
                }
                onCallMethod(method, valueMap);
            } else {
                String methodName = url.replace("hybird://method/", "");
                onCallMethod(methodName, null);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 执行js
     *
     * @param method
     * @param params
     */
    public void runJs(String method, String... params) {
        StringBuffer jsBuffer = new StringBuffer();
        jsBuffer.append("javascript:");
        jsBuffer.append(method);
        jsBuffer.append("(");
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                if (i == 0) {
                    jsBuffer.append("'" + params[i] + "'");
                } else {
                    jsBuffer.append(",'" + params[i] + "'");
                }
            }
        }
        jsBuffer.append(")");
        loadUrl(jsBuffer.toString());
    }

    /**
     * Web页面请求方法调用
     *
     * @param methodName
     * @param paramsMap
     */
    public void onCallMethod(String methodName, Hashtable<String, String> paramsMap) throws Exception {
        LogUtil.v("ActivityWeb", "onCallMethod : " + methodName);
        JSInterface jsBridge = new JSInterface();
        if ("doShare".equals(methodName)) {
            jsBridge.doShare(paramsMap.get("platform"), paramsMap.get("data"), paramsMap.get("jsCallBack"));
        } else if ("exit".equals(methodName)) {
            mFragment.finish();
        } else if ("setTitle".equals(methodName)) {
            String title = paramsMap.get("title");
            if (mOnWebViewListener != null) {
                mOnWebViewListener.onReceiveTitle(this, title);
            }
//            if (!TextUtils.isEmpty(title)) {
//                mFragment.getTitleBar().setTitleVisible(true);
//                mFragment.getTitleBar().setTitle(title);
//            } else {
//                mFragment.getTitleBar().setTitleVisible(false);
//            }
        } else if ("showLoading".equals(methodName)) {
            mFragment.getLoadingView().showLoading("正在加载中...");
        } else if ("showEmpty".equals(methodName)) {
            mFragment.getEmptyView().showEmpty("", paramsMap.get("hint"));
        } else if ("showContent".equals(methodName)) {
            mFragment.showContent();
        } else if ("showLoadingWhenLoadPage".equals(methodName)) {//加载页面时是否需要显示loading
            mShowLoadingWhenLoadPage = "1".equals(paramsMap.get("isShow")) ? true : false;
        } else if ("showAlert".equals(methodName)) {
            String title = paramsMap.get("title");
            String msg = paramsMap.get("msg");
            final String callBack = paramsMap.get("jsCallBack");
            String confirmBtnTxt = paramsMap.get("confirmTxt");
            String cancelBtnTxt = paramsMap.get("cancelTxt");
            if (mCurrentDialog != null && mCurrentDialog.isShowing()) {
                mCurrentDialog.dismiss();
            }
            if (!TextUtils.isEmpty(confirmBtnTxt) || !TextUtils.isEmpty(cancelBtnTxt)) {
                mCurrentDialog = DialogUtils.getMessageDialog(mFragment.getActivity(), title,
                        confirmBtnTxt, cancelBtnTxt, msg, new DialogUtils.OnDialogButtonClickListener() {
                            @Override
                            public void onItemClick(Dialog dialog, int btnId) {
                                if (btnId == BUTTON_CONFIRM) {
                                    runJs(callBack, "confirm");
                                } else {
                                    runJs(callBack, "cancel");
                                }
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        });
            } else {
                mCurrentDialog = DialogUtils.getMessageDialog(mFragment.getActivity(), title,
                        "确定", "取消", msg, new DialogUtils.OnDialogButtonClickListener() {
                            @Override
                            public void onItemClick(Dialog dialog, int btnId) {
                                if (btnId == BUTTON_CONFIRM) {
                                    runJs(callBack, "confirm");
                                } else {
                                    runJs(callBack, "cancel");
                                }
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        });
            }
            mCurrentDialog.show();
        } else if ("copy2Clipboard".equals(methodName)) {
            String content = paramsMap.get("content");
            if (!TextUtils.isEmpty(content)) {
                mClipboardManager.setText(content);
                UiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showShortToast(mFragment.getActivity(),"成功复制到粘贴板");
                    }
                });
            }
        } else if ("handleBack".equals(methodName)) {
            this.mJsHandleBack = "1".equals(paramsMap.get("handleBack")) ? true : false;
        } else if ("cmdQueue".equals(methodName)) {
            String cmdList = paramsMap.get("cmdQueue");
            if (!TextUtils.isEmpty(cmdList)) {
                JSONArray array = new JSONArray(cmdList);
                for (int i = 0; i < array.length(); i++) {
                    handleUrlLoading(array.getString(i));
                }
            }
        } else if ("Authentication".equals(methodName)){
            UserAuthNotyetFragment fragment = (UserAuthNotyetFragment) Fragment.
                    instantiate(mFragment.getActivity(), UserAuthNotyetFragment.class.getName());
            mFragment.showFragment(fragment);
        } else if("openNewWindow".equals(methodName)) {
            String title = paramsMap.get("title");
            String url = paramsMap.get("url");
            jsBridge.openWindow(title, url);
        } else if("createClass".equals(methodName)) {
            Bundle mBundle = new Bundle();
            mBundle.putInt("type", AddGradeClassFragment.TYPE_ADD_CLASS);
            AddGradeClassFragment fragment = (AddGradeClassFragment) Fragment
                    .instantiate(mFragment.getActivity(),
                            AddGradeClassFragment.class.getName(), mBundle);
            mFragment.showFragment(fragment);
            if (mOnWebViewListener != null) {
                mOnWebViewListener.onFinishFragment();
            }
        } else if("AssignHomework".equals(methodName)) {
            if (mOnWebViewListener != null) {
                mOnWebViewListener.onFinishFragment();
            }
            mFragment.removeAllFragment();
        } else if ("showRightMenu".equals(methodName)) {
            String txt = paramsMap.get("txt");
            String jsCallback = paramsMap.get("jsCallBack");
            if (mOnWebViewListener != null) {
                mOnWebViewListener.onShowRightMenu(txt, jsCallback);
            }
        } else if ("showTip".equals(methodName)) {
            String content = paramsMap.get("content");
            ToastUtils.showShortToast(getContext(), content);
        } else {
            MessagePushUtils pushUtils = new MessagePushUtils(mFragment);
            pushUtils.onCallMethod(methodName, paramsMap);
        }
    }

    /**
     * JSBridge
     */
    public class JSInterface {

        /**
         * 打开新的页面
         * @param title
         * @param url
         */
        public void openWindow(String title, String url){
            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            bundle.putString("url", url);
            ActivityWebViewFragment fragment = (ActivityWebViewFragment) Fragment
                    .instantiate(mFragment.getActivity(),
                            ActivityWebViewFragment.class.getName(), bundle);
            mFragment.showFragment(fragment);
            if (mOnWebViewListener != null) {
                mOnWebViewListener.openFragment(title, url);
            }
        }

        /**
         * 显示分享对话框
         *
         * @param platform
         * @param data
         */
        public void doShare(String platform, String data, final String jsCallBack) {
            try {
                JSONObject json = new JSONObject(data);
                ShareContent content = new ShareContent();
                content.mShareContent = json.optString("text");
                content.mShareUrl = json.optString("url");
                content.mDescription = json.optString("description");

                content.mUrlImage = json.optString("imageUrl");
                content.mShareTitle = json.optString("title");
                content.mShareTitleUrl = json.optString("titleUrl");
                content.mSiteName = json.optString("site");
                content.mSiteUrl = json.optString("siteUrl");

                ShareListener listener = new ShareListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        if (!TextUtils.isEmpty(jsCallBack))
                            runJs(jsCallBack, "success");
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        if (!TextUtils.isEmpty(jsCallBack))
                            runJs(jsCallBack, "fail");
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        if (!TextUtils.isEmpty(jsCallBack))
                            runJs(jsCallBack, "cancel");
                    }
                };

                if ("QQ".equals(platform)) {
                    mShareService.shareToQQ(mFragment.getActivity(), content, listener);
                } else if ("QQZone".equals(platform)) {
                    mShareService.shareToQQZone(mFragment.getActivity(), content, listener);
                } else if ("WX".equals(platform)) {
                    mShareService.shareToWX(mFragment.getActivity(), content, listener);
                } else if ("WXCircle".equals(platform)) {
                    mShareService.shareToWXCircle(mFragment.getActivity(), content, listener);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private OnWebViewListener mOnWebViewListener;
    public void setOnWebViewListener(OnWebViewListener listener) {
        mOnWebViewListener = listener;
    }

    public interface OnWebViewListener {
        public void onPageStart(WebView view, String url, Bitmap favicon);
        public void onPageFinished(WebView view, String url);
        public void openFragment(String title, String url);
        public void onPageError(WebView view, int errorCode, String description, String failingUrl);
        public void onFinishFragment();
        public void onPageProgress(WebView view, int newProgress);
        public void onShowRightMenu(String title, String jsCallBack);
        public void onReceiveTitle(WebView webView, String title);
    }
}
