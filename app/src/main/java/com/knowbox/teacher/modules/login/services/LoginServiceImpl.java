/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.knowbox.teacher.modules.login.services;

import android.text.TextUtils;

import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.datacache.db.DataCacheTable;
import com.hyena.framework.error.ErrorManager;
import com.hyena.framework.utils.BaseApp;
import com.knowbox.teacher.base.bean.OnlineLoginInfo;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.base.database.tables.UserTable;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.base.utils.PreferencesController;
import com.knowbox.teacher.modules.utils.ConstantsUtils;
import com.knowbox.teacher.modules.utils.DeviceUtils;
import com.knowbox.teacher.modules.utils.UmengConstant;
import com.knowbox.teacher.modules.utils.Utils;
import com.knowbox.teacher.widgets.AdvanceTimer;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录实现类
 * 
 * @author yangzc
 *
 */
public class LoginServiceImpl implements LoginService {

	// 当前登录的用户信息
	private UserItem mLoginUser;
	// 服务观察者
	private LoginServiceObserver mLoginServiceObserver = new LoginServiceObserver();
	// 注册用信息构造器
	private RegistUserInfoBuilder mRegistUserInfoBuilder = new RegistUserInfoBuilder();

	//登录验证码倒计时
	private AdvanceTimer mReSendSmsCodeTimer;
	private AdvanceTimer mSmsCodeValidTimer;

	@Override
	public void executeCountDown(int time, String lastTime, AdvanceTimer.TimeChangeListener listener) {
		// 启动60s重新获取倒计时
		mReSendSmsCodeTimer = new AdvanceTimer();
		mReSendSmsCodeTimer.setCurSeconds(time);
		mReSendSmsCodeTimer.setTimeChangeListener(listener);
		mReSendSmsCodeTimer.start();

		// 启动300s验证码有效时间倒计时
		mSmsCodeValidTimer = new AdvanceTimer();
		mSmsCodeValidTimer.setCurSeconds(Integer
				.parseInt(lastTime));
		mSmsCodeValidTimer.start();
	}

	@Override
	public void continueTimer(AdvanceTimer.TimeChangeListener listener) {
		if (null != mReSendSmsCodeTimer) {
			mReSendSmsCodeTimer.setTimeChangeListener(listener);
		}
	}

	@Override
	public AdvanceTimer getReSendSmsCodeTimer() {
		return mReSendSmsCodeTimer;
	}

	@Override
	public AdvanceTimer getSmsCodeValidTimer() {
		return mSmsCodeValidTimer;
	}

	@Override
	public void releaseTimer() {
		if (null != mReSendSmsCodeTimer) {
			mReSendSmsCodeTimer.destory();
			mReSendSmsCodeTimer = null;
		}
		if (null != mSmsCodeValidTimer) {
			mSmsCodeValidTimer.destory();
			mSmsCodeValidTimer = null;
		}
	}

	@Override
	public void clearUserInfo() {
		mLoginUser = null;
	}

