package com.knowbox.teacher.modules.message;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.easemob.chat.EMChatConfig;
import com.easemob.chat.FileMessageBody;
import com.easemob.cloud.CloudOperationCallback;
import com.easemob.cloud.HttpFileManager;
import com.easemob.util.FileUtils;
import com.hyena.framework.app.fragment.BaseSubFragment;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.teacher.R;
import com.knowbox.teacher.modules.utils.ToastUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ShowNormalFileFragment extends BaseSubFragment {
	private ProgressBar progressBar;
	private File file;

	@Override
	public View onCreateViewImpl(ViewGroup container,
			Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.layout_message_show_file, null);
		return view;
	}
	
	@Override
	public void onViewCreatedImpl(View view, Bundle savedInstanceState) {
		super.onViewCreatedImpl(view, savedInstanceState);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		final FileMessageBody messageBody = getArguments().getParcelable("msgbody");
		file = new File(messageBody.getLocalUrl());
		//set head map
		final Map<String, String> maps = new HashMap<String, String>();
		if (!TextUtils.isEmpty(messageBody.getSecret())) {
			maps.put("share-secret", messageBody.getSecret());
		}
		//下载文件
		new Thread(new Runnable() {
			public void run() {
				HttpFileManager fileManager = new HttpFileManager(getActivity(), EMChatConfig.getInstance().getStorageUrl());
				fileManager.downloadFile(messageBody.getRemoteUrl(), messageBody.getLocalUrl(), maps,
						new CloudOperationCallback() {
							
							@Override
							public void onSuccess(String result) {
								UiThreadHandler.post(new Runnable() {
									public void run() {
										FileUtils.openFile(file, getActivity());
										finish();
										MsgCenter.sendLocalBroadcast(new Intent(EMChatFragment.MSG_REFRESH_CHAT_LIST));
									}
								});
							}
							
							@Override
							public void onProgress(final int progress) {
								UiThreadHandler.post(new Runnable() {
									public void run() {
										progressBar.setProgress(progress);
									}
								});
							}
							
							@Override
							public void onError(final String msg) {
								UiThreadHandler.post(new Runnable() {
									public void run() {
										if(file != null && file.exists()&&file.isFile())
											file.delete();
										ToastUtils.showShortToast(getActivity(), "下载文件失败: ");
										finish();
									}
								});
							}
						});

			}
		}).start();
	}
	
}
