/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.knowbox.teacher.modules.login.forgetpass;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.modules.login.regist.StepsFragment;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.Utils;
import com.knowbox.teacher.modules.utils.VirtualClassUtils;
import com.knowbox.teacher.widgets.ForbidSlideViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * @name 忘记密码
 * @author Fanjb
 * @date 2015-3-20
 */
public class ForgetPasswordFragment extends BaseUIFragment<UIFragmentHelper> {

	private List<StepsFragment> mStepsFragments;
	private ForbidSlideViewPager mViewPager;
	private PagerAdapter mPagerAdapter;
	private TextView mPreviousBtn;
	private TextView mNextBtn;
	private TextView mPageNo;
	private TextView mTitleBar;

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		setSlideable(true);
		setTitleStyle(STYLE_NO_TITLE);
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		return View.inflate(getActivity(), R.layout.layout_forgetpsd, null);
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		// 按注册步骤创建各个页面
		if (mStepsFragments == null) {
			mStepsFragments = new ArrayList<StepsFragment>();
			mStepsFragments.add((StepsFragment) Fragment.instantiate(
					getActivity(),
					ForgetPasswordStepSmsCodeFragment.class.getName()));
			ForgetPasswordStepUpdatePassFragment fragment = (ForgetPasswordStepUpdatePassFragment) Fragment
					.instantiate(getActivity(),
							ForgetPasswordStepUpdatePassFragment.class
									.getName());
			mStepsFragments.add(fragment);
		}

		ImageButton mBackView = (ImageButton) view.findViewById(R.id.title_bar_back);
		mBackView.setOnClickListener(mClickListener);

		mTitleBar = (TextView) view.findViewById(R.id.title_bar_title);

		// viewPager
		mViewPager = (ForbidSlideViewPager) view.findViewById(R.id.view_pager);
		mPagerAdapter = new PagerAdapter(getChildFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setScrollable(false);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				setViewState(position);
			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int position) {
			}
		});

		// 下一步、上一步
		mPreviousBtn = (Button) view.findViewById(R.id.previous_btn);
		mPreviousBtn.setOnClickListener(mClickListener);
		mNextBtn = (Button) view.findViewById(R.id.next_btn);
		mNextBtn.setOnClickListener(mClickListener);

		// 头部标题和页码
//		((TextView) view.findViewById(R.id.header_title_txt)).setText("忘记密码");
//		((TextView) view.findViewById(R.id.page_no_all)).setText(" / 2");
		mPageNo = (TextView) view.findViewById(R.id.page_no);
		setViewState(0);
		if (!TextUtils.isEmpty(Utils.getToken())  && !VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
			mTitleBar.setText("修改密码");
		}
	}

	/**
	 * 监听下一步、上一步按钮点击
	 */
	private View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == mPreviousBtn) {
				goProvious();
			} else if (v == mNextBtn) {
				goNext();
			} else if (v.getId() == R.id.title_bar_back) {
				finish();
			}
		}
	};

	/**
	 * 上一步
	 */
	private void goProvious() {
		UIUtils.hideInputMethod(getActivity());
		int postion = mViewPager.getCurrentItem();
		if (postion > 0) {
			mViewPager.setCurrentItem(postion - 1, true);
		} else {
			finish();
		}
	}

	/**
	 * 下一步
	 */
	private void goNext() {
		UIUtils.hideInputMethod(getActivity());
		new NextStepTask().execute();
	}

	@Override
	public void onDestroyImpl() {
		super.onDestroyImpl();
		UIUtils.hideInputMethod(getActivity());
		if (forgetPwdDestroyListener != null) {
			forgetPwdDestroyListener.onDestory();
		}
	}

	/**
	 * @name 验证本页面输入的信息，切换下一步操作（触发：点击下一步）
	 */
	private class NextStepTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPreviousBtn.setEnabled(false);
			mNextBtn.setEnabled(false);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			StepsFragment curFragment = mStepsFragments.get(mViewPager
					.getCurrentItem());
			return curFragment.isValid();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mPreviousBtn.setEnabled(true);
			mNextBtn.setEnabled(true);
			if (result) {
				int curPosition = mViewPager.getCurrentItem();
				// 下一页或成功结束
				if (curPosition < mStepsFragments.size() - 1) {
					mViewPager.setCurrentItem(curPosition + 1, true);
				} else {
					finish();
				}
			}
		}
	}

	/**
	 * 改变View的一些状态
	 * 
	 * @param position
	 */
	private void setViewState(int position) {
		mPageNo.setText(position + 1 + "");
		if (position == 0) {
			mPreviousBtn.setText("返  回");
		} else {
			mPreviousBtn.setText("上一步");
		}

		if (position == mStepsFragments.size() - 1) {
			mNextBtn.setText("完  成");
		} else {
			mNextBtn.setText("下一步");
		}
	}

	/**
	 * @name 忘记密码两步页面适配器
	 */
	private class PagerAdapter extends FragmentPagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return mStepsFragments.get(position);
		}

		@Override
		public int getCount() {
			if(mStepsFragments == null)
				return 0;
			return mStepsFragments.size();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			goProvious();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public interface OnForgetPwdDestroyListener {
		public abstract void onDestory();
	}

	private OnForgetPwdDestroyListener forgetPwdDestroyListener;

	public void setForgetPwdDestroyListener(OnForgetPwdDestroyListener listener) {
		forgetPwdDestroyListener = listener;
	}

}
