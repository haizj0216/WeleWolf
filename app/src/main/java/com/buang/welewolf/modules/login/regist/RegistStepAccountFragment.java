package com.buang.welewolf.modules.login.regist;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineSmsCodeInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.utils.StringUtils;
import com.buang.welewolf.base.utils.UiHelper;
import com.buang.welewolf.widgets.AdvanceTimer;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.UiThreadHandler;
import com.buang.welewolf.R;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UmengConstant;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * @name 注册第一步：填写帐户信息（用户名和密码）
 * @author Fanjb
 * @date 2015-3-11
 */
public class RegistStepAccountFragment extends StepsFragment {

	private EditText mPhoneEdit;
	private View mClearPhoneView;
	private EditText mPasswordEdit;
	private View mClearPassView;
	private EditText mSmsCodeEdit;
	private Button mSendSmsCodeBtn;
	private AdvanceTimer mReSendSmsCodeTimer;
	private AdvanceTimer mSmsCodeValidTimer;
	private OnlineSmsCodeInfo mSmsCodeInfo;
	private boolean mIsPhoneValid;
	private String mOldPhoneNum;
	private SmsReceiver mSmsReceiver;
	private TextView mServicePhone;
	private String mSmsType = "0";
	private TextWatcher mTextWatcher = new TextWatcher() {
		private int beforeLen;
		private int afterLen;

		@Override
		public void onTextChanged(CharSequence phoneNumber, int start,
								  int before, int count) {

			// 隐藏和显示一键清空按钮
			if (phoneNumber.length() > 0) {
				mClearPhoneView.setVisibility(View.VISIBLE);
			} else {
				mClearPhoneView.setVisibility(View.GONE);
			}

			phoneNumber = phoneNumber.toString().replace("-", "");
			// 自动检测手机号码可用性（11位、正确的格式、未被注册）
			if (phoneNumber.toString().length() == 11) {
				if (StringUtils.isMobilePhoneNumber(phoneNumber.toString())) {
					loadData(0xa0, 0, new BaseObject());
				} else {
					mIsPhoneValid = false;
					changeViewState(mIsPhoneValid);
					ToastUtils.showShortToast(getActivity(), "请填写正确的手机号码");
					UiHelper.notify2shake(mPhoneEdit);
				}
			} else {
				mIsPhoneValid = false;
				changeViewState(mIsPhoneValid);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after) {
			beforeLen = s.length();
		}

		@Override
		public void afterTextChanged(Editable s) {
			// 格式化电话号码成135-0000-0001格式
//			String txt = mPhoneEdit.getText().toString();
//			afterLen = txt.length();
//			if (afterLen > beforeLen) {
//				if (txt.length() == 4 || txt.length() == 9) {
//					mPhoneEdit.setText(new StringBuffer(txt).insert(
//							txt.length() - 1, "-").toString());
//					mPhoneEdit.setSelection(mPhoneEdit.getText().length());
//				}
//			} else {
//				if (txt.endsWith("-")) {
//					mPhoneEdit.setText(new StringBuffer(txt).delete(
//							afterLen - 1, afterLen).toString());
//					mPhoneEdit.setSelection(mPhoneEdit.getText().length());
//				}
//			}
			notifyNextBtnEnable();
		}
	};

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		return View.inflate(getActivity(), R.layout.layout_regist_step_account,
				null);
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);

		// 手机号码
		mPhoneEdit = (EditText) view.findViewById(R.id.regist_phone_edit);
		mPhoneEdit.addTextChangedListener(mTextWatcher);

		mClearPhoneView = view.findViewById(R.id.clear_phone_view);
		mClearPhoneView.setOnClickListener(mOnClickListener);

