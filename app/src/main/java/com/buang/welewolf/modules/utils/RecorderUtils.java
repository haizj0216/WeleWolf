package com.buang.welewolf.modules.utils;

import android.app.Activity;
import android.text.TextUtils;

import com.knowbox.base.service.upload.UploadListener;
import com.knowbox.base.service.upload.UploadService;
import com.knowbox.base.service.upload.UploadTask;
import com.buang.welewolf.base.bean.OnlineRecorderItemInfo;

import java.io.File;
import java.util.List;

/**
 * Created by weilei on 15/9/30.
 */
public class RecorderUtils {

    private Activity mActivity;
    private UploadService uploadService;
    private int index = 0;

    public RecorderUtils(Activity activity) {
        index = 0;
        mActivity = activity;
        uploadService = (UploadService) activity.getSystemService(UploadService.SERVICE_NAME_QINIU);
    }

    public void uploadRecorder(final List<OnlineRecorderItemInfo> files, UploadListener listener) {
        if (files == null) {
            if (listener != null)
                listener.onUploadError(null, UploadListener.ERROR_CODE_UNKNOWN, "未知异常", "");
            return;
        }
        if (files.isEmpty()) {
            if (listener != null) {
                listener.onUploadComplete(null, null);
                return;
            }
        }
        uploadRecorderImp(files, listener);
    }

    private void uploadRecorderImp(final List<OnlineRecorderItemInfo> files, final UploadListener listener) {
        if (index >= files.size()) {
            if (listener != null)
                listener.onUploadError(null, UploadListener.ERROR_CODE_UNKNOWN, "未知异常", "");
            return;
        }
        final OnlineRecorderItemInfo item = files.get(index);
        if (item == null) {
            if (listener != null)
                listener.onUploadError(null, UploadListener.ERROR_CODE_UNKNOWN, "未知异常", "");
            return;
        }
        if (!TextUtils.isEmpty(item.mOnlineUrl)) {
            index++;
            if (index >= files.size()) {
                if (listener != null)
                    listener.onUploadComplete(new UploadTask(UploadTask.TYPE_RECORDER, item.mLocalPath), item.mOnlineUrl);
                return;
            } else {
                uploadRecorderImp(files, listener);
                return;
            }
        }
        if (TextUtils.isEmpty(item.mLocalPath) || !new File(item.mLocalPath).exists()) {
            if (listener != null)
                listener.onUploadError(null, UploadListener.ERROR_CODE_NO_FILE, "文件异常", "");
            return;
        }
        uploadRecorderItem(item, new UploadListener() {
            @Override
            public void onUploadStarted(UploadTask uploadTask) {

            }

            @Override
            public void onUploadProgress(UploadTask uploadTask, double v) {

            }

            @Override
            public void onUploadComplete(UploadTask uploadTask, String s) {
                item.mOnlineUrl = s;
                index++;
                if (index >= files.size()) {
                    if (listener != null)
                        listener.onUploadComplete(uploadTask, s);
                    return;
                } else {
                    uploadRecorderImp(files, listener);
                    return;
                }
            }

            @Override
            public void onUploadError(UploadTask uploadTask, int i, String s, String token) {
                if (listener != null)
                    listener.onUploadError(uploadTask, i, s, token);
            }

			@Override
			public void onRetry(UploadTask uploadTask, int errorCode,
					String error, String extend) {
				// TODO Auto-generated method stub
				
			}
        });
    }

    /**
     * 上传语音
     * @param item
     * @param listener
     */
    public void uploadRecorderItem(OnlineRecorderItemInfo item, UploadListener listener) {
        if (item == null) {
            if (listener != null)
                listener.onUploadError(null, UploadListener.ERROR_CODE_UNKNOWN, "未知异常", "");
            return;
        }
        if (TextUtils.isEmpty(item.mLocalPath) || !new File(item.mLocalPath).exists()) {
            if (listener != null)
                listener.onUploadError(null, UploadListener.ERROR_CODE_NO_FILE, "文件异常", "");
            return;
        }
        uploadService.upload(new UploadTask(UploadTask.TYPE_RECORDER, item.mLocalPath), listener);
    }

}
