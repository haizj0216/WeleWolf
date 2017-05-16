package com.buang.welewolf.modules.webactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Observable;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.hyena.framework.app.widget.HybirdWebView;
import com.hyena.framework.utils.UiThreadHandler;
import com.buang.welewolf.modules.utils.ConstantsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HybirdWebListView extends HybirdWebView {
    public static final int PAGE_SIZE = 10;
    private HybirdWebListView.WebViewListAdapter<?> mAdapter;
    private SparseArray<String> mRowKeys = new SparseArray(2000);
    private HybirdWebListView.WebDataSetObserver dataSetObserver = new HybirdWebListView.WebDataSetObserver() {
        public void onSetItems() {
            HybirdWebListView.this.mRowKeys.clear();
            if(HybirdWebListView.this.mAdapter != null) {
                JSONObject jsonObj = new JSONObject();
                try {
                    JSONArray itemArray = new JSONArray();
                    for(int i = 0; i < HybirdWebListView.this.mAdapter.getCount(); ++i) {
                        JSONObject itemJSONObject = HybirdWebListView.this.mAdapter.getItemJSONObject(i);
                        if(itemJSONObject != null) {
                            HybirdWebListView.this.mRowKeys.setValueAt(i, HybirdWebListView.this.mAdapter.getItemId(i));
                            itemArray.put(itemJSONObject);
                        }
                    }
                    jsonObj.put("groupQuestionList", itemArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HybirdWebListView.this.replaceAllRow(jsonObj.toString());
            }

        }

        public void onAddItems(int startIndex) {
            if(HybirdWebListView.this.mAdapter != null) {
                JSONObject jsonObj = new JSONObject();
                try {
                    JSONArray itemArray = new JSONArray();
                    for(int i = startIndex; i < HybirdWebListView.this.mAdapter.getCount(); ++i) {
                        JSONObject itemJSONObject = HybirdWebListView.this.mAdapter.getItemJSONObject(i);
                        if(itemJSONObject != null) {
                            HybirdWebListView.this.mRowKeys.setValueAt(i, HybirdWebListView.this.mAdapter.getItemId(i));
                            itemArray.put(itemJSONObject);
                        }
                    }
                    jsonObj.put("groupQuestionList", itemArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HybirdWebListView.this.appendRows(jsonObj.toString());
            }

        }

        public void onRemoveItem(int index) {
            this.onSetItems();
        }

        public void onDataSetChange() {
            if(HybirdWebListView.this.mAdapter != null) {
                for(int i = 0; i < HybirdWebListView.this.mAdapter.getCount(); ++i) {
                    String itemId = HybirdWebListView.this.mAdapter.getItemId(i);
                    if(!TextUtils.isEmpty(itemId)) {
                        String cacheKey = (String)HybirdWebListView.this.mRowKeys.valueAt(i);
                        if(!itemId.equals(cacheKey)) {
                            HybirdWebListView.this.mRowKeys.setValueAt(i, itemId);
                            HybirdWebListView.this.replaceOrAddRow(i, HybirdWebListView.this.mAdapter.getItemJSONObject(i).toString());
                        }
                    }
                }

                HybirdWebListView.this.runJs("checkRows", new String[]{String.valueOf(HybirdWebListView.this.mAdapter.getCount())});
            }

        }
    };

    public HybirdWebListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HybirdWebListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HybirdWebListView(Context context) {
        super(context);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.initView();
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public void initView() {
        this.setWebViewClient(new WebViewClient());
        this.setWebChromeClient(new WebChromeClient());
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setAppCacheEnabled(false);
        this.getSettings().setAllowFileAccess(true);
        getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        getSettings().setUserAgentString(ConstantsUtils.WEB_USER_AGENT);
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    public void setAdapter(HybirdWebListView.WebViewListAdapter<?> adapter) {
        if(this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.dataSetObserver);
        }

        this.mAdapter = adapter;
        this.mAdapter.registerDataSetObserver(this.dataSetObserver);
    }

    private void replaceOrAddRow(int position, String json) {
        this.runJs("replaceOrAddRow", json);
    }

    private void replaceAllRow(String json) {
        this.runJs("initQuestionList", json, PAGE_SIZE+"");
    }

    private void appendRows(String json) {
        this.runJs("appendQuestionList", json);
    }

    @SuppressLint({"NewApi"})
    public void runJs(String method, String... params) {
        final StringBuffer jsBuffer = new StringBuffer();
        jsBuffer.append("javascript:");
        jsBuffer.append(method);
        jsBuffer.append("(");
        if(params != null && params.length > 0) {
            for(int i = 0; i < params.length; ++i) {
                if(i == 0) {
                    jsBuffer.append("\'" + params[i] + "\'");
                } else {
                    jsBuffer.append(",\'" + params[i] + "\'");
                }
            }
        }

        jsBuffer.append(")");

        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if(VERSION.SDK_INT >= 19) {
                    evaluateJavascript(jsBuffer.toString(), new ValueCallback() {
                        @Override
                        public void onReceiveValue(Object value) {

                        }
                    });
                } else {
                    loadUrl(jsBuffer.toString());
                }
            }
        });
//        if(VERSION.SDK_INT >= 19) {
//            this.evaluateJavascript(jsBuffer.toString(), new ValueCallback() {
//                @Override
//                public void onReceiveValue(Object value) {
//
//                }
//            });
//        } else {
//            this.loadUrl(jsBuffer.toString());
//        }

    }

    public static class WebDataSetObservable extends Observable<HybirdWebListView.WebDataSetObserver> {
        public WebDataSetObservable() {
        }

        public void notifySetItems() {
            ArrayList var1 = this.mObservers;
            synchronized(this.mObservers) {
                for(int i = this.mObservers.size() - 1; i >= 0; --i) {
                    ((HybirdWebListView.WebDataSetObserver)this.mObservers.get(i)).onSetItems();
                }

            }
        }

        public void notifyAddItems(int startIndex) {
            ArrayList var2 = this.mObservers;
            synchronized(this.mObservers) {
                for(int i = this.mObservers.size() - 1; i >= 0; --i) {
                    ((HybirdWebListView.WebDataSetObserver)this.mObservers.get(i)).onAddItems(startIndex);
                }

            }
        }

        public void notifyRemoveItem(int index) {
            ArrayList var2 = this.mObservers;
            synchronized(this.mObservers) {
                for(int i = this.mObservers.size() - 1; i >= 0; --i) {
                    ((HybirdWebListView.WebDataSetObserver)this.mObservers.get(i)).onRemoveItem(index);
                }

            }
        }

        public void notifyDataSetChange() {
            ArrayList var1 = this.mObservers;
            synchronized(this.mObservers) {
                for(int i = this.mObservers.size() - 1; i >= 0; --i) {
                    ((HybirdWebListView.WebDataSetObserver)this.mObservers.get(i)).onDataSetChange();
                }

            }
        }
    }

    public abstract static class WebDataSetObserver {
        public WebDataSetObserver() {
        }

        public abstract void onDataSetChange();

        public abstract void onAddItems(int var1);

        public abstract void onRemoveItem(int var1);

        public abstract void onSetItems();
    }

    public abstract static class WebViewListAdapter<T> {
        private final HybirdWebListView.WebDataSetObservable mDataSetObservable = new HybirdWebListView.WebDataSetObservable();
        private List<T> mItems;
        protected Context mContext;

        public WebViewListAdapter(Context context) {
            this.mContext = context;
        }

        public int getCount() {
            return this.mItems == null?0:this.mItems.size();
        }

        public T getItem(int position) {
            return this.mItems == null?null:(position < this.mItems.size()?this.mItems.get(position):null);
        }

        public void setItems(List<T> items) {
            this.mItems = items;
            if(this.mDataSetObservable != null) {
                this.mDataSetObservable.notifySetItems();
            }

        }

        public void addItems(List<T> items) {
            if(this.mItems != null) {
                int startIndex = this.getCount();
                this.mItems.addAll(items);
                if(this.mDataSetObservable != null) {
                    this.mDataSetObservable.notifyAddItems(startIndex);
                }
            }

        }

        public List<T> getItems() {
            return this.mItems;
        }

        public void removeItem(T t) {
            if(this.mItems.contains(t)) {
                int index = this.mItems.indexOf(t);
                this.mItems.remove(t);
                if(this.mDataSetObservable != null) {
                    this.mDataSetObservable.notifyRemoveItem(index);
                }
            }

        }

        public void removeItems(List<T> items) {
            if(items != null && !items.isEmpty()) {
                this.mItems.removeAll(items);
                if(this.mDataSetObservable != null) {
                    this.mDataSetObservable.notifySetItems();
                }
            }

        }

        public void removeAllItems() {
            if(this.mItems != null) {
                this.mItems.clear();
                if(this.mDataSetObservable != null) {
                    this.mDataSetObservable.notifySetItems();
                }
            }

        }

        public abstract JSONObject getItemJSONObject(int var1);

        public abstract String getItemId(int var1);

        public void notifyDataSetChange() {
            if(this.mDataSetObservable != null) {
                this.mDataSetObservable.notifyDataSetChange();
            }

        }

        public void registerDataSetObserver(HybirdWebListView.WebDataSetObserver observer) {
            this.mDataSetObservable.registerObserver(observer);
        }

        public void unregisterDataSetObserver(HybirdWebListView.WebDataSetObserver observer) {
            this.mDataSetObservable.unregisterObserver(observer);
        }
    }

/*    private long preTouchTime = 0;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            long currentTouchTime = System.currentTimeMillis();
            if (currentTouchTime - preTouchTime <= ViewConfiguration.getDoubleTapTimeout()) {
                preTouchTime = currentTouchTime;
                return true;
            }
            preTouchTime = currentTouchTime;
        }
        return super.dispatchTouchEvent(ev);
    }*/
}
