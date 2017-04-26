package com.knowbox.teacher.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import com.knowbox.teacher.R;

/**
 * @name 自定义控件：双圆环进度条
 * @author Fanjb
 * @date 2015年5月27日
 */
public class DoubleRingProgress extends View {
	private Paint paint;
	private Paint fillPaint;

	private RectF rectF = new RectF();

	private int max;
	private float arcAngle;
	private float innerStrokeWidth, outterStrokeWidth;
	private float innerProgress, outterProgress;
	private int innerFinishedStrokeColor, outterFinishedStrokeColor;
	private int innerUnfinishedStrokeColor, outterUnfinishedStrokeColor;
	private int fillColor;

	private final int default_finished_color = Color.WHITE;
	private final int default_unfinished_color = Color.BLUE;
	private final int default_fill_color = Color.GREEN;
	private final float default_stroke_width;
	private final int default_max = 100;
	private final float default_arc_angle = 360f;
	private final int min_size;

	private static final String INSTANCE_STATE = "saved_instance";
	private static final String INSTANCE_MAX = "max";
	private static final String INSTANCE_ARC_ANGLE = "arc_angle";
	private static final String INSTANCE_INNER_STROKE_WIDTH = "inner_stroke_width";
	private static final String INSTANCE_INNER_PROGRESS = "inner_progress";
	private static final String INSTANCE_INNER_FINISHED_STROKE_COLOR = "inner_finished_stroke_color";
	private static final String INSTANCE_INNER_UNFINISHED_STROKE_COLOR = "inner_unfinished_stroke_color";
	private static final String INSTANCE_OUTTER_STROKE_WIDTH = "outter_stroke_width";
	private static final String INSTANCE_OUTTER_PROGRESS = "outter_progress";
	private static final String INSTANCE_OUTTER_FINISHED_STROKE_COLOR = "outter_finished_stroke_color";
	private static final String INSTANCE_OUTTER_UNFINISHED_STROKE_COLOR = "outter_unfinished_stroke_color";

	private int mViewSize;

	public DoubleRingProgress(Context context) {
		this(context, null);
	}

	public DoubleRingProgress(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DoubleRingProgress(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		min_size = (int) dp2px(getResources(), 100);
		default_stroke_width = dp2px(getResources(), 5);

		TypedArray attributes = context.getTheme().obtainStyledAttributes(
				attrs, R.styleable.DoubleRingProgress, defStyleAttr, 0);
		initByAttributes(attributes);
		attributes.recycle();

		initPainters();
	}

	protected void initByAttributes(TypedArray attributes) {
		arcAngle = attributes.getDimension(
				R.styleable.DoubleRingProgress_arc_angle, default_arc_angle);
		setMax(attributes.getInt(R.styleable.DoubleRingProgress_arc_max,
				default_max));
		setInnerProgress(attributes.getInt(
				R.styleable.DoubleRingProgress_progress_inner, 0));
		innerStrokeWidth = attributes.getDimension(
				R.styleable.DoubleRingProgress_stroke_width_inner,
				default_stroke_width);
		innerFinishedStrokeColor = attributes.getColor(
				R.styleable.DoubleRingProgress_finished_color_inner,
				default_finished_color);
		innerUnfinishedStrokeColor = attributes.getColor(
				R.styleable.DoubleRingProgress_unfinished_color_inner,
				default_unfinished_color);

		setOutterProgress(attributes.getInt(
				R.styleable.DoubleRingProgress_progress_outter, 0));
		outterStrokeWidth = attributes.getDimension(
				R.styleable.DoubleRingProgress_stroke_width_outter,
				default_stroke_width);
		outterFinishedStrokeColor = attributes.getColor(
				R.styleable.DoubleRingProgress_finished_color_outter,
				default_finished_color);
		outterUnfinishedStrokeColor = attributes.getColor(
				R.styleable.DoubleRingProgress_unfinished_color_outter,
				default_unfinished_color);

		fillColor = attributes.getColor(
				R.styleable.DoubleRingProgress_fill_color, default_fill_color);
	}

	protected void initPainters() {
		paint = new Paint();
		paint.setColor(default_unfinished_color);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(innerStrokeWidth);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);

		fillPaint = new Paint();
		fillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		fillPaint.setStyle(Paint.Style.FILL);
		fillPaint.setColor(fillColor);
	}