	@Override
	public void updateSchool(final String[] keys, final String[] values, final boolean isUserDefined) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = OnlineServices.getUpdateUserInfo();
				JSONObject object = new JSONObject();
				if (isUserDefined) {
					if (keys.length == 2 && values.length == 2) {
						try {
							object.put(keys[0], values[0]);
							object.put(keys[1], values[1]);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}else {
						return;
					}
				}else {
					if (keys.length == 1 && values.length == 1) {
						try {
							object.put(keys[0], values[0]);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}else {
						return;
					}
				}
				OnlineLoginInfo result = new DataAcquirer<OnlineLoginInfo>()
						.post(url, object.toString(), new OnlineLoginInfo());
				if (result.isAvailable()) {
					UserItem item = Utils.getLoginUserItem();
					item.schoolName = result.mUserItem.schoolName;
					item.school = result.mUserItem.schoolName;
					item.city = result.mUserItem.city;
					UserTable table = DataBaseManager.getDataBaseManager().getTable(UserTable.class);
					table.updateCurrentUserInfo(item);
					getServiceObvserver().notifyUpdateSchoolSuccess(item, isUserDefined);
				} else {
					getServiceObvserver().notifyUpdateSchoolFailed(result.getRawResult(), isUserDefined);
				}
			}
		}).start();
	}


	@Override
	public void updateJiaoCai(final String clazzName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = OnlineServices.getUpdateUserInfo();
				JSONObject object = new JSONObject();
				String mPublisherId = PreferencesController.getStringValue(ConstantsUtils.BUNK_PUBLISHER_VALUE);
				String mRequireBookId = PreferencesController.getStringValue(ConstantsUtils.BUNK_REQUIREBOOK_VALUE);
				String mPublisherName = PreferencesController.getStringValue(ConstantsUtils.BUNK_PUBLISHER_NAME);
				String mRequireBookName = PreferencesController.getStringValue(ConstantsUtils.BUNK_REQUIREBOOK_NAME);
				try {
					if (TextUtils.isEmpty(mRequireBookId)) {
						object.put("book_id", "0");
					}else {
						object.put("book_id", mRequireBookId);
					}
					if (TextUtils.isEmpty(mPublisherId)) {
						object.put("edition_id", "0");
					}else {
						object.put("edition_id", mPublisherId);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String data = object.toString();
				OnlineLoginInfo result = new DataAcquirer<OnlineLoginInfo>()
						.post(url, data, new OnlineLoginInfo());
				if (result.isAvailable()) {
					UserItem item = Utils.getLoginUserItem();
					item.mBookId = mRequireBookId;
					item.mEditionId = mPublisherId;
					item.mBookName = mRequireBookName;
					item.mEditionName = mPublisherName;
					UserTable table = DataBaseManager.getDataBaseManager().getTable(UserTable.class);
					table.updateCurrentUserInfo(item);
					getServiceObvserver().notifyUpdateJiaocaiSuccess(item, clazzName);
				}else {
					getServiceObvserver().notifyUpdateJiaocaiFailed(result.getRawResult(), clazzName);
				}
			}
		}).start();
	}

	@Override
	public void releaseAll() {
		mLoginUser = null;
	}

	@Override
	public boolean isLogin() {
		getLoginUser();
		return mLoginUser != null;
	}

	@Override
	public UserItem getLoginUser() {
		if (mLoginUser != null) {
			return mLoginUser;
		}
		UserTable table = DataBaseManager.getDataBaseManager().getTable(
				UserTable.class);
		if (table != null) {
			List<UserItem> users = table.queryAll();
			if (users != null && users.size() > 0) {
				UserItem user = users.get(0);
				this.mLoginUser = user;
				return user;
			}
		}
		return null;
	}

	@Override
	public void login(final String phone, final String pwd,
			final LoginListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Map<String, String> umengCount = new HashMap<String, String>();
				long reqTime = System.currentTimeMillis();
				if (listener != null) {
					listener.onLoginStart();
				}

				// 登录要求实时性高，不能使用缓存
				String loginUrl = OnlineServices.getLoginUrl();
				JSONObject object = new JSONObject();
				try {
					object.put("mobile", phone);
					object.put("password", pwd);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String data = object.toString();
				OnlineLoginInfo loginInfo = new DataAcquirer<OnlineLoginInfo>()
						.post(loginUrl, data, new OnlineLoginInfo());
				if (loginInfo.isAvailable()) {
					releaseTimer();
					umengCount.put(UmengConstant.RESULT, UmengConstant.SUCCESS);
					try {
						loginInfo.mUserItem.loginName = phone;
					} catch (Exception e) {
					}
					mLoginUser = loginInfo.mUserItem;
					if (listener != null) {
						listener.onLoginSuccess(loginInfo);
					}
					getServiceObvserver().notifyOnLogin(mLoginUser);
					if (mLoginUser != null) {
//						umengCount.put(UmengConstant.LOGIN_USER_INFO_SUBJECT,
//								SubjectUtils.getNameByCode(Integer.parseInt(mLoginUser.subjectCode)));
						umengCount.put(UmengConstant.LOGIN_USER_INFO_SCHOOL,
								mLoginUser.schoolName);
					}
				} else {
					String message = "网络连接异常，请稍后再试";
					if (!TextUtils.isEmpty(loginInfo.getRawResult())) {
						if ("20201".equals(loginInfo.getRawResult())) {
							message = "用户名或密码错误";
						} else {
							message = ErrorManager.getErrorManager().getErrorHint(loginInfo.getRawResult(), loginInfo.getErrorDescription());
						}

					}
					if (listener != null) {
						listener.onLoginFailed(message);
					}
					StringBuilder sb = new StringBuilder();
					sb.append("(statusCode = ").append(
							loginInfo.getStatusCode());
					if (!TextUtils.isEmpty(loginInfo.getRawResult())) {
						sb.append(" , rawCode = ")
								.append(loginInfo.getRawResult()).append(")");
					} else {
						sb.append(")");
					}
					umengCount.put(UmengConstant.RESULT, UmengConstant.FAIL
							+ sb.toString());
				}
				reqTime = System.currentTimeMillis() - reqTime;
				umengCount.put(UmengConstant.REQ_TIME, "" + (reqTime / 1000.0));
				MobclickAgent.onEvent(BaseApp.getAppContext(),
						UmengConstant.LOGIN, umengCount);
			}
		}).start();

	}

	@Override
	public void smsLogin(final String loginName, final String smsCode, final SmsLoginListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (listener != null) {
					listener.onSmsLoginStart();
				}

				String loginUrl = OnlineServices.getSmsLoginUrl();
				JSONObject object = new JSONObject();
//				String mPublisherId = PreferencesController.getStringValue(ConstantsUtils.BUNK_PUBLISHER_VALUE);
//				String mRequireBookId = PreferencesController.getStringValue(ConstantsUtils.BUNK_REQUIREBOOK_VALUE);
				try {
					object.put("mobile", loginName);
					object.put("code", smsCode);
					String deviceId = DeviceUtils.getSafeDeviceID();
//					if (!TextUtils.isEmpty(deviceId)) {
//						object.put("device", deviceId);
//					}
//					if (Utils.getLoginUserItem() == null || TextUtils.isEmpty(Utils.getLoginUserItem().subjectCode)) {
////						object.put("subject", -1);
////						object.put("grade_part", "");
//					} else {
//						object.put("subject", Utils.getLoginUserItem().subjectCode);
//						object.put("grade_part", Utils.getLoginUserItem().gradePart);
//						if (!TextUtils.isEmpty(Utils.getLoginUserItem().school)) {
//							object.put("school_id", Utils.getLoginUserItem().school);
//						}
//						if (!TextUtils.isEmpty(Utils.getLoginUserItem().schoolName)) {
//							object.put("school_name", Utils.getLoginUserItem().schoolName);
//						}
//						if (!TextUtils.isEmpty(Utils.getLoginUserItem().city)) {
//							object.put("city_id", Utils.getLoginUserItem().city);
//						}
//					}
//
//					if (!TextUtils.isEmpty(mRequireBookId)) {
//						object.put("jiaocai_id", mRequireBookId);
//					}
//					if (!TextUtils.isEmpty(mPublisherId)) {
//						object.put("teaching_id", mPublisherId);
//					}


				} catch (JSONException e) {
					e.printStackTrace();
				}
				String data = object.toString();
				OnlineLoginInfo loginInfo = new DataAcquirer<OnlineLoginInfo>()
						.post(loginUrl, data, new OnlineLoginInfo());
				if (loginInfo.isAvailable()) {
					try {
						releaseTimer();
						loginInfo.mUserItem.loginName = loginName;
					} catch (Exception e) {
					}
					mLoginUser = loginInfo.mUserItem;
					if (listener != null) {
						listener.onSmsLoginSuccess(loginInfo);
					}
					getServiceObvserver().notifyOnLogin(mLoginUser);
				} else {
					String message = ErrorManager.getErrorManager().getErrorHint(loginInfo.getRawResult(), loginInfo.getErrorDescription());
					if (listener != null) {
						listener.onSmsLoginFailed(message);
					}
				}
			}
		}).start();
	}

	@Override
	public void logout(final LogoutListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (listener != null) {
					listener.onLogoutStart();
				}
				try {
					//清缓存
					DataCacheTable cacheTable = DataBaseManager
							.getDataBaseManager().getTable(DataCacheTable.class);
					cacheTable.deleteByCase(null, null);
					removeAssist();
				} catch (Exception e) {
				}

				UserTable table = DataBaseManager.getDataBaseManager()
						.getTable(UserTable.class);
				table.deleteByCase(null, null);
				if (listener != null) {
					listener.onLogoutSuccess();
				}
				String token = "";
				if (mLoginUser != null) {
					token = mLoginUser.token;
					getServiceObvserver().notifyOnLogout(mLoginUser);
					mLoginUser = null;
				}

				if (!TextUtils.isEmpty(token)) {
					String logoutUrl = OnlineServices
							.getLogOutUrl();
					new DataAcquirer<BaseObject>().acquire(logoutUrl,
							new BaseObject(), -1);
				}

			}
		}).start();

	}
	
	private void removeAssist() {
		PreferencesController.setStringValue(ConstantsUtils.BUNK_PUBLISHER_NAME, null);
		PreferencesController.setStringValue(ConstantsUtils.BUNK_PUBLISHER_VALUE, null);
		PreferencesController.setStringValue(ConstantsUtils.BUNK_REQUIREBOOK_NAME, null);
		PreferencesController.setStringValue(ConstantsUtils.BUNK_REQUIREBOOK_VALUE, null);
		PreferencesController.setStringValue(ConstantsUtils.TIKU_PREVIEW_SECTIONID, "");
	}

	@Override
	public LoginServiceObserver getServiceObvserver() {
		return mLoginServiceObserver;
	}

	@Override
	public RegistUserInfoBuilder getUserInfoBuilder() {
		return mRegistUserInfoBuilder;
	}

	public static class RegistUserInfoBuilder {
		private String mPhoneNumber;
		private String mUserName;
		private String mSubjectCode;
		private String mPassword;
		private String mSchoolId;
		private String mCityId;
		private String mSchoolName;
		private String mGrade;
		private String mClassName;
		private String mSex;
		private String mEmsCode;
		private String mGradepart;

		public String getPhoneNumber() {
			return mPhoneNumber;
		}

		public String getPassword() {
			return mPassword;
		}

		public static RegistUserInfoBuilder getBuilder() {
			return new RegistUserInfoBuilder();
		}
		
		public RegistUserInfoBuilder setGradePart(String gradePart) {
			this.mGradepart = gradePart;
			return this;
		}

		public RegistUserInfoBuilder setPhoneNumber(String phoneNum) {
			this.mPhoneNumber = phoneNum;
			return this;
		}

		public RegistUserInfoBuilder setUserName(String userName) {
			this.mUserName = userName;
			return this;
		}

		public RegistUserInfoBuilder setSubjectCode(String subjectCode) {
			this.mSubjectCode = subjectCode;
			return this;
		}

		public RegistUserInfoBuilder setPassword(String pwd) {
			this.mPassword = pwd;
			return this;
		}

		public RegistUserInfoBuilder setSchoolId(String schoolId) {
			this.mSchoolId = schoolId;
			return this;
		}

		public RegistUserInfoBuilder setCityId(String cityId) {
			this.mCityId = cityId;
			return this;
		}

		public RegistUserInfoBuilder setSchoolName(String schoolName) {
			this.mSchoolName = schoolName;
			return this;
		}

		public RegistUserInfoBuilder setGrade(String grade) {
			this.mGrade = grade;
			return this;
		}

		public RegistUserInfoBuilder setClassName(String className) {
			this.mClassName = className;
			return this;
		}

		public RegistUserInfoBuilder setSex(String sex) {
			this.mSex = sex;
			return this;
		}

		public RegistUserInfoBuilder setEmsCode(String ems) {
			this.mEmsCode = ems;
			return this;
		}

		public String buildRegistUrl() {
			UserItem user = new UserItem();
			user.loginName = mPhoneNumber;
			user.userName = mUserName;
			user.password = mPassword;
			user.school = mSchoolId;
			user.city = mCityId;
			user.schoolName = mSchoolName;
			user.sex = mSex;
			user.subjectCode = mSubjectCode;
			user.gradePart = mGradepart;
			JSONObject object = new JSONObject();
			try {
				object.put("mobile", user.loginName);
				object.put("password", user.password);
				object.put("user_name", user.userName);
//				object.put("subject", user.subjectCode);
				object.put("grade", mGrade);
				object.put("grade_part", user.gradePart);
				object.put("class_name", mClassName);
				object.put("sex", user.sex);
				object.put("school_id", user.school);
				object.put("key", mEmsCode);
				object.put("city_id", user.city);
				object.put("school_name", user.schoolName);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String data = object.toString();
			if (TextUtils.isEmpty(data))
				return null;
			return data;
		}
	}

}
