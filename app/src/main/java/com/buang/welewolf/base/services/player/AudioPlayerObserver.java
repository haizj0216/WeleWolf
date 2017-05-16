package com.buang.welewolf.base.services.player;

import com.hyena.framework.audio.bean.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 16/8/29.
 */
public class AudioPlayerObserver {

    private List<AudioPlayerStatusListener> mAudioPlayerStatusListeners = new ArrayList<AudioPlayerStatusListener>();

    public void addAudioPlayerStatusListener(AudioPlayerStatusListener listener) {
        if (!mAudioPlayerStatusListeners.contains(listener)) {
            mAudioPlayerStatusListeners.add(listener);
        }
    }

    public void removeAudioPlayerStatusListener(AudioPlayerStatusListener listener) {
        if (mAudioPlayerStatusListeners.contains(listener)) {
            mAudioPlayerStatusListeners.remove(listener);
        }
    }

    public void notifyAudioDownloadChanged(long status, long progress) {
        for (int i = 0; i < mAudioPlayerStatusListeners.size(); i++) {
            mAudioPlayerStatusListeners.get(i).onAudioDownloadProgress(status, progress);
        }
    }

    public void notifyAudioPlayerChanged(long status, long progress) {
        for (int i = 0; i < mAudioPlayerStatusListeners.size(); i++) {
            mAudioPlayerStatusListeners.get(i).onAudioPlayProgress(status, progress);
        }
    }

    public void notifyAudioPlayStatusChanged(Song song, int status) {
        for (int i = 0; i < mAudioPlayerStatusListeners.size(); i++) {
            mAudioPlayerStatusListeners.get(i).onStatusChange(song, status);
        }
    }

    public void notifyDownloadStatusChanged(int status) {
        for (int i = 0; i < mAudioPlayerStatusListeners.size(); i++) {
            mAudioPlayerStatusListeners.get(i).onDownloadStatusChanged(status);
        }
    }

}
