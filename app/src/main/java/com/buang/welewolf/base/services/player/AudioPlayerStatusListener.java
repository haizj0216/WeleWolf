package com.buang.welewolf.base.services.player;

import com.hyena.framework.audio.bean.Song;

/**
 * Created by weilei on 16/8/29.
 */
public interface AudioPlayerStatusListener {

    public void onAudioDownloadProgress(long progress, long duration);

    public void onAudioPlayProgress(long progress, long duration);

    public void onStatusChange(Song song, int status);

    public void onDownloadStatusChanged(int status);
}
