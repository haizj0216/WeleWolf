package com.knowbox.teacher.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.FileUtils;
import com.hyena.framework.utils.VersionUtils;
import com.knowbox.teacher.base.database.bean.QuestionItem;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.SubjectUtils;
import com.knowbox.teacher.modules.utils.TemplateUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by weilei on 15/12/4.
 */
public class WebListView extends WebView {

    private WebViewListAdapter mAdapter;
    private static String mTemplate = "";
    private static String mBaseUrl = "";

    public WebListView(Context context) {
        this(context, null);
    }

    public WebListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebListView(Context context, AttributeSet attrs, int defStyle) {
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
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        getSettings().setUserAgentString(ConstantsUtils.WEB_USER_AGENT);
        getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
    }

    public void loadData(String html) {
        try {
            //重用模板
            if (TextUtils.isEmpty(mTemplate)) {
                File file = new File(new TemplateUtils().getTemplatePath("QuestionView.html"));
                byte data[];
                if (file.exists()) {
                    data = FileUtils.getBytes(file);
                    mBaseUrl = "file://" + file.getParentFile().getAbsolutePath() + File.separator;
                } else {
                    data = FileUtils.getBytes(getResources().getAssets().open("QuestionView.html"));
                    mBaseUrl = "file:///android_asset/";
                }
                if (data == null || data.length ==0) {
                    data = FileUtils.getBytes(getResources().getAssets().open("QuestionView.html"));
                    mBaseUrl = "file:///android_asset/";
                }
                mTemplate = new String(data);
                mTemplate = TemplateUtils.replaceBundleJs(mTemplate);
                mTemplate = TemplateUtils.replaceLlktCss(mTemplate);
            }

            loadDataWithBaseURL(mBaseUrl, mTemplate.replace("${HTML}", html), "text/html", "UTF-8", "temp.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAdapter(WebViewListAdapter adapter) {
        if (mAdapter != null) {
            // 解注册数据观察器
            mAdapter.unregisterDataSetObserver(dataSetObserver);
        }
        this.mAdapter = adapter;
        // 注册数据观察器
        this.mAdapter.registerDataSetObserver(dataSetObserver);
        // 刷新数据
        refreshData(true);
    }

    // 数据监听器
    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            // 刷新数据
            refreshData(false);
        }
    };

    /**
     * 刷新数据
     *
     * @param force
     */
    private void refreshData(boolean force) {
        if (force) {
//			runJs("setHtml", "");
        }
        StringBuffer html = new StringBuffer();
        if (mAdapter != null) {
            for (int i = 0; i < mAdapter.getCount(); i++) {
                String item = mAdapter.getItemJson(i);
                html.append(item);
            }
//			if (!force) {
//				runJs("setHtml", html.toString());
            loadData(html.toString());
//			}
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
     * WebViewListAdapter
     *
     * @param <T>
     */
    public static abstract class WebViewListAdapter<T> {

        private final DataSetObservable mDataSetObservable = new DataSetObservable();

        private List<T> mItems;
        protected Context mContext;

        public WebViewListAdapter(Context context) {
            super();
            this.mContext = context;
        }

        public int getCount() {
            if (mItems == null)
                return 0;
            return mItems.size();
        }

        public T getItem(int position) {
            if (mItems == null)
                return null;
            if (position < mItems.size())
                return mItems.get(position);
            return null;
        }

        public void setItems(List<T> items) {
            this.mItems = items;
            notifyDataSetChanged();
        }

        public void addItems(List<T> items) {
            if (mItems != null) {
                this.mItems.addAll(items);
                notifyDataSetChanged();
            }
        }

        public List<T> getItems() {
            return mItems;
        }

        public void removeItem(T t) {
            if (mItems.contains(t)) {
                mItems.remove(t);
                // notifyDataSetChanged();
            }
        }

        public void removeAllItems() {
            if (mItems != null) {
                mItems.clear();
                notifyDataSetChanged();
            }
        }

        /**
         * 获得模板内容
         *
         * @param position 位置
         * @return 模板内容
         */
        public abstract String getItemJson(int position);

        /**
         * 通知数据改变
         */
        public void notifyDataSetChanged() {
            if (mDataSetObservable != null)
                mDataSetObservable.notifyChanged();
        }

        /**
         * 通知UI重绘
         */
        public void notifyDataSetInvalidated() {
            mDataSetObservable.notifyInvalidated();
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            mDataSetObservable.registerObserver(observer);
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            mDataSetObservable.unregisterObserver(observer);
        }
    }

    public String buildQuestionHtml(QuestionItem quesetion, int mIndex) {
        StringBuffer html = new StringBuffer();
        html.append("<dd class=\"rubberBand\" questionID=\"");
        html.append(quesetion.mQuestionId);
        html.append("\"><div class=\"choice\"></div><div class=\"content\">");
        html.append("<span class=\"questionno\">"+String.valueOf(mIndex)+"</span>");
        html.append("<span class=\"questype\">"+ SubjectUtils.getQuestionType(quesetion.mQuestionType)+"题</span>");
//        int maxWidth = UIUtils.getWindowWidth(getActivity()) - UIUtils.px2dip(300);
        if (quesetion.wellChosen) {
//            maxWidth -= 50;
            html.append("<span class=\"tag\">精</span>");
        }

        if (!TextUtils.isEmpty(quesetion.mTag)) {
            html.append("<span class=\"tag\">" + quesetion.mTag + "</span>");
        }

        if (quesetion.mFromType == ConstantsUtils.FROM_TYPE_ERRORBOOK) {
            html.append("<span class=\"tag\">错题重做</span>");
        }
        if (quesetion.mIsOut) {
//            maxWidth -= 90;
            html.append("<span style=\"float:right;\" class=\"tag_gray\">已布置</span>");
        }
        html.append("<br/><br/>" + quesetion.mContent).append("</div><br/><br/>");
        html.append("<div class = \"quesfooter\">");
        html.append("<span class=\"dificult dif"+String.valueOf(quesetion.mDifficulty)+"\">难度</span>");
        if (quesetion.mHot > 0) {
            html.append("<div class=\"sx\"></div>");
            html.append("<span>出过&nbsp;<span class=\"span-color\">"+String.valueOf(quesetion.mHot)+"次</span></span>");
        }

        if (quesetion.mRightRate > 0) {
            html.append("<div class=\"sx\"></div>");
            html.append("<span>正确&nbsp;<span class=\"span-color\">"+floatToString(quesetion.mRightRate)+"%</span></span>");
        }

        html.append("</div>");
        if (quesetion.isSpecial) {
            html.append("<div style=\"float:left;\" class = \"changeQuestion\">换一换</div>");
        }
        html.append("<div style=\"clear:both\"></div>");

        html.append("<div class=\"drift\"><div class=\"prev\"></div><div class=\"next\"></div></div>");
        html.append("</dd>");
        return html.toString();
    }

    private String floatToString(float f) {
        if (f <= 0.0)
            return "0";
        DecimalFormat fnum = new DecimalFormat("##0.0");
        return fnum.format(f);
    }
}
