package com.knowbox.teacher.modules.image;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.knowbox.base.utils.ImageFetcher;
import com.knowbox.teacher.R;
import com.knowbox.teacher.modules.utils.DialogUtils;
import com.knowbox.teacher.modules.utils.DialogUtils.OnDialogButtonClickListener;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;
import com.knowbox.teacher.widgets.photoview.PhotoView;
import com.knowbox.teacher.widgets.photoview.PhotoViewAttacher;

import java.util.ArrayList;
import java.util.List;

/**
 * @name 左右滚动样式图片浏览编辑器
 */
public class ImagePreviewerEditFragment extends BaseUIFragment<UIFragmentHelper> {

	private ViewPager viewPager;
	private ImagePreviewerAdapter adapter;
	private ArrayList<String> mUrls;
	private TextView countText;
	private TextView curIndexText;
	private View footerLayout;
	private View headerLayout;
	private boolean isLocal;
	private boolean isHideEdit; //隐藏编辑
	private boolean isShowPreview; //只预览
	private Dialog mDialog;

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.layout_picture_edit,
				null);
		return view;
	}

	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		initViews(view);
		loadData();
		
	}

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		setSlideable(true);
		if (getArguments() != null && getArguments().containsKey("hideEdit")) {
			isHideEdit = getArguments().getBoolean("hideEdit");
		}
		if (getArguments() != null && getArguments().containsKey("showPreview")) {
			isShowPreview = getArguments().getBoolean("showPreview");
		}
	}

	private void initViews(View view) {
		headerLayout = (View) view.findViewById(R.id.header_layout);
		footerLayout = (View) view.findViewById(R.id.footer_layout);
		countText = (TextView) view.findViewById(R.id.header4_count_text);
		curIndexText = (TextView) view.findViewById(R.id.header4_cur_index);
		curIndexText.setText("1");

		viewPager = (ViewPager) view.findViewById(R.id.image_view_pager);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				curIndexText.setText("" + (position + 1));
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		view.findViewById(R.id.delete_text).setOnClickListener(mClickListener);
		view.findViewById(R.id.edit_text).setOnClickListener(mClickListener);
		view.findViewById(R.id.header4_back_btn).setOnClickListener(
				mClickListener);
		if (isHideEdit) {
			view.findViewById(R.id.edit_text).setVisibility(View.GONE);
		}

		if (isShowPreview) {
			footerLayout.setVisibility(View.GONE);
		}
	}

	OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.delete_text:
				showDeleteDialog();
				break;
			case R.id.edit_text:
				openPictureProcessFragment();
				break;
			case R.id.header4_back_btn:
				finish();
			default:
				break;
			}

		}
	};

	private void openPictureProcessFragment() {
		String path;
		if (isLocal) {
			path = mUrls.get(viewPager.getCurrentItem());
		} else {
			path = ImageFetcher.getImageFetcher()
					.getCacheFilePath(mUrls.get(viewPager.getCurrentItem()))
					.getAbsolutePath();
		}
		if (!TextUtils.isEmpty(path)) {
			Bundle mBundle = new Bundle();
			mBundle.putString("path", path);
			mBundle.putInt("index", viewPager.getCurrentItem());
			mBundle.putInt("size", viewPager.getChildCount());
			PictureProcessFragment fragment = (PictureProcessFragment) Fragment
					.instantiate(getActivity(),
							PictureProcessFragment.class.getName(), mBundle);
			fragment.setOnImageProcessListener(new PictureProcessFragment.OnImageProcessListener() {
				@Override
				public void onImageProcess(String path) {
					imageProcess(path);
				}
			});
			showFragment(fragment);
		}
	}

	/**
	 * 显示删除提示框
	 * @author weilei
	 *
	 */
	private void showDeleteDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		mDialog = DialogUtils.getMessageDialog(getActivity(), "提示", "确定", "取消",
				"确定要删除吗？", new OnDialogButtonClickListener() {

					@Override
					public void onItemClick(Dialog dialog, int btnId) {
						if (btnId == OnDialogButtonClickListener.BUTTON_CONFIRM) {
							if (mUrls != null && mUrls.size() > 0) {
								deleteImage();
							}
						}
						mDialog.dismiss();
					}
				});
		mDialog.show();
	}

	/**
	 * 图片处理结果刷新
	 * @author weilei
	 *
	 * @param path
	 */
	private void imageProcess(String path) {
		int index = viewPager.getCurrentItem();
		mUrls.remove(index);
		mUrls.add(index, path);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 删除图片操作
	 */
	private void deleteImage() {
		int curIndex = viewPager.getCurrentItem();
		countText.setText("" + (mUrls.size() - 1));
		mUrls.remove(curIndex);
		adapter.notifyDataSetChanged();
		if (mUrls.size() == 0) {
			finish();
		}
	}

	private void loadData() {
		int startIndex = 0;
		Bundle bundle = getArguments();
		mUrls = bundle.getStringArrayList("list");
		startIndex = bundle.getInt("index", 0);
		if (mUrls == null || mUrls.size() == 0) {
			this.finish();
		}
		isLocal = bundle.getBoolean("islocal");
		if (adapter == null) {
			adapter = new ImagePreviewerAdapter(getActivity(), mUrls);
			viewPager.setAdapter(adapter);
			viewPager.setCurrentItem(startIndex);
		}

		countText.setText("" + mUrls.size());
	}

	/**
	 * @name 图片滚动适配器
	 * 
	 */
	class ImagePreviewerAdapter extends PagerAdapter {

		private List<View> views;
		private List<String> urls;

		public ImagePreviewerAdapter(Context context, List<String> urls) {
			this.urls = urls;
			if (views == null) {
				views = new ArrayList<View>();
			}
			for (int i = 0; i < urls.size(); i++) {
				View view = View.inflate(context,
						R.layout.layout_image_previewer_edit_item, null);
				views.add(view);
			}

		}

		@Override
		public int getCount() {
			return urls.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			container.addView(views.get(position));
			String fileName = urls.get(position);
			final PhotoView imageView = (PhotoView) views.get(position)
					.findViewById(R.id.image);
			ImageFetcher.getImageFetcher().loadImage(fileName, imageView, R.drawable.default_image);

			imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
				@Override
				public void onPhotoTap(View view, float x, float y) {
					finish();
				}
			});
			return views.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(views.get(position));
		}
	}

	@Override
	public void onPanelClosed(View pPanel) {
		super.onPanelClosed(pPanel);
		if (mOnImageEditListener != null) {
			mOnImageEditListener.onImageEdit(mUrls);
		}
	}

	private OnImageEditListener mOnImageEditListener;

	public void setOnImageEditListener(OnImageEditListener listener) {
		mOnImageEditListener = listener;
	}

	public interface OnImageEditListener {
		public void onImageEdit(List<String> paths);
	}
}
