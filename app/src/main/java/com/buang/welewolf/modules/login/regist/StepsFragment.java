package com.buang.welewolf.modules.login.regist;

import android.os.Bundle;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.buang.welewolf.modules.login.services.LoginService;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

public abstract class StepsFragment extends BaseUIFragment<UIFragmentHelper> {

	protected LoginService mLoginService;

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		mLoginService = (LoginService) getActivity().getSystemService(
				LoginService.SERVICE_NAME);
	}

	public static interface OnNextActionListener {
		public void onNext(boolean isEnable);
	}

	private OnNextActionListener mNextActionListener;

	public void setOnNextListener(OnNextActionListener listener) {
		this.mNextActionListener = listener;
	}

	public abstract boolean isNextEnable();

	public abstract boolean isValid();

	public void notifyNextBtnEnable() {
		if (mNextActionListener != null) {
			mNextActionListener.onNext(isNextEnable());
		}
	}

}