	@Override
	public void invalidate() {
		initPainters();
		super.invalidate();
	}

	public float getInnerStrokeWidth() {
		return innerStrokeWidth;
	}

	public void setInnerStrokeWidth(float innerStrokeWidth) {
		this.innerStrokeWidth = innerStrokeWidth;
		invalidate();
	}

	public float getOutterStrokeWidth() {
		return outterStrokeWidth;
	}

	public void setOutterStrokeWidth(float outterStrokeWidth) {
		this.outterStrokeWidth = outterStrokeWidth;
		invalidate();
	}

	public float getInnerProgress() {
		return innerProgress;
	}

	public void setInnerProgress(float innerProgress) {
		this.innerProgress = innerProgress;
		if (this.innerProgress > getMax()) {
			this.innerProgress %= getMax();
		}
		invalidate();
	}

	public float getOutterProgress() {
		return outterProgress;
	}

	public void setOutterProgress(float outterProgress) {
		this.outterProgress = outterProgress;
		if (this.outterProgress > getMax()) {
			this.outterProgress %= getMax();
		}
		invalidate();
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		if (max > 0) {
			this.max = max;
			invalidate();
		}
	}

	public int getInnerFinishedStrokeColor() {
		return innerFinishedStrokeColor;
	}

	public void setInnerFinishedStrokeColor(int innerFinishedStrokeColor) {
		this.innerFinishedStrokeColor = innerFinishedStrokeColor;
		invalidate();
	}

	public int getOutterFinishedStrokeColor() {
		return outterFinishedStrokeColor;
	}

	public void setOutterFinishedStrokeColor(int outterFinishedStrokeColor) {
		this.outterFinishedStrokeColor = outterFinishedStrokeColor;
		invalidate();
	}

	public int getInnerUnfinishedStrokeColor() {
		return innerUnfinishedStrokeColor;
	}

	public void setInnerUnfinishedStrokeColor(int innerUnfinishedStrokeColor) {
		this.innerUnfinishedStrokeColor = innerUnfinishedStrokeColor;
		invalidate();
	}

	public int getOutterUnfinishedStrokeColor() {
		return outterUnfinishedStrokeColor;
	}

	public void setOutterUnfinishedStrokeColor(int outterUnfinishedStrokeColor) {
		this.outterUnfinishedStrokeColor = outterUnfinishedStrokeColor;
		invalidate();
	}

	public float getArcAngle() {
		return arcAngle;
	}

	public void setArcAngle(float arcAngle) {
		this.arcAngle = arcAngle;
		this.invalidate();
	}

	@Override
	protected int getSuggestedMinimumHeight() {
		return min_size;
	}

