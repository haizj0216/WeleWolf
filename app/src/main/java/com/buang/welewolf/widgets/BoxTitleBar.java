/**
 * Copyright (C) 2015 The AndroidPhoneStudent Project
 */
package com.buang.welewolf.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.app.widget.TitleBar;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.utils.UIUtils;
import com.buang.welewolf.R;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class BoxTitleBar extends TitleBar {

    private ImageView mBackBtn;
    private ImageView mMenuBtn;
    private TextView mTitleTxt;
    private TextView mDescTxt;
    private TextView mRightView;
    private PopupWindow mMenuPopWindows;
    private String mUmengKey;
    private ImageView mOptView;
    private RelativeLayout mTitlelayout;
    private ImageView mBarTips;
    private TextView mRightText;
    private TextView mLeftText;
    private View mButtomLine;
    private View mTitleRight;

    public BoxTitleBar(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.layout_title_bar, this);
        mTitlelayout = (RelativeLayout) findViewById(R.id.title_bar_layout);
        mBackBtn = (ImageView) findViewById(R.id.title_bar_back);
        mBackBtn.setOnClickListener(mOnClickListener);
        mRightView = (TextView) findViewById(R.id.title_bar_rightView);
        mMenuBtn = (ImageView) findViewById(R.id.title_bar_menu);
        mOptView = (ImageView) findViewById(R.id.title_bar_optview);
        mTitleTxt = (TextView) findViewById(R.id.title_bar_title);
        findViewById(R.id.title_bar_center).setOnClickListener(mOnClickListener);
        findViewById(R.id.title_bar_back).setOnClickListener(mOnClickListener);
        findViewById(R.id.title_bar_menu).setOnClickListener(mOnClickListener);
        mDescTxt = (TextView) findViewById(R.id.title_bar_desc);
        mBarTips = (ImageView) findViewById(R.id.title_bar_redtip);
        mRightText = (TextView) findViewById(R.id.title_bar_rightText);
        mLeftText = (TextView) findViewById(R.id.title_bar_lefttext);
        mButtomLine = findViewById(R.id.title_buttom_divier);
        mTitleRight = findViewById(R.id.title_bar_right);
    }

    @Override
    public void setBaseUIFragment(BaseUIFragment<?> baseUIFragment) {
        super.setBaseUIFragment(baseUIFragment);
        updateMenus();
    }

    public void setTitleBarTipVisible(final boolean visible) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mBarTips != null) {
                    mBarTips.setVisibility(visible ? VISIBLE : GONE);
                }
            }
        });
    }

    public void updateMenus() {
        if (getBaseUIFragment() == null) {
            return;
        }
        List<MenuItem> menuItems = getBaseUIFragment().getMenuItems();
        if (menuItems == null || menuItems.isEmpty()) {
            mMenuBtn.setVisibility(View.GONE);
        } else {
            if (menuItems.size() == 1) {
                // int padding = UIUtils.dip2px(15);
                mMenuBtn.setImageResource(menuItems.get(0).icon);
                // mMenuBtn.setPadding(padding, padding, padding, padding);
            } else {
                mMenuBtn.setImageResource(R.drawable.title_bar_menu);
            }
            mMenuBtn.setVisibility(View.VISIBLE);
        }
    }

    public View getTitleBar(){
        return findViewById(R.id.title_bar_layout);
    }

    /**
     * 设置背景色
     * @param color
     */
    public void setTitleBarBgColor(final int color) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mTitlelayout.setBackgroundColor(color);
                mButtomLine.setVisibility(GONE);
            }
        });

    }

    public void setDividerVisible(final int visible) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mButtomLine.setVisibility(visible);
            }
        });
    }

    /**
     * 设置返回键是否可见
     *
     * @param visible
     */
    public void setBackBtnVisible(final boolean visible) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
                mBackBtn.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    public void setBackBtnEnable(final boolean enable) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mBackBtn.setEnabled(enable);
            }
        });
    }

    /**
     * 设置返回键图片
     *
     * @param resourceId
     */
    public void setBackBtnDrawable(final int resourceId) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
                mBackBtn.setImageResource(resourceId);
            }
        });
    }

    /**
     * 获得更多图片
     *
     * @return
     */
    public void setTitleMore(final int resId, final OnClickListener clickListener) {
        UiThreadHandler.post(new Runnable() {

            @Override
            public void run() {
                setVisibility(View.VISIBLE);
            }
        });
    }

    public void setTitleMoreHint(final int value) {
        UiThreadHandler.post(new Runnable() {

            @Override
            public void run() {
                setVisibility(View.VISIBLE);
            }
        });
    }

    public void setTitleMoreHintVisible(final boolean visible) {
        UiThreadHandler.post(new Runnable() {

            @Override
            public void run() {
                setVisibility(View.VISIBLE);
            }
        });
    }

    public TextView getMoreTextView() {
        return mRightView;
    }


    /**
     * 右侧点击动作
     */
    public void setRightMoreTxt(final String text, final OnClickListener listener) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(text)) {
                    UiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mMenuBtn.setVisibility(GONE);
                            mRightView.setText(text);
                            mRightView.setOnClickListener(listener);
                            mRightView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    public void setTitleRightText(final String text, final OnClickListener listener) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(VISIBLE);
                if (!TextUtils.isEmpty(text)) {
                    UiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mTitleRight.setVisibility(GONE);
                            mRightText.setVisibility(VISIBLE);
                            mRightText.setText(text);
                            mRightText.setOnClickListener(listener);
                        }
                    });
                }
            }
        });
    }

    public void setTitleRightText(final String text, final int drawable,
                                  final int textSize, final int color, final OnClickListener listener) {
        final RelativeLayout.LayoutParams params =
                new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, UIUtils.dip2px(15), 0);
        params.addRule(RelativeLayout.RIGHT_OF, 0);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(VISIBLE);
                if (!TextUtils.isEmpty(text)) {
                    UiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mTitleRight.setVisibility(GONE);
                            mRightText.setVisibility(VISIBLE);
                            mRightText.setText(text);
                            mRightText.setOnClickListener(listener);
                            mRightText.setGravity(Gravity.CENTER);
                            mRightText.setBackgroundResource(drawable);
                            mRightText.setPadding(UIUtils.dip2px(4),0,UIUtils.dip2px(4),UIUtils.dip2px(1));
                            mRightText.setEllipsize(null);
                            mRightText.setLayoutParams(params);
                            mRightText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                            mRightText.setTextColor(color);
                            mRightText.setOnClickListener(listener);
                            mRightText.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    public void setTitleRightText(final String text) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(VISIBLE);
                if (!TextUtils.isEmpty(text)) {
                    UiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mTitleRight.setVisibility(GONE);
                            mRightText.setVisibility(VISIBLE);
                            mRightText.setText(text);
                        }
                    });
                }
            }
        });
    }

    public void setTitleLeftText(final String text, final OnClickListener listener) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(VISIBLE);
                if (!TextUtils.isEmpty(text)) {
                    UiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mLeftText.setVisibility(VISIBLE);
                            mLeftText.setText(text);
                            mLeftText.setOnClickListener(listener);
                        }
                    });
                }
            }
        });
    }

    public TextView getTitleLeftText() {
        return mLeftText;
    }

    /**
     * 右侧点击动作
     */
    public void setRightMoreTxtDrawable(final Drawable drawable, final int padding) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
                if (drawable != null) {
                    UiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mMenuBtn.setVisibility(GONE);
                            mRightView.setCompoundDrawables(drawable, null, null , null);
                            mRightView.setCompoundDrawablePadding(padding);
                            mRightView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    public void setRightMoreTxt(final String txt) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
                mMenuBtn.setVisibility(GONE);
                mRightView.setVisibility(View.VISIBLE);
                mRightView.setText(txt);
            }
        });
    }

    public void setRightMoreTxt(final SpannableString txt) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
                mMenuBtn.setVisibility(GONE);
                mRightView.setVisibility(View.VISIBLE);
                mRightView.setText(txt);
            }
        });
    }

    public void setRightMoreTxtColor( final int color) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
                mMenuBtn.setVisibility(GONE);
                mRightView.setVisibility(View.VISIBLE);
                mRightView.setTextColor(color);
            }
        });
    }

    /**
     * 设置标题
     *
     * @param title
     */
    @Override
    public void setTitle(final String title) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
                if (mTitleTxt != null) {
                    mTitleTxt.setText(title);
                }
            }
        });
    }

    public void setTitle(final String title, final String desc) {
        setTitle(title);
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mDescTxt == null) {
                    return;
                }
                if (!TextUtils.isEmpty(desc)) {
                    mDescTxt.setText(desc);
                    mTitleTxt.setTextSize(15);
                    mDescTxt.setVisibility(View.VISIBLE);
                } else {
                	mTitleTxt.setTextSize(18);
                    mDescTxt.setVisibility(View.GONE);
                }

            }
        });
    }

    public void setTitle(final int id, final String title, final OnClickListener listener) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {

                setVisibility(View.VISIBLE);
                if (mTitleTxt != null) {
                    if (id > 0) {
                        Drawable nav_up=getResources().getDrawable(id);
                        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
                        mTitleTxt.setCompoundDrawables(null, null, nav_up, null);
                    }else {
                        mTitleTxt.setCompoundDrawables(null, null, null, null);
                    }
                    mTitleTxt.setText(title);
                    mTitleTxt.setOnClickListener(listener);
                }
            }
        });
    }

    public void setTitleDrawable(final int id) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {

                setVisibility(View.VISIBLE);
                if (mTitleTxt != null) {
                    if (id > 0) {
                        Drawable nav_up=getResources().getDrawable(id);
                        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
                        mTitleTxt.setCompoundDrawables(null, null, nav_up, null);
                    }else {
                        mTitleTxt.setCompoundDrawables(null, null, null, null);
                    }
                }
            }
        });
    }

    public void setUMengKey(String umengKey) {
        mUmengKey = umengKey;
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.title_bar_back: {
                    if (mListener != null)
                        mListener.onBackPressed(v);
                    if (!TextUtils.isEmpty(mUmengKey)) {
                        MobclickAgent.onEvent(BaseApp.getAppContext(),
                                mUmengKey);
                    }
                    break;
                }
                case R.id.title_bar_menu: {
                    if (getBaseUIFragment() == null) {
                        return;
                    }
                    List<MenuItem> menuItems = getBaseUIFragment().getMenuItems();
                    if (menuItems == null) {
                        return;
                    }
                    if (menuItems.size() == 1) {
                        getBaseUIFragment().onMenuItemClick(menuItems.get(0));
                    } else {
                        if (mMenuPopWindows != null && mMenuPopWindows.isShowing()) {
                            mMenuPopWindows.dismiss();
                            WindowManager.LayoutParams params = getBaseUIFragment().getActivity().getWindow().getAttributes();
                            params.alpha = 1f;
                            getBaseUIFragment().getActivity().getWindow().setAttributes(params);
                        }
                        mMenuPopWindows = DialogUtils.getMoreListPopupWindow(
                                getBaseUIFragment().getActivity(), UIUtils.dip2px(165), getBaseUIFragment().getMenuItems(),
                                mOnMenuItemClickListener);
                        int xPos = getResources().getDisplayMetrics().widthPixels
                                / 2 - mMenuPopWindows.getWidth() / 2 - 20;
                        mMenuPopWindows.showAsDropDown(v, xPos, 0);
                        mMenuPopWindows.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                WindowManager.LayoutParams params = getBaseUIFragment().getActivity().getWindow().getAttributes();
                                params.alpha = 1f;
                                getBaseUIFragment().getActivity().getWindow().setAttributes(params);
                            }
                        });
                        WindowManager.LayoutParams params = getBaseUIFragment().getActivity().getWindow().getAttributes();
                        params.alpha = 0.7f;
                        getBaseUIFragment().getActivity().getWindow().setAttributes(params);
                    }
                    break;
                }
            }
        }
    };

    public AdapterView.OnItemClickListener mOnMenuItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            List<MenuItem> items = getBaseUIFragment().getMenuItems();
            if (items == null)
                return;

            if (position < items.size()) {
                getBaseUIFragment().onMenuItemClick(items.get(position));
                if (mMenuPopWindows.isShowing()) {
                    mMenuPopWindows.dismiss();
                }
            }
        }
    };

    public void setTextSize(final int size) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.VISIBLE);
                if (mTitleTxt != null) {
                    mTitleTxt.setTextSize(size);
                }
            }
        });
    }

    public TextView getTitleText() {
        return mTitleTxt;
    }

    public void setOptViewClickListener(final int id, final OnClickListener listener) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (id != 0 && listener != null) {
                    mOptView.setVisibility(VISIBLE);
                    mOptView.setImageResource(id);
                    mOptView.setOnClickListener(listener);
                } else {
                    mOptView.setVisibility(INVISIBLE);
                }
            }
        });
    }

    public void setMenuViewClickListener(final int id, final OnClickListener listener) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (id != 0 && listener != null) {
                    mMenuBtn.setVisibility(VISIBLE);
                    mMenuBtn.setImageResource(id);
                    mMenuBtn.setOnClickListener(listener);
                } else {
                    mMenuBtn.setVisibility(INVISIBLE);
                }
            }
        });
    }

    public void setOptViewVisible(final boolean visible) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mOptView.setVisibility(visible ? VISIBLE : GONE);
            }
        });
    }

    public void setMenuButtonVisible(final boolean visible) {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mMenuBtn.setVisibility(visible ? VISIBLE : GONE);
            }
        });
    }

}
