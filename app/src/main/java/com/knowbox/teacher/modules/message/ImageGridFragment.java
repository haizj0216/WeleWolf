package com.knowbox.teacher.modules.message;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.utils.MsgCenter;
import com.knowbox.teacher.BuildConfig;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.database.bean.ChatListItem;
import com.knowbox.teacher.base.utils.FileUtils;
import com.knowbox.teacher.modules.message.bean.VideoEntity;
import com.knowbox.teacher.modules.message.utils.ImageCache;
import com.knowbox.teacher.modules.message.utils.SendUtils;
import com.knowbox.teacher.modules.utils.ToastUtils;
import com.knowbox.teacher.modules.utils.UIFragmentHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageGridFragment extends BaseUIFragment<UIFragmentHelper> implements
		OnItemClickListener {

	private static final String TAG = "ImageGridFragment";
	private int mImageThumbSize;
	private int mImageThumbSpacing;
	private ImageAdapter mAdapter;
	private List<VideoEntity> mList;
	private ChatListItem mChatItem;

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		mImageThumbSize = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_size);
		mImageThumbSpacing = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_spacing);
		mList = new ArrayList<VideoEntity>();
		getVideoFile();
		mAdapter = new ImageAdapter(getActivity());
	}

	@Override
	public void onViewCreatedImpl(View v, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreatedImpl(v, savedInstanceState);
		mChatItem = (ChatListItem) getArguments().getSerializable("chatItem");

		final GridView mGridView = (GridView) v.findViewById(R.id.gridView);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
		mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@SuppressWarnings("deprecation")
					@TargetApi(VERSION_CODES.JELLY_BEAN)
					@Override
					public void onGlobalLayout() {
						final int numColumns = (int) Math.floor(mGridView
								.getWidth()
								/ (mImageThumbSize + mImageThumbSpacing));
						if (numColumns > 0) {
							final int columnWidth = (mGridView.getWidth() / numColumns)
									- mImageThumbSpacing;
							mAdapter.setItemHeight(columnWidth);
							if (BuildConfig.DEBUG) {
								Log.d(TAG, "onCreateView - numColumns set to "
										+ numColumns);
							}
							if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
								mGridView.getViewTreeObserver()
										.removeOnGlobalLayoutListener(this);
							} else {
								mGridView.getViewTreeObserver()
										.removeGlobalOnLayoutListener(this);
							}
						}
					}
				});
	}

	@Override
	public View onCreateViewImpl(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getTitleBar().setTitle("视频");
		View v = View.inflate(getActivity(),
				R.layout.layout_message_image_grid_fragment, null);
		return v;
	}

	@Override
	public void onResumeImpl() {
		super.onResumeImpl();
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroyImpl() {
		super.onDestroyImpl();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, final int position,
			long id) {
		if (position == 0) {
			Intent intent = new Intent();
			intent.setClass(getActivity(), RecorderVideoActivity.class);
			startActivityForResult(intent, 100);
		} else {
			VideoEntity vEntty = mList.get(position - 1);
			// 限制大小不能超过10M
			if (vEntty.size > 1024 * 1024 * 10) {
				String st = getResources().getString(
						R.string.temporary_does_not);
				ToastUtils.showShortToast(getActivity(), st);
				return;
			}
			// Intent intent = getActivity().getIntent()
			// .putExtra("path", vEntty.filePath)
			// .putExtra("dur", vEntty.duration);
			// getActivity().setResult(Activity.RESULT_OK, intent);
			// getActivity().finish();
			sendImpl(vEntty.filePath, vEntty.duration);
		}
	}

	private class ImageAdapter extends BaseAdapter {

		private final Context mContext;
		private int mItemHeight = 0;
		private RelativeLayout.LayoutParams mImageViewLayoutParams;

		public ImageAdapter(Context context) {
			super();
			mContext = context;
			mImageViewLayoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}

		@Override
		public int getCount() {
			return mList.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			return (position == 0) ? null : mList.get(position - 1);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup container) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(mContext,
						R.layout.layout_message_choose_griditem, null);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.imageView);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.video_icon);
				holder.tvDur = (TextView) convertView
						.findViewById(R.id.chatting_length_iv);
				holder.tvSize = (TextView) convertView
						.findViewById(R.id.chatting_size_iv);
				holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				holder.imageView.setLayoutParams(mImageViewLayoutParams);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (holder.imageView.getLayoutParams().height != mItemHeight) {
				holder.imageView.setLayoutParams(mImageViewLayoutParams);
			}
			String st1 = getResources().getString(R.string.Video_footage);
			if (position == 0) {
				holder.icon.setVisibility(View.GONE);
				holder.tvDur.setVisibility(View.GONE);
				holder.tvSize.setText(st1);
				holder.tvSize.setGravity(Gravity.CENTER);
				holder.imageView.setImageResource(R.drawable.chat_camera_icon);
			} else {
				holder.icon.setVisibility(View.VISIBLE);
				VideoEntity entty = mList.get(position - 1);
				holder.tvDur.setVisibility(View.VISIBLE);

				holder.tvDur.setText(DateUtils.toTime(entty.duration));
				holder.tvSize.setText(FileUtils.byteCountToDisplaySize(entty.size, 2));
				holder.imageView.setImageResource(R.drawable.empty_photo);
				Bitmap bitmap = ImageCache.getInstance().get(entty.filePath);
				if (bitmap != null) {
					holder.imageView.setImageBitmap(bitmap);
				} else {
					bitmap = ThumbnailUtils.createVideoThumbnail(
							entty.filePath, Thumbnails.MICRO_KIND);
					if(bitmap != null && !TextUtils.isEmpty(entty.filePath))
						ImageCache.getInstance().put(entty.filePath, bitmap);
				}
			}
			return convertView;
		}

		/**
		 * Sets the item height. Useful for when we know the column width so the
		 * height can be set to match.
		 * 
		 * @param height
		 */
		public void setItemHeight(int height) {
			if (height == mItemHeight) {
				return;
			}
			mItemHeight = height;
			mImageViewLayoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, mItemHeight);
			notifyDataSetChanged();
		}

		class ViewHolder {
			ImageView imageView;
			ImageView icon;
			TextView tvDur;
			TextView tvSize;
		}

	}

	private void getVideoFile() {
		ContentResolver mContentResolver = getActivity().getContentResolver();
		Cursor cursor = mContentResolver.query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Video.DEFAULT_SORT_ORDER);
		if (cursor.moveToFirst()) {
			do {
				// ID:MediaStore.Audio.Media._ID
				int id = cursor.getInt(cursor
						.getColumnIndexOrThrow(MediaStore.Video.Media._ID));

				// 名称：MediaStore.Audio.Media.TITLE
				String title = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
				// 路径：MediaStore.Audio.Media.DATA
				String url = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

				// 总播放时长：MediaStore.Audio.Media.DURATION
				int duration = cursor
						.getInt(cursor
								.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

				// 大小：MediaStore.Audio.Media.SIZE
				long size = cursor.getLong(cursor
						.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));

				VideoEntity entty = new VideoEntity();
				entty.ID = id;
				entty.title = title;
				entty.filePath = url;
				entty.duration = duration;
				entty.size = size;
				mList.add(entty);
			} while (cursor.moveToNext());

		}
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 100) {
				Uri uri = data.getParcelableExtra("uri");
				String[] projects = new String[] { MediaStore.Video.Media.DATA,
						MediaStore.Video.Media.DURATION };
				Cursor cursor = getActivity().getContentResolver().query(uri,
						projects, null, null, null);
				int duration = 0;
				String filePath = null;
				if (cursor.moveToFirst()) {
					// 路径：MediaStore.Audio.Media.DATA
					filePath = cursor
							.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
					// 总播放时长：MediaStore.Audio.Media.DURATION
					duration = cursor
							.getInt(cursor
									.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
					System.out.println("duration:" + duration);
				}
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
				sendImpl(filePath, duration);
			}
		}
	}

	private void sendImpl(String filePath, int duration) {
		if (mChatItem == null)
			return;

		File file = new File(PathUtil.getInstance().getImagePath(), "thvideo"
				+ System.currentTimeMillis());
		Bitmap bitmap = null;
		FileOutputStream fos = null;
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			bitmap = ThumbnailUtils.createVideoThumbnail(filePath, 3);
			if (bitmap == null) {
				EMLog.d("chatactivity",
						"problem load video thumbnail bitmap,use default icon");
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.chat_app_panel_video_icon);
			}
			fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, fos);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fos = null;
			}
			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
			}

		}
		finish();
		SendUtils.sendVideo(mChatItem.mUserId, mChatItem, filePath,
				file.getAbsolutePath(), duration / 1000);
		MsgCenter.sendLocalBroadcast(new Intent(
				EMChatFragment.MSG_REFRESH_CHAT_LIST));
	}
}
