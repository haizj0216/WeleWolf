/*
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.buang.welewolf.modules.main;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.buang.welewolf.App;
import com.buang.welewolf.base.services.share.ShareService;
import com.buang.welewolf.base.utils.ActionUtils;
import com.buang.welewolf.modules.message.utils.MessagePushUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.welewolf.fragment.MainGameFragment;
import com.buang.welewolf.welewolf.fragment.MainMessageFragment;
import com.buang.welewolf.welewolf.fragment.MainRankFragment;
import com.buang.welewolf.welewolf.login.LoginFragment;
import com.buang.welewolf.welewolf.login.UserInfoEditFragment;
import com.hyena.framework.app.fragment.BaseSubFragment;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.UiThreadHandler;

import org.apache.http.protocol.HTTP;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * 主场景
 *
 * @author yangzc
 */
public class MainFragment extends BaseUIFragment<UIFragmentHelper> {

    private static final String PREFS_NOTIFYTION_TIME = "prefs_notify_time";
    private static final String PREFS_ACTIVITY = "prefs_activity";

    public static final int SCENE_GAME = 0;
    public static final int SCENE_LIST = 1;
    public static final int SCENE_MESSAGE = 2;

    private int mCurrentTab;
    private View mTabGroup;
    private View mTabList;
    private View mTabGame;
    private View mTabMessage;
    private View mTabClassTips;
    private View mTabProfileTips;
    private View mTabHomeworkTips;
    private List<BaseSubFragment> mSparseArray;
    private ViewPager mViewPager;
    private View mLoadingView;
    private ImageView mLoadingImg;
    private Dialog mNotifitionDialog;

