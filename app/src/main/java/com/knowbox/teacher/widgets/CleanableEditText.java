package com.knowbox.teacher.widgets;

/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.LoginFilter.UsernameFilterGeneric;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hyena.framework.utils.UIUtils;
import com.knowbox.teacher.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 可删除的EditText
 * @author yangzc
 */
public class CleanableEditText extends RelativeLayout {

	private EditText mEditText;
	private ImageView mLeftIcon;
	private View mEyeIcon;
	private View mClearBtn;
	
	public CleanableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 设置提示
	 * 
	 * @param hint
	 */
	public void setHint(String hint) {
		if (mEditText != null) {
			mEditText.setHint(hint);
		}
	}

	public void setSelection(int index) {
		if (mEditText != null) {
			mEditText.setSelection(index);
		}
	}

	public void setText(CharSequence text) {
		if (mEditText != null) {
			mEditText.setText(text);
		}
	}

	public void setRequestFocus() {
		if (mEditText != null) {
			mEditText.clearFocus();
//			mEditText.setFocusable(true);
			mEditText.requestFocus();
		}
	}

	/**
	 * 设置左侧图标
	 * 
	 * @param resourceId
	 */
	public void setLeftIcon(int resourceId) {
		if (mLeftIcon != null){
			LayoutParams params = (LayoutParams) mEditText.getLayoutParams();
			params.leftMargin = UIUtils.dip2px(10);
			mEditText.setLayoutParams(params);
			mLeftIcon.setVisibility(View.VISIBLE);
			mLeftIcon.setImageResource(resourceId);
		}
	}

	public void setHintTextColor(int color){
		mEditText.setHintTextColor(color);
	}

	/**
	 * 设置输入类型
	 * 
	 * @param inputType
	 */
	public void setInputType(int inputType) {
		if (mEditText != null) {
			mEditText.setInputType(inputType);
		}
	}
	
	public void setDigist(String digist){
		if (mEditText != null) {
			mEditText.setKeyListener(DigitsKeyListener.getInstance(digist));
		}
	}

	/**
	 * 设置最大长度
	 * @param maxLength
	 */
	public void setMaxLength(int maxLength) {
		addFilter(new InputFilter.LengthFilter(maxLength));
	}
	
	/**
	 * 添加过滤器
	 * @param filter
	 */
	public void addFilter(InputFilter filter){
		if (mEditText != null) {
			InputFilter filters[] = mEditText.getFilters();
			if(filters != null){
				InputFilter filterNew[] = new InputFilter[filters.length + 1];
				for (int i = 0; i < filters.length; i++) {
					filterNew[i] = filters[i];
				}
				filterNew[filters.length] = filter;
				mEditText.setFilters(filterNew);
			}else{
				mEditText.setFilters(new InputFilter[]{filter});
			}
		}
	}
	
	public static class UserNameLoginFilter extends UsernameFilterGeneric {

		public UserNameLoginFilter() {
			super();
		}

		@Override
		public boolean isAllowed(char c) {
			String pattern = "[_A-Za-z0-9\u4E00-\u9FA5]";
			return (c + "").matches(pattern);
		}
		
	}

	public static class EmojiFilter implements InputFilter {
		String str = "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]";
		private Pattern mEmojiPattern = Pattern.compile(str, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			Matcher emojiMatcher = mEmojiPattern.matcher(source);

			if (emojiMatcher.find()) {
				return "";
			}
			return source;
		}

	}

	public static class VerifyCodeFilter extends  UsernameFilterGeneric {
		public VerifyCodeFilter() {}

		@Override
		public boolean isAllowed(char c) {
			if ('0' <= c && c <= '9')
				return true;
			if ('a' <= c && c <= 'z')
				return true;
			if ('A' <= c && c <= 'Z')
				return true;
			return false;
		}
	}
	
	/**
	 * 获得编辑文本
	 * @return
	 */
	public EditText getEditText(){
		return mEditText;
	}

	/**
	 * 获得输入的文本
	 * @return
	 */
	public String getText(){
		if(mEditText == null || mEditText.getText() == null)
			return "";
		return mEditText.getText().toString().trim();
	}
	
	/**
	 * 是否显示眼睛
	 * @param isShowEye
	 */
	public void setIsShowEye(boolean isShowEye){
		mEyeIcon.setVisibility(View.VISIBLE);
		mEyeIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEyeIcon.setSelected(!mEyeIcon.isSelected());
				
				if(mEyeIcon.isSelected()){
					mEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
				}else{
					mEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT
							| EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
				}
                if(mEditText.getText() != null)
				    Selection.setSelection(mEditText.getText(), mEditText.getText().length());
			}
		});
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		View.inflate(getContext(), R.layout.widget_edit_text, this);
		mEyeIcon = findViewById(R.id.edit_text_eye);
		mLeftIcon = (ImageView) findViewById(R.id.edit_text_lefticon);
		mEditText = (EditText) findViewById(R.id.edit_text_edt);
		mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				mLeftIcon.setSelected(hasFocus);
			}
		});
		mClearBtn = findViewById(R.id.edit_text_clear);
		mClearBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditText.setText("");
			}
		});
		// 添加文本改变监听器
		mEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
//				String input = mEditText.getText().toString();
//				mEditText.setText(input.replaceAll("[^_A-Za-z0-9\u4E00-\u9FA5]", ""));
				// 同步清空按钮状态
				if (!TextUtils.isEmpty(mEditText.getText().toString())) {
					mClearBtn.setVisibility(View.VISIBLE);
				} else {
					mClearBtn.setVisibility(View.GONE);
				}

				// 回调状态
				if (mTextWatcher != null) {
					mTextWatcher.onTextChanged(s, start, before, count);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
				// 回调状态
				if (mTextWatcher != null) {
					mTextWatcher.beforeTextChanged(s, start, count, after);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// 回调状态
				if (mTextWatcher != null) {
					mTextWatcher.afterTextChanged(s);
				}
			}
		});
	}

	private TextWatcher mTextWatcher;

	public void addTextChangedListener(TextWatcher textWatcher) {
		this.mTextWatcher = textWatcher;
	}
}
