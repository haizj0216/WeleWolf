package com.buang.welewolf.modules.profile;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.buang.welewolf.modules.utils.DialogUtils;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.datacache.BaseObject;
import com.knowbox.base.utils.UIUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

/**
 * @name 意见反馈页面
 * @author Fanjb
 * @date 2015-3-17
 */
public class SuggestionFragment extends BaseUIFragment<UIFragmentHelper> {

	private static int ACTION_REQUEST = 0x01;

	private EditText mSuggestionEdit;
	private TextView mRemainText;
	private View mHeaderSendText;

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		setSlideable(true);
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		return View.inflate(getActivity(), com.buang.welewolf.R.layout.layout_suggestion, null);
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);

		mRemainText = (TextView) view.findViewById(com.buang.welewolf.R.id.remain_text);
		mSuggestionEdit = (EditText) view.findViewById(com.buang.welewolf.R.id.suggestion_text);
		mSuggestionEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String suggestionText = mSuggestionEdit.getText().toString();
				if (!TextUtils.isEmpty(suggestionText)) {
					changeState(true);
				} else {
					changeState(false);
				}
				int remain = 120 - s.toString().length();
				mRemainText.setText("" + remain);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		mHeaderSendText = view.findViewById(com.buang.welewolf.R.id.header_send);
		mHeaderSendText.setSelected(false);
		mHeaderSendText.setOnClickListener(mOnClickListener);
		view.findViewById(com.buang.welewolf.R.id.header_back)
				.setOnClickListener(mOnClickListener);
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == com.buang.welewolf.R.id.header_back) {
				finish();
				UIUtils.hideInputMethod(getActivity());
			} else if (v.getId() == com.buang.welewolf.R.id.header_send) {
				if (isValid()) {
					loadData(ACTION_REQUEST, 1, new BaseObject());
				}
			}
		}
	};

	@Override
	public BaseObject onProcess(int action, int pageNo, Object... params) {
		if (action == ACTION_REQUEST) {
//			String url = OnlineServices.getSuggestionUrl(Utils.getToken(), mSuggestionEdit
//					.getText().toString());
//			return new DataAcquirer<BaseObject>().acquire(url,
//					new BaseObject());
		}
		return null;
	}

	@Override
	public void onGet(int action, int pageNo, BaseObject result) {
		super.onGet(action, pageNo, result);
		if (action == ACTION_REQUEST) {
			Dialog dialog = DialogUtils.getMessageDialog(getActivity(), "提示",
					"确认", "", "已收录您的宝贵意见，谢谢您的反馈！",
					new DialogUtils.OnDialogButtonClickListener() {
						@Override
						public void onItemClick(Dialog dialog, int btnId) {
							if (dialog.isShowing()) {
								dialog.dismiss();
							}
							finish();
						}
					});
			dialog.show();
		}
	}

	@Override
	public void onFail(int action, int pageNo, BaseObject result) {
		super.onFail(action, pageNo, result);
		if (action == ACTION_REQUEST) {
			showContent();
			ToastUtils.showShortToast(getActivity(), "反馈失败");
		}
	}

	private boolean isValid() {
		if (mSuggestionEdit.getText().toString().equals("")) {
			return false;
		}
		return true;
	}

	private void changeState(boolean state) {
		mHeaderSendText.setClickable(state);
		mHeaderSendText.setSelected(state);
	}

	@Override
	public void onDestroyViewImpl() {
		super.onDestroyImpl();
		mSuggestionEdit = null;
	}
}
