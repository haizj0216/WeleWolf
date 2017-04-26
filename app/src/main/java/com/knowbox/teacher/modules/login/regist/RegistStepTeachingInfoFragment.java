package com.knowbox.teacher.modules.login.regist;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.utils.UiHelper;
import com.knowbox.teacher.modules.profile.CitySelectFragment;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.DialogUtils.OnTeachingInfoSelectListener;
import com.knowbox.teacher.modules.utils.ToastUtils;

/**
 * @name 注册第二步：填写姓名、学校、科目、学段
 * @author Fanjb
 * @date 2015-3-11
 */
public class RegistStepTeachingInfoFragment extends StepsFragment {

	private EditText mUserNameEdit;
//
//	private Dialog mChooseCityDialog;
//	private Dialog mChooseSchoolDialog;
//	private Dialog mCreateSchoolDialog;

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		return View.inflate(getActivity(),
				R.layout.layout_regist_step_teachinginfo, null);
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		mUserNameEdit = (EditText) view.findViewById(R.id.regist_name_edit);
		mUserNameEdit.addTextChangedListener(mTextWatcher);

		mTeachingInfo = (RelativeLayout) view.findViewById(R.id.regist_teachinginfo_layout);
		mTeachingInfo.setOnClickListener(mOnClickListener);

		mRegistSchool = (RelativeLayout) view.findViewById(R.id.regist_school_layout);
		mRegistSchool.setOnClickListener(mOnClickListener);
		
		mTeachInfo = (TextView) view.findViewById(R.id.regist_teachinginfo);
		mSchoolText = (TextView) view.findViewById(R.id.regist_school_text);
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(ActionUtils.ACTION_SCHOOL_CHANGED);
//		MsgCenter.registerLocalReceiver(mReceiver, intentFilter);
	}

//	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//		public void onReceive(Context context, final Intent intent) {
//			if (intent.getAction().equals(ActionUtils.ACTION_SCHOOL_CHANGED)) {
//				PinyinIndexModel school = intent.getParcelableExtra("school");
//				if (school != null) {
//					String school_id = school.getId();
//					String school_name = school.getName();
//					if (mSchoolText != null && !TextUtils.isEmpty(school_name)) {
//						mSchoolText.setText(school_name);
//						notifyNextBtnEnable();
//					}
//					mLoginService.getUserInfoBuilder().setSchoolId(school_id);
//					mLoginService.getUserInfoBuilder().setCityId("");
//					mLoginService.getUserInfoBuilder().setSchoolName("");
//				} else {
//					CityModel city = (CityModel) intent.getSerializableExtra("city");
//					String schoolName = intent.getStringExtra("schoolName");
//					String city_id = city.getId();
//					if (mSchoolText != null && !TextUtils.isEmpty(schoolName)) {
//						mSchoolText.setText(schoolName);
//					}
//					mLoginService.getUserInfoBuilder().setSchoolId("");
//					mLoginService.getUserInfoBuilder().setCityId(city_id);
//					mLoginService.getUserInfoBuilder().setSchoolName(schoolName);
//					notifyNextBtnEnable();
//				}
//			}
//		}
//	};

	/**
	 * 学校、教学信息的点击事件
	 */
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == mRegistSchool) {
				CitySelectFragment fragment = CitySelectFragment.newFragment
						(getActivity(), CitySelectFragment.class, null);
				showFragment(fragment);
			} else if (v == mTeachingInfo) {
				Dialog dialog = DialogUtils.getSelectTeachingInfoDialog(getActivity(), mteachingInfoSelectListener);
				dialog.show();
			}
		}
	};
	
	private OnTeachingInfoSelectListener mteachingInfoSelectListener = new OnTeachingInfoSelectListener() {

		@Override
		public void onTeachingInfoSelected(String sectionInfo,
				String subjectInfo, String teachingInfoStr) {
			String section_str = sectionInfo;
			String subject_str = subjectInfo;
			String teachingInfo_Str = teachingInfoStr;
			if (mTeachInfo != null && !TextUtils.isEmpty(teachingInfo_Str)) {
				mTeachInfo.setText(teachingInfo_Str);
				notifyNextBtnEnable();
			}
			mLoginService.getUserInfoBuilder().setGradePart(section_str);
			mLoginService.getUserInfoBuilder().setSubjectCode(subject_str);
		}
		
	};
	
	

	SelectSubjectFragment mSubjectFragment;

	private TextWatcher mTextWatcher = new TextWatcher() {
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
	};

	@Override
	public boolean isNextEnable() {
		if(mUserNameEdit == null) 
			return false;
		 if (TextUtils.isEmpty(mUserNameEdit.getText().toString())) {
			 return false;
		 }
		 
		 if (mUserNameEdit.getText().toString().length() < 2) {
			 return false;
		 }
		
		 if (TextUtils.isEmpty(mSchoolText.getText().toString())) {
			 return false;
		 }
		
		 if (TextUtils.isEmpty(mTeachInfo.getText().toString())) {
			 return false;
		 }

		return true;
	}

	@Override
	public boolean isValid() {
		String name = mUserNameEdit.getText().toString().trim();
		if (TextUtils.isEmpty(name)) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					mUserNameEdit.requestFocus();
					UiHelper.notify2shake(mUserNameEdit);
					ToastUtils.showShortToast(getActivity(), "请填写您的姓名");
				}
			});
			return false;
		}

		if (name.length() < 2) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					mUserNameEdit.requestFocus();
					UiHelper.notify2shake(mUserNameEdit);
					ToastUtils.showShortToast(getActivity(), "姓名长度至少两位");
				}
			});
			return false;
		}
		mLoginService.getUserInfoBuilder().setUserName(name);

		if (TextUtils.isEmpty(mSchoolText.getText().toString())) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					UiHelper.notify2shake(mSchoolText);
					ToastUtils.showShortToast(getActivity(), "请选择您所在的学校");
				}
			});
			return false;
		}

		if (TextUtils.isEmpty(mTeachInfo.getText().toString())) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					UiHelper.notify2shake(mTeachInfo);
					ToastUtils.showShortToast(getActivity(), "请选择您的教学阶段与科目");
				}
			});
			return false;
		}
		return true;
	}

