/*
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.buang.welewolf.modules.main;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.buang.welewolf.App;
import com.buang.welewolf.base.bean.OnlineGlobalInfo;
import com.buang.welewolf.base.bean.OnlineSchoolTeacherInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.utils.ActionUtils;
import com.buang.welewolf.base.utils.PreferencesController;
import com.buang.welewolf.modules.message.utils.MessagePushUtils;
import com.buang.welewolf.modules.profile.ActivityWebViewFragment;
import com.buang.welewolf.modules.utils.ConstantsUtils;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.SubjectUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.UmengConstant;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.modules.utils.VirtualClassUtils;
import com.buang.welewolf.welewolf.fragment.MainGameFragment;
import com.buang.welewolf.welewolf.fragment.MainMessageFragment;
import com.buang.welewolf.welewolf.fragment.MainRankFragment;
import com.buang.welewolf.welewolf.login.LoginFragment;
import com.hyena.framework.app.fragment.BaseSubFragment;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.service.share.ShareService;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

/**
 * 主场景
 * 
 * @author yangzc
 *
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

	private ShareService mShareService;

	public static final String ACTION_TAB_TIPS = "action_tab_tips";
	public static final int TYPE_TAB_TIPS_CLASS = 0;
	public static final int TYPE_TAB_TIPS_BANK = 1;
	public static final int TYPE_TAB_TIPS_PROFILE = 2;
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

		new DataLoaderTask().execute();

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
			String dataString  = intent.getDataString();
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
		if(mNotifitionDialog != null && mNotifitionDialog.isShowing()) {
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
				if (VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
					ActionUtils.notifyVirtualTip();
					break;
				}
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
			if (VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
				if (position > SCENE_LIST) {
					ActionUtils.notifyVirtualTip();
					setCurrentItem(SCENE_LIST);
					return;
				}
			}
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
			mTabProfileTips.setVisibility(visible ? View.VISIBLE :View.GONE);
		}
	}

	public void updateClassTips(boolean hasTransfer) {
		if (mTabClassTips != null) {
			mTabClassTips.setVisibility(hasTransfer ? View.VISIBLE :View.GONE);
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
		if(mNotifitionDialog != null && mNotifitionDialog.isShowing()) {
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
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
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

	private class DataLoaderTask extends AsyncTask<Object, Void, BaseObject> {

		public DataLoaderTask() {
			super();
		}

		@Override
		protected BaseObject doInBackground(Object... params) {
			try {
				String url = OnlineServices.getTeacherGlobalInfoUrl();
				OnlineGlobalInfo result = new DataAcquirer<OnlineGlobalInfo>().acquire(
						url, new OnlineGlobalInfo(), -1);
				if (result != null && result.isAvailable()) {
					return result;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(BaseObject result) {
			super.onPostExecute(result);
			if (result == null) {
				return;
			}
			try {
				OnlineGlobalInfo info = (OnlineGlobalInfo)result;
				if (info != null && info.mInfos != null && info.mInfos.size() > 0) {
					showNotifyDialog(info);
				}
				if (info != null && info.maxQuestionNumPerHomework > 0) {
					PreferencesController.setInt(ConstantsUtils.MAX_QUESTION_NUM_PREHOMEWORK, info.maxQuestionNumPerHomework);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class GlobalCallBack extends AsyncTask<Object, Void, BaseObject> {

		public GlobalCallBack() {
			super();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showLoading();
		}

		@Override
		protected BaseObject doInBackground(Object... params) {
			String url = OnlineServices.getGlobalCallBackUrl((String) params[0]);
			BaseObject result = new DataAcquirer<BaseObject>().acquire(url,
					new BaseObject(), -1);
			if (result != null && result.isAvailable()) {
				return result;
			}
			return null;
		}

		@Override
		protected void onPostExecute(BaseObject result) {
			super.onPostExecute(result);
			hideLoading();
		}
	}

	private class ClassTransferTask extends AsyncTask<Object, Void, BaseObject> {

		public ClassTransferTask() {
			super();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showLoading();
		}

		@Override
		protected BaseObject doInBackground(Object... params) {
			String url = OnlineServices.getTransferClassUrl(Utils.getToken());
			JSONObject object = new JSONObject();
			try {
				object.put("to_teacher_id", (String) params[0]);
				object.put("class_id", (String) params[1]);
				object.put("status", (String) params[2]);
				object.put("is_my_class", (String) params[3]);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String data = object.toString();
			if (TextUtils.isEmpty(data))
				return null;
			BaseObject result = new DataAcquirer<BaseObject>().post( url,
					data, new BaseObject());
			if (result != null && result.isAvailable()) {
				return result;
			}
			return null;
		}

		@Override
		protected void onPostExecute(BaseObject result) {
			super.onPostExecute(result);
			hideLoading();
			if (result != null && result.isAvailable()) {
				ToastUtils.showShortToast(BaseApp.getAppContext(), "接收成功");
			} else {
				ToastUtils.showShortToast(BaseApp.getAppContext(), "接收失败,请稍后重试");
				updateClassTips(true);
			}
		}
	}

	private void showNotifyDialog(OnlineGlobalInfo info) {
		Stack<OnlineGlobalInfo.OnlineGlobalInfoItem> mStack = new Stack<OnlineGlobalInfo.OnlineGlobalInfoItem>();
		for (int i = info.mInfos.size() - 1; i >= 0; i--) {
			mStack.add(info.mInfos.get(i));
		}
		showNotifyDialog(mStack);
	}

	/**
	 * 显示通知对话框
	 */
	private void showNotifyDialog(final Stack<OnlineGlobalInfo.OnlineGlobalInfoItem> items) {
		if(mNotifitionDialog != null && mNotifitionDialog.isShowing()) {
			mNotifitionDialog.dismiss();
		}
		int icon = 0;
		String message = "";
		String confirm = "";
		String cancel = null;
		String highTxt = "";
		final OnlineGlobalInfo.OnlineGlobalInfoItem item = items.pop();
		final OnlineSchoolTeacherInfo.TeacherInfo info = (OnlineSchoolTeacherInfo.TeacherInfo)
				item.mObject;
		Date date = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		if (info != null) {
			date = new Date(Long.parseLong(info.mAddTime) * 1000);
		}

		switch (item.mInfoType) {
			case OnlineGlobalInfo.TYPE_CLASS_TRANSFER_NOTIFYTION:
				icon = com.buang.welewolf.R.drawable.icon_class_transfer_tip;
				message = Utils.getLoginUserItem().userName + "老师将" +
						SubjectUtils.getGradeName(info.mGrade) + info.mClassName
						+ "转交于您，确认接收后开始和学生们一起使用单词部落吧！";
				highTxt = SubjectUtils.getGradeName(info.mGrade) + info.mClassName
						+ "转交";
				confirm = "接受班群";
				cancel = "稍后看看";
				break;
			case OnlineGlobalInfo.TYPE_CLASS_TRANSFER_REFUE:
				message = "你于" + formatter.format(date) + "\n" + "转交班群" +
						SubjectUtils.getGradeName(info.mGrade) + info.mClassName
						+ "\n" + "被" + info.mUserName + "(" + info.mMobile + ")拒绝";
				icon = com.buang.welewolf.R.drawable.icon_class_transfer_refuse;
				confirm = "我知道了";
				break;
			case OnlineGlobalInfo.TYPE_CLASS_TRANSFER_SUCCESS:
				message = "你于" + formatter.format(date) + "\n" + "转交班群" +
						SubjectUtils.getGradeName(info.mGrade) + info.mClassName
						+ "\n" + "被" + info.mUserName + "(" + info.mMobile + ")接收";
				icon = com.buang.welewolf.R.drawable.icon_class_transfer_sucess;
				confirm = "我知道了";
				break;
			case OnlineGlobalInfo.TYPE_ACTIVITY:
				showActivityFragment(item, items);
				return;
		}
		mNotifitionDialog = DialogUtils.getClassTransferDialog(getActivity(), highTxt, confirm,
				cancel, message, icon, new DialogUtils.OnDialogButtonClickListener() {
					@Override
					public void onItemClick(Dialog dialog, int btnId) {
						if (item.mInfoType == OnlineGlobalInfo.TYPE_CLASS_TRANSFER_NOTIFYTION) {
							if (btnId == DialogUtils.OnDialogButtonClickListener.BUTTON_CONFIRM) {
								if (TextUtils.isEmpty(info.mTeacherId)) {
									info.mTeacherId = Utils.getLoginUserItem().loginName;
								}
								new ClassTransferTask().execute(info.mTeacherId, info.mClassId,
										"1", "0");
							} else {
								updateClassTips(true);
							}
						} else if (item.mInfoType == OnlineGlobalInfo.TYPE_CLASS_TRANSFER_REFUE ||
								item.mInfoType == OnlineGlobalInfo.TYPE_CLASS_TRANSFER_SUCCESS) {
							new GlobalCallBack().execute(String.valueOf(item.mInfoID));
						}
						mNotifitionDialog.dismiss();
					}
				});
		mNotifitionDialog.show();

		mNotifitionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				UiThreadHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (!items.isEmpty())
							showNotifyDialog(items);
					}
				}, 200);
			}
		});
	}

	private void showActivityFragment(final OnlineGlobalInfo.OnlineGlobalInfoItem item, final Stack<OnlineGlobalInfo.OnlineGlobalInfoItem> items) {
		if (PreferencesController.getBoolean(PREFS_ACTIVITY + item.mInfoID, false)) {
			return;
		}
		mNotifitionDialog = DialogUtils.getActivityDialog(getActivity(), item.mImg, new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivityFragment(item);
				mNotifitionDialog.dismiss();
			}
		}, new DialogUtils.OnLoadCompleteListener() {
			@Override
			public void onLoadComplete() {
				mNotifitionDialog.show();
				PreferencesController.setBoolean(PREFS_ACTIVITY + item.mInfoID, true);
			}
		});

		mNotifitionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				UiThreadHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (!items.isEmpty())
							showNotifyDialog(items);
					}
				}, 200);
			}
		});
	}

	/**
	 * 打开活动页
	 * @param item
	 */
	private void openActivityFragment(final OnlineGlobalInfo.OnlineGlobalInfoItem item) {
		if (TextUtils.isEmpty(item.mUrl)) {
			return;
		}

		UmengConstant.reportUmengEvent(UmengConstant.EVENT_ACTIVITY_FRAGMENT_OPEN, null);
		Bundle mBundle = new Bundle();
		mBundle.putString("title", item.mTitle);
		mBundle.putString("url", item.mUrl);
		ActivityWebViewFragment fragment = (ActivityWebViewFragment) Fragment.
				instantiate(getActivity(), ActivityWebViewFragment.class.getName(), mBundle);
		showFragment(fragment);
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
