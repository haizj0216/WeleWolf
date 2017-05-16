package com.buang.welewolf.base.services.player;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.buang.welewolf.base.utils.DirContext;
import com.hyena.framework.audio.StatusCode;
import com.hyena.framework.audio.bean.Song;
import com.hyena.framework.security.MD5Util;
import com.hyena.framework.servcie.audio.PlayerBusService;
import com.hyena.framework.servcie.audio.listener.PlayStatusChangeListener;
import com.hyena.framework.servcie.audio.listener.ProgressChangeListener;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.HttpHelper;
import com.hyena.framework.utils.UiThreadHandler;

import java.io.File;

/**
 * Created by weilei on 16/8/29.
 */
public class AudioPlayerServiceImp implements AudioPlayerService {
    private AudioPlayerObserver audioPlayerObserver = new AudioPlayerObserver();
    private String mAudioUrl;
    private PlayerBusService mPlayerService;
    private int mStatus = -1;
    private DownloadTask mDownloadTask;


    public AudioPlayerServiceImp() {

    }

    @Override
    public void playSong(String url) {
        if (mPlayerService == null) {
            mPlayerService = (PlayerBusService) BaseApp.getAppContext().getSystemService(
                    PlayerBusService.BUS_SERVICE_NAME);
            mPlayerService.getPlayerBusServiceObserver()
                    .addPlayStatusChangeListener(mPlayStatusChangeListener);
            mPlayerService.getPlayerBusServiceObserver().addProgressChangeListener(mProgressChangeListener);
        }

//        if (url.equals(mAudioUrl)) {
//            if (isPlaying()) {
//                pause();
//            } else {
//                resume();
//            }
//            return;
//        }
        String localFile = MD5Util.encode(url);
        mAudioUrl = url;
        File file = new File(getVoiceFilePath(localFile));
        if (file.exists() && file.length() == 0) {
            file.delete();
        }
        if (new File(getVoiceFilePath(localFile)).exists()) {
            audioPlayerObserver.notifyDownloadStatusChanged(0);
            Song song = new Song(false, url, getVoiceFilePath(localFile));
            playSongImp(song);
            audioPlayerObserver.notifyDownloadStatusChanged(1);
        } else {
            downloadAudio(url);
        }
    }

    private void downloadAudio(final String url) {
        if (mDownloadTask != null) {
            mDownloadTask.cancel(false);
        }
        mDownloadTask = new DownloadTask(url);
        mDownloadTask.execute();
    }

    private class DownloadTask extends AsyncTask<Void, Void, Boolean> {

        private String mDownloadUrl;

        public DownloadTask(String url) {
            mDownloadUrl = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    audioPlayerObserver.notifyDownloadStatusChanged(0);
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return HttpHelper.storeFile(mDownloadUrl, getVoiceFilePath(MD5Util.encode(mDownloadUrl)), new HttpHelper.ProgressListener() {
                @Override
                public void onStart(long total) {
                }

                @Override
                public void onAdvance(final long len, final long total) {
                }

                @Override
                public void onComplete(final boolean isSuccess) {
                }
            });
        }

        @Override
        protected void onPostExecute(final Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (isSuccess && mAudioUrl.equals(mDownloadUrl)) {
                        Song song = new Song(false, mDownloadUrl, getVoiceFilePath(MD5Util.encode(mDownloadUrl)));
                        playSongImp(song);
                        audioPlayerObserver.notifyDownloadStatusChanged(1);
                    } else {
                        String localFile = MD5Util.encode(mAudioUrl);
                        File file = new File(getVoiceFilePath(localFile));
                        if (file.exists()) {
                            file.delete();
                        }
                        audioPlayerObserver.notifyDownloadStatusChanged(-1);
                    }
                }
            });
        }
    }

    public String getVoiceFilePath(String fileName) {
        return DirContext.getAudioCacheDir() + "/" + fileName;
    }

    private void playSongImp(Song song) {
        try {
            mPlayerService.play(song);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                mPlayerService.getPosition();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isPlaying()) {
                sendEmptyMessageDelayed(1, 1000);
            }
        }
    };

    PlayStatusChangeListener mPlayStatusChangeListener = new PlayStatusChangeListener() {

        @Override
        public void onStatusChange(Song song, int status) {
            audioPlayerObserver.notifyAudioPlayStatusChanged(song, status);
            if (status == StatusCode.STATUS_PLAYING || status == StatusCode.STATUS_PREPARED || status == StatusCode.STATUS_BUFFING) {
                mHander.sendEmptyMessage(1);
            } else {
            }
            if (status == StatusCode.STATUS_COMPLETED) {
                mAudioUrl = "";
                mHander.sendEmptyMessage(1);
            }
            mStatus = status;
        }

    };

    ProgressChangeListener mProgressChangeListener = new ProgressChangeListener() {

        @Override
        public void onPlayProgressChange(long l, long l1) {
            audioPlayerObserver.notifyAudioPlayerChanged(l, l1);
        }

        @Override
        public void onDownloadProgressChange(int i, long l) {
        }
    };

    @Override
    public AudioPlayerObserver getObserver() {
        return audioPlayerObserver;
    }

    @Override
    public void pause() {
        try {
            if (mPlayerService != null && isPlaying()) {
                mPlayerService.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPlaying() {
        return mStatus == StatusCode.STATUS_PLAYING || mStatus == StatusCode.STATUS_PREPARED
                || mStatus == StatusCode.STATUS_BUFFING;
    }

    @Override
    public void resume() {
        try {
            if (mPlayerService != null && mStatus == StatusCode.STATUS_PAUSE) {
                mPlayerService.resume();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void seekTo(long progress) {
        try {
            if (mPlayerService != null) {
                mPlayerService.seekTo(progress);
            }
            mHander.removeMessages(1);
            mHander.sendEmptyMessageDelayed(1, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPlayUrl() {
        return mAudioUrl;
    }

    @Override
    public void cancelDownload() {
        try {
            mAudioUrl = "";
            if (mDownloadTask != null) {
                mDownloadTask.cancel(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void releaseAll() {
        if (mPlayerService != null) {
            mPlayerService.getPlayerBusServiceObserver().removemPlayStatusChangeListener(mPlayStatusChangeListener);
            mPlayerService.getPlayerBusServiceObserver().removeProgressChangeListener(mProgressChangeListener);
        }

    }
}
