package com.knowbox.teacher.modules.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.knowbox.base.utils.UIUtils;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.utils.ImageUtil;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.widgets.CropCanvas;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 图片处理模块
 * 
 * @author weilei
 * 
 */
public class PictureProcessFragment extends BaseUIFragment<UIFragmentHelper> implements
		OnClickListener {

	private int showCount;
	private int tempAngle;
	private PictureType type;
	private Handler handler;

	private boolean isEnhanced = true;
	private CropCanvas cropCanvas;
	private View btmLay;

	private TextView commitText;
	private TextView orignalText;
	private TextView enhanceText;
	private ImageView backBtn;
	private String mImagePath;

	private FrameLayout controlLay;
	private Bitmap orignalBitmap, tempBitmap;
	private ImageView orignalImg, enhanceImg;
	private ImageView displayImg;

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		View view = View.inflate(getActivity(),
				R.layout.layout_picture_process, null);
		return view;
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		initWidgets(view);
	}

	private void initWidgets(View view) {
		Bundle comingIntent = getArguments();
		int index = comingIntent.getInt("index", -1);
		int pageTotalSize = comingIntent.getInt("size", 1);
		mImagePath = comingIntent.getString("path");

		if (TextUtils.isEmpty(mImagePath)) {
			closeAndReturn();
			return;
		}

//		orignalBitmap = ImageUtil.createBitmap(mImagePath,
//				UIUtils.getWindowWidth(getActivity()));
		try{
			orignalBitmap = ImageUtil.setOrientation(ImageLoader.getInstance().loadImageSync("file://" + mImagePath), mImagePath);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		if (null == orignalBitmap) {
			closeAndReturn();
			return;
		}

		handler = new Handler();
		tempBitmap = orignalBitmap;

		displayImg = (ImageView) view.findViewById(R.id.displayImgId);
		controlLay = (FrameLayout) view.findViewById(R.id.controlLayId);
		displayImg.setImageBitmap(orignalBitmap);
		cropCanvas = (CropCanvas) view.findViewById(R.id.cropCanvasId);
		btmLay = view.findViewById(R.id.btm0LayId);
		view.findViewById(R.id.pbackFrayId).setOnClickListener(this);

		TextView title = (TextView) view.findViewById(R.id.header_title_txt);
		title.setText((index + 1) + "/" + pageTotalSize);
		commitText = (TextView) view.findViewById(R.id.header_submit_txt);
		commitText.setOnClickListener(this);
		type = PictureType.ORIGNAL;
		setTextProp("提交");

		backBtn = (ImageView) view.findViewById(R.id.header_back_btn);
		// backBtn.setOnClickListener(this);

		// 原图
		orignalImg = (ImageView) view.findViewById(R.id.orignalImgId);
		orignalText = (TextView) view.findViewById(R.id.orignalTextId);
		view.findViewById(R.id.orignalLayId).setOnClickListener(this);

		// 增强
		enhanceImg = (ImageView) view.findViewById(R.id.enhanceImgId);
		enhanceText = (TextView) view.findViewById(R.id.enhanceTextId);
		view.findViewById(R.id.enhanceLayId).setOnClickListener(this);

		// 裁剪
		view.findViewById(R.id.cropLayId).setOnClickListener(this);

		// 旋转
		view.findViewById(R.id.rotateLayId).setOnClickListener(this);

		setOrignalControlsState(orignalImg, R.drawable.icon_picprocess_y,
				orignalText, true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pbackFrayId:
			if (type == PictureType.CROP) {
				setCommitCropOperate(false);
			} else
				finish();
			break;

		case R.id.orignalLayId:
			setOrignalImg();
			break;

		case R.id.enhanceLayId:
			if (isEnhanced) {
				enhanceThisImg();
			}
			break;
		case R.id.cropLayId:
			cropThisImg();
			break;
		case R.id.rotateLayId:
			rotateThisImg();
			break;
		case R.id.header_submit_txt:
			commitOrOperate();
			break;
		default:
			break;
		}
	}

	/**
	 * 图片增强
	 * 
	 * @author weilei
	 * 
	 */
	private void enhanceThisImg() {
		type = PictureType.ENHANCE;
		setEnhanceImgState();
		tempBitmap = getLighterBitmap(tempBitmap);
		displayImg.setImageBitmap(tempBitmap);
	}

	/**
	 * 设置图片增强状态
	 * 
	 * @author weilei
	 * 
	 */
	private void setEnhanceImgState() {
		showCount = 0;
		isEnhanced = false;
		invalidateCropImg();
		cropCanvas.setVisibility(View.GONE);
		displayImg.setVisibility(View.VISIBLE);
		setEnhanceControlsState(enhanceImg, R.drawable.icon_light_y,
				enhanceText, true);
		setOrignalControlsState(orignalImg, R.drawable.icon_picprocess_n,
				orignalText, false);
	}

	/**
	 * 裁剪原图
	 */
	private void cropThisImg() {
		if (isCanCropImg()) {
			if (showCount <= 3) {
				showCount++;
				ToastUtils.showLongToast(getActivity(), "作业图片太小,不能裁剪");
			}
			return;
		}
		cropCanvas.setBitmap(tempBitmap);
		cropCanvas.setVisibility(View.VISIBLE);

		displayImg.setVisibility(View.GONE);

		startTop2BtmAnimation(getActivity(), btmLay, View.GONE);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				type = PictureType.CROP;
				setTextProp("应用");
				backBtn.setImageResource(R.drawable.ic_scratch_close);
			}
		}, 300);
	}

	private boolean isCanCropImg() {
		if (tempBitmap.getWidth() <= (UIUtils.getWindowWidth(getActivity()) * 2 / 3)
				&& tempBitmap.getHeight() <= (controlLay.getHeight() * 2 / 3)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 显示原图
	 */
	private void setOrignalImg() {
		resetTempState();
		displayImg.setImageBitmap(orignalBitmap);
		displayImg.setVisibility(View.VISIBLE);

		invalidateCropImg();
		cropCanvas.setVisibility(View.GONE);
		setEnhanceControlsState(enhanceImg, R.drawable.icon_light_n,
				enhanceText, false);
		setOrignalControlsState(orignalImg, R.drawable.icon_picprocess_y,
				orignalText, true);
	}

	private void invalidateCropImg() {
		cropCanvas.invalidate();
	}

	/**
	 * 旋转图片
	 */
	private void rotateThisImg() {
		if (null == tempBitmap) {
			ToastUtils.showLongToast(getActivity(), "图片不存在,请重新获取");
			return;
		}

		if (tempAngle <= 4) {
			tempAngle++;
		} else {
			tempAngle = 1;
		}

		Animation operatingAnim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.rotate_turn_round);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		displayImg.startAnimation(operatingAnim);

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				type = PictureType.ROTATE;
				Matrix mtx = new Matrix();
				mtx.postRotate(90);
				Bitmap bitamp = Bitmap.createBitmap(tempBitmap, 0, 0,
						tempBitmap.getWidth(), tempBitmap.getHeight(), mtx,
						true);
				tempBitmap = bitamp;
				displayImg.setImageBitmap(tempBitmap);
				displayImg.invalidate();
				displayImg.clearAnimation();
			}
		}, 300);
	}

	/**
	 * 重置
	 */
	private void resetTempState() {
		tempAngle = 0;
		showCount = 0;
		isEnhanced = true;
		type = PictureType.ORIGNAL;
		tempBitmap = orignalBitmap;
		enhanceText.setTextColor(Color.parseColor("#8f9199"));
		enhanceImg.setImageResource(R.drawable.tab_enhance_photo_tab);
	}

	/**
	 * 提交或应用
	 */
	private void commitOrOperate() {
		switch (type) {
		case CROP:
			setCommitCropOperate(true);
			break;
		default:
			commitThisPhoto();
			break;
		}
	}

	private void commitThisPhoto() {
		try {
			if (!android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				ToastUtils.showLongToast(getActivity(), "SD卡不存在,不能保存该图片");
			}
			if (mOnImageProcessListener != null && tempBitmap != null
					&& !tempBitmap.isRecycled()) {
				String cachePath = mImagePath + "_cache";
				ImageUtil.writeToFile(tempBitmap, cachePath, 100, false);
				if (!cachePath.contains("file://"))
					mOnImageProcessListener.onImageProcess("file://" + cachePath);
				else
					mOnImageProcessListener.onImageProcess(cachePath);
			}
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 设置控件显示状态
	 * 
	 * @param isCrop
	 */
	private void setCommitCropOperate(boolean isCrop) {
		if (isCrop) {
			tempBitmap = cropCanvas.getSubsetBitmap();
		}
		startBtm2TopAnimation(getActivity(), btmLay, View.VISIBLE);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				type = PictureType.ORIGNAL;
				setTextProp("提交");
				displayImg.setImageBitmap(tempBitmap);
				displayImg.setVisibility(View.VISIBLE);
				cropCanvas.setVisibility(View.GONE);
				backBtn.setImageResource(R.drawable.bt_title_back);
			}
		}, 300);
	}

	/**
	 * @param text
	 */
	protected void setTextProp(String text) {
		if (commitText != null)
			commitText.setText(text);
	}

	/**
	 *
	 */
	private void closeAndReturn() {
		ToastUtils.showLongToast(getActivity(), "获取图片失败,请重试");
		finish();
	}

	/**
	 * 设置原图icon
	 * 
	 * @author weilei
	 * 
	 * @param iView
	 * @param imgRes
	 * @param tView
	 * @param isPressed
	 */
	private void setOrignalControlsState(ImageView iView, int imgRes,
			TextView tView, boolean isPressed) {

		if (isPressed) {
			tView.setTextColor(getResources().getColor(
					R.color.color_main_app));
			iView.setImageResource(imgRes);
		} else {
			tView.setTextColor(getResources().getColor(
					R.color.text_nopressed_color));
			iView.setImageResource(imgRes);
		}

	}

	/**
	 * 设置增强icon
	 * 
	 * @author weilei
	 * 
	 * @param iView
	 * @param imgRes
	 * @param tView
	 * @param isPressed
	 */
	private void setEnhanceControlsState(ImageView iView, int imgRes,
			TextView tView, boolean isPressed) {
		if (isPressed) {
			tView.setTextColor(getResources().getColor(
					R.color.color_main_app));
			iView.setImageResource(imgRes);
		} else {
			tView.setTextColor(getResources().getColor(
					R.color.text_nopressed_color));
			iView.setImageResource(imgRes);
		}
	}

	/**
	 * 
	 * @param mBitmap
	 * @return
	 */
	private Bitmap getLighterBitmap(Bitmap mBitmap) {

		float mHueValue = 220 * 1.0F / 127;
		Bitmap bmp = Bitmap.createBitmap(mBitmap.getWidth(),
				mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setAntiAlias(true);

		ColorMatrix mHueMatrix = new ColorMatrix();
		ColorMatrix mAllMatrix = new ColorMatrix();

		mHueMatrix.reset();
		mHueMatrix.setScale(mHueValue, mHueValue, mHueValue, 1);
		mAllMatrix.reset();
		mAllMatrix.postConcat(mHueMatrix);

		paint.setColorFilter(new ColorMatrixColorFilter(mAllMatrix));
		canvas.drawBitmap(mBitmap, 0, 0, paint);
		return bmp;
	}

	private void startTop2BtmAnimation(Context context, View skipRight,
			int visibility) {
		if (skipRight == null)
			return;
		Animation alphaAnimation = AnimationUtils.loadAnimation(context,
				R.anim.translate_to_bottom);
		skipRight.startAnimation(alphaAnimation);
		skipRight.setVisibility(visibility);
	}

	private void startBtm2TopAnimation(Context context, View skipRight,
			int visibility) {
		if (skipRight == null)
			return;
		Animation alphaAnimation = AnimationUtils.loadAnimation(context,
				R.anim.translate_to_top);
		skipRight.startAnimation(alphaAnimation);
		skipRight.setVisibility(visibility);
	}

	@Override
	public void onDestroyImpl() {
		super.onDestroyImpl();
		if (orignalBitmap != null && !orignalBitmap.isRecycled()) {
			orignalBitmap.recycle();
		}
		if (tempBitmap != null && !tempBitmap.isRecycled()) {
			tempBitmap.recycle();
		}
	}

	private OnImageProcessListener mOnImageProcessListener;

	public void setOnImageProcessListener(OnImageProcessListener listener) {
		mOnImageProcessListener = listener;
	}

	public interface OnImageProcessListener {
		public void onImageProcess(String path);
	}

}
