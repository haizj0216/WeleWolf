package com.buang.welewolf.modules.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.buang.welewolf.base.bean.OnlineVersion;
import com.buang.welewolf.base.services.update.UpdateService;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.UiThreadHandler;
import com.hyena.framework.utils.VersionUtils;
import com.buang.welewolf.App;
import com.buang.welewolf.base.services.update.CheckVersionListener;
import com.buang.welewolf.modules.utils.ToastUtils;

/**
 * @name 关于页面
 * @author Fanjb
 * @date 2015-3-17
 */
public class AboutAppFragment extends BaseUIFragment {

	private UpdateService mUpdateService;
	
	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		setSlideable(true);
		mUpdateService = (UpdateService) getActivity().getSystemService(
				UpdateService.SERVICE_NAME);
		mUpdateService.getObserver().addVersionChangeListener(
				mCheckVersionListener);
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		getTitleBar().setTitle("关于我们");
		View view = View
				.inflate(getActivity(), com.buang.welewolf.R.layout.layout_about_app, null);
		return view;
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		String versionName = VersionUtils.getVersionName(App.getAppContext());
		((TextView) view.findViewById(com.buang.welewolf.R.id.profile_assist_version))
				.setText("当前版本 : " + versionName);
//		view.findViewById(R.id.profile_assist_intro).setOnClickListener(
//				mOnClickListener);
		view.findViewById(com.buang.welewolf.R.id.profile_assist_update).setOnClickListener(
				mOnClickListener);
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			// 功能介绍
//			case R.id.profile_assist_intro:
//				Bundle mBundle = new Bundle();
//				mBundle.putString("title", "新特性");
//				mBundle.putString("url", OnlineServices.getNewFeaturesUrl());
//				BaseUIFragment fragment = (BaseUIFragment) Fragment
//						.instantiate(getActivity(),
//								ActivityWebViewFragment.class.getName(), mBundle);
//
//				showFragment(fragment);
//				break;

			// 检测更新
			case com.buang.welewolf.R.id.profile_assist_update:
				getLoadingView().showLoading("获取版本信息...");
				mUpdateService.checkVersion(true);
				break;
			}
		}
	};

	/**
	 * 监听版本检测结果
	 */
	private CheckVersionListener mCheckVersionListener = new CheckVersionListener() {
		
		@Override
		public void onVersionChange(boolean auto, OnlineVersion version) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
//					showNewVersionInfoDialog();
					showContent();
				}
			});
		}

		@Override
		public void onCheckFinish(boolean auto, final int reason) {
			showContent();
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					if (reason == CheckVersionListener.REASON_NORMAL) {
						ToastUtils.showShortToast(getActivity(), "当前已是最新版本");
					} else if (reason == CheckVersionListener.REASON_ERROR) {
						ToastUtils.showShortToast(getActivity(),"检测失败");
					}
				}
			});
		}
	};
	
	@Override
	public void onDestroyImpl() {
		super.onDestroyImpl();
		if(mUpdateService != null) {
			mUpdateService.getObserver().removeVersionChangeListener(mCheckVersionListener);
		}
	};

	/**
	 * 显示版本更新对话框，点击下载安装时能出应用，下载最新版本安装包
	 */
//	private Dialog mVersionDialog;
	
//	private void showNewVersionInfoDialog() {
//		showContent();
//		if (mVersionDialog != null && mVersionDialog.isShowing()) {
//			mVersionDialog.dismiss();
//		}
//		if (mUpdateService.getLastVersion() != null) {
//			mVersionDialog = DialogUtils.getMessageDialog(getActivity(), "更新", "下载安装",
//					"取消", mUpdateService.getLastVersion().description,
//					new OnDialogButtonClickListener() {
//						@Override
//						public void onItemClick(Dialog dialog, int btnId) {
//							if (btnId == BUTTON_CONFIRM) {
//								if (mUpdateService == null) {
//									return;
//								}
//								PackageUpdateTask task = new PackageUpdateTask();
//								File target = new File(android.os.Environment
//										.getExternalStorageDirectory(),
//										mUpdateService.getLastVersion().version
//												+ ".apk");
//								task.execute(
//										mUpdateService.getLastVersion().downloadUrl,
//										target.getAbsolutePath());
//							}
//							if (OnlineVersion.FLAG_UPDATE_FORCE == mUpdateService
//									.getLastVersion().type) {
//								finish();
//								if (dialog.isShowing()) {
//									dialog.dismiss();
//								}
//							}
//						}
//					});
//			if (OnlineVersion.FLAG_UPDATE_FORCE == mUpdateService
//					.getLastVersion().type) {
//				mVersionDialog.getWindow().getDecorView()
//						.setOnTouchListener(null);
//				mVersionDialog.setCancelable(false);
//			}
//			mVersionDialog.show();
//		}
//	}
}
