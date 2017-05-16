package com.buang.welewolf.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * @name 文本提示的进度条
 * @author Fanjb
 * @date 2015年5月28日
 */
public class TextProgressBar extends ProgressBar {
	private static final int TEXT_SIZE = 13;
	final int FRESH_INC = 2;

	private String text;
	private Paint mPaint;
	private boolean mFresh = false;
	private int mProgressValue = -1;

	public TextProgressBar(Context context) {
		super(context);
		initText();
	}

	public TextProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initText();
	}

	public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initText();
	}

	private void initText() {
		this.mPaint = new Paint();
		this.mPaint.setAntiAlias(true);
		this.mPaint.setColor(Color.WHITE);
		this.mPaint.setTextSize(TEXT_SIZE
				* getResources().getDisplayMetrics().density);
	}

	public void setProgressValue(int progress) {
		setProgress(0);
		int i = (int) ((progress * 1.0f / this.getMax()) * 100);
		this.text = String.valueOf(i) + "%";
		// if (mProgressValue < 0)
		// mProgressValue = progress;
		if (mProgressValue != progress)
			mProgressValue = progress;
		postInvalidate();
	}

	public void setFreshAble() {
		this.mFresh = true;
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Rect rect = new Rect();
		this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
		int x = 0;
		if (mProgressValue <= 0 ) {
			x = rect.centerX();
		}else {
			x = getWidth() * getProgress() / getMax() - rect.centerX() * 3;
		}
		// int x = (getWidth() / 2) - rect.centerX();
//		int x = rect.centerX();
		x = Math.max(x, 5);
		int y = (getHeight() / 2) - rect.centerY();
		canvas.drawText(this.text, x, y, this.mPaint);
		if (mFresh) {
			int progress = getProgress();
			if (progress < mProgressValue) {
				int newProgress = progress + FRESH_INC;
				if (newProgress >= mProgressValue)
					newProgress = mProgressValue;
				setProgress(newProgress);
			}
		}
	}
}