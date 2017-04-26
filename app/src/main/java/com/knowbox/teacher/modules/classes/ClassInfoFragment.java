package com.knowbox.teacher.modules.classes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.bean.MenuItem;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.UIUtils;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.service.upload.UploadListener;
import com.knowbox.base.service.upload.UploadService;
import com.knowbox.base.service.upload.UploadTask;
import com.knowbox.base.utils.ImageFetcher;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.database.bean.ClassInfoItem;
import com.knowbox.teacher.base.http.services.OnlineServices;
import com.knowbox.teacher.base.services.updateclass.UpdateClassService;
import com.knowbox.teacher.base.utils.ActionUtils;
import com.knowbox.teacher.base.utils.DirContext;
import com.knowbox.teacher.base.utils.ImageUtil;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.DialogUtils.OnFillDialogBtnClickListener;
import com.knowbox.teacher.modules.utils.SubjectUtils;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.modules.utils.UmengConstant;
import com.knowbox.teacher.modules.utils.VirtualClassUtils;
import com.knowbox.teacher.widgets.RoundDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 班级详情页面
 * 
 * @author weilei
 *
 */
public class ClassInfoFragment extends BaseUIFragment<UIFragmentHelper> {
	private static final String PREFS_CLASS_INFO_GUIDE = "prefs_class_info_guide";
	private static final int REQCODE_CAMERA = 0xA0;
	private static final int REQCODE_PICKER = 0xA1;
	private static final int REQCODE_FROM_CROP = 0xA2;

	private final int ACTION_DELETE = 1;
	private final int ACTION_CLOSE = 2;
//	private final int ACTION_CHANGE_NAME = 3;
//	private final int ACTION_CHANGE_GRADE = 4;
	private final int ACTION_CHANGE_HEAD = 5;
	private File headImageFile;

	ClassInfoItem mClassItem;
	private Dialog mDialog;
	private ImageView mToggleButton;
	private View mGuideView;
	private TextView mClassNameView;
	private ImageView mHeadView;
//	private TextView mGradeView;

	private String mHeadPhoto;

