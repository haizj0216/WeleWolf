/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.knowbox.teacher.modules.message.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.hyena.framework.app.fragment.BaseSubFragment;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.utils.BaseApp;
import com.knowbox.teacher.R;
import com.knowbox.teacher.base.database.bean.ChatListItem;
import com.knowbox.teacher.base.utils.ImageUtil;
import com.knowbox.teacher.modules.image.ImagePreviewerEditFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoadImageTask extends AsyncTask<Object, Void, Bitmap> {
	private ImageView iv = null;
	String localFullSizePath = null;
	String thumbnailPath = null;
	String remotePath = null;
	EMMessage message = null;
	ChatType chatType;
	Context mContext;
	BaseSubFragment mFragment;
	ChatListItem mChatItem;

	public LoadImageTask(BaseSubFragment fragment, ChatListItem chatItem) {
		super();
		this.mFragment = fragment;
		mChatItem = chatItem;
	}
	
	@Override
	protected Bitmap doInBackground(Object... args) {
		thumbnailPath = (String) args[0];
		localFullSizePath = (String) args[1];
		remotePath = (String) args[2];
		chatType = (ChatType) args[3];
		iv = (ImageView) args[4];
		// if(args[2] != null) {
		mContext = (Context) args[5];
		// }
		message = (EMMessage) args[6];
		File file = new File(thumbnailPath);
//		if (file.exists()) {
//			return ImageUtils.decodeScaleImage(thumbnailPath, 160, 160);
//		} else {
//			if (message.direct == EMMessage.Direct.SEND) {
//				return ImageUtils.decodeScaleImage(localFullSizePath, 160, 160);
//			} else {
//				return null;
//			}
//		}
		int maskDrawableId = message.direct == EMMessage.Direct.RECEIVE ? R.drawable.chatfrom_bg_normal : R.drawable.chatto_bg_normal;
		try {
			if (file.exists()) {
				Bitmap maskBitmap = BitmapFactory.decodeResource(BaseApp.getAppContext().getResources(), maskDrawableId);
				
				Bitmap sourceBitmap = BitmapFactory.decodeFile(thumbnailPath);
				float density = mContext.getResources().getDisplayMetrics().density;
				Bitmap bitmap = ImageUtil.scale(sourceBitmap, (int)(80 * density), (int)(80 * density), ScaleType.CENTER_CROP, true);
				
				return ImageUtil.maskDrawable(bitmap, maskBitmap);
			} else {
				if (message.direct == EMMessage.Direct.SEND) {
					Bitmap maskBitmap = BitmapFactory.decodeResource(BaseApp.getAppContext().getResources(), maskDrawableId);
//					return ImageUtil.maskDrawable(com.easemob.util.ImageUtils.decodeScaleImage(localFullSizePath, 200, 160), maskBitmap);
					Bitmap sourceBitmap = ImageUtil.createBitmap(localFullSizePath);
					float density = mContext.getResources().getDisplayMetrics().density;
					Bitmap bitmap = ImageUtil.scale(sourceBitmap, (int)(80 * density), (int)(80 * density), ScaleType.CENTER_CROP, true);
					
					return ImageUtil.maskDrawable(bitmap, maskBitmap);
				} else {
					return null;
				}
			}
		} catch (Throwable e) {
			LogUtil.e("", e);
		}
		return null;
	}

	protected void onPostExecute(Bitmap image) {
		if (image != null) {
			iv.setImageBitmap(image);
			ImageCache.getInstance().put(thumbnailPath, image);
			iv.setClickable(true);
			iv.setTag(thumbnailPath);
			iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (thumbnailPath != null) {
						if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked) {
							message.isAcked = true;
							try {
								// 看了大图后发个已读回执给对方
								EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
//						Intent intent = new Intent(activity, ShowBigImage.class);
//						File file = new File(localFullSizePath);
//						if (file.exists()) {
//							Uri uri = Uri.fromFile(file);
//							intent.putExtra("uri", uri);
//						} else {
//							// The local full size pic does not exist yet.
//							// ShowBigImage needs to download it from the server
//							// first
//							intent.putExtra("remotepath", remotePath);
//						}
//						if (message.getChatType() != ChatType.Chat) {
//							// delete the image from server after download
//						}
//						activity.startActivity(intent);
						
						if(mFragment != null){
//							showAllPictures(message);
						}
					}
				}
			});
		} else {
			if (message.status == EMMessage.Status.FAIL) {
				if(NetworkProvider.getNetworkProvider() == null
						|| NetworkProvider.getNetworkProvider().getNetworkSensor() == null)
					return;
				
				if (NetworkProvider.getNetworkProvider().getNetworkSensor().isNetworkAvailable()) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							EMChatManager.getInstance().asyncFetchMessage(message);
						}
					}).start();
				}
			}

		}
	}

	private void showAllPictures(EMMessage curMessage) {
		EMConversation mEmConversation = EMChatManager.getInstance().getConversation(mChatItem.mUserId);
		List<EMMessage> mMessages = mEmConversation.getAllMessages();
		ArrayList<String> allPaths = new ArrayList<String>();
		int index = 0;
		for (int i = 0; i < mMessages.size(); i++) {
			EMMessage message = mMessages.get(i);
			if (message.getType() == EMMessage.Type.IMAGE) {
				ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
				if (message.direct == EMMessage.Direct.RECEIVE) {
					if (imgBody.getLocalUrl() != null) {
						String remotePath = imgBody.getRemoteUrl();
						String filePath = ImageUtils.getImagePath(remotePath);
						if (new File(filePath).exists()) {
							allPaths.add("file://" + filePath);
						} else {
							allPaths.add(remotePath);
						}
						if (curMessage.equals(message)) {
							index = allPaths.size() - 1;
						}
					}
				} else {
					if (imgBody.getLocalUrl() != null) {
						if (new File(imgBody.getLocalUrl()).exists()){
							allPaths.add("file://" + imgBody.getLocalUrl());
							if (curMessage.equals(message)) {
								index = allPaths.size() - 1;
							}
						}

					}
				}

			}
		}

		Bundle bundle = new Bundle();
		bundle.putStringArrayList("list", allPaths);
		bundle.putInt("index", index);
		bundle.putBoolean("showPreview", true);
		ImagePreviewerEditFragment fragment = (ImagePreviewerEditFragment) Fragment
				.instantiate(mFragment.getActivity(),
						ImagePreviewerEditFragment.class.getName(), bundle);
		fragment.setOnImageEditListener(new ImagePreviewerEditFragment.OnImageEditListener() {

			@Override
			public void onImageEdit(List<String> paths) {

			}
		});
		mFragment.showFragment(fragment);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
}
