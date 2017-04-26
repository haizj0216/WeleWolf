/**
 * Copyright (C) 2014 The KnowBoxTeacher2.0 Project
 */
package com.knowbox.teacher.modules.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.hyena.framework.network.utils.MultiFileDownloadHelper;
import com.hyena.framework.network.utils.MultiFileDownloadHelper.MultiFile;
import com.hyena.framework.network.utils.MultiFileDownloadHelper.MultiHttpListener;
import com.hyena.framework.utils.BaseApp;
import com.knowbox.teacher.MainActivity;
import com.knowbox.teacher.R;

public class PackageUpdateTask extends AsyncTask<String, Integer, Boolean> {

	private NotificationManager manager = null;
	private NotificationCompat.Builder builder;
	private boolean mDownloading = false;

	public PackageUpdateTask() {
		super();
		manager = (NotificationManager) BaseApp.getAppContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	protected void onPreExecute() {
		builder = new NotificationCompat.Builder(BaseApp.getAppContext());
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setLargeIcon(BitmapFactory.decodeResource(BaseApp
				.getAppContext().getResources(), R.drawable.ic_launcher));
		builder.setContentTitle("版本升级");
		Intent intent = new Intent(BaseApp.getAppContext(), MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(BaseApp.getAppContext(),
				0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pi);
		builder.setProgress(100, 0, false);
		manager.notify(1, builder.build());
	}

	private String mFilePath;
	@Override
	protected Boolean doInBackground(String... params) {
		if(mOnUpdateProgressListener != null) {
			mOnUpdateProgressListener.onUpdateStart();
		}
		mDownloading = true;
		MultiFileDownloadHelper helper = new MultiFileDownloadHelper();
		boolean result = helper.downloadMultiFiles(new MultiHttpListener() {

			private int mLastPercent = 0;
			@Override
			public void onSingleFileComplete(String url) {
			}

			@Override
			public void onFail() {
			}

			@Override
			public void onBuffering(float percent) {
				int p = (int) (percent * 100);
				if(p == mLastPercent){
					return;
				}
				mLastPercent = p;
				publishProgress(mLastPercent);
			}
		}, new MultiFile(params[0], params[1], 1.0f));
		mFilePath = params[1];
		return result;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		mDownloading = false;
		if (result) {
			builder.setProgress(0, 0, true);
			builder.setContentText("下载完成");
			Notification notifycation = builder.build();
			notifycation.flags = Notification.FLAG_AUTO_CANCEL;
			notifycation.defaults = Notification.DEFAULT_SOUND;
			manager.notify(1, notifycation);
			
			if(mOnUpdateProgressListener != null) {
				mOnUpdateProgressListener.onUpdateCommplete();
			}
			
			Intent i = new Intent(Intent.ACTION_VIEW); 
			i.setDataAndType(Uri.parse("file://" + mFilePath), "application/vnd.android.package-archive"); 
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			BaseApp.getAppContext().startActivity(i);
		} else {
			
			if(mOnUpdateProgressListener != null) {
				mOnUpdateProgressListener.onUpdateFail();
			}
			
			builder.setProgress(0, 0, true);
			builder.setContentText("下载失败..");
			manager.notify(1, builder.build());
			ToastUtils.showLongToast(BaseApp.getAppContext(), "下载失败");

		}
	}
	
	public boolean isDownloading(){
		return mDownloading;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		builder.setContentText("下载进度：" + values[0] + "%");
		builder.setProgress(100, values[0], false);
		Notification no = builder.build();
//		no.flags = Notification.FLAG_NO_CLEAR;
		manager.notify(1, no);
		no = null;
		
		if(mOnUpdateProgressListener != null) {
			mOnUpdateProgressListener.onUpdateProgress(values[0]);
		}
	}
	
	public interface OnUpdateProgressListener {
		public void onUpdateStart();
		
		public void onUpdateProgress(int progress);
		
		public void onUpdateCommplete();
		
		public void onUpdateFail();
	}
	
	private OnUpdateProgressListener mOnUpdateProgressListener;
	
	public void setOnUpdateProgressListener(OnUpdateProgressListener listener) {
		mOnUpdateProgressListener = listener;
	}


}