//	/**
//	 * 选择省市
//	 *
//	 * @param model
//	 */
//	private void showChooseCityDialog(CityModel model) {
//		if (mChooseCityDialog != null && mChooseCityDialog.isShowing()) {
//			mChooseCityDialog.dismiss();
//		}
//		mChooseCityDialog = DialogUtils.getChooseCityDialog(getActivity(),
//				model, mCityCallback);
//		mChooseCityDialog.show();
//	}
//
//	private ChooseCityCallback mCityCallback = new ChooseCityCallback() {
//		@Override
//		public void onChosenCity4School(CityModel city) {
//			showChooseSchoolDialog(city);
//		}
//
//		@Override
//		public void onBack(CityModel parent) {
//			showChooseCityDialog(parent);
//		}
//	};
//
//	/**
//	 * 选择学校
//	 *
//	 * @param model
//	 */
//	private void showChooseSchoolDialog(CityModel model) {
//		if (mChooseSchoolDialog != null && mChooseSchoolDialog.isShowing()) {
//			mChooseSchoolDialog.dismiss();
//		}
//		mChooseSchoolDialog = DialogUtils.getChooseSchoolDialog(getActivity(),
//				model, mChooseSchoolCallback);
//		mChooseSchoolDialog.show();
//	}
//
//	private ChooseSchoolCallback mChooseSchoolCallback = new ChooseSchoolCallback() {
//		@Override
//		public void onChosenSchool(PinyinIndexModel school) {
//			if (school != null) {
//				String school_id = school.getId();
//				String school_name = school.getName();
//				if (mSchoolText != null && !TextUtils.isEmpty(school_name)) {
//					mSchoolText.setText(school_name);
//					notifyNextBtnEnable();
//				}
//				mLoginService.getUserInfoBuilder().setSchoolId(school_id);
//			}
//			if (mChooseCityDialog != null && mChooseCityDialog.isShowing()) {
//				mChooseCityDialog.dismiss();
//			}
//			if (mChooseSchoolDialog != null && mChooseSchoolDialog.isShowing()) {
//				mChooseSchoolDialog.dismiss();
//			}
//		}
//
//		@Override
//		public void onSchoolNotFound(CityModel city) {
//			showCreateSchoolDialog(city);
//		}
//	};
//
//	/**
//	 * 创建自定义学校
//	 *
//	 * @param model
//	 */
//	private void showCreateSchoolDialog(CityModel model) {
//		if (mCreateSchoolDialog != null && mCreateSchoolDialog.isShowing()) {
//			mCreateSchoolDialog.dismiss();
//		}
//		mCreateSchoolDialog = DialogUtils.getCreateSchoolDialog(getActivity(),
//				model, mCreateSchoolCallback);
//		mCreateSchoolDialog.show();
//	}
//
//	private CreateSchoolCallback mCreateSchoolCallback = new CreateSchoolCallback() {
//		@Override
//		public void onCreateSchool(CityModel city, PinyinIndexModel school) {
//
//			if (city != null && school != null) {
//				String city_id = city.getId();
//				String school_name = school.getName();
//				if (mSchoolText != null && !TextUtils.isEmpty(school_name)) {
//					mSchoolText.setText(school_name);
//				}
//				mLoginService.getUserInfoBuilder().setCityId(city_id);
//				mLoginService.getUserInfoBuilder().setSchoolName(school_name);
//				notifyNextBtnEnable();
//			}
//
//			if (mChooseCityDialog != null && mChooseCityDialog.isShowing()) {
//				mChooseCityDialog.dismiss();
//			}
//			if (mChooseSchoolDialog != null && mChooseSchoolDialog.isShowing()) {
//				mChooseSchoolDialog.dismiss();
//			}
//			if (mCreateSchoolDialog != null && mCreateSchoolDialog.isShowing()) {
//				mCreateSchoolDialog.dismiss();
//			}
//		}
//	};
	private RelativeLayout mTeachingInfo;
	private RelativeLayout mRegistSchool;
	private TextView mTeachInfo;
	private TextView mSchoolText;

//	@Override
//	public void onDestroyImpl() {
//		super.onDestroyImpl();
//		MsgCenter.unRegisterLocalReceiver(mReceiver);
//	}
}
