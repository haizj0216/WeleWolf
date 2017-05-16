package com.buang.welewolf.modules.login.forgetpass;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineSmsCodeInfo;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.utils.StringUtils;
import com.buang.welewolf.base.utils.UiHelper;
import com.buang.welewolf.modules.login.regist.SmsReceiver;
import com.buang.welewolf.modules.utils.UmengConstant;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.modules.utils.VirtualClassUtils;
import com.buang.welewolf.widgets.AdvanceTimer;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.error.ErrorManager;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.UiThreadHandler;
import com.buang.welewolf.modules.login.regist.StepsFragment;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * @name 忘记密码第一步(获取短信验证码)
 * @author Fanjb
 * @date 2015-3-23
 */
public class ForgetPasswordStepSmsCodeFragment extends StepsFragment {

	private EditText mPhoneEdit;
	private EditText mSmsCodeEdit;
	private Button mSendSmsCodeBtn;
	private OnlineSmsCodeInfo mSmsCodeInfo;
	private AdvanceTimer mReSendSmsCodeTimer;
	private boolean mIsPhoneValid;
	private String mValiedPhoneNum;
	private String mOldPhoneNum;
	private SmsReceiver mSmsReceiver;
	private String mSmsType = "0";
	private TextWatcher mTextWatcher = new TextWatcher() {
		private int beforeLen;
		private int afterLen;

		@Override
		public void onTextChanged(CharSequence phoneNumber, int start,
								  int before, int count) {

			// 自动检测手机号码可用性（11位、正确的格式、已注册）
			if (!TextUtils.isEmpty(Utils.getToken())) {//修改密码不验证手机号
				mIsPhoneValid = true;
				mValiedPhoneNum = mPhoneEdit.getText().toString()
						.replace("-", "");
				changeViewState(mIsPhoneValid);
				return;
			}
			phoneNumber = phoneNumber.toString().replace("-", "");
			if (phoneNumber.toString().length() == 11) {
				if (StringUtils.isMobilePhoneNumber(phoneNumber.toString())) {
					loadData(0xa0, 0, new BaseObject());
				} else {
					mIsPhoneValid = false;
					changeViewState(mIsPhoneValid);
					ToastUtils.showShortToast(getActivity(),"请填写正确的手机号码");
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
		}
	};

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		setSlideable(false);
		setTitleStyle(STYLE_NO_TITLE);
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		return View.inflate(getActivity(),
				com.buang.welewolf.R.layout.layout_forgetpsd_step_smscode, null);
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		mPhoneEdit = (EditText) view.findViewById(com.buang.welewolf.R.id.phone_edit);
		mPhoneEdit.addTextChangedListener(mTextWatcher);

		mSmsCodeEdit = (EditText) view.findViewById(com.buang.welewolf.R.id.sms_code_edit);
		mSendSmsCodeBtn = (Button) view.findViewById(com.buang.welewolf.R.id.send_sms_code_btn);
		changeViewState(false);
		mSendSmsCodeBtn.setOnClickListener(mOnClickListener);
		
		//客服电话
		mServicePhone = (TextView) view.findViewById(com.buang.welewolf.R.id.customer_service_phone);
		mServicePhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
		mServicePhone.setTextColor(getResources().getColor(com.buang.welewolf.R.color.color_main_app));
		mServicePhone.setClickable(true);
		mServicePhone.setOnClickListener(mOnClickListener);
		
		// 自动填写短信验证码
		mSmsReceiver = new SmsReceiver();
		mSmsReceiver.setEditText(mSmsCodeEdit);
		IntentFilter filter = new IntentFilter(SmsReceiver.ACTION_SMS_RECEIVED);
		filter.setPriority(800);
		MsgCenter.registerGlobalReceiver(mSmsReceiver, filter);

		if (!TextUtils.isEmpty(Utils.getToken()) && !VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
			mPhoneEdit.setText(Utils.getLoginUserItem().loginName);
			mPhoneEdit.setEnabled(false);
			((TextView) view.findViewById(com.buang.welewolf.R.id.phone_text)).setText("请先获取短信验证码，进行身份验证");
		}
	}
	
	/**
	 * 发送验证码的监听器
	 */
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == mSendSmsCodeBtn) {
				loadData(0xa1, 1, new OnlineSmsCodeInfo());
				Map<String, String> umengCount = new HashMap<String, String>();
				umengCount.put(UmengConstant.KEY_MESSAGE, "send");
				MobclickAgent.onEvent(getActivity(), UmengConstant.EVENT_PHONE_SMS,
						umengCount);
			}else if(v == mServicePhone) {
				//打电话
				String phoneStr =mServicePhone.getText().toString().trim();
				phoneStr = phoneStr.replace("-", "");
				if(!TextUtils.isEmpty(phoneStr)){
					Intent intent =new Intent();
		            intent.setAction(Intent.ACTION_DIAL);
		            intent.setData(Uri.parse("tel:"+phoneStr));
		            startActivity(intent);
				}
	            
			}
		}
	};

	/**
	 * 以下三次网络请求操作要求实时性高，不带缓存
	 */
	@Override
	public BaseObject onProcess(int action, int pageNo, Object... params) {

		// 验证手机号码
		if (action == 0xa0) {
			String phoneNum = mPhoneEdit.getText().toString().replace("-", "");
			String url = OnlineServices
					.getVerifyMobileUrl(phoneNum);
			return new DataAcquirer<OnlineSmsCodeInfo>().get(url, new OnlineSmsCodeInfo());
		}

		// 发送短信验证码
		if (action == 0xa1) {
			String type = mSmsType;
			if (TextUtils.isEmpty(type) || "1".equals(type)) {
				type = "0";
			} else if ("0".equals(type)){
				type = "1";
			}
			mSmsType = type;
			String url = OnlineServices
					.getSendSmsCode4ForgetPass(mValiedPhoneNum, type);
			return new DataAcquirer<OnlineSmsCodeInfo>().get(url, new OnlineSmsCodeInfo());
		}
		return null;
	}

	@Override
	public void onGet(int action, int pageNo, BaseObject result) {
		super.onGet(action, pageNo, result);

		// 验证手机号码是否可用
		if (action == 0xa0) {

			OnlineSmsCodeInfo info = (OnlineSmsCodeInfo) result;
			if (info.existed == 0) {//no exist
				UiHelper.notify2shake(mPhoneEdit);
				ToastUtils.showShortToast(getActivity(), "手机号码不存在");
				mIsPhoneValid = false;
				changeViewState(mIsPhoneValid);
			}else if (info.existed == 1) {//exist
				UiThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						mValiedPhoneNum = mPhoneEdit.getText().toString()
								.replace("-", "");
						mIsPhoneValid = true;
						ToastUtils.showShortToast(getActivity(), "手机号码可用");

						// 将原有手机验证码置空，防止欺骗验证
						if (mSmsCodeInfo != null
								&& !mOldPhoneNum.equals(mValiedPhoneNum)) {
							mSmsCodeInfo = null;
						}
						// 记录已经验证通过的号码
						mOldPhoneNum = mValiedPhoneNum;

						// 计时器不能重复启动
						if (mReSendSmsCodeTimer == null
								|| mReSendSmsCodeTimer.getCurSeconds() <= 0) {
							changeViewState(true);
						}
					}
				});
			}


		}

		// 获取短信验证码
		if (action == 0xa1) {
			mSmsCodeInfo = (OnlineSmsCodeInfo) result;
			ToastUtils.showShortToast(getActivity(), "短信验证码已下发");
			// 启动60s重新获取验证码倒计时
			mReSendSmsCodeTimer = new AdvanceTimer();
			mReSendSmsCodeTimer.setCurSeconds(60);
			mReSendSmsCodeTimer.setTimeChangeListener(mTimeChangerListener);
			mReSendSmsCodeTimer.start();
		}
	}

	@Override
	public void onFail(int action, int pageNo, BaseObject result) {
//		super.onFail(action, pageNo, result);
		showContent();

		// 验证手机号码结果
		if (action == 0xa0) {
			final OnlineSmsCodeInfo info = (OnlineSmsCodeInfo) result;
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					UiHelper.notify2shake(mPhoneEdit);
					mPhoneEdit.requestFocus();
					ToastUtils.showShortToast(getActivity(), ErrorManager.getErrorManager().getErrorHint(info.getRawResult(), info.getErrorDescription()));
				}
			});
		}

		// 发送短信验证码结果
		if (action == 0xa1) {
			String rawResult = result.getRawResult();
			if (!TextUtils.isEmpty(rawResult)
					&& "170003".equalsIgnoreCase(rawResult)) {
				UiThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						UiHelper.notify2shake(mPhoneEdit);
						ToastUtils.showShortToast(getActivity(), "手机号码不存在");
					}
				});
			} else if (!TextUtils.isEmpty(rawResult)
					&& "170001".equalsIgnoreCase(rawResult)) {
				UiThreadHandler.post(new Runnable() {
					@Override
					public void run() {
//						UiHelper.notify2shake(mPhoneEdit);
						ToastUtils.showShortToast(getActivity(), "请求太频繁");
					}
				});
			} else {
			 	UiThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						UiHelper.notify2shake(mSmsCodeEdit);
						mPhoneEdit.requestFocus();
						ToastUtils.showShortToast(getActivity(), "网络请求失败，请重试");
					}
				});
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
					mSendSmsCodeBtn.setText(mCurSeconds + "s后重发");
				}
			});
		}

		@Override
		public void onFinish(int mCurSeconds) {
			mSendSmsCodeBtn.setText("获取验证码");
			changeViewState(true);
		}
	};
	private TextView mServicePhone;

	@Override
	public boolean isValid() {
		String smsCode = mSmsCodeEdit.getText().toString().trim();

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

		if (smsCode.length() < 4) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					ToastUtils.showShortToast(getActivity(), "验证码错误");
					UiHelper.notify2shake(mSmsCodeEdit);
				}
			});
			return false;
		}

		// 验证短信验证码
		String url = OnlineServices.getConfirmSmsCodeUrl(mValiedPhoneNum,
				mSmsCodeEdit.getText().toString());
		final OnlineSmsCodeInfo info = new DataAcquirer<OnlineSmsCodeInfo>().get(url, new OnlineSmsCodeInfo());
		if (info.isAvailable()) {
			// 给第二页发广播
			Intent intent = new Intent(
					ForgetPasswordStepUpdatePassFragment.ACTION_FORGET_PASSWORD);
			intent.putExtra("valid_phone_number", mValiedPhoneNum);
			intent.putExtra("token", info.token);
			MsgCenter.sendLocalBroadcast(intent);
			return true;
		} else {
			final String rawResult = info.getRawResult();
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					ToastUtils.showShortToast(getActivity(), ErrorManager.getErrorManager().getErrorHint(rawResult, info.getErrorDescription()));
					UiHelper.notify2shake(mSmsCodeEdit);
				}
			});
			return false;
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

	@Override
	public void onDestroyViewImpl() {
		super.onDestroyViewImpl();
		mPhoneEdit = null;
		mPhoneEdit = null;
		mSendSmsCodeBtn = null;
		if (mReSendSmsCodeTimer != null) {
			mReSendSmsCodeTimer.destory();
		}
		if(mSmsReceiver != null){
			MsgCenter.unRegisterGlobalReceiver(mSmsReceiver);
		}
	}

	@Override
	public boolean isNextEnable() {
		return true;
	}
}