/**
 * Copyright (C) 2015 The AndroidPhoneStudent Project
 */
package com.buang.welewolf.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyena.framework.app.widget.EmptyView;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.utils.UIUtils;
import com.hyena.framework.utils.UiThreadHandler;

/**
 * 盒子空页面
 *
 * @author yangzc
 */
public class BoxEmptyView extends EmptyView {

    public static final int TYPE_EMPTY_NONETWORK = 0;
    public static final int TYPE_EMPTY_HOMEWORK = 1;
    public static final int TYPE_EMPTY_MESSAGE = 2;
    public static final int TYPE_EMPTY_STUDENT = 3;
    public static final int TYPE_EMPTY_DEFAULT = 4;

    private ImageView mEmptyHintImg;
    private TextView mEmptyHintTxt;
    private TextView mEmptyHintTxt2;
    private Button mEmptyBtn;
    private View mEmptyView;

    public BoxEmptyView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), com.buang.welewolf.R.layout.layout_empty, this);
        mEmptyBtn = (Button) findViewById(com.buang.welewolf.R.id.empty_button);
        mEmptyHintImg = (ImageView) findViewById(com.buang.welewolf.R.id.emtpy_image);
        mEmptyHintTxt = (TextView) findViewById(com.buang.welewolf.R.id.empty_hint);
        mEmptyHintTxt2 = (TextView) findViewById(com.buang.welewolf.R.id.empty_hint2);
        mEmptyView = findViewById(com.buang.welewolf.R.id.empty);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public void showNoNetwork() {
        showEmpty(com.buang.welewolf.R.drawable.icon_empty_networkerror, "哎呀，网络连接失败", "请连接后重试", null, null);
    }

    @Override
    public void showEmpty(String s, String s1) {
        showEmpty(com.buang.welewolf.R.drawable.icon_empty_nodata, s1, null, null, null);
    }

    public void showEmpty(String hint) {
        showEmpty(com.buang.welewolf.R.drawable.icon_empty_nodata, hint, null, null, null);
    }

    public void setEmptyBg(int color) {
        mEmptyView.setBackgroundColor(color);
    }

    public void showNoDataEmpty(String hint) {
        showEmpty(com.buang.welewolf.R.drawable.icon_empty_networkerror, hint, null, null, null);
    }

    /**
     * 显示空页面
     *
     * @param resId            图片资源ID
     * @param hint             提示
     * @param desc             描述
     * @param btnTxt           按钮文案
     * @param btnClickListener 点击监听器
     */
    public void showEmpty(final int resId, final String hint, final String desc,
                          final String btnTxt, final OnClickListener btnClickListener) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mEmptyHintImg != null)
                    mEmptyHintImg.setImageResource(resId);

                if (mEmptyHintTxt != null && !TextUtils.isEmpty(hint)) {
                    mEmptyHintTxt.setVisibility(View.VISIBLE);
                    mEmptyHintTxt.setText(hint);
                } else {
                    mEmptyHintTxt.setVisibility(View.GONE);
                }
                if (mEmptyHintTxt2 != null && !TextUtils.isEmpty(desc)) {
                    mEmptyHintTxt2.setVisibility(View.VISIBLE);
                    mEmptyHintTxt2.setText(desc);
                } else {
                    mEmptyHintTxt2.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(btnTxt) && btnClickListener != null) {
                    mEmptyBtn.setText(btnTxt);
                    mEmptyBtn.setOnClickListener(btnClickListener);
                    mEmptyBtn.setVisibility(VISIBLE);
                } else {
                    if (btnClickListener != null) {
                        mEmptyView.setOnClickListener(btnClickListener);
                    }
                    mEmptyBtn.setVisibility(GONE);
                }
                getBaseUIFragment().getLoadingView().setVisibility(View.GONE);
                getBaseUIFragment().getEmptyView().setVisibility(View.VISIBLE);
            }
        });
    }
    
    /**
     * 显示空页面
     *
     * @param resId            图片资源ID
     * @param hint             提示
     * @param hintClickListener 点击监听器
     */
    public void showEmpty(final int resId, final String hint, final OnClickListener hintClickListener) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mEmptyHintImg != null)
                    mEmptyHintImg.setImageResource(resId);
                	mEmptyHintImg.setOnClickListener(hintClickListener);

                if (mEmptyHintTxt != null && !TextUtils.isEmpty(hint)) {
                    mEmptyHintTxt.setVisibility(View.VISIBLE);
                    mEmptyHintTxt.setText(hint);
                    mEmptyHintTxt.setOnClickListener(hintClickListener);
                } else {
                    mEmptyHintTxt.setVisibility(View.GONE);
                }
               
                getBaseUIFragment().getLoadingView().setVisibility(View.GONE);
                getBaseUIFragment().getEmptyView().setVisibility(View.VISIBLE);
            }
        });
    }

    public void showEmpty(int id, String hint) {
        if (!NetworkProvider.getNetworkProvider().getNetworkSensor().isNetworkAvailable()) {
            id = com.buang.welewolf.R.drawable.icon_empty_networkerror;
            hint = "当前无网络";
        }
        if (id == 0) {
            id = com.buang.welewolf.R.drawable.icon_empty_nodata;
        }
        final int resId = id;
        final String hints = hint;
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mEmptyHintImg != null)
                    mEmptyHintImg.setImageResource(resId);
                if (mEmptyHintTxt != null)
                    mEmptyHintTxt.setText(hints);
                getBaseUIFragment().getLoadingView().setVisibility(View.GONE);
                getBaseUIFragment().getEmptyView().setVisibility(View.VISIBLE);
            }
        });

    }

    public void showEmpty() {
        showEmpty(com.buang.welewolf.R.drawable.icon_empty_networkerror, "获取数据失败,请稍后再试!");
    }

    public void setEmptyMargin(int marginTop) {
        LayoutParams params = (LayoutParams) getLayoutParams();
        params.topMargin = UIUtils.dip2px(marginTop);
        setLayoutParams(params);
    }

    public void setEmptyMargin(int marginLeft, int marginTop, int marginRight, int marginButtom) {
        LayoutParams params = (LayoutParams) getLayoutParams();
        if (marginLeft >= 0) {
            params.leftMargin = UIUtils.dip2px(marginLeft);
        }
        if (marginTop >= 0) {
            params.topMargin = UIUtils.dip2px(marginTop);
        }
        if (marginRight >= 0) {
            params.rightMargin = UIUtils.dip2px(marginRight);
        }
        if (marginButtom >= 0) {
            params.bottomMargin = UIUtils.dip2px(marginButtom);
        }
        setLayoutParams(params);
    }

}
