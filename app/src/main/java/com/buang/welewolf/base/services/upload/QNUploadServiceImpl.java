package com.buang.welewolf.base.services.upload;

import android.text.TextUtils;

import com.buang.welewolf.base.bean.OnlineUploadInfo;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.error.ErrorManager;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.security.MD5Util;
import com.hyena.framework.utils.AppPreferences;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

/**
 * Created by weilei on 17/6/7.
 */

public abstract class QNUploadServiceImpl implements UploadService {
    private static final String TAG = "QNUploadServiceImpl";
    private static final String UPLOAD_TOKEN = "prefs_upload_token";
    private static final String TOKEN_EXPIRED = "prefs_upload_token_expired";
    private static final String TOKEN_DOMAIN = "prefs_upload_token_domain";
    private LinkedList<UploadJob> mJobList = new LinkedList();
    private QNUploadServiceImpl.UploadJob currentJob = null;
    private volatile boolean isWorking = false;

    public QNUploadServiceImpl() {
    }

    public abstract String getPicTokenUrl();

    public abstract String getRecordTokenUrl();

    public void upload(UploadTask uploadTask, UploadListener listener) {
        if(uploadTask != null) {
            this.mJobList.add(new QNUploadServiceImpl.UploadJob(uploadTask, listener));
            if(!this.isWorking) {
                this.scheduleNextJob();
            }
        }
    }

    public void cancelJob(String taskId) {
        if(this.mJobList != null) {
            if(this.currentJob != null && taskId.equals(this.currentJob.getTaskId())) {
                this.currentJob.cancel();
            } else {
                QNUploadServiceImpl.UploadJob cancelJob = null;

                for(int i = 0; i < this.mJobList.size() && i < this.mJobList.size(); ++i) {
                    QNUploadServiceImpl.UploadJob job = (QNUploadServiceImpl.UploadJob)this.mJobList.get(i);
                    if(taskId.equals(job.getTaskId())) {
                        cancelJob = job;
                        break;
                    }
                }

                if(cancelJob != null) {
                    this.mJobList.remove(cancelJob);
                }
            }
        }

    }

    public void cancelAllJobs() {
        if(this.currentJob != null) {
            this.currentJob.cancel();
        }

        this.mJobList.clear();
    }

