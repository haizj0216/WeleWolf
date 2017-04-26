package com.knowbox.teacher.modules.message.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.VideoMessageBody;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.utils.BaseApp;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.utils.ImageUtil;
import com.knowbox.teacher.modules.message.ShowVideoActivity;

import java.io.File;

public class LoadVideoImageTask extends AsyncTask<Object, Void, Bitmap> {

	private ImageView iv = null;
	String thumbnailPath = null;
	String thumbnailUrl = null;
	Context mContext;
	EMMessage message;
	BaseAdapter adapter;

	@Override
	protected Bitmap doInBackground(Object... params) {
		thumbnailPath = (String) params[0];
		thumbnailUrl = (String) params[1];
		iv = (ImageView) params[2];
		mContext = (Context) params[3];
		message = (EMMessage) params[4];
		adapter = (BaseAdapter) params[5];
		
		int maskDrawableId = message.direct == EMMessage.Direct.RECEIVE ? R.drawable.chatfrom_bg_normal : R.drawable.chatto_bg_normal;
		try {
			if (new File(thumbnailPath).exists()) {
//				Bitmap bitmap = ImageUtils.decodeScaleImage(thumbnailPath, 600, 600);
				Bitmap sourceBitmap = BitmapFactory.decodeFile(thumbnailPath);
				float density = mContext.getResources().getDisplayMetrics().density;
				Bitmap bitmap = ImageUtil.scale(sourceBitmap, (int)(130 * density), (int)(145 * density), ScaleType.CENTER_CROP, true);
				Bitmap maskBitmap = BitmapFactory.decodeResource(BaseApp.getAppContext().getResources(), maskDrawableId);
				return ImageUtil.maskDrawable(bitmap, maskBitmap);
			} else {
				return null;
			}
		} catch (Throwable e) {
			LogUtil.e("", e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		if (result != null) {
			iv.setImageBitmap(result);
			ImageCache.getInstance().put(thumbnailPath, result);
			iv.setClickable(true);
			iv.setTag(thumbnailPath);
			iv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (thumbnailPath != null) {
						if (message != null && message.direct == EMMessage.Direct.RECEIVE
								&& !message.isAcked) {
							try {
								message.isAcked = true;
								EMChatManager.getInstance().ackMessageRead(
										message.getFrom(), message.getMsgId());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						VideoMessageBody videoBody = (VideoMessageBody) message
								.getBody();
						Intent intent = new Intent(mContext,
								ShowVideoActivity.class);
						intent.putExtra("localpath", videoBody.getLocalUrl());
						intent.putExtra("secret", videoBody.getSecret());
						intent.putExtra("remotepath", videoBody.getRemoteUrl());
						mContext.startActivity(intent);

					}
				}
			});

		} else {
			if (message.status == EMMessage.Status.FAIL
					|| message.direct == EMMessage.Direct.RECEIVE) {
				if (NetworkProvider.getNetworkProvider().getNetworkSensor().isNetworkAvailable()) {
					new AsyncTask<Void, Void, Void>() {
						@Override
						protected Void doInBackground(Void... params) {
							EMChatManager.getInstance().asyncFetchMessage(message);
							return null;
						}
						@Override
						protected void onPostExecute(Void result) {
							adapter.notifyDataSetChanged();
						};
					}.execute();
				}
			}

		}
	}

}
