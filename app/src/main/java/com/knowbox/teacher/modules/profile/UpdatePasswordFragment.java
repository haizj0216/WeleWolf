package com.knowbox.teacher.modules.profile;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.error.ErrorManager;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.bean.OnlineSmsCodeInfo;
import com.knowbox.teacher.base.database.bean.UserItem;
import com.knowbox.teacher.base.database.tables.UserTable;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.base.utils.UiHelper;
import com.knowbox.teacher.modules.login.services.LoginService;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.DialogUtils.OnDialogButtonClickListener;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.Utils;
import com.knowbox.teacher.widgets.CleanableEditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @name 设置-用户修改密码页面
 * @author Fanjb
 * @date 2015-3-17
 */
public class UpdatePasswordFragment extends BaseUIFragment {

	private CleanableEditText mSourcePassword;
	private CleanableEditText mNewPassword;
	private CleanableEditText mSecondaryPassword;
	private TextView mSaveText;

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		setSlideable(true);
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		return View.inflate(getActivity(), R.layout.layout_update_password,
				null);
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		mSourcePassword = (CleanableEditText) view.findViewById(R.id.source_password);
		mSourcePassword.setDigist("1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM");
		mSourcePassword.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		mSourcePassword.setHint("输入原密码");
		mSourcePassword.setMaxLength(20);
		mSourcePassword.setHintTextColor(0xffc9c8ce);
		mSourcePassword.getEditText().setTextColor(0xff525252);
		mSourcePassword.addTextChangedListener(mTextWatcher);
		
		mNewPassword = (CleanableEditText) view.findViewById(R.id.new_password);
		mNewPassword
				.setDigist("1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM");
		mNewPassword.setInputType(EditorInfo.TYPE_CLASS_TEXT
				| EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		mNewPassword.setHint("输入6位以上新密码");
		mNewPassword.setMaxLength(20);
		mNewPassword.setHintTextColor(0xffc9c8ce);
		mNewPassword.getEditText().setTextColor(0xff525252);
		mNewPassword.addTextChangedListener(mTextWatcher);

		mSecondaryPassword = (CleanableEditText) view
				.findViewById(R.id.secondary_password);
        mSecondaryPassword
                .setDigist("1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM");
        mSecondaryPassword.setInputType(
                EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        mSecondaryPassword.setHint("再次输入密码");
        mSecondaryPassword.setMaxLength(20);
        mSecondaryPassword.setHintTextColor(0xffc9c8ce);
        mSecondaryPassword.getEditText().setTextColor(0xff525252);
        mSecondaryPassword.addTextChangedListener(mTextWatcher);

		view.findViewById(R.id.header_back)
				.setOnClickListener(mOnClickListener);
		mSaveText = (TextView) view.findViewById(R.id.header_save);
		mSaveText.setOnClickListener(mOnClickListener);
		changeState(false);
	}

	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String sourcePass = mSourcePassword.getText();
			String newPass = mNewPassword.getText();
			String secondaryPass = mSecondaryPassword.getText();
			if (!TextUtils.isEmpty(sourcePass) && !TextUtils.isEmpty(newPass)
					&& !TextUtils.isEmpty(secondaryPass)) {
				changeState(true);
			} else {
				changeState(false);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	private void changeState(boolean state) {
		mSaveText.setClickable(state);
		mSaveText.setSelected(state);
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.header_save) {
				if (isValid()) {
					UIUtils.hideInputMethod(getActivity());
					loadData(0xaa, 1, new OnlineSmsCodeInfo());
				}
			} else if (v.getId() == R.id.header_back) {
				finish();
			}
		}
	};

	@Override
	public BaseObject onProcess(int action, int pageNo, Object... params) {
		super.onProcess(action, pageNo, params);
		final String pass = mNewPassword.getText();
		final String oldPass = mSourcePassword.getText();
		String url = OnlineServices.getUpdatePassword(null);
		JSONObject object = new JSONObject();
		try {
			object.put("password", pass);
			object.put("old_password", oldPass);
			object.put("mobile", Utils.getLoginUserItem().loginName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String data = object.toString();
		if (TextUtils.isEmpty(data))
			return null;
		return new DataAcquirer<OnlineSmsCodeInfo>().post(
				url, data, new OnlineSmsCodeInfo());
	}

	@Override
	public void onGet(int action, int pageNo, BaseObject result) {
		super.onGet(action, pageNo, result);
		OnlineSmsCodeInfo info = (OnlineSmsCodeInfo) result;
		UserItem item = ((LoginService) getActivity().getSystemService(
				LoginService.SERVICE_NAME)).getLoginUser();
		item.password = mNewPassword.getText();
		item.token = info.token;
		UserTable table = DataBaseManager.getDataBaseManager().getTable(
				UserTable.class);
		if (table.updateByCase(item, UserTable.USERID + " = ?",
				new String[] { item.userId }) != -1) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					ToastUtils.showShortToast(getActivity(), "修改成功");
				}
			});
			finish();
		}
	}

	@Override
	public void onFail(int action, int pageNo, final BaseObject result) {
		showContent();
		UiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				if (result.getRawResult().equals("20201")) {
					ToastUtils.showShortToast(getActivity(), ErrorManager.getErrorManager().getErrorHint(result.getRawResult(), result.getErrorDescription()));
				} else if (result.getRawResult().equals("20001")) {
					ToastUtils.showShortToast(getActivity(), ErrorManager.getErrorManager().getErrorHint(result.getRawResult(), result.getErrorDescription()));
				} else if(result.getRawResult().equals("20206")) {
					ToastUtils.showShortToast(getActivity(), ErrorManager.getErrorManager().getErrorHint(result.getRawResult(), result.getErrorDescription()));
				}
				else {
					ToastUtils.showShortToast(getActivity(), "网络请求失败，请重试");
				}
			}
		});

	}

	/**
	 * 校验输入的密码
	 * 
	 * @return
	 */
	private boolean isValid() {
		return !isNull(mSourcePassword) && isLengthValid(mSourcePassword)
				&& !isNull(mNewPassword) && isLengthValid(mNewPassword)
				&& !isNull(mSecondaryPassword)
				&& isLengthValid(mSecondaryPassword)
				&& isEquals(mNewPassword, mSecondaryPassword);
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

	/**
	 * 两次输入的密码是否一致
	 */
	private boolean isEquals(final CleanableEditText oldPass, final CleanableEditText pass) {
		if (!oldPass.getText().equals(pass.getText())) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					Dialog dialog = DialogUtils.getMessageDialog(getActivity(),
							"提示", "确认", "", "两次输入密码不一致，请重新输入",
							new OnDialogButtonClickListener() {
								@Override
								public void onItemClick(Dialog dialog, int btnId) {
									dialog.dismiss();
								}
							});
					if (!dialog.isShowing()) {
						dialog.show();
					}
				}
			});
			return false;
		}
		return true;
	}

	@Override
	public void onDestroyViewImpl() {
		super.onDestroyViewImpl();
		UIUtils.hideInputMethod(getActivity());
		mSourcePassword = null;
		mNewPassword = null;
		mSecondaryPassword = null;
	}
}
