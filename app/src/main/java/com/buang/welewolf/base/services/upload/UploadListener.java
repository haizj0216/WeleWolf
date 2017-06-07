package com.buang.welewolf.base.services.upload;

/**
 * Created by weilei on 17/6/7.
 */

public interface UploadListener {
    int ERROR_CODE_NO_NETWORK = 10000;
    int ERROR_CODE_NETWORK_ERROR = 10001;
    int ERROR_CODE_UNKNOWN = 10002;
    int ERROR_CODE_NO_FILE = 10003;

    void onUploadStarted(UploadTask var1);

    void onUploadProgress(UploadTask var1, double var2);

    void onUploadComplete(UploadTask var1, String var2);

    void onUploadError(UploadTask var1, int var2, String var3, String var4);

    void onRetry(UploadTask var1, int var2, String var3, String var4);
}
