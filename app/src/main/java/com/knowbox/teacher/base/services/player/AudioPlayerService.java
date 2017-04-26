package com.knowbox.teacher.base.services.player;

import com.hyena.framework.servcie.BaseService;

/**
 * Created by weilei on 16/8/29.
 */
public interface AudioPlayerService extends BaseService {

    public static final String SERVICE_NAME = "com.knowbox.wb_audioPlayerservice";

    public void playSong(String song);

    public AudioPlayerObserver getObserver();

    public void pause();

    public boolean isPlaying();

    public void resume();

    public void seekTo(long progress);

    public String getPlayUrl();

    public void cancelDownload();

}