	@Override
	protected int getSuggestedMinimumWidth() {
		return min_size;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mViewSize = Math.min(MeasureSpec.getSize(widthMeasureSpec),
				MeasureSpec.getSize(heightMeasureSpec));
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float startAngle = 270f;
		float finishedStartAngle = startAngle;

		// 绘制外环
		rectF.set(innerStrokeWidth / 2f, innerStrokeWidth / 2f, mViewSize
				- innerStrokeWidth / 2f, mViewSize - innerStrokeWidth / 2f);
		paint.setStrokeWidth(outterStrokeWidth);

		// 外环底环
		paint.setColor(outterUnfinishedStrokeColor);
		canvas.drawArc(rectF, startAngle, arcAngle, false, paint);

		// 外环进度环
		float outterFinishedSweepAngle = outterProgress / getMax() * arcAngle;
		paint.setColor(outterFinishedStrokeColor);
		canvas.drawArc(rectF, finishedStartAngle, outterFinishedSweepAngle,
				false, paint);

		// 绘制内环
		rectF.set(rectF.left + outterStrokeWidth,
				rectF.top + outterStrokeWidth, rectF.right - outterStrokeWidth,
				rectF.bottom - outterStrokeWidth);
		paint.setStrokeWidth(innerStrokeWidth);

		// 内环底环
		paint.setColor(innerUnfinishedStrokeColor);
		canvas.drawArc(rectF, startAngle, arcAngle, false, paint);

		// 内环进度环
		float innerFinishedSweepAngle = innerProgress / getMax() * arcAngle;
		paint.setColor(innerFinishedStrokeColor);
		canvas.drawArc(rectF, finishedStartAngle, innerFinishedSweepAngle,
				false, paint);

		// 中心圆
		rectF.set(rectF.left + innerStrokeWidth, rectF.top + innerStrokeWidth,
				rectF.right - innerStrokeWidth, rectF.bottom - innerStrokeWidth);
		canvas.drawCircle(rectF.centerX(), rectF.centerY(), (rectF.width() / 2)
				+ 3 * getResources().getDisplayMetrics().density, fillPaint);

	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Bundle bundle = new Bundle();
		bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
		bundle.putInt(INSTANCE_MAX, getMax());
		bundle.putFloat(INSTANCE_ARC_ANGLE, getArcAngle());
		bundle.putFloat(INSTANCE_INNER_STROKE_WIDTH, getInnerStrokeWidth());
		bundle.putFloat(INSTANCE_INNER_PROGRESS, getInnerProgress());
		bundle.putInt(INSTANCE_INNER_FINISHED_STROKE_COLOR,
				getInnerFinishedStrokeColor());
		bundle.putInt(INSTANCE_INNER_UNFINISHED_STROKE_COLOR,
				getInnerUnfinishedStrokeColor());
		bundle.putFloat(INSTANCE_OUTTER_STROKE_WIDTH, getOutterStrokeWidth());
		bundle.putFloat(INSTANCE_OUTTER_PROGRESS, getOutterProgress());
		bundle.putInt(INSTANCE_OUTTER_FINISHED_STROKE_COLOR,
				getOutterFinishedStrokeColor());
		bundle.putInt(INSTANCE_OUTTER_UNFINISHED_STROKE_COLOR,
				getOutterUnfinishedStrokeColor());
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			final Bundle bundle = (Bundle) state;
			setMax(bundle.getInt(INSTANCE_MAX));
			setArcAngle(bundle.getFloat(INSTANCE_ARC_ANGLE));
			innerStrokeWidth = bundle.getFloat(INSTANCE_INNER_STROKE_WIDTH);
			setInnerProgress(bundle.getFloat(INSTANCE_INNER_PROGRESS));
			innerFinishedStrokeColor = bundle
					.getInt(INSTANCE_INNER_FINISHED_STROKE_COLOR);
			innerUnfinishedStrokeColor = bundle
					.getInt(INSTANCE_INNER_UNFINISHED_STROKE_COLOR);
			outterStrokeWidth = bundle.getFloat(INSTANCE_OUTTER_STROKE_WIDTH);
			setOutterProgress(bundle.getFloat(INSTANCE_OUTTER_PROGRESS));
			outterFinishedStrokeColor = bundle
					.getInt(INSTANCE_OUTTER_FINISHED_STROKE_COLOR);
			outterUnfinishedStrokeColor = bundle
					.getInt(INSTANCE_OUTTER_UNFINISHED_STROKE_COLOR);
			initPainters();
			super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
			return;
		}
		super.onRestoreInstanceState(state);
	}

	public static float dp2px(Resources resources, float dp) {
		final float scale = resources.getDisplayMetrics().density;
		return dp * scale + 0.5f;
	}

	public static float sp2px(Resources resources, float sp) {
		final float scale = resources.getDisplayMetrics().scaledDensity;
		return sp * scale;
	}
}
