/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.knowbox.teacher.modules.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.UIUtils;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.teacher.App;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineLoginInfo;
import com.knowbox.teacher.base.bean.OnlineSmsCodeInfo;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.base.database.tables.UserTable;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.base.services.updateclass.UpdateClassService;
import com.knowbox.teacher.base.utils.PreferencesController;
import com.knowbox.teacher.base.utils.StringUtils;
import com.knowbox.teacher.base.utils.UiHelper;
import com.knowbox.teacher.modules.login.forgetpass.ForgetPasswordFragment;
import com.knowbox.teacher.modules.login.services.LoginListener;
import com.knowbox.teacher.modules.login.services.LoginService;
import com.knowbox.teacher.modules.login.services.UserStateChangeListener;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.UmengConstant;
import com.knowbox.teacher.modules.utils.VirtualClassUtils;
import com.knowbox.teacher.widgets.CleanableEditText;
import com.knowbox.teacher.widgets.ResizeLayout;
import com.knowbox.teacher.widgets.ResizeLayout.OnResizeListener;
import com.knowbox.teacher.widgets.TextWatcherAdapter;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * @name 登录
 */
@SuppressLint("ClickableViewAccessibility")
public class LoginFragment extends BaseUIFragment<UIFragmentHelper> {

	private static final String USER_PHONENUMBER = "knowbox_user_phonenumber";
	private static final int VERIFYCODE_LOGIN = 0;
	private static final int ACCOUNTPWD_LOGIN = 1;

	private static final int ACTION_SEND_CODE = 0;

	private int CURRENT_LOGIN_MOD = VERIFYCODE_LOGIN;

	private CleanableEditText mPhoneEditText;
	private CleanableEditText mPasswordEditText;
	private ResizeLayout mRootLayout;
	private Button mSubmitBtn;
	private View mForgetPsdText;
	private TextView mRegistBtn;
	private LoginService mLoginService;
	private UpdateClassService mUpdateClassService;
	private TextWatcher mTextWatcher = new TextWatcherAdapter(){
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			checkValid();
		}
	};
	private RelativeLayout mPwdlayout;
	private String mLastPhoneNumber;
	private TextView mTitleView;
	private ImageView mLogoImg;
	private View mLogoDivider;
	private TextView mRegistPre;
	private TextView mRegistBehind;
	//	private RelativeLayout mAccount;
//	private RelativeLayout mPwdLayout;

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		setSlideable(false);
		setTitleStyle(STYLE_NO_TITLE);
		mUpdateClassService = (UpdateClassService) getActivity().getSystemService(UpdateClassService.SERVICE_NAME);
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		return View.inflate(getActivity(), R.layout.layout_login, null);
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		mTitleView = (TextView) view.findViewById(R.id.title_bar_title);
		mTitleView.setText("免密码登录");
//		mAccount = (RelativeLayout) view.findViewById(R.id.layout_account);
//		mPwdLayout = (RelativeLayout) view.findViewById(R.id.layout_pwd);
		mPhoneEditText = (CleanableEditText) view.findViewById(R.id.login_phone_edit);
		mPhoneEditText.setMaxLength(11);
		mPhoneEditText.setHint(getString(R.string.login_hint_phone_number));
		mPhoneEditText.setHintTextColor(getActivity().getResources().getColor(R.color.color_text_third));
		mPhoneEditText.getEditText().setTextColor(getActivity().getResources().getColor(R.color.color_text_main));
		String phoneNumber = PreferencesController.getStringValue(USER_PHONENUMBER);
		if (!TextUtils.isEmpty(phoneNumber)) {
			mPhoneEditText.getEditText().setText(phoneNumber);
		}
		mPhoneEditText.addTextChangedListener(mTextWatcher);
		mPhoneEditText.setInputType(
				InputType.TYPE_CLASS_PHONE | InputType.TYPE_TEXT_VARIATION_PHONETIC);
		mPhoneEditText.setDigist("1234567890");