		// 短信验证码
		mSmsCodeEdit = (EditText) view.findViewById(R.id.regit_sms_code_edit);
		mSendSmsCodeBtn = (Button) view
				.findViewById(R.id.regist_send_sms_code_btn);
		mSendSmsCodeBtn.setOnClickListener(mOnClickListener);
		mSmsCodeEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				notifyNextBtnEnable();
			}
		});

		changeViewState(mIsPhoneValid);
		mPasswordEdit = (EditText) view.findViewById(R.id.regist_password_edit);
		mPasswordEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 隐藏和显示一键清空按钮
				if (s.length() > 0) {
					mClearPassView.setVisibility(View.VISIBLE);
				} else {
					mClearPassView.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				notifyNextBtnEnable();
			}
		});
		
		mServicePhone = (TextView) view.findViewById(R.id.customer_service_phone);
		//mServicePhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
		mServicePhone.setTextColor(getResources().getColor(R.color.color_main_app));
		mServicePhone.setClickable(true);
		mServicePhone.setOnClickListener(mOnClickListener);
		

		mClearPassView = view.findViewById(R.id.clear_password_view);
		mClearPassView.setOnClickListener(mOnClickListener);

		// 切换明文和密文密码
		((CheckBox) view.findViewById(R.id.show_plaintext_password_view))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (!TextUtils.isEmpty(mPasswordEdit.getText()
								.toString())) {
							if (mPasswordEdit.getInputType() == 129) {
								mPasswordEdit.setInputType(1);
							} else {
								mPasswordEdit.setInputType(129);
							}
							mPasswordEdit.setSelection(mPasswordEdit.length());
						}
					}
				});
		mSmsReceiver = new SmsReceiver();
		mSmsReceiver.setEditText(mSmsCodeEdit);
		IntentFilter filter = new IntentFilter(SmsReceiver.ACTION_SMS_RECEIVED);
		filter.setPriority(800);
		MsgCenter.registerGlobalReceiver(mSmsReceiver, filter);
	}
	
	/**
	 * 清空输入的手机号码和密码
	 */
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.clear_phone_view:
				mPhoneEdit.setText("");
				break;
			case R.id.clear_password_view:
				mPasswordEdit.setText("");
				break;
			case R.id.regist_send_sms_code_btn:
				loadData(0xa1, 1, new OnlineSmsCodeInfo());
				Map<String, String> umengCount = new HashMap<String, String>();
				umengCount.put(UmengConstant.KEY_MESSAGE, "send");
				MobclickAgent.onEvent(getActivity(), UmengConstant.EVENT_PHONE_SMS,
						umengCount);

