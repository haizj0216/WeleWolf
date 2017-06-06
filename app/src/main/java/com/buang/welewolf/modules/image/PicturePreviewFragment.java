package com.buang.welewolf.modules.image;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.UiThreadHandler;
import com.buang.welewolf.R;
import com.buang.welewolf.modules.utils.ToastUtils;
import com.buang.welewolf.modules.utils.UIFragmentHelper;
import com.buang.welewolf.widgets.correct.DrawContaienr;

public class PicturePreviewFragment extends BaseUIFragment<UIFragmentHelper> {
	private DrawContaienr mImage;
	private ImageView mDelete;

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateImpl(savedInstanceState);
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		mImage = (DrawContaienr) view.findViewById(R.id.img_preview);
		mDelete = (ImageView) view.findViewById(R.id.img_preview_delete);
		String url = getArguments().getString("url");
		ImageFetcher.getImageFetcher().loadImage(url, "",
				new ImageFetcher.ImageFetcherListener() {

					@Override
					public void onLoadComplete(String imageUrl,
							final Bitmap bitmap, Object object) {
						if (bitmap == null) {
							ToastUtils.showShortToast(BaseApp.getAppContext(),
									"加载图片失败");
							finish();
						}
						UiThreadHandler.post(new Runnable() {

							@Override
							public void run() {
								if (bitmap != null) {
									mImage.setRawBitmap(bitmap);
								}
							}
						});
					}
				});

		mDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		View view = View.inflate(getActivity(),
				R.layout.layout_picture_preview, null);
		return view;
	}

	@Override
	public void onDestroyImpl() {
		// TODO Auto-generated method stub
		super.onDestroyImpl();
		if (mImage != null) {
			mImage.setRawBitmap(null);
			mImage = null;
		}
	}

}
