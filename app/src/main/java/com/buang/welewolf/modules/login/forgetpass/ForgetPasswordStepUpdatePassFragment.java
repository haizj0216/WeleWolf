package com.buang.welewolf.modules.login.forgetpass;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineSmsCodeInfo;
import com.buang.welewolf.base.database.bean.UserItem;
import com.buang.welewolf.base.database.tables.UserTable;
import com.buang.welewolf.base.http.services.OnlineServices;
import com.buang.welewolf.base.utils.UiHelper;
import com.buang.welewolf.modules.utils.Utils;
import com.buang.welewolf.widgets.CleanableEditText;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.UiThreadHandler;
import com.buang.welewolf.modules.login.regist.StepsFragment;
import com.buang.welewolf.modules.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @name 忘记密码第二步（填写新密码）
 * @author Fanjb
 * @date 2015-3-23
 */
public class ForgetPasswordStepUpdatePassFragment extends StepsFragment {

	public static final String ACTION_FORGET_PASSWORD = "com.buang.welewolf.forget_password";
	private TextView mPhoneText;
	private CleanableEditText mNewPasswordEdit;
	private CleanableEditText mSecondaryPasswordEdit;
	private String phoneNumber;
	private String token;

	/**
	 * 已验证通过的有效的手机号码
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(android.content.Context context,
				android.content.Intent intent) {
			if (intent.getAction().equals(ACTION_FORGET_PASSWORD)) {
				phoneNumber = intent.getStringExtra("valid_phone_number");
				token = intent.getStringExtra("token");
				if (mPhoneText != null) {
					mPhoneText.setText(phoneNumber);
				}
			}
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
				com.buang.welewolf.R.layout.layout_forgetpsd_step_updatepsd, null);
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		mPhoneText = (TextView) view.findViewById(com.buang.welewolf.R.id.phone_text);
		mNewPasswordEdit = (CleanableEditText) view.findViewById(com.buang.welewolf.R.id.new_psd_edit);
		mNewPasswordEdit.setHint(getString(com.buang.welewolf.R.string.forgetpsd_hint_new_password));
		mNewPasswordEdit.setMaxLength(20);
		mNewPasswordEdit.setHintTextColor(getActivity().getResources().getColor(com.buang.welewolf.R.color.color_text_third));
		mNewPasswordEdit.getEditText().setTextColor(getActivity().getResources().getColor(com.buang.welewolf.R.color.color_text_main));
		mNewPasswordEdit.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		mSecondaryPasswordEdit = (CleanableEditText) view.findViewById(com.buang.welewolf.R.id.new_agin_psd_edit);
		mSecondaryPasswordEdit.setHint(getString(com.buang.welewolf.R.string.forgetpsd_hint_new_agin_password));
		mSecondaryPasswordEdit.setMaxLength(20);
		mSecondaryPasswordEdit.setHintTextColor(getActivity().getResources().getColor(com.buang.welewolf.R.color.color_text_third));
		mSecondaryPasswordEdit.getEditText().setTextColor(getActivity().getResources().getColor(com.buang.welewolf.R.color.color_text_main));
		mSecondaryPasswordEdit.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		IntentFilter filter = new IntentFilter(ACTION_FORGET_PASSWORD);
		MsgCenter.registerLocalReceiver(mReceiver, filter);
	}

	/**
	 * 检验数据完整性并提交网络数据
	 */
	@Override
	public boolean isValid() {
		if (isNull(mNewPasswordEdit) || !isLengthValid(mNewPasswordEdit)
				|| isNull(mSecondaryPasswordEdit)
				|| !isLengthValid(mSecondaryPasswordEdit)
				|| !isEquals(mNewPasswordEdit, mSecondaryPasswordEdit)) {
			return false;
		}

		String newPass = mNewPasswordEdit.getText();
		String url = OnlineServices.getUpdatePassword(token);
		JSONObject object = new JSONObject();
		try {
			object.put("password", newPass);
			object.put("mobile", mPhoneText.getText().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String data = object.toString();
		OnlineSmsCodeInfo info = new DataAcquirer<OnlineSmsCodeInfo>().post(
				url, data, new OnlineSmsCodeInfo());

		if (info.isAvailable()) {
			// 如果本地数据库中有当前用户，则更新其token，（此时数据库没有用户记录，因为用户还没有登录）
			UserTable table = DataBaseManager.getDataBaseManager().getTable(
					UserTable.class);
			List<UserItem> items = table.queryByCase(UserTable.LOGINNAME
					+ " = ?", new String[] { phoneNumber }, null);
			if (items != null && items.size() > 0) {
				int count = 0;
				UserItem item = items.get(0);
				item.token = info.token;
				try {
					Utils.getLoginUserItem().token = info.token;
				} catch (Exception e) {
					e.printStackTrace();
				}
				count = table.updateByCase(item, UserTable.LOGINNAME + " = ?",
						new String[] { phoneNumber });
				if (count > 0) {
					UiThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							ToastUtils.showShortToast(getActivity(), "修改成功");
						}
					});
					return true;
				} else {
					UiThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							ToastUtils.showShortToast(getActivity(), "本地数据库更新失败");
						}
					});
					return true;
				}
			} else {
				UiThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						ToastUtils.showShortToast(getActivity(), "修改成功");
					}
				});
				return true;
			}

		} else {
			if (info.getRawResult() != null
					&& info.getRawResult().equals("20001")) {
				UiThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						ToastUtils.showShortToast(getActivity(), "TOKEN失效");
					}
				});
			} else {
				UiThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						ToastUtils.showShortToast(getActivity(), "网络请求失败，请重试");
					}
				});
			}
			return false;
		}
	}

	private boolean isNull(final CleanableEditText view) {
		if (view.getText().equals("")) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					UiHelper.notify2shake(view);
					ToastUtils.showShortToast(getActivity(), "密码不能为空");
				}
			});
			return true;
		}
		return false;
	}

	private boolean isLengthValid(final CleanableEditText view) {
		if (view.getText().length() < 6) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					UiHelper.notify2shake(view);
					ToastUtils.showShortToast(getActivity(), "密码为6-20位字母数字组合");
				}
			});
			return false;
		}
		return true;
	}

	private boolean isEquals(final CleanableEditText firstPass, final CleanableEditText secondPass) {
		if (!firstPass.getText().equals(secondPass.getText())) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					UiHelper.notify2shake(firstPass);
					UiHelper.notify2shake(secondPass);
					ToastUtils.showShortToast(getActivity(),"两次输入的密码不一致，请重新输入");
				}
			});
			return false;
		}
		return true;
	}

	@Override
	public void onDestroyViewImpl() {
		super.onDestroyViewImpl();
		mNewPasswordEdit = null;
		mSecondaryPasswordEdit = null;
		MsgCenter.unRegisterLocalReceiver(mReceiver);
	}

	@Override
	public boolean isNextEnable() {
		return true;
	}
}
