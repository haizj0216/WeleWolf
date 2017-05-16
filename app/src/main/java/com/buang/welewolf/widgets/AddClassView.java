package com.buang.welewolf.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.buang.welewolf.widgets.numberpicker.NumberPicker;
import com.buang.welewolf.widgets.numberpicker.NumberPicker.OnValueChangeListener;

import java.util.ArrayList;
import java.util.List;

public class AddClassView extends LinearLayout implements OnClickListener {

	private NumberPicker mYearsPicker;
	private NumberPicker mGradePicker;
	private NumberPicker mClassPicker;
	private View mAddClasBtn;

	public AddClassView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mYearsPicker = (NumberPicker) findViewById(com.buang.welewolf.R.id.regist_step_years);
		mGradePicker = (NumberPicker) findViewById(com.buang.welewolf.R.id.regist_step_grade);
		mClassPicker = (NumberPicker) findViewById(com.buang.welewolf.R.id.regist_step_class);
		mAddClasBtn = findViewById(com.buang.welewolf.R.id.addclass_btn);
		mAddClasBtn.setOnClickListener(this);

		initYearsPicker();
		initGradePicker(true);
		initClassPicker();
	}

	private void initYearsPicker() {
		List<String> years = new ArrayList<String>();
		years.add("小学");
		years.add("初中");
		years.add("高中");
		mYearsPicker.setMinValue(0);
		mYearsPicker.setMaxValue(years.size() - 1);
		mYearsPicker
				.setDisplayedValues(years.toArray(new String[years.size()]));
		mYearsPicker.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				switch (newVal) {
				case 0: {
					initGradePicker(true);
					break;
				}
				case 1: {
					initGradePicker(false);
					break;
				}
				default:
					break;
				}
			}
		});
	}

	private boolean isHasMore = false;

	private void initGradePicker(boolean isMore) {
		if (isMore == isHasMore) {
			return;
		}
		this.isHasMore = isMore;
		List<String> grades = new ArrayList<String>();
		grades.add("一年级");
		grades.add("二年级");
		grades.add("三年级");
		grades.add("四年级");
		if (isMore) {
			grades.add("五年级");
			grades.add("六年级");
		}
		mGradePicker.setValue(0);
		mGradePicker.setMinValue(0);
		mGradePicker.setMaxValue(grades.size() - 1, false);
		mGradePicker
				.setDisplayedValues(grades.toArray(new String[grades.size()]));
	}

//	private void addClass(String className) {
//		String[] visibleClasses = mClassPicker.getDisplayedValues();
//		List<String> classes = new ArrayList<String>();
//		for (int i = 0; i < visibleClasses.length; i++) {
//			classes.add(visibleClasses[i]);
//		}
//		classes.add(className);
//		mClassPicker.setMinValue(0);
//		mClassPicker.setMaxValue(classes.size() - 1, false);
//		mClassPicker.setDisplayedValues(classes.toArray(new String[classes
//				.size()]));
//		mClassPicker.setValue(classes.size() - 1);
//	}

	private void initClassPicker() {
		List<String> classes = new ArrayList<String>();
		for (int i = 0; i < 12; i++) {
			classes.add((i + 1) + "班");
		}
		mClassPicker.setMinValue(0);
		mClassPicker.setMaxValue(classes.size() - 1);
		mClassPicker.setDisplayedValues(classes.toArray(new String[classes
				.size()]));
	}
	
//	private Dialog mAddClassDialog;

//	private void showAddClassDialog() {
//		if (mAddClassDialog != null && mAddClassDialog.isShowing()) {
//			mAddClassDialog.dismiss();
//		}
////		View view = View.inflate(getContext(), R.layout.dialog_rename, null);
////		final EditText editText = (EditText) view
////				.findViewById(R.id.dialog_rename_edt);
////		mAddClassDialog = DialogUtils.getCommonDialog(getContext(), view,
////				"添加班级", "确认", "取消", new OnDialogButtonClickListener() {
////					@Override
////					public void onItemClick(Dialog dialog, int btnId) {
////						if (btnId == BUTTON_CONFIRM) {
////							String className = editText.getText().toString();
////							if (TextUtils.isEmpty(className)) {
////								Toast.makeText(getContext(), "请创建（选择）您教学的班级",
////										Toast.LENGTH_SHORT).show();
////							} else {
////								addClass(editText.getText().toString());
////								if (dialog != null && dialog.isShowing()) {
////									dialog.dismiss();
////								}
////							}
////						} else {
////							if (dialog != null && dialog.isShowing()) {
////								dialog.dismiss();
////							}
////						}
////					}
////				});
//		mAddClassDialog.setCanceledOnTouchOutside(false);
//		mAddClassDialog.show();
//	}

	public String getClassName() {
		return mClassPicker.getDisplayedValues()[mClassPicker.getValue()];
	}

	public String getGradeName() {
		String yearsStr = "";
		int years = mYearsPicker.getValue();
		switch (years) {
		case 0: {
			yearsStr = "Grade";
			break;
		}
		case 1: {
			yearsStr = "Middle";
			break;
		}
		case 2: {
			yearsStr = "High";
			break;
		}
		default:
			break;
		}
		String gradeStr = "";
		int grade = mGradePicker.getValue();
		switch (grade) {
		case 0: {
			gradeStr = "First";
			break;
		}
		case 1: {
			gradeStr = "Second";
			break;
		}
		case 2: {
			gradeStr = "Third";
			break;
		}
		case 3: {
			gradeStr = "Fourth";
			break;
		}
		case 4: {
			gradeStr = "Fifth";
			break;
		}
		case 5: {
			gradeStr = "Sixth";
			break;
		}
		default:
			break;
		}

		return gradeStr + yearsStr;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == com.buang.welewolf.R.id.addclass_btn) {

		}
	}
}