	private boolean isUploading;
	private UpdateClassService updateClassService;


	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		setSlideable(true);
		mClassItem = (ClassInfoItem) getArguments().getParcelable("class");
		if (mClassItem == null) {
			finish();
		}
		getActivity()
				.getWindow()
				.setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED
								| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED
								| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		updateClassService = (UpdateClassService) getSystemService(UpdateClassService.SERVICE_NAME);
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		getTitleBar().setTitle(mClassItem.className);
		View view = View.inflate(getActivity(),
				R.layout.layout_profile_class_info, null);
		mToggleButton = (ImageView) view
				.findViewById(R.id.profile_class_switch);
		if (ClassInfoItem.STATE_OPENED == mClassItem.state) {
			mToggleButton
					.setImageResource(R.drawable.btn_toggle_button_cache_on);
		} else {
			mToggleButton
					.setImageResource(R.drawable.btn_toggle_button_cache_off);
		}
		mToggleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeClass();
			}
		});
		return view;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		try {
			Date date = new Date(Long.parseLong(mClassItem.createTime) * 1000L);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			((TextView) view.findViewById(R.id.profile_class_found))
					.setText(formatter.format(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
		((TextView) view.findViewById(R.id.profile_class_code))
				.setText(mClassItem.classCode);
		((TextView) view.findViewById(R.id.profile_class_delete))
				.setOnClickListener(mOnClickListener);
		mClassNameView = ((TextView) view.findViewById(R.id.profile_class_class_name));
		mClassNameView.setText(SubjectUtils.getGradeName(mClassItem.grade) + " " + mClassItem.className);
//		mGradeView.setText(SubjectUtils.getGradeName(mClassItem.grade));
		view.findViewById(R.id.profile_class_class_layout).setOnClickListener(mOnClickListener);
		view.findViewById(R.id.profile_class_transfer_layout).setOnClickListener(mOnClickListener);
		mGuideView = view.findViewById(R.id.profile_class_info_guide);
		mHeadView = (ImageView) view.findViewById(R.id.profile_class_headphoto);
		ImageFetcher.getImageFetcher().loadImage(mClassItem.mHeadPhoto, mHeadView,
				R.drawable.icon_class_default, new RoundDisplayer());
		mHeadView.setOnClickListener(mOnClickListener);
//		showGuideView();
	}

//	private void showGuideView() {
//		if (!PreferencesController.getBoolean(PREFS_CLASS_INFO_GUIDE, false)) {
//			mGuideView.setVisibility(View.VISIBLE);
//			mGuideView.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					mGuideView.setVisibility(View.GONE);
//				}
//			});
//			PreferencesController.setBoolean(PREFS_CLASS_INFO_GUIDE, true);
//		}
//	}

	OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id){
				case R.id.profile_class_delete:
					if (VirtualClassUtils.getInstance(getActivity()).isVirtualToken()) {
						ActionUtils.notifyVirtualTip();
						return;
					}
					showDeleteDialog();
					break;
				case R.id.profile_class_class_layout:
					UmengConstant.reportUmengEvent(UmengConstant.EVENT_RENAME_CLASSGROUP, null);
					updateClassName();
					break;
//				case R.id.profile_class_grade_layout:
//					showGradeChangedDialog();
//					break;
				case R.id.profile_class_transfer_layout:
					openClassTransferFragment();
					break;
				case R.id.profile_class_headphoto:
					updateUserHeadImg();
					break;
			}
		}
	};

	/**
	 * 修改用户头像
	 */
	private void updateUserHeadImg() {
		List<MenuItem> items = new ArrayList<MenuItem>();
		items.add(new MenuItem(0, "拍照", ""));
		items.add(new MenuItem(0, "相册", ""));
		headImageFile = new File(DirContext
				.getImageCacheDir(), mClassItem.classId
				+ "_"
				+ System.currentTimeMillis() + ".jpg");
		headImageFile.getParentFile().mkdirs();
		mDialog = DialogUtils.getListDialog(getActivity(), "更换头像", items,
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
											int position, long arg3) {
						// 调用系统相机拍照
						if (position == 0) {
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(headImageFile));
							intent.putExtra("return-data", false);
							startActivityForResult(intent, REQCODE_CAMERA);
						}

						// 调用图片浏览器
						else if (position == 1) {
							Intent intent = new Intent(Intent.ACTION_PICK);
							intent.setDataAndType(
									MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
									"image/*");
							startActivityForResult(intent, REQCODE_PICKER);
						}
						if (mDialog.isShowing()) {
							mDialog.dismiss();
						}
					}
				});
		if (!mDialog.isShowing()) {
			mDialog.show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// 拍照或从相册选取图片结束后默认进入裁剪照片
		if (requestCode == REQCODE_CAMERA && resultCode == -1) {
			crop(Uri.fromFile(headImageFile));
		} else if (requestCode == REQCODE_PICKER && resultCode == -1) {
			crop(data.getData());
		}
		// 裁剪结束后先写入到本地，在读取本地图片显示
		else if (requestCode == REQCODE_FROM_CROP && resultCode == -1) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				final Bitmap bitmap = extras.getParcelable("data");
				if (bitmap != null) {
					new Thread() {
						@Override
						public void run() {
							super.run();
							isUploading = true;
							getUIFragmentHelper().getTitleBar().setBackBtnEnable(false);
							writeBmpNative(bitmap);
							uploadBmpNet();
						}
					}.start();

				}
			}
		}
	}

	/**
	 * 将图片写入到本地保存
	 *
	 * @param bitmap
	 */
	private void writeBmpNative(Bitmap bitmap) {
		headImageFile.delete();
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(headImageFile);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 将图片上传到网络
	 *
	 */
	private void uploadBmpNet() {
		final byte[] imageData = ImageUtil.getBitmapBytes(
				headImageFile, 300, 800, 600);
		UploadService uploadService = (UploadService) getActivity().getSystemService(UploadService.SERVICE_NAME_QINIU);
		uploadService.upload(new UploadTask(UploadTask.TYPE_PICTURE, imageData), mUploadListener);
	}

	private UploadListener mUploadListener = new UploadListener() {

		@Override
		public void onUploadStarted(UploadTask uploadTask) {
			getLoadingView().showLoading();
		}

		@Override
		public void onUploadProgress(UploadTask uploadTask, double v) {

		}

		@Override
		public void onUploadComplete(UploadTask uploadTask, String url) {
			showContent();
			if (!TextUtils.isEmpty(url)) {
				loadData(ACTION_CHANGE_HEAD, PAGE_MORE, url, mClassItem.classId);
				mHeadPhoto = url;
			}
		}

		@Override
		public void onUploadError(UploadTask uploadTask, int i, String s, String token) {
			UiThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					showContent();
					ToastUtils.showShortToast(getActivity(), "上传头像失败");
					isUploading = false;
					getUIFragmentHelper().getTitleBar().setBackBtnEnable(true);
				}
			});
		}

		@Override
		public void onRetry(UploadTask uploadTask, int errorCode,
							String error, String extend) {
		}
	};

	@Override
	public void onDestroyImpl() {
		super.onDestroyImpl();
		mUploadListener = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && isUploading || super.onKeyDown(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 调用系统裁剪照片
	 *
	 * @param uri
	 */
	private void crop(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, REQCODE_FROM_CROP);
	}

	/**
	 * 打开班级转移页
	 */
	private void openClassTransferFragment() {
		Bundle mBundle = new Bundle();
		mBundle.putParcelable("class", mClassItem);
		ClassTransferSelectFragment fragment = (ClassTransferSelectFragment)
				Fragment.instantiate(getActivity(), ClassTransferSelectFragment.class.getName(), mBundle);
		showFragment(fragment);
	}

	/**
	 * 修改班级名称对话框
	 */
	private void updateClassName() {
		Bundle mBundle = new Bundle();
		mBundle.putString("title","编辑班群名称");
		mBundle.putInt("type", AddGradeClassFragment.TYPE_CHANGE_CLASS);
		mBundle.putString("classId", mClassItem.classId);
		mBundle.putString("grade", mClassItem.grade);
		mBundle.putString("classname", mClassItem.className);
		AddGradeClassFragment fragment = (AddGradeClassFragment) Fragment
				.instantiate(getActivity(),
						AddGradeClassFragment.class.getName(), mBundle);
		fragment.setOnConfirmClickListenr(new AddGradeClassFragment.OnAddClassSucessListenr() {
			@Override
			public void onAddClassSucess() {

			}

			@Override
			public void onChangeClassName(String grade, String name) {
				updateClassName(grade, name);
				ActionUtils.notifyMatchChanged();
			}
		});
		showFragment(fragment);
	}

	/**
	 * 显示删除班级对话框
	 * 
	 * @author weilei
	 *
	 */
	private void showDeleteDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		String title = "删除班群";

		mDialog = DialogUtils.getFillBlackDialog(getActivity(), title, "确认",
				"取消", "请输入登录密码", InputType.TYPE_TEXT_VARIATION_PASSWORD, new OnFillDialogBtnClickListener() {

					@Override
					public void onItemClick(Dialog dialog, boolean isConfirm,
							String resutl) {
						// TODO Auto-generated method stub

						if (isConfirm) {
							if (!TextUtils.isEmpty(resutl)) {
								deleteClass(resutl);
							} else {
								ToastUtils.showShortToast(getActivity(), "请输入登录密码确认删除");
							}
							mDialog.dismiss();
						} else{
							UIUtils.hideInputMethod(getActivity());
							mDialog.dismiss();
						}
					}

				});
		mDialog.show();
	}

	/**
	 * 更新班级名称
	 */
	private void updateClassName(String grade, String name) {
		mClassItem.className = name;
		mClassItem.grade = grade;
		mClassNameView.setText(SubjectUtils.getGradeName(mClassItem.grade) + " " + mClassItem.className);
		getTitleBar().setTitle(name);
		updateClassService.updateClassItem(mClassItem);
	}

	/**
	 * 更新班级头像
	 *
	 * @param url
	 */
	private void updateHeadPhoto(String url) {
		if (mHeadView != null) {
			ImageFetcher.getImageFetcher().loadImage(url, mHeadView,
					R.drawable.icon_class_default, new RoundDisplayer());
			mClassItem.mHeadPhoto = url;
			updateClassService.updateClassItem(mClassItem);
		}
	}

	/**
	 * 关闭班级操作
	 * 
	 * @author weilei
	 *
	 */
	private void closeClass() {
		// UserItem user = Utils.getLoginUserItem();
		// new DeleteClassTask(false, mToggleButton.isChecked()).execute(
		// user.token, mClassItem.classId);
		loadData(ACTION_CLOSE, PAGE_MORE, mClassItem.classId,
				ClassInfoItem.STATE_OPENED == mClassItem.state ? "1" : "0");
	}

	/**
	 * 删除班级操作
	 * 
	 * @author weilei
	 *
	 * @param password
	 */
	private void deleteClass(String password) {
		// UserItem user = Utils.getLoginUserItem();
		// new DeleteClassTask(true, true).execute(user.token,
		// mClassItem.classId,
		// password);
		loadData(ACTION_DELETE, PAGE_MORE, mClassItem.classId,
				password);
	}

	private void updateClose() {
		if (ClassInfoItem.STATE_OPENED == mClassItem.state) {
			ToastUtils.showShortToast(getActivity(), "不允许新同学加入");
			mClassItem.state = ClassInfoItem.STATE_CLOSED;
			mToggleButton
					.setImageResource(R.drawable.btn_toggle_button_cache_off);
		} else {
			ToastUtils.showShortToast(getActivity(), "允许新同学加入");
			mClassItem.state = ClassInfoItem.STATE_OPENED;
			mToggleButton
					.setImageResource(R.drawable.btn_toggle_button_cache_on);
		}
		updateClassService.updateClassItem(mClassItem);
	}

	@Override
	public void onPreAction(int action, int pageNo) {
		super.onPreAction(action, pageNo);
	}

	@Override
	public BaseObject onProcess(int action, int pageNo, Object... params) {
		if (action == ACTION_DELETE) {
			String url = OnlineServices.getDeleteClassUrl();
			JSONObject object = new JSONObject();
			try {
				object.put("class_id", (String) params[0]);
				object.put("password", (String) params[1]);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String data = object.toString();
			if (TextUtils.isEmpty(data))
				return null;
			BaseObject onlineInfo = new DataAcquirer<BaseObject>()
					.post(url, data, new BaseObject());
			return onlineInfo;
		} else if (action == ACTION_CLOSE) {
			String url = OnlineServices.getCloseClassUrl();
			JSONObject object = new JSONObject();
			try {
				object.put("class_id", (String) params[0]);
				object.put("is_close", (String) params[1]);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String data = object.toString();
			if (TextUtils.isEmpty(data))
				return null;
			BaseObject onlineInfo = new DataAcquirer<BaseObject>().post(url, data, new BaseObject());
			return onlineInfo;
		} else if (action == ACTION_CHANGE_HEAD) {
			String url = OnlineServices.getUpdateClassInfoUrl();
			JSONObject object = new JSONObject();
			try {
				object.put("class_id", (String) params[1]);
				object.put("image", (String) params[0]);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			String data = object.toString();
			if (TextUtils.isEmpty(data))
				return null;
			BaseObject result = new DataAcquirer<BaseObject>().post(url,
					data, new BaseObject());
			return result;
		}

		return null;
	}

	@Override
	public void onGet(int action, int pageNo, BaseObject result) {
		super.onGet(action, pageNo, result);
		showContent();
		if (result.isAvailable()) {
			if (action == ACTION_DELETE) {
				ToastUtils.showShortToast(getActivity(), "删除班群成功");
				updateClassService.removeClassItem(mClassItem.classId);
				ActionUtils.notifyMatchChanged();
				finish();
			} else if (action == ACTION_CLOSE) {
				updateClose();
			} else if (action == ACTION_CHANGE_HEAD) {
				updateHeadPhoto(mHeadPhoto);
				ToastUtils.showShortToast(getActivity(), "修改成功");
				isUploading = false;
				getUIFragmentHelper().getTitleBar().setBackBtnEnable(true);
			}
		}
	}

	@Override
	public void onFail(int action, int pageNo, BaseObject result) {
		super.onFail(action, pageNo, result);
		if (action == ACTION_CHANGE_HEAD) {
			isUploading = false;
			getUIFragmentHelper().getTitleBar().setBackBtnEnable(true);
		}
	}

}
