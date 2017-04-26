/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.knowbox.teacher.modules.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.UrlModelPair;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineLoginInfo;
import com.knowbox.teacher.base.bean.OnlineTeacherExtInfo;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.base.database.tables.UserTable;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.modules.debugs.DebugFragment;
import com.knowbox.teacher.modules.login.services.LoginService;
import com.knowbox.teacher.modules.login.services.UpdateSchoolListener;
import com.knowbox.teacher.modules.profile.ActivityWebViewFragment;
import com.knowbox.teacher.modules.profile.SettingsFragment;
import com.knowbox.teacher.modules.profile.UserAuthNotyetFragment;
import com.knowbox.teacher.modules.profile.UserInfoEditFragment;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.UmengConstant;
import com.knowbox.teacher.modules.utils.Utils;
import com.knowbox.teacher.modules.utils.VirtualClassUtils;
import com.knowbox.teacher.widgets.AdvanceTimer;
import com.knowbox.teacher.widgets.RoundDisplayer;

/**
 * 我的页面 首页
 *
 * @author weilei
 */
public class MainProfileFragment extends BaseUIFragment<UIFragmentHelper> {

    private static final int ACTION_GETINFO = 1;
    private static final int ACTION_GETEXT = 2;

    public static final String ACTION_USERINFO_CHANGE = "com.knowbox.teacher_userinfochange";
    private UserItem mUserItem;
    private View mDebugLayout;
    private OnlineTeacherExtInfo mTeacherExtInfo;
    private LoginService mLoginService;

