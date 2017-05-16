package com.buang.welewolf.modules.profile;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.buang.welewolf.widgets.photoview.PhotoView;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.buang.welewolf.R;
import com.buang.welewolf.base.utils.ImageUtil;
import com.buang.welewolf.modules.utils.DialogUtils;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;

/**
 * @name 图片预览
 * @author LiuYu
 * @date 2015年9月5日
 */
public class DocumentImgPreviewFragment extends BaseUIFragment<UIFragmentHelper> {

	private PhotoView mPhotoView;
	private String mFilename;

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		setSlideable(true);
		if (getArguments() != null && getArguments().containsKey("filename")) {
			mFilename = getArguments().getString("filename");
		}
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		return View.inflate(getActivity(), R.layout.fragment_image_preview,
				null);
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		mPhotoView = (PhotoView) view.findViewById(R.id.photoview);
		view.findViewById(R.id.header_back_btn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();

					}
				});
		view.findViewById(R.id.delete_text).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						deleteImage();
					}
				});
		setImage();
	}

	private void setImage() {
		if (TextUtils.isEmpty(mFilename)) {
			ToastUtils.showShortToast(getActivity(), "图片读取失败");
			return;
		}
		Bitmap bitmap = ImageUtil.createBitmap(mFilename, 400);
		if (bitmap == null) {
			ToastUtils.showShortToast(getActivity(), "图片解析失败");
			return;
		}
		mPhotoView.setImageBitmap(bitmap);
	}

	private void deleteImage() {
		Dialog dialog = DialogUtils.getMessageDialog(getActivity(), "删除", "确定",
				"取消", "确定要删除该图片？",
				new DialogUtils.OnDialogButtonClickListener() {
					@Override
					public void onItemClick(Dialog dialog, int btnId) {
						if (btnId == 0) {
							if (mOnImageDeleteListener != null) {
								mOnImageDeleteListener.onImageDelete(mFilename);
								finish();
							}
						}
						dialog.dismiss();
					}
				});
		if (dialog != null && !dialog.isShowing()) {
			dialog.show();
		}
	}

	private OnImageDeleteListener mOnImageDeleteListener;

	public void setOnImageDeleteListener(OnImageDeleteListener listener) {
		mOnImageDeleteListener = listener;
	}

	public interface OnImageDeleteListener {
		public void onImageDelete(String filename);
	}

}
