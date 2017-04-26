package com.knowbox.teacher.base.audio.recorder;

import android.annotation.SuppressLint;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaRecorder.OutputFormat;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.knowbox.teacher.base.utils.DirContext;

import java.io.File;
import java.util.Date;

public class SoundRecorder {
	static final String PREFIX = "voice";
	static final String EXTENSION = ".amr";
	private boolean isRecording = false;
	private long startTime;
	private String voiceFilePath = null;
	private String voiceFileName = null;
	private File file;
	private Handler handler;
	private MediaRecorder recorder;

	public SoundRecorder(Handler paramHandler) {
		this.handler = paramHandler;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("InlinedApi")
	public String startRecording(int maxDuration, String path,
			OnInfoListener infoListener, OnErrorListener errorListener) {
		this.file = null;
		try {
			if (this.recorder != null) {
				this.recorder.release();
				this.recorder = null;
			}
			this.recorder = new MediaRecorder();
			this.recorder.setAudioSource(AudioSource.MIC);
			if (VERSION.SDK_INT >= 10)
				this.recorder.setOutputFormat(OutputFormat.AMR_NB);
			else
				this.recorder.setOutputFormat(OutputFormat.RAW_AMR);
			this.recorder.setAudioEncoder(AudioEncoder.AMR_NB);
			this.recorder.setAudioChannels(1);
			this.recorder.setAudioSamplingRate(8000);
			this.recorder.setAudioEncodingBitRate(64);
			if (maxDuration > 0)
				this.recorder.setMaxDuration(maxDuration);
			this.voiceFileName = getVoiceFileName(path);
			this.voiceFilePath = getVoiceFilePath();
			this.file = new File(this.voiceFilePath);
			this.recorder.setOutputFile(this.file.getAbsolutePath());
			this.recorder.prepare();
			this.isRecording = true;
			this.recorder.start();
			if (errorListener != null)
				recorder.setOnErrorListener(errorListener);
			if (infoListener != null)
				this.recorder.setOnInfoListener(infoListener);
		} catch (Throwable localIOException) {
			localIOException.printStackTrace();
		}
		new Thread(new Runnable() {
			public void run() {
				try {
					while (SoundRecorder.this.isRecording) {
						Message localMessage = new Message();
						localMessage.what = (SoundRecorder.this.recorder
								.getMaxAmplitude() * 13 / 32767);
						SoundRecorder.this.handler.sendMessage(localMessage);
						SystemClock.sleep(100L);
					}
				} catch (Exception localException) {
					localException.printStackTrace();
				}
			}
		}).start();
		this.startTime = new Date().getTime();
		return this.file == null ? null : this.file.getAbsolutePath();
	}

	public void discardRecording() {
		if (this.recorder != null) {
			try {
				this.recorder.stop();
				this.recorder.release();
				this.recorder = null;
				if ((this.file != null) && (this.file.exists())
						&& (!this.file.isDirectory()))
					this.file.delete();
			} catch (IllegalStateException localIllegalStateException) {
			} catch (RuntimeException localRuntimeException) {
			}
			this.isRecording = false;
		}
	}

	public int stopRecoding() {
		if (this.recorder != null) {
			this.isRecording = false;
			this.recorder.stop();
			this.recorder.release();
			this.recorder = null;
			if ((this.file == null) || (!this.file.exists())
					|| (!this.file.isFile()))
				return -1;
			if (this.file.length() == 0L) {
				this.file.delete();
				return -1;
			}
			int i = (int) (new Date().getTime() - this.startTime) / 1000;
			return i;
		}
		return 0;
	}

	protected void finalize() throws Throwable {
		super.finalize();
		if (this.recorder != null)
			this.recorder.release();
	}

	public String getVoiceFileName(String paramString) {
		return paramString + "_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000) +".amr";
	}

	public boolean isRecording() {
		return this.isRecording;
	}

	public String getVoiceFilePath() {
		return DirContext.getAudioCacheDir() + "/" + this.voiceFileName;
	}

}