//		mPhoneEditText.setOnClickListener(mOnClickListener);

		mLogoImg = (ImageView) view.findViewById(R.id.login_app_login_icon);
		mLogoDivider = view.findViewById(R.id.logo_divider);
		mRegistPre = (TextView) view.findViewById(R.id.regist_pre);
		mRegistBehind = (TextView) view.findViewById(R.id.regist_behind);

		
		mPasswordEditText = (CleanableEditText) view.findViewById(R.id.login_password_edit);
		mPasswordEditText.setHint(getString(R.string.login_hint_password));
		mPasswordEditText.setMaxLength(20);
		mPasswordEditText.setHintTextColor(getActivity().getResources().getColor(R.color.color_text_third));
		mPasswordEditText.getEditText().setTextColor(getActivity().getResources().getColor(R.color.color_text_main));
		mPasswordEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		mPasswordEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				checkValid();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
//		mPasswordEditText.setOnClickListener(mOnClickListener);
		mPwdlayout = (RelativeLayout) view.findViewById(R.id.layout_pwd);
		mForgetPsdText = view.findViewById(R.id.login_forget_password_txt);
		mForgetPsdText.setOnClickListener(mOnClickListener);
		mRegistBtn = (TextView) view.findViewById(R.id.login_regist_btn);
		mRegistBtn.setOnClickListener(mOnClickListener);
		if (CURRENT_LOGIN_MOD == VERIFYCODE_LOGIN) {
			mPwdlayout.setVisibility(View.GONE);
			mForgetPsdText.setVisibility(View.GONE);
			mRegistPre.setVisibility(View.VISIBLE);
			mRegistBehind.setVisibility(View.GONE);
			mRegistBtn.setText("账号密码登录");
			mTitleView.setText("免密码登录");
		}else {
			mPwdlayout.setVisibility(View.VISIBLE);
			mForgetPsdText.setVisibility(View.VISIBLE);
			mRegistPre.setVisibility(View.GONE);
			mRegistBehind.setVisibility(View.VISIBLE);
			mRegistBtn.setText("免密登录");
			mTitleView.setText("账号密码登录");
		}

		mSubmitBtn = (Button) view.findViewById(R.id.login_submit_btn);
		mSubmitBtn.setOnClickListener(mOnClickListener);

		mRootLayout = (ResizeLayout) view.findViewById(R.id.login_layout);
		mRootLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				UIUtils.hideInputMethod(getActivity());
				return true;
			}
		});

		final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		mRootLayout.setOnResizeListener(new OnResizeListener() {
			@Override
			public void OnResize(int width, int height, int oldWidth,
								 int oldHeight) {
				if (height < oldHeight) {
					UiThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							int px = UIUtils.dip2px(getActivity(), 8);
							params.setMargins(0, -px, 0, 0);
							params.gravity = Gravity.CENTER_HORIZONTAL;
							mLogoImg.setLayoutParams(params);
							mLogoDivider.setVisibility(View.GONE);
						}
					});

				} else {
					UiThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							int px = UIUtils.dip2px(getActivity(), 70);
							params.setMargins(0, px, 0, 0);
							params.gravity = Gravity.CENTER_HORIZONTAL;
							mLogoImg.setLayoutParams(params);
							mLogoDivider.setVisibility(View.VISIBLE);

						}
					});
				}
			}
		});
		checkValid();
		mLoginService = (LoginService) getActivity().getSystemService(
				LoginService.SERVICE_NAME);
		mLoginService.getServiceObvserver().addUserStateChangeListener(userStateChangeListener);
	}

	private UserStateChangeListener userStateChangeListener = new UserStateChangeListener() {
		@Override
		public void onLogin(UserItem user) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					finish();
				}
			});
		}

		@Override
		public void onLogout(UserItem user) {

		}
	};

	/**
	 * 登录、忘记密码、注册事件监听
	 */
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == mSubmitBtn) {
				UIUtils.hideInputMethod(getActivity());
				if (!TextUtils.isEmpty(mLastPhoneNumber) && !mLastPhoneNumber.equals(mPhoneEditText.getText())) {
					mLoginService.releaseTimer();
					loginImpl();
				}else {
					if (mLoginService.getSmsCodeValidTimer() != null && mLoginService.getSmsCodeValidTimer().getCurSeconds() > 0){
						if (CURRENT_LOGIN_MOD == VERIFYCODE_LOGIN) {
							Bundle bundle = new Bundle();
							bundle.putString("phone_number", mPhoneEditText.getText());
							LoginVerifyCodeFragment fragment = (LoginVerifyCodeFragment) Fragment.
									instantiate(getActivity(), LoginVerifyCodeFragment.class.getName(), bundle);
							showFragment(fragment);
						}else {
							loginImpl();
						}
						return;
					}else if (mLoginService.getSmsCodeValidTimer() != null && mLoginService.getSmsCodeValidTimer().getCurSeconds() <= 0) {
						mLoginService.releaseTimer();
						loginImpl();
						return;
					}
					loginImpl();
				}
			} else if (v == mForgetPsdText) {
				ForgetPasswordFragment fragment = ForgetPasswordFragment
						.newFragment(getActivity(), ForgetPasswordFragment.class, null);
				fragment.setForgetPwdDestroyListener(new ForgetPasswordFragment.OnForgetPwdDestroyListener() {
					@Override
					public void onDestory() {
						mPhoneEditText.setRequestFocus();
					}
				});
				showFragment(fragment);

				UmengConstant.reportUmengEvent(UmengConstant.EVENT_FORGETPWD, null);
			} else if (v == mRegistBtn) {
				if (CURRENT_LOGIN_MOD == VERIFYCODE_LOGIN) {//变成账号密码登录
					CURRENT_LOGIN_MOD = ACCOUNTPWD_LOGIN;
					mPwdlayout.setVisibility(View.VISIBLE);
					mForgetPsdText.setVisibility(View.VISIBLE);
					mRegistPre.setVisibility(View.GONE);
					mRegistBehind.setVisibility(View.VISIBLE);
					mRegistBtn.setText("免密登录");
					mSubmitBtn.setText("登录");
					mTitleView.setText("账号密码登录");

				}else {
					CURRENT_LOGIN_MOD = VERIFYCODE_LOGIN;
					mPwdlayout.setVisibility(View.GONE);
					mForgetPsdText.setVisibility(View.GONE);
					mRegistPre.setVisibility(View.VISIBLE);
					mRegistBehind.setVisibility(View.GONE);
					mRegistBtn.setText("账号密码登录");
					mSubmitBtn.setText("获取验证码");
					mTitleView.setText("免密码登录");
				}
				checkValid();
			}
		}
	};

	/**
	 * 登录过程监听
	 */
	private LoginListener mLoginListener = new LoginListener() {
		@Override
		public void onLoginStart() {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					mSubmitBtn.setEnabled(false);
					getUIFragmentHelper().getLoadingView().showLoading();
				}
			});
		}

		@Override
		public void onLoginSuccess(OnlineLoginInfo loginInfo) {
			if (loginInfo != null && loginInfo.mUserItem != null) {
				PreferencesController.setStringValue(USER_PHONENUMBER, loginInfo.mUserItem.loginName);
				// 保存用户信息、班级列表到数据库
				DataBaseManager.getDataBaseManager().getTable(UserTable.class)
						.addUserInfo(loginInfo.mUserItem);
				UiThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						showContent();
						mSubmitBtn.setEnabled(true);
						ToastUtils.showShortToast(getActivity(), "登录成功！");
					}
				});
				VirtualClassUtils.getInstance(getActivity()).setVirtualToken(false);
				Map<String, String> umengCountMap = new HashMap<String, String>();
				umengCountMap.put(UmengConstant.LOGIN_USER_INFO_SUBJECT, loginInfo.mUserItem.subject);
				umengCountMap.put(UmengConstant.LOGIN_USER_INFO_SCHOOL, loginInfo.mUserItem.schoolName);
				MobclickAgent.onEvent(BaseApp.getAppContext(), UmengConstant.LOGIN_USERINFO,
	            		umengCountMap);

			}
		}

		@Override
		public void onLoginFailed(final String message) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					mSubmitBtn.setEnabled(true);
					showContent();
					ToastUtils.showShortToast(getActivity(),message);
				}
			});
		}
	};
	
	/**
	 * 登录的主要验证的主要逻辑
	 */
	private void loginImpl() {
		String phoneNumber = mPhoneEditText.getText();
		if (!StringUtils.isMobilePhoneNumber(phoneNumber)) {
			UiHelper.notify2shake(mPhoneEditText);
			ToastUtils.showShortToast(getActivity(), "请输入正确的手机号码");
			return;
		}
		if (CURRENT_LOGIN_MOD == VERIFYCODE_LOGIN) {
			loadData(ACTION_SEND_CODE, PAGE_MORE);
			UIUtils.hideInputMethod(getActivity());
		}else {
			String password = mPasswordEditText.getText();
			if (NetworkProvider.getNetworkProvider().getNetworkSensor()
					.isNetworkAvailable()) {
				mLoginService.login(phoneNumber, password, mLoginListener);
				UIUtils.hideInputMethod(getActivity());
				UmengConstant.reportUmengEvent(UmengConstant.EVENT_ACCOUNT_PWD_LOGIN_BTN_CLICK, null);
			} else {
				ToastUtils.showShortToast(getActivity(), "当前网络无连接");
			}
		}
	}

	private void checkValid() {
		String phone = mPhoneEditText.getText();
		if (CURRENT_LOGIN_MOD == VERIFYCODE_LOGIN) {
			if (phone.length() == 11) {
				mSubmitBtn.setClickable(true);
				mSubmitBtn.setFocusable(true);
				mSubmitBtn.setEnabled(true);
			}else {
				mSubmitBtn.setClickable(false);
				mSubmitBtn.setFocusable(false);
				mSubmitBtn.setEnabled(false);
			}
		}else {
			String pass = mPasswordEditText.getText();
			if (phone.length() == 11 && pass.length() >= 6) {
				mSubmitBtn.setClickable(true);
				mSubmitBtn.setFocusable(true);
				mSubmitBtn.setEnabled(true);
			} else {
				mSubmitBtn.setClickable(false);
				mSubmitBtn.setFocusable(false);
				mSubmitBtn.setEnabled(false);
			}
		}
	}

	@Override
	public BaseObject onProcess(int action, int pageNo, Object... params) {
		super.onProcess(action, pageNo, params);
		if (action == ACTION_SEND_CODE) {
			String url = OnlineServices.getSendSmsCode4Regist(mPhoneEditText.getText(), "1");
			return new DataAcquirer<OnlineSmsCodeInfo>().get(url, new OnlineSmsCodeInfo());
		}
		return null;
	}

	@Override
	public void onGet(int action, int pageNo, BaseObject result, Object... params) {
		super.onGet(action, pageNo, result, params);
		if (action == ACTION_SEND_CODE) {
			OnlineSmsCodeInfo mSmsCodeInfo = (OnlineSmsCodeInfo) result;
			if (mSmsCodeInfo.isAvailable()) {
				ToastUtils.showShortToast(getActivity(), "已发送验证码");
				mLastPhoneNumber = mPhoneEditText.getText();
				Bundle bundle = new Bundle();
				bundle.putString("phone_number", mLastPhoneNumber);
				bundle.putString("last_time", mSmsCodeInfo.lastTime);
				LoginVerifyCodeFragment fragment = (LoginVerifyCodeFragment) Fragment.
						instantiate(getActivity(), LoginVerifyCodeFragment.class.getName(), bundle);
				showFragment(fragment);
			}
		}
	}

	@Override
	public void onDestroyImpl() {
		super.onDestroyImpl();
		if (mLoginService != null) {
			mLoginService.getServiceObvserver().removeUserStateChangeListener(userStateChangeListener);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (getActivity() != null) {
				((App) BaseApp.getAppContext()).exit();
				getActivity().finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
