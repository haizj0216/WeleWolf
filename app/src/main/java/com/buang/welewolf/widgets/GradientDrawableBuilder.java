package com.buang.welewolf.widgets;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;

import com.hyena.framework.animation.utils.UIUtils;

/**
 * Created by LiuYu on 2017/2/8.
 */
public class GradientDrawableBuilder {

    private GradientDrawable gradientDrawable;
    private Context context;

    public GradientDrawableBuilder createCommonDrawable(Context context) {
        gradientDrawable = new GradientDrawable();
        this.context = context;
        return this;
    }

    public GradientDrawableBuilder createSpecialDrawable(Context context, Orientation orientation, int[] colors) {
        gradientDrawable = new GradientDrawable(orientation, colors);
        this.context = context;
        return this;
    }

    public GradientDrawableBuilder buildRectangleShape() {
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        return this;
    }

    public GradientDrawableBuilder buildOvalShape() {
        gradientDrawable.setShape(GradientDrawable.OVAL);
        return this;
    }

    public GradientDrawableBuilder buildLineShape() {
        gradientDrawable.setShape(GradientDrawable.LINE);
        return this;
    }

    public GradientDrawableBuilder buildRingShape() {
        gradientDrawable.setShape(GradientDrawable.RING);
        return this;
    }

    public GradientDrawableBuilder buildShape(int shape) {
        gradientDrawable.setShape(shape);
        return this;
    }

    public GradientDrawableBuilder buildColor(int color) {
        gradientDrawable.setColor(color);
        return this;
    }

    public GradientDrawableBuilder buildCornerRadius(float radius) {
        gradientDrawable.setCornerRadius(radius);
        return this;
    }

    public GradientDrawableBuilder buildCornerRadii(float[] radii) {
        gradientDrawable.setCornerRadii(radii);
        return this;
    }

    public GradientDrawableBuilder buildDashStroke(int width, int color) {
        gradientDrawable.setStroke(width, color, UIUtils.dp2px(context, 2), UIUtils.dp2px(context, 1));
        return this;
    }

    public GradientDrawableBuilder buildStroke(int width, int color) {
        gradientDrawable.setStroke(width, color);
        return this;
    }

    public GradientDrawableBuilder buildSize(int width, int height) {
        gradientDrawable.setSize(width, height);
        return this;
    }

    public GradientDrawable create() {
        return gradientDrawable;
    }

}