    private void scheduleNextJob() {
        if(this.mJobList.isEmpty()) {
            this.currentJob = null;
            this.isWorking = false;
        } else {
            this.isWorking = true;
            this.currentJob = (QNUploadServiceImpl.UploadJob)this.mJobList.remove(0);
            this.currentJob.setJobListener(new QNUploadServiceImpl.JobListener() {
                public void onJobStart() {
                    LogUtil.v("QNUploadServiceImpl", "开始执行上传任务:" + QNUploadServiceImpl.this.currentJob.getUploadTask());
                }

                public void onJobFinished() {
                    LogUtil.v("QNUploadServiceImpl", "完成上传任务:" + QNUploadServiceImpl.this.currentJob.getUploadTask());
                    this.next();
                }

                public void onJobError(String error) {
                    LogUtil.v("QNUploadServiceImpl", "完成上传失败:" + QNUploadServiceImpl.this.currentJob.getUploadTask());
                    this.next();
                }

                private void next() {
                    QNUploadServiceImpl.this.currentJob.setJobListener((QNUploadServiceImpl.JobListener)null);
                    QNUploadServiceImpl.this.scheduleNextJob();
                }
            });
            (new Thread(this.currentJob)).start();
        }
    }

    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        return format.format(date);
    }

    public void releaseAll() {
    }

    private interface JobListener {
        void onJobStart();

        void onJobFinished();

        void onJobError(String var1);
    }

    private class UploadJob implements Runnable {
        private UploadListener listener;
        private QNUploadServiceImpl.JobListener jobListener;
        private UploadTask uploadTask;
        private boolean mIsCancel = false;

        public UploadJob(UploadTask uploadTask, UploadListener listener) {
            this.uploadTask = uploadTask;
            this.listener = listener;
        }

        public void setJobListener(QNUploadServiceImpl.JobListener listener) {
            this.jobListener = listener;
        }

        public void cancel() {
            this.mIsCancel = true;
        }

        public UploadTask getUploadTask() {
            return this.uploadTask;
        }

        public String getTaskId() {
            return this.uploadTask == null?null:this.uploadTask.getTaskId();
        }

        public void run() {
            this.runImpl(false);
        }

        private void postRun(final boolean isRetry) {
            (new Thread() {
                public void run() {
                    UploadJob.this.runImpl(isRetry);
                }
            }).start();
        }

        private void runImpl(final boolean isRetry) {
            if(this.jobListener != null && !isRetry) {
                this.jobListener.onJobStart();
            }

            if(this.listener != null && !isRetry) {
                this.listener.onUploadStarted(this.uploadTask);
            }

            if(this.uploadTask != null && !this.uploadTask.isEmpty()) {
                if(!NetworkProvider.getNetworkProvider().getNetworkSensor().isNetworkAvailable()) {
                    if(this.listener != null) {
                        this.listener.onUploadError(this.uploadTask, 10000, "没有网络连接", "");
                    }

                    if(this.jobListener != null) {
                        this.jobListener.onJobError("没有网络连接");
                    }

                } else {
                    String PToken = AppPreferences.getStringValue("prefs_upload_token");
                    Long expiration = AppPreferences.getLongValue("prefs_upload_token_expired");
                    String domain = AppPreferences.getStringValue("prefs_upload_token_domain");
                    if(isRetry || expiration == null || TextUtils.isEmpty(PToken) || expiration.longValue() <= System.currentTimeMillis() / 1000L || TextUtils.isEmpty(domain)) {
                        String finalToken;
                        if(this.uploadTask.getType() == 1) {
                            finalToken = QNUploadServiceImpl.this.getPicTokenUrl();
                        } else if(this.uploadTask.getType() == 2) {
                            finalToken = QNUploadServiceImpl.this.getRecordTokenUrl();
                        } else {
                            finalToken = QNUploadServiceImpl.this.getPicTokenUrl();
                        }

                        OnlineUploadInfo config = (OnlineUploadInfo)(new DataAcquirer()).get(finalToken, new OnlineUploadInfo());
                        if(!config.isAvailable()) {
                            String manager1 = ErrorManager.getErrorManager().getErrorHint(String.valueOf(config.getErrorCode()), (String)null);
                            if(this.listener != null) {
                                this.listener.onUploadError(this.uploadTask, 10001, manager1, config.getRawResult());
                            }

                            if(this.jobListener != null) {
                                this.jobListener.onJobError(manager1);
                            }

                            return;
                        }

                        AppPreferences.setStringValue("prefs_upload_token", config.mToken);
                        AppPreferences.setLongValue("prefs_upload_token_expired", Long.valueOf(config.mExpiredTime));
                        AppPreferences.setStringValue("prefs_upload_token_domain", config.mDomain);
                        PToken = config.mToken;
                    }
                    final String token = PToken;
                    Configuration config1 = (new Configuration.Builder()).build();
                    UploadManager manager = new UploadManager(config1);
                    UpCompletionHandler handler = new UpCompletionHandler() {
                        public void complete(String key, ResponseInfo response, JSONObject json) {
                            if(json == null) {
                                if(response != null && response.statusCode == 401 && !isRetry) {
                                    if(UploadJob.this.listener != null) {
                                        UploadJob.this.listener.onRetry(UploadJob.this.uploadTask, -5, "server error: " + response.error, token);
                                    }

                                    UploadJob.this.postRun(true);
                                } else {
                                    if(UploadJob.this.listener != null) {
                                        UploadJob.this.listener.onUploadError(UploadJob.this.uploadTask, response.statusCode, "server error: " + response.error, token);
                                    }

                                    if(UploadJob.this.jobListener != null) {
                                        UploadJob.this.jobListener.onJobError(response.error);
                                    }

                                }
                            } else {
                                if(response.isOK()) {
                                    if(UploadJob.this.listener != null) {
                                        String errorCode = AppPreferences.getStringValue("prefs_upload_token_domain");
                                        UploadJob.this.listener.onUploadComplete(UploadJob.this.uploadTask, errorCode + "/" + json.optString("key"));
                                    }

                                    if(UploadJob.this.jobListener != null) {
                                        UploadJob.this.jobListener.onJobFinished();
                                    }
                                } else {
                                    int errorCode1 = json.optInt("code");
                                    String errorMsg = json.optString("error");
                                    String extend = "";
                                    if(errorCode1 == -5 && !isRetry) {
                                        if(UploadJob.this.listener != null) {
                                            UploadJob.this.listener.onRetry(UploadJob.this.uploadTask, errorCode1, errorMsg, token);
                                        }

                                        UploadJob.this.postRun(true);
                                        return;
                                    }

                                    if(errorCode1 == -5) {
                                        extend = token;
                                    }

                                    if(UploadJob.this.listener != null) {
                                        UploadJob.this.listener.onUploadError(UploadJob.this.uploadTask, errorCode1, errorMsg, extend);
                                    }

                                    if(UploadJob.this.jobListener != null) {
                                        UploadJob.this.jobListener.onJobError(errorMsg);
                                    }
                                }

                            }
                        }
                    };
                    UpCancellationSignal signal = new UpCancellationSignal() {
                        public boolean isCancelled() {
                            return UploadJob.this.mIsCancel;
                        }
                    };
                    UpProgressHandler progress = new UpProgressHandler() {
                        public void progress(String s, double percent) {
                            if(UploadJob.this.listener != null) {
                                UploadJob.this.listener.onUploadProgress(UploadJob.this.uploadTask, percent);
                            }

                        }
                    };
                    UploadOptions options = new UploadOptions((Map)null, (String)null, false, progress, signal);
                    String key;
                    if(this.uploadTask.buf == null) {
                        key = QNUploadServiceImpl.this.getDate() + "/" + (new File(this.uploadTask.filePath)).getName();
                        manager.put(this.uploadTask.filePath, key, token, handler, options);
                    } else {
                        key = QNUploadServiceImpl.this.getDate() + "/" + MD5Util.encode(String.valueOf(System.currentTimeMillis()));
                        manager.put(this.uploadTask.buf, key, token, handler, options);
                    }

                }
            } else {
                if(this.listener != null) {
                    this.listener.onUploadError(this.uploadTask, 10003, "上传内容不存在", "");
                }

                if(this.jobListener != null) {
                    this.jobListener.onJobError("上传内容不存在");
                }

            }
        }
    }
}
