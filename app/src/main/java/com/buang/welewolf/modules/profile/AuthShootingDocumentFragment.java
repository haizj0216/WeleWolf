package com.buang.welewolf.modules.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.BaseApp;
import com.buang.welewolf.R;
import com.buang.welewolf.base.utils.DirContext;
import com.buang.welewolf.base.utils.FileUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.modules.utils.UmengConstant;
import com.buang.welewolf.modules.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.HashMap;


/**
 * @name 资格证拍照页面
 */
public class AuthShootingDocumentFragment extends BaseUIFragment<UIFragmentHelper> {

	private File mCertImageLocalFile;

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		setSlideable(true);
		mCertImageLocalFile = new File(DirContext.getImageCacheDir(), "auth"
				+ Utils.getLoginUserItem().userId + ".jpg");
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		return View.inflate(getActivity(), R.layout.layout_profile_auth_shooting_document, null);
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		getTitleBar().setTitle("证件审核");
		view.findViewById(R.id.capture_btn).setOnClickListener(mOnClickListener);
	}

	private static final int REQUEST_CODE_MCAMERA = 0xA0;

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!FileUtils.isSdcardAvailable()) {
				ToastUtils.showShortToast(getActivity(),"SD卡不存在，不能拍照" );
				return;
			}
			HashMap<String,String> umengMap = new HashMap<String, String>();
			umengMap.put(UmengConstant.KEY_AUTH_CAMERA, "1");
			MobclickAgent.onEvent(BaseApp.getAppContext(), UmengConstant.EVENT_AUTH_UPLOAD_FILE, umengMap);
			try {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCertImageLocalFile));
				intent.putExtra("return-data", false);
				startActivityForResult(intent, REQUEST_CODE_MCAMERA);
			} catch (Exception e) {
				e.printStackTrace();
				umengMap = new HashMap<String, String>();
				umengMap.put(UmengConstant.KEY_AUTH_CAMERA_RESULT, UmengConstant.FAIL);
				MobclickAgent.onEvent(BaseApp.getAppContext(), UmengConstant.EVENT_AUTH_UPLOAD_FILE, umengMap);
			}

		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_MCAMERA && resultCode == Activity.RESULT_OK
				&& mCertImageLocalFile != null && mCertImageLocalFile.exists()) {
			if (mOnCaptureFinshListener != null) {
				finish();
				mOnCaptureFinshListener.onCaptureFinish(mCertImageLocalFile);
				final HashMap<String,String> umengMap = new HashMap<String, String>();
				umengMap.put(UmengConstant.KEY_AUTH_CAMERA_RESULT, UmengConstant.SUCCESS);
				MobclickAgent.onEvent(BaseApp.getAppContext(), UmengConstant.EVENT_AUTH_UPLOAD_FILE, umengMap);
			}
		} else {
			ToastUtils.showShortToast(getActivity(), "拍摄证件照失败，请重试!");
			final HashMap<String,String> umengMap = new HashMap<String, String>();
			umengMap.put(UmengConstant.KEY_AUTH_CAMERA_RESULT, UmengConstant.FAIL);
			MobclickAgent.onEvent(BaseApp.getAppContext(), UmengConstant.EVENT_AUTH_UPLOAD_FILE, umengMap);
		}
	}
	
	public interface OnCaptureFinshListener {
		void onCaptureFinish(File localImageFile);
	}

	private OnCaptureFinshListener mOnCaptureFinshListener;

	public void setOnCaptureFinshListener(OnCaptureFinshListener listener) {
		this.mOnCaptureFinshListener = listener;
	}
}