    private TextView mUserNameText;
    private TextView mSubjectText;
    private TextView mSchoolText;
    private ImageView mUserHeadPhoto;
    private ListView mOtherItemLayout;

    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
        super.onCreateImpl(savedInstanceState);
        setSlideable(false);
        setTitleStyle(STYLE_NO_TITLE);
        mUserItem = Utils.getLoginUserItem();
        mLoginService = (LoginService) getSystemService(LoginService.SERVICE_NAME);
        mLoginService.getServiceObvserver().addUpdateSchoolListener(updateSchoolListener);
    }

    @Override
    public View onCreateViewImpl(Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.layout_main_profile, null);
    }

    @Override
    public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
        super.onViewCreatedImpl(view, savedInstanceState);
        initView(view);
        IntentFilter intentFilter = new IntentFilter(ACTION_USERINFO_CHANGE);
        MsgCenter.registerLocalReceiver(mReceiver, intentFilter);
        if (!VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
            loadData(ACTION_GETINFO, PAGE_MORE);
        }
    }

    private void initView(View view) {
        mUserNameText = (TextView) view.findViewById(R.id.profile_name);
        mSubjectText = (TextView) view.findViewById(R.id.profile_subject);
        mSchoolText = (TextView) view.findViewById(R.id.profile_school);
        mUserHeadPhoto = (ImageView) view.findViewById(R.id.profile_icon);
        mDebugLayout = view.findViewById(R.id.profile_debug_layout);

        mOtherItemLayout = (ListView) view.findViewById(R.id.other_item_layout);
        mOtherItemLayout.setVisibility(View.GONE);
        setUserInfoData();

        view.findViewById(R.id.profile_settings_layout)
                .setOnClickListener(mOnClickListener);
        view.findViewById(R.id.profile_edit)
                .setOnClickListener(mOnClickListener);
        view.findViewById(R.id.profile_help_layout)
                .setOnClickListener(mOnClickListener);
        mUserHeadPhoto.setOnClickListener(mOnClickListener);
        mDebugLayout.setOnClickListener(mOnClickListener);
    }

    private void setUserInfoData() {
        if (mUserItem == null) {
            return;
        }
        if (mUserNameText != null){
            if (TextUtils.isEmpty(mUserItem.userName)) {
                String mobile = mUserItem.loginName.substring(7);
                mUserNameText.setText("尾号为" + mobile + "的老师");
            }else {
                mUserNameText.setText(mUserItem.userName);
            }
        }
        if (mSubjectText != null) {
            mSubjectText.setText("英语");
        }
        if (mSchoolText != null) {
            if (TextUtils.isEmpty(mUserItem.schoolName)) {
                mSchoolText.setText("未设置学校");
            }else {
                mSchoolText.setText(mUserItem.schoolName);
            }
        }
        if (mUserHeadPhoto != null) {
            ImageFetcher.getImageFetcher().loadImage(mUserItem.headPhoto,
                    mUserHeadPhoto, R.drawable.profile_icon_default, new RoundDisplayer());
        }
    }

    public void updateExtInfo(final OnlineTeacherExtInfo info) {
        if (info.mInfos.size() > 0) {
            final ExtAdapter adapter = new ExtAdapter(getActivity());
            adapter.setItems(info.mInfos);
            mOtherItemLayout.setAdapter(adapter);
            mOtherItemLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    OnlineTeacherExtInfo.TeacherExtInfo extInfo = adapter.getItem(position);
                    openActivityFragment(extInfo.mTitle, extInfo.mUrl);
                    if (extInfo.mHasNew == 1) {
                        extInfo.mHasNew = 0;
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            mOtherItemLayout.setVisibility(View.VISIBLE);
        } else {
            mOtherItemLayout.setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            // 重新加载用户信息
			if (MainProfileFragment.ACTION_USERINFO_CHANGE.equals(action)) {
				mUserItem = Utils.getLoginUserItem();
                setUserInfoData();
			}
        }
    };

    /**
     * 打开settings
     *
     * @author weilei
     */
    private void openSettingFragment() {
        SettingsFragment fragment = (SettingsFragment) Fragment.instantiate(
                getActivity(), SettingsFragment.class.getName(), null);
        showFragment(fragment);
    }

    /**
     * 打开我的资料页
     *
     * @author weilei
     */
    private void openProfileEditFragment() {
        UserInfoEditFragment fragment = (UserInfoEditFragment) Fragment
                .instantiate(getActivity(),
                        UserInfoEditFragment.class.getName(), null);
        showFragment(fragment);
    }

    private void openHelpFragment() {
        Bundle mBundle = new Bundle();
        mBundle.putString("url", OnlineServices.getHelpUrl());
        mBundle.putString("title", "客服中心");
        ActivityWebViewFragment fragment = (ActivityWebViewFragment) Fragment
                .instantiate(getActivity(),
                        ActivityWebViewFragment.class.getName(), mBundle);
        showFragment(fragment);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.profile_settings_layout:// 设置
                    openSettingFragment();
                    UmengConstant.reportUmengEvent(UmengConstant.EVENT_SETTINGS, null);
                    break;
                case R.id.profile_edit:// 我的资料
                    openProfileEditFragment();
                    break;
                case R.id.profile_debug_layout:// 开发者模式
                    showDebug();
                    break;
                case R.id.profile_icon:// 连续点击5次头像显示debug开关
                    switchDebug();
                    break;
                case R.id.profile_help_layout:
                    openHelpFragment();
                    UmengConstant.reportUmengEvent(UmengConstant.EVENT_HELP, null);
                    break;
                default:
                    break;
            }
        }

		
    };

    private void openUserAuthenticationFragment() {
    	UserAuthNotyetFragment fragment = (UserAuthNotyetFragment) Fragment
                .instantiate(getActivity(),
                		UserAuthNotyetFragment.class.getName(), null);
        showFragment(fragment);
    }

    private void openActivityFragment(String title, String url) {
        Bundle mBundle = new Bundle();
        mBundle.putString("title", title);
        mBundle.putString("url", url);
        ActivityWebViewFragment fragment = (ActivityWebViewFragment) Fragment.instantiate(
                getActivity(), ActivityWebViewFragment.class.getName(), mBundle);
        showFragment(fragment);
    }

    private int mCurCount;
    private AdvanceTimer mTimer;

    private void switchDebug() {
        if (mCurCount == 0) {
            mTimer = new AdvanceTimer();
            mTimer.setCurSeconds(2);
            mTimer.start();
            mCurCount++;
        } else if ((mTimer.getCurSeconds() > 0)) {
            mTimer = new AdvanceTimer();
            mTimer.setCurSeconds(2);
            mTimer.start();
            mCurCount++;
            if (mCurCount >= 5) {
                if (mDebugLayout.getVisibility() == View.GONE) {
                    mDebugLayout.setVisibility(View.VISIBLE);
                } else {
                    mDebugLayout.setVisibility(View.GONE);
                }
                mCurCount = 0;
                mTimer.stop();
            }
        }
    }

    private void showDebug() {
        DebugFragment fragment = (DebugFragment) Fragment.instantiate(
                getActivity(), DebugFragment.class.getName());
        showFragment(fragment);
    }

    class ExtAdapter extends SingleTypeAdapter<OnlineTeacherExtInfo.TeacherExtInfo> {

        public ExtAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(getActivity(), R.layout.layout_profile_item, null);
                viewHolder.mTitle = (TextView) convertView.findViewById(R.id.profile_item_title);
                viewHolder.mDesc = (TextView) convertView.findViewById(R.id.profile_item_desc);
                viewHolder.mIcon = (ImageView) convertView.findViewById(R.id.profile_item_icon);
                viewHolder.mTip = (ImageView) convertView.findViewById(R.id.profile_item_tip);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OnlineTeacherExtInfo.TeacherExtInfo teacherExtInfo = getItem(position);
            viewHolder.mTitle.setText(teacherExtInfo.mTitle);
            if (TextUtils.isEmpty(teacherExtInfo.mDesc)) {
                viewHolder.mDesc.setVisibility(View.GONE);
            } else {
                viewHolder.mDesc.setText(teacherExtInfo.mDesc);
                viewHolder.mDesc.setVisibility(View.VISIBLE);
            }

            ImageFetcher.getImageFetcher().loadImage(teacherExtInfo.icon, viewHolder.mIcon, R.drawable.icon_profile_shopping);
            if (teacherExtInfo.mHasNew == 1) {
                viewHolder.mTip.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mTip.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    class ViewHolder {
        public TextView mTitle;
        public TextView mDesc;
        public ImageView mIcon;
        public ImageView mTip;
    }

    @Override
    public void setVisibleToUser(boolean visible) {
        super.setVisibleToUser(visible);
        if (visible) {
            loadData(ACTION_GETEXT, PAGE_MORE);
        }
    }

    @Override
    public void onDestroyImpl() {
        super.onDestroyImpl();
        MsgCenter.unRegisterLocalReceiver(mReceiver);
        if (mTimer != null) {
            mTimer.destory();
        }
        if (null != mLoginService) {
            mLoginService.getServiceObvserver().removeUpdateSchoolListener(updateSchoolListener);
        }
    }

    @Override
    public void onPreAction(int action, int pageNo) {}

    @Override
    public BaseObject onProcess(int action, int pageNo, Object... params) {
        super.onProcess(action, pageNo, params);
        if (action == ACTION_GETINFO) {
            String url = OnlineServices.getUserInfoUrl();
            return new DataAcquirer<OnlineLoginInfo>().acquire(url, new OnlineLoginInfo(), -1);
        } else if (action == ACTION_GETEXT) {
            String url = OnlineServices.getTeacherExtUrl(Utils.getToken());
            return new DataAcquirer<OnlineTeacherExtInfo>().acquire(url, new OnlineTeacherExtInfo(), -1);
        }
        return null;
    }

    @Override
    public UrlModelPair getRequestUrlModelPair(int action, int pageNo, Object... params) {
        if (action == ACTION_GETEXT) {
            return new UrlModelPair(OnlineServices.getTeacherExtUrl(Utils.getToken()), new OnlineTeacherExtInfo());
        }
        return super.getRequestUrlModelPair(action, pageNo, params);
    }

    @Override
    public void onGetCache(int action, int pageNo, BaseObject result) {
        super.onGetCache(action, pageNo, result);
        if (action == ACTION_GETEXT && pageNo == PAGE_FIRST) {
            updateExtInfo((OnlineTeacherExtInfo) result);
        }
    }

    @Override
    public void onGet(int action, int pageNo, BaseObject result, Object... params) {
        super.onGet(action, pageNo, result, params);
        if (action == ACTION_GETINFO) {
            OnlineLoginInfo loginInfo = (OnlineLoginInfo) result;
            mUserItem = loginInfo.mUserItem;
            DataBaseManager.getDataBaseManager().getTable(UserTable.class)
                    .updateCurrentUserInfo(mUserItem);
            mLoginService.clearUserInfo();
            setUserInfoData();
//            loadData(ACTION_GETEXT, PAGE_FIRST);
        } else if (action == ACTION_GETEXT) {
            mTeacherExtInfo = (OnlineTeacherExtInfo) result;
            updateExtInfo((OnlineTeacherExtInfo) result);
        }
    }

    @Override
    public void onFail(int action, int pageNo, BaseObject result, Object... params) {
        if (action == ACTION_GETINFO) {
            loadData(ACTION_GETEXT, PAGE_MORE);
        } else if (action == ACTION_GETEXT) {
        }
    }

    private UpdateSchoolListener updateSchoolListener = new UpdateSchoolListener() {
        @Override
        public void onUpdateSuccess(UserItem userItem, boolean isUserDefined) {
            mUserItem = Utils.getLoginUserItem();
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSchoolText.setText(mUserItem.schoolName);
                }
            });
        }

        @Override
        public void onUpdateFailed(String error, boolean isUserDefined) {
        }
    };
}
