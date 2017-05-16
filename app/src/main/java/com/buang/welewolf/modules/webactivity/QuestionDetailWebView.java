package com.buang.welewolf.modules.webactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hyena.framework.clientlog.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 问题详情页面（除了题干部分）
 * @author wangming
 */
public class QuestionDetailWebView extends WebView {
    public static final int STATE_NORMAL = 1;
    public static final int STATE_SELECTED = 2;
    public static final int STATE_RIGHT = 3;
    public static final int STATE_ERROR = 4;
    public static final int STATE_NORMAL_RIGHT = 5;
    public static final int STATE_ERROR_RIGHT = 6;
    public static final String YES = "YES";
    public static final String NO = "NO";
    public static final String METHOD_SET_MODE_SINGLE = "setSingleMode"; //默认yes 单选
    public static final String METHOD_SET_MODE_ANSWER = "setAnswerMode"; //默认yes 答题
    public static final String METHOD_ADD_TRUNK = "addTrunk";
    public static final String METHOD_ADD_OPTION = "addOption";
    public static final String METHOD_CHANGE_OPTION_STATE = "changeOptionState";
    public static final String METHOD_ADD_RIGHT_ANSWER_4_OBJECTVICE = "addRightAnswer4Objective";
    public static final String METHOD_ADD_VOICE_ANALYZE = "addVoiceAnalyze";
    public static final String METHOD_ADD_TEACHER_REMARK = "addTeacherRemark";
    public static final String METHOD_ADD_ANALYZE = "addAnalyze";
    public static final String METHOD_ADD_OPTION_RIGHT_RATE = "addOptionRightRate";
    public static final String METHOD_ADD_OPTIONS_CLICK_LISTENER = "addOptionsClickListener";


    private static final String HTML_URL_PREFIX = "hybird://";
    private WebViewClientListener mWebViewClientListener;

    public QuestionDetailWebView(Context context) {
        this(context, null);
    }

    public QuestionDetailWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuestionDetailWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 初始化webView
        setWebChromeClient(new WebChromeClient());
        getSettings().setJavaScriptEnabled(true);
        getSettings().setUseWideViewPort(true);
        getSettings().setAppCacheEnabled(false);
        getSettings().setAllowFileAccess(true);
        getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        setLongClickable(false);
        setHapticFeedbackEnabled(false);
    }

    public interface WebViewClientListener {
        void onCallMethod(String functionName, Map<String, String> paramsMap);
        void onPageStarted(WebView view, String url, Bitmap favicon);
        void onPageFinished(WebView view, String url);
    }

    public void runJs(String methodName, String... params) {
        StringBuffer jsBuffer = new StringBuffer();
        jsBuffer.append("javascript:");
        jsBuffer.append(methodName);
        jsBuffer.append("(");
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                params[i] = params[i].replaceAll("'", "\"");
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

    public void loadTemplate() {
        loadUrl("file:///android_asset/question_detail.html");
    }

    public void setWebViewClientListener(WebViewClientListener listener) {
        mWebViewClientListener = listener;
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.e("js", "url: " + url);
                if (!url.startsWith(HTML_URL_PREFIX)) {
                    return true;
                }
                try {
                    String body = url.replace(HTML_URL_PREFIX, "").trim();
                    LogUtil.d("shouldOverrideUrlLoading[body]: ", body);
                    if (body.indexOf("?") != -1) {
                        String method = body.substring(0, body.indexOf("?"));
                        LogUtil.d("shouldOverrideUrlLoading[method]: ", method);
                        String paramsString = body.replace(method + "?", "");
                        LogUtil.d("shouldOverrideUrlLoading[paramsString]: ", paramsString);
                        if (!TextUtils.isEmpty(paramsString)) {
                            String paramsArray[] = paramsString.split("&");
                            Map<String, String> paramsMap = new HashMap<String, String>();
                            for (int i = 0; i < paramsArray.length; i++) {
                                String[] params = paramsArray[i].split("=");
                                paramsMap.put(params[0], params[1]);
                            }
                            onCallMethod(method, paramsMap);
                        } else {
                            onCallMethod(method, new HashMap<String, String>());
                        }
                    } else {
                        String methodName = url.replace(HTML_URL_PREFIX, "");
                        onCallMethod(methodName, new HashMap<String, String>());
                    }
                    return true;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            private void onCallMethod(String methodName, Map<String, String> paramsMap) {
                LogUtil.d("shouldOverrideUrlLoading[onCallMethod]", methodName);
                if(mWebViewClientListener != null) {
                    mWebViewClientListener.onCallMethod(methodName, paramsMap);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(mWebViewClientListener != null) {
                    mWebViewClientListener.onPageStarted(view, url, favicon);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(mWebViewClientListener != null) {
                    mWebViewClientListener.onPageFinished(view, url);
                }
            }
        });
    }
}
