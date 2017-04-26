package com.knowbox.teacher.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.knowbox.teacher.R;

/**
 * Created by LiuYu on 2016/2/24.
 */
public class CircleWaveProgressBar extends View {

    private boolean isPregressStr = false;

    private final int DEFAULT_ABOVE_WAVE_COLOR = 0x00ff00;

    private final int DEFAULT_PROGRESS_TEXT_COLOR = 0X000000;

    private static final int PROGRESS_MAX = 100;
    private static final int PROGRESS_DEFAULT = 0;

    private static final int DEFAULT_WAVE_HEIGHT = 20;
    private static final int DEFAULT_PROGRESS_TEXT_SIZE = 14;

    private static final int WIDTH_RATE_LARGE = 1;
    private static final int WIDTH_RATE_MIDDLE = 2;
    private static final int WIDTH_RATE_SMALL = 3;

    private static final int HZ_FAST = 15;
    private static final int HZ_NORMAL = 10;
    private static final int HZ_SLOW = 5;
    private static final int HZ_SOSLOW = 2;

    private final float X_SPACE = 10;//密度，越小越平滑
    private final double PI2 = 2 * Math.PI;

    private Path mWavePath = new Path();
    private Paint mWavePaint = new Paint();
    private Paint mPaint = new Paint();
    private float mAboveOffset = 0.0f;
    private RefreshProgressRunnable mRefreshProgressRunnable;
    private int     left, right, bottom;
    private double  omega;
    private int     mWaveWidth;
    private int     mWaveTopHeight;
    private float   mMaxRight;

    /**
     * 水流颜色
     */
    private int     mWaveColor;
    /**
     * 进度值
     */
    private int     mProgress;
    /**
     * 波动幅度
     */
    private int     mWaveHeight;
    /**
     * 波宽，越大越平滑
     */
    private int     mWaveWidthRate;
    /**
     * 波动频率
     */
    private float   mWaveHz = HZ_SOSLOW;
    /**
     * 进度值颜色
     */
    private int     mProgressTextColor;
    /**
     * 进度值字号
     */
    private float   mProgressTextSize;
    /**
     * 进度值是否可见
     */
    private boolean mProgressTextVisible;
    /**
     * 描边颜色
     */
    private int     strokeColor;
    /**
     * 描边宽度
     */
    private float   strokeWidth;

    /**
     * 特殊型号
     */
    public static final String MODEL_GALAXY_S3 = "SAMSUNG-SGH-I747";
    public static final String MODEL_GALAXY_S4 = "SAMSUNG-SGH-I337";


