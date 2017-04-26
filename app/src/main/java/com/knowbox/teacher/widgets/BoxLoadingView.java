/**
 * Copyright (C) 2015 The AndroidPhoneStudent Project
 */
package com.knowbox.teacher.widgets;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyena.framework.app.widget.LoadingView;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.teacher.R;

public class BoxLoadingView extends LoadingView {

    private View mLoadingView_1;
    private ImageView mLoadingImg;
    private TextView mLoadingHintTxt;

    private View mLoadingView_3;
    private ImageView mLoadingImg_3;
    private TextView mLoadingHintTxt_3;

    public BoxLoadingView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.layout_loading, this);
        mLoadingView_1 = findViewById(R.id.loading_layout_1);//蜡笔小新
        mLoadingView_3 = findViewById(R.id.loading_layout_3);//小象奔跑

        mLoadingHintTxt = (TextView) findViewById(R.id.loading_hint);
        mLoadingImg = (ImageView) findViewById(R.id.loading_anim);

        mLoadingHintTxt_3 = (TextView) findViewById(R.id.loading_hint_3);
        mLoadingImg_3 = (ImageView) findViewById(R.id.loading_anim_3);

    }

    @Override
    public void showLoading(final String hint) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mLoadingHintTxt != null && !TextUtils.isEmpty(hint))
                    mLoadingHintTxt.setText(hint);

                setVisibility(View.VISIBLE);
                mLoadingView_3.setVisibility(View.VISIBLE);
                mLoadingView_1.setVisibility(View.GONE);
                //Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_loading_2);
                //mLoadingImg_2.startAnimation(anim);
                //anim.start();
                AnimationDrawable drawable = (AnimationDrawable) mLoadingImg_3.getDrawable();
                drawable.start();
                getBaseUIFragment().getEmptyView().setVisibility(View.GONE);
            }
        });
    }

    public void showHomeworkLoading() {
        showHomeworkLoading(null);
    }

    public void showHomeworkLoading(final String hint) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mLoadingHintTxt_3 != null && !TextUtils.isEmpty(hint))
                    mLoadingHintTxt_3.setText(hint);

                setVisibility(View.VISIBLE);
                mLoadingView_1.setVisibility(View.VISIBLE);
                mLoadingView_3.setVisibility(View.GONE);
                AnimationDrawable drawable = (AnimationDrawable) mLoadingImg.getDrawable();
                drawable.start();
                getBaseUIFragment().getEmptyView().setVisibility(View.GONE);

            }
        });
    }

}