//				UmengConstant.reportUmengEvent(UmengConstant.EVENT_SIGNUP_SMS, null);
				break;
			case R.id.customer_service_phone:
				//打电话
				String phoneStr =mServicePhone.getText().toString().trim();
				phoneStr = phoneStr.replace("-", "");
				if(!TextUtils.isEmpty(phoneStr)){
					Intent intent =new Intent();
		            intent.setAction(Intent.ACTION_DIAL);
		            intent.setData(Uri.parse("tel:"+phoneStr));
		            startActivity(intent);
				}
				break;
			}
		}
	};

	@Override
	public BaseObject onProcess(int action, int pageNo, Object... params) {

		// 验证手机号码、发送短信验证码操作要求实时性高，不带缓存
		if (action == 0xa0) {
			String phoneNum = mPhoneEdit.getText().toString().replace("-", "");
			String url = OnlineServices.getVerifyMobileUrl(phoneNum);
			return new DataAcquirer<BaseObject>().get(url, new BaseObject());
		}

		if (action == 0xa1) {
			String phoneNum = mLoginService.getUserInfoBuilder()
					.getPhoneNumber();
			String type = mSmsType;
			if (TextUtils.isEmpty(type) || "1".equals(type)) {
				type = "0";
			} else if ("0".equals(type)){
				type = "1";
			}
			mSmsType = type;
			String url = OnlineServices.getSendSmsCode4Regist(phoneNum, type);
			return new DataAcquirer<OnlineSmsCodeInfo>().get(url, new OnlineSmsCodeInfo());
		}
		return null;
	}

	@Override
	public void onGet(int action, int pageNo, BaseObject result) {
		super.onGet(action, pageNo, result);

		// 验证手机号码是否可用
		if (action == 0xa0) {
			if (result.isAvailable()) {
				String phoneNum = mPhoneEdit.getText().toString()
						.replace("-", "");
				mLoginService.getUserInfoBuilder().setPhoneNumber(phoneNum);
				mIsPhoneValid = true;
				ToastUtils.showShortToast(getActivity(), "手机号码可用");

				// 将原有手机验证码置空，防止欺骗验证
				if (mSmsCodeInfo != null && !mOldPhoneNum.equals(phoneNum)) {
					mSmsCodeInfo = null;
				}
				// 记录已经验证通过的号码
				mOldPhoneNum = phoneNum;

				// 计时器不能重复启动
				if (mReSendSmsCodeTimer == null
						|| mReSendSmsCodeTimer.getCurSeconds() <= 0) {
					changeViewState(true);
				}
			}
		}

		// 获取短信验证码
		if (action == 0xa1) {
			mSmsCodeInfo = (OnlineSmsCodeInfo) result;
			if (mSmsCodeInfo.isAvailable()) {
				ToastUtils.showShortToast(getActivity(), "已发送验证码");

				// 启动60s重新获取倒计时
				mReSendSmsCodeTimer = new AdvanceTimer();
				mReSendSmsCodeTimer.setCurSeconds(60);
				mReSendSmsCodeTimer.setTimeChangeListener(mTimeChangerListener);
				mReSendSmsCodeTimer.start();

				// 启动300s验证码有效时间倒计时
				mSmsCodeValidTimer = new AdvanceTimer();
				mSmsCodeValidTimer.setCurSeconds(Integer
						.parseInt(mSmsCodeInfo.lastTime));
				mSmsCodeValidTimer.start();
			}
		}
	}

	/**
	 * 监听计时变化
	 */
	private AdvanceTimer.TimeChangeListener mTimeChangerListener = new AdvanceTimer.TimeChangeListener() {
		@Override
		public void onPreChanged(int mCurSeconds) {
			changeViewState(false);
		}

		@Override
		public void onTimeChanged(final int mCurSeconds) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					mSendSmsCodeBtn.setText(mCurSeconds + "s");
				}
			});
		}

		@Override
		public void onFinish(int mCurSeconds) {
			mSendSmsCodeBtn.setText("重新获取");
			changeViewState(true);
		}
	};
	

	@Override
	public void onFail(int action, int pageNo, BaseObject result) {
		super.onFail(action, pageNo, result);
		showContent();
		// 验证手机号码结果
		if (action == 0xa0) {
			String rawResult = result.getRawResult();
			if (!TextUtils.isEmpty(rawResult)
					&& "20501".equalsIgnoreCase(rawResult)) {
				UiThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						UiHelper.notify2shake(mPhoneEdit);
						mPasswordEdit.requestFocus();
						ToastUtils.showShortToast(getActivity(), "手机号码已被注册");
					}
				});
			} else {
				UiThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						UiHelper.notify2shake(mPhoneEdit);
						mPhoneEdit.requestFocus();
						ToastUtils.showShortToast(getActivity(), "网络请求失败，请重试");
					}
				});
			}
			mIsPhoneValid = false;
			changeViewState(mIsPhoneValid);
		}

		// 短信验证码结果
		if (action == 0xa1) {
			ToastUtils.showShortToast(getActivity(), "网络请求失败，请重试");
		}

	}

	private void changeViewState(boolean flag) {
		if (flag) {
			mSendSmsCodeBtn.setClickable(true);
			mSendSmsCodeBtn.setEnabled(true);
		} else {
			mSendSmsCodeBtn.setClickable(false);
			mSendSmsCodeBtn.setEnabled(false);
		}
	}

	/**
	 * 是否可以点击下一步
	 * 
	 * @return
	 */
	@Override
	public boolean isNextEnable() {
		if (!mIsPhoneValid) {
			return false;
		}

		if (mSmsCodeEdit.getText().toString().length() < 4) {
			return false;
		}

		if (mPasswordEdit.getText().toString().length() < 6) {
			return false;
		}
		return true;
	}

	/**
	 * 页面输入的信息有效性校验
	 */
	@Override
	public boolean isValid() {
		String smsCode = mSmsCodeEdit.getText().toString().trim();
		String pwd = mPasswordEdit.getText().toString().trim();

		if (!mIsPhoneValid) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					UiHelper.notify2shake(mPhoneEdit);
				}
			});
			return false;
		}

		if (TextUtils.isEmpty(smsCode)) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					ToastUtils.showShortToast(getActivity(), "请输入短信验证码");
					UiHelper.notify2shake(mSmsCodeEdit);
				}
			});
			return false;
		}
		String md5Code = StringUtils.getReplaceStr(smsCode);
		if (mSmsCodeInfo == null || mSmsCodeInfo.mobileCode == null ||
				mSmsCodeInfo.mobileCode.size() == 0 || !(
				md5Code.equals(mSmsCodeInfo.mobileCode.get(0)) ||
				md5Code.equals(mSmsCodeInfo.mobileCode.get(1)))) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					ToastUtils.showShortToast(getActivity(), "验证码错误");
					UiHelper.notify2shake(mSmsCodeEdit);
				}
			});
			return false;
		}

		// 有效时间验证
		if (mSmsCodeValidTimer.getCurSeconds() <= 0) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					UiHelper.notify2shake(mSmsCodeEdit);
					ToastUtils.showShortToast(getActivity(), "验证码已过期");
				}
			});
			return false;
		}

		if (!pwd.matches("\\w{6,}")) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					ToastUtils.showShortToast(getActivity(), "密码为6-20位字母数字组合");
					UiHelper.notify2shake(mPasswordEdit);
				}
			});
			return false;
		}
		mLoginService.getUserInfoBuilder().setPassword(pwd);
		mLoginService.getUserInfoBuilder().setEmsCode(smsCode);
		return true;
	}

	@Override
	public void onDestroyImpl() {
		super.onDestroyImpl();
		if (mReSendSmsCodeTimer != null) {
			mReSendSmsCodeTimer.destory();
		}
		MsgCenter.unRegisterGlobalReceiver(mSmsReceiver);
	}
}
