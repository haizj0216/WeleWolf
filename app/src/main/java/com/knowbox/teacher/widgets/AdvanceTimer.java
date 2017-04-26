package com.knowbox.teacher.widgets;

import com.hyena.framework.utils.UiThreadHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @name 计时器TextView（01：12）
 * @author Fanjb
 * @date 2015-3-10
 */
public class AdvanceTimer {

	private Timer mTimer;
	private int mCurSeconds;
	private TimeChangeListener mListener;

	/**
	 * @name 监听计时改变
	 * @author Fanjb
	 * @date 2015-3-10
	 */
	public static interface TimeChangeListener {
		public void onPreChanged(int mCurSeconds);

		public void onTimeChanged(int mCurSeconds);

		public void onFinish(int mCurSeconds);
	}

	public AdvanceTimer() {
		mTimer = new Timer();
	}

	/**
	 * 设定初始计时时间
	 * 
	 * @param curSeconds
	 */
	public void setCurSeconds(int curSeconds) {
		this.mCurSeconds = curSeconds;
	}

	public int getCurSeconds(){
		return this.mCurSeconds;
	}
	
	/**
	 * 绑定事件监听器
	 * 
	 * @param listener
	 */
	public void setTimeChangeListener(TimeChangeListener listener) {
		mListener = listener;
	}

	/**
	 * 开始计时
	 */
	public void start() {
		if (mListener != null) {
			mListener.onPreChanged(mCurSeconds);
		}

		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				UiThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						if (mCurSeconds > 0) {
							if (mListener != null) {
								mListener.onTimeChanged(mCurSeconds);
							}
							mCurSeconds--;
						} else {
							stop();
						}
					}
				});
			}
		}, 0, 1000);
	}

	/**
	 * 停止
	 */
	public void stop() {
		mTimer.cancel();
		if (mListener != null) {
			mListener.onFinish(mCurSeconds);
		}
	}

	/**
	 * 销毁
	 */
	public void destory() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}
}
