package com.buang.welewolf.base.services.upload;

import com.hyena.framework.servcie.BaseService;

/**
 * Created by weilei on 17/6/7.
 */

public interface UploadService extends BaseService {
    String SERVICE_NAME_QINIU = "com.buang.welewolf.upload_qiniu";

    void upload(UploadTask var1, UploadListener var2);

    void cancelJob(String var1);

    void cancelAllJobs();
}