    public static final String ACTION_TAB_TIPS = "action_tab_tips";
    public static final int TYPE_TAB_TIPS_CLASS = 0;
    public static final int TYPE_TAB_TIPS_BANK = 1;
    public static final int TYPE_TAB_TIPS_PROFILE = 2;
    private ShareService mShareService;
//	private LocationService locationService;
//	private int mStartUpCount;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
        setTitleStyle(STYLE_NO_TITLE);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_main, null);
        return view;
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        mSparseArray = new ArrayList<BaseSubFragment>();
        mSparseArray.add(MainGameFragment.newFragment(getActivity(), MainGameFragment.class, null, AnimType.ANIM_NONE));// 作业
        mSparseArray.add(MainRankFragment.newFragment(getActivity(), MainRankFragment.class, null, AnimType.ANIM_NONE));// 题库
        mSparseArray.add(MainMessageFragment.newFragment(getActivity(), MainMessageFragment.class, null, AnimType.ANIM_NONE));// 我

        mTabGroup = view.findViewById(com.buang.welewolf.R.id.main_tab_group);

        mTabList = view.findViewById(com.buang.welewolf.R.id.main_tab_list);
        mTabList.setOnClickListener(mOnClickListener);
        mTabGame = view.findViewById(com.buang.welewolf.R.id.main_tab_game);
        mTabGame.setOnClickListener(mOnClickListener);
        mTabMessage = view.findViewById(com.buang.welewolf.R.id.main_tab_message);
        mTabMessage.setOnClickListener(mOnClickListener);
        mTabClassTips = view.findViewById(com.buang.welewolf.R.id.main_list_tips);
        mTabProfileTips = view.findViewById(com.buang.welewolf.R.id.main_message_tips);
        mTabHomeworkTips = view.findViewById(com.buang.welewolf.R.id.main_game_tips);
        mViewPager = (ViewPager) view.findViewById(com.buang.welewolf.R.id.main_pagers);
        mViewPager.setOffscreenPageLimit(mSparseArray.size());
        mViewPager
                .setAdapter(new MainFragmentAdapter(getChildFragmentManager()));
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
        mLoadingView = view.findViewById(com.buang.welewolf.R.id.loading_layout);
        mLoadingImg = (ImageView) view.findViewById(com.buang.welewolf.R.id.loading_anim);

        mViewPager.setCurrentItem(SCENE_GAME);
        setCurrentTab(SCENE_GAME);

        mShareService = (ShareService) getActivity().getSystemService(ShareService.SERVICE_NAME);
        mShareService.initConfig(getActivity());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_TAB_TIPS);
        intentFilter.addAction(ActionUtils.ACTION_VIRTUAL_TIPS);
        intentFilter.addAction(ActionUtils.ACTION_MAIN_TAB);
        intentFilter.addAction(ActionUtils.ACTION_SHOW_REDBADGE);
        MsgCenter.registerLocalReceiver(mReceiver, intentFilter);

        if (mOnMainFragmentLoadListener != null) {
            mOnMainFragmentLoadListener.onMainLoad();
        }

        if (TextUtils.isEmpty(Utils.getLoginUserItem().userName)) {
            UserInfoEditFragment fragment = UserInfoEditFragment.newFragment(getActivity(), UserInfoEditFragment.class, null);
            showFragment(fragment);
        }

    }

    /**
     * 处理HandlerURL
     *
     * @param url
     */
    private void handleUrlLoading(String url) {
        try {
            LogUtil.d("BaseWebViewFragment", "handleUrlLoading:" + url);
            String body = url.replace("tknowbox://method/", "");
            if (body.indexOf("?") != -1) {
                final String method = body.substring(0, body.indexOf("?"));
                String query = body.replace(method + "?", "");
                String paramsArray[] = query.split("&");
                final Hashtable<String, String> valueMap = new Hashtable<String, String>();
                for (int i = 0; i < paramsArray.length; i++) {
                    String params[] = paramsArray[i].split("=");
                    String key = URLDecoder.decode(params[0], HTTP.UTF_8);
                    String value = URLDecoder.decode(params[1], HTTP.UTF_8);
                    valueMap.put(key, value);
                }
                UiThreadHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MessagePushUtils pushUtils = new MessagePushUtils(MainFragment.this);
                            pushUtils.onCallMethod(method, valueMap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 200);

            } else {
                final String methodName = url.replace("tknowbox://method/", "");
                UiThreadHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MessagePushUtils pushUtils = new MessagePushUtils(MainFragment.this);
                            pushUtils.onCallMethod(methodName, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String dataString = intent.getDataString();
            String scheme = intent.getScheme();
            if (!TextUtils.isEmpty(dataString) && !TextUtils.isEmpty(scheme)) {
                handleUrlLoading(dataString);
            } else {
            }
        }
    }

    @Override
    public void onDestroyViewImpl() {
        super.onDestroyViewImpl();
        MsgCenter.unRegisterLocalReceiver(mReceiver);
        if (mNotifitionDialog != null && mNotifitionDialog.isShowing()) {
            mNotifitionDialog.dismiss();
        }
    }


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case com.buang.welewolf.R.id.main_tab_game: {
                    mViewPager.setCurrentItem(SCENE_GAME, true);
                    break;
                }
                case com.buang.welewolf.R.id.main_tab_list: {
                    mViewPager.setCurrentItem(SCENE_LIST, true);
                    break;
                }
                case com.buang.welewolf.R.id.main_tab_message: {
                    mViewPager.setCurrentItem(SCENE_MESSAGE, true);
                    break;
                }
                default:
                    break;
            }
        }
    };

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            setCurrentTab(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    public void setCurrentItem(int tab) {
        mViewPager.setCurrentItem(tab, true);
    }


    /**
     * 同步Tab显示状态
     *
     * @param tabId
     */
    private void setCurrentTab(int tabId) {
        this.mCurrentTab = tabId;
        switch (tabId) {
            case SCENE_MESSAGE: {
                mTabMessage.setSelected(true);
                mTabGame.setSelected(false);
                mTabList.setSelected(false);
                updateProfileTips(false);
                break;
            }
            case SCENE_GAME: {
                mTabMessage.setSelected(false);
                mTabGame.setSelected(true);
                mTabList.setSelected(false);
                updateHomeworkTips(false);
                break;
            }
            case SCENE_LIST: {
                mTabMessage.setSelected(false);
                mTabGame.setSelected(false);
                mTabList.setSelected(true);
                updateClassTips(false);
                break;
            }
            default:
                break;
        }
    }

    public int getCurrentTab() {
        return mCurrentTab;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            if (ACTION_TAB_TIPS.equals(action)) {
                int type = intent.getIntExtra("type", -1);
                boolean visible = intent.getBooleanExtra("visible", false);
                updateTabTips(type, visible);
            } else if (action.equals(ActionUtils.ACTION_VIRTUAL_TIPS)) {
                showLoginFragment();
            } else if (action.equals(ActionUtils.ACTION_MAIN_TAB)) {
                int tab = intent.getIntExtra("tab", TYPE_TAB_TIPS_CLASS);
                setCurrentItem(tab);
                removeAllFragment();
            } else if (action.equals(ActionUtils.ACTION_SHOW_REDBADGE)) {
                int type = Integer.parseInt(intent.getStringExtra("type"));
                updateTabTips(type, true);
            }
        }
    };

    private void showLoginFragment() {
        LoginFragment fragment = (LoginFragment) Fragment.instantiate(
                getActivity(), LoginFragment.class.getName(), null);
        showFragment(fragment);
    }

    private void updateTabTips(int type, boolean visible) {
        switch (type) {
            case TYPE_TAB_TIPS_CLASS:
                updateHomeworkTips(visible);
                break;
            case TYPE_TAB_TIPS_BANK:
                updateClassTips(visible);
                break;
            case TYPE_TAB_TIPS_PROFILE:
                updateProfileTips(visible);
                break;
            default:
                break;
        }

    }

    /**
     * 设置Tab是否可见
     *
     * @param visible
     */
    public void setTabViewVisible(boolean visible) {
        if (mTabGroup != null) {
            mTabGroup.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void updateProfileTips(boolean visible) {
        if (mTabProfileTips != null) {
            mTabProfileTips.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void updateClassTips(boolean hasTransfer) {
        if (mTabClassTips != null) {
            mTabClassTips.setVisibility(hasTransfer ? View.VISIBLE : View.GONE);
        }
    }

    public void updateHomeworkTips(boolean visible) {
        if (mTabHomeworkTips != null) {
            mTabHomeworkTips.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    private class MainFragmentAdapter extends FragmentPagerAdapter {

        public MainFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mSparseArray.get(position);
        }

        @Override
        public int getCount() {
            return mSparseArray.size();
        }
    }

    public void setVisibleToUser(boolean visible) {
        if (mSparseArray != null && mCurrentTab < mSparseArray.size()) {
            BaseSubFragment fragment = mSparseArray.get(mCurrentTab);
            fragment.setVisibleToUser(visible);
        }
    }

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        if (mNotifitionDialog != null && mNotifitionDialog.isShowing()) {
            mNotifitionDialog.dismiss();
        }
        try {
            removeAllFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean mExitMode = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (getActivity() != null) {
                if (mExitMode) {
                    ((App) BaseApp.getAppContext()).exit();
                    getActivity().finish();
                } else {
                    ToastUtils.showShortToast(getActivity(), "再按一次后退键退出程序");
                    mExitMode = true;
                    UiThreadHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mExitMode = false;
                        }
                    }, 2000);
                }
            }
            return true;
        }
        return false;
    }


    public void showLoading() {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mLoadingView.setVisibility(View.VISIBLE);
                Animation anim = AnimationUtils.loadAnimation(getActivity(), com.buang.welewolf.R.anim.anim_loading_2);
                mLoadingImg.startAnimation(anim);
                anim.start();

            }
        });
    }

    public void hideLoading() {
        mLoadingImg.clearAnimation();
        mLoadingView.setVisibility(View.GONE);
    }

    private OnMainFragmentLoadListener mOnMainFragmentLoadListener;

    public void setOnMainFragmentLoadListener(OnMainFragmentLoadListener listener) {
        mOnMainFragmentLoadListener = listener;
    }

    public interface OnMainFragmentLoadListener {
        public void onMainLoad();
    }
}