    public CircleWaveProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleWaveProgressBar, R.attr.waveViewStyle, 0);
        mWaveColor = attributes.getColor(R.styleable.CircleWaveProgressBar_above_wave_color, DEFAULT_ABOVE_WAVE_COLOR);
        mProgress = attributes.getColor(R.styleable.CircleWaveProgressBar_progress, PROGRESS_DEFAULT);
        mWaveHeight = attributes.getDimensionPixelSize(R.styleable.CircleWaveProgressBar_wave_height, DEFAULT_WAVE_HEIGHT);
        mWaveWidthRate = attributes.getInt(R.styleable.CircleWaveProgressBar_wave_width_rate, WIDTH_RATE_MIDDLE);
        mWaveHz = attributes.getInt(R.styleable.CircleWaveProgressBar_wave_hz, HZ_NORMAL);
        mProgressTextColor = attributes.getColor(R.styleable.CircleWaveProgressBar_progressTextColor, 40);
        mProgressTextSize = attributes.getDimension(R.styleable.CircleWaveProgressBar_progressTextSize, HZ_NORMAL);
        mProgressTextVisible = attributes.getBoolean(R.styleable.CircleWaveProgressBar_progressTextVisible, false);
        strokeWidth = attributes.getDimension(R.styleable.CircleWaveProgressBar_wavestrokeWidth, 0);
        strokeColor = attributes.getColor(R.styleable.CircleWaveProgressBar_wavestrokeColor, 0xfff);
        initializePainters();
    }

    public CircleWaveProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initializePainters() {
        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                || (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT  && (MODEL_GALAXY_S3.equals(Build.MODEL) || MODEL_GALAXY_S4.equals(Build.MODEL)))) {
            // clipPath only available on hardware for 18+, and not S3/S4 with 4.4.2
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
        mWavePaint.setColor(mWaveColor);
        mWavePaint.setStyle(Paint.Style.FILL);
        mWavePaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        int width = getWidth();
        if (width > getHeight()) {
            width = getHeight();
        }
        Path path = new Path();
        path.reset();
        path.addCircle(width / 2, width / 2, width / 2, Path.Direction.CCW);
        canvas.clipPath(path);
        canvas.drawColor(Color.WHITE);
        if(mProgressTextVisible){
            canvas.drawPath(mWavePath, mWavePaint);
        }

        mPaint.setAntiAlias(true);
        if (mProgressTextVisible) {
            mPaint.setColor(mProgressTextColor);
            mPaint.setTextSize(mProgressTextSize);
            mPaint.setFakeBoldText(false);
            mPaint.setStyle(Paint.Style.FILL);
            String mProgressText = (String.valueOf(mProgress)+"%");
            canvas.drawText(mProgressText,
                    (getWidth() - mPaint.measureText(mProgressText)) / 2,
                    (getHeight() - (mPaint.getFontMetrics().descent + mPaint.getFontMetrics().ascent)) / 2, mPaint);
        }else {
            mPaint.setColor(strokeColor);
            mPaint.setTextSize(mProgressTextSize);
            mPaint.setFakeBoldText(false);
            mPaint.setStyle(Paint.Style.FILL);
            String mProgressText = ("待批改");
            canvas.drawText(mProgressText,
                    (getWidth() - mPaint.measureText(mProgressText)) / 2,
                    (getHeight() - (mPaint.getFontMetrics().descent + mPaint.getFontMetrics().ascent)) / 2, mPaint);
        }

        if (strokeWidth != 0) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(strokeWidth);
            mPaint.setColor(strokeColor);
            mPaint.setAntiAlias(true);
            canvas.drawCircle(width / 2, width / 2, width / 2, mPaint);
        }
        canvas.restore();
    }

    private void calculatePath() {
        mWavePath.reset();
        getWaveOffset();
        float y;
        mWavePath.moveTo(left, bottom);
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (mWaveHeight * Math.sin(omega * (x + mAboveOffset)) + mWaveTopHeight);
            mWavePath.lineTo(x, y);
        }
        mWavePath.lineTo(right, bottom);
    }

    public void setProgress(int progress) {
        this.mProgress = progress > 100 ? 100 : progress;
        computeWaveToTop();
    }

    public void startWave(boolean mProgressTextVisible) {
        this.mProgressTextVisible = mProgressTextVisible;
        startWave();
    }

    public void startWave(int progress) {
        this.mProgress = progress;
        startWave();
    }

    public void startWave() {

        if (getWidth() != 0) {
            int width = getWidth();
            mWaveWidth =  width* mWaveWidthRate;
//            mWaveWidth = 2 * width * mWaveWidthRate;
            left = 0;
            right = getWidth();
            bottom = getHeight();
            mMaxRight = right + X_SPACE;
            omega = PI2 / mWaveWidth;
            computeWaveToTop();
        }
    }

    private void computeWaveToTop() {
        mWaveTopHeight = (int) (getHeight() * (1 - mProgress / 100f));
    }

    private void getWaveOffset() {
        if (mAboveOffset > Float.MAX_VALUE - mWaveHz) {
            mAboveOffset = 0;
        } else {
            mAboveOffset += mWaveHz;
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (View.GONE == visibility) {
            removeCallbacks(mRefreshProgressRunnable);
        } else {
            removeCallbacks(mRefreshProgressRunnable);
            mRefreshProgressRunnable = new RefreshProgressRunnable();
            post(mRefreshProgressRunnable);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            if (mWaveWidth == 0) {
                startWave();
            }
        }
    }

    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (CircleWaveProgressBar.this) {
                long start = System.currentTimeMillis();
                calculatePath();
                invalidate();
                long gap = 20 - (System.currentTimeMillis() - start);
                postDelayed(this, gap < 0 ? 0 : gap);
            }
        }
    }

}
