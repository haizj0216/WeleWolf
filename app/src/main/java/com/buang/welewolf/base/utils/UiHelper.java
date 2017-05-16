/**
 * Copyright (C) 2015 The AndroidPhoneTeacher Project
 */
package com.buang.welewolf.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.hyena.framework.utils.BaseApp;
import com.buang.welewolf.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * UI相关操作帮助类
 * 
 * @author yangzc
 */
public class UiHelper {

	/**
	 * 通过ID获得View
	 * 
	 * @param activity
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View> T findViewById(Activity activity, int id) {
		return (T) activity.findViewById(id);
	}

	/**
	 * 通过ID获得View
	 * 
	 * @param view
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View> T findViewById(View view, int id) {
		return (T) view.findViewById(id);
	}

	/**
	 * 震动
	 */
	public static void doVibrator() {
		Vibrator vibrator = (Vibrator) BaseApp.getAppContext()
				.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(200);
	}

	/**
	 * 使View飞到屏幕底端
	 * 
	 * @param view
	 * @param marginBottom
	 * @param visible
	 * @param animate
	 * @param force
	 */
	public static void flyView2Bottom(final View view, final int marginBottom,
			final boolean visible, final boolean animate, boolean force) {
		if (view.isShown() != visible || force) {
			int height = view.getHeight();
			if (height == 0 && !force) {
				ViewTreeObserver vto = view.getViewTreeObserver();
				if (vto.isAlive()) {
					vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
						@Override
						public boolean onPreDraw() {
							ViewTreeObserver currentVto = view
									.getViewTreeObserver();
							if (currentVto.isAlive()) {
								currentVto.removeOnPreDrawListener(this);
							}
							flyView2Bottom(view, marginBottom, visible,
									animate, true);
							return true;
						}
					});
					return;
				}
			}
			int translationY = visible ? 0 : height + marginBottom;
			if (animate) {
				ViewPropertyAnimator
						.animate(view)
						.setInterpolator(new AccelerateDecelerateInterpolator())
						.setDuration(200).translationY(translationY).start();
			} else {
				ViewHelper.setTranslationY(view, translationY);
			}
		}
	}

	/**
	 * 设置view显示还是隐藏
	 * 
	 * @param view
	 * @param visible
	 * @param force
	 */
	public static void setViewVisible(final View view, final boolean visible,
			boolean force) {
		if (view.isShown() != visible || force) {
			int height = view.getHeight();
			if (height == 0 && !force) {
				ViewTreeObserver vto = view.getViewTreeObserver();
				if (vto.isAlive()) {
					vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
						@Override
						public boolean onPreDraw() {
							ViewTreeObserver currentVto = view
									.getViewTreeObserver();
							if (currentVto.isAlive()) {
								currentVto.removeOnPreDrawListener(this);
							}
							setViewVisible(view, visible, true);
							return true;
						}
					});
					return;
				}
			}
			if (visible) {
				view.setVisibility(View.VISIBLE);
				ViewPropertyAnimator
						.animate(view)
						.setInterpolator(new AccelerateDecelerateInterpolator())
						.setDuration(200).scaleX(1).scaleY(1).start();
			} else {
				ViewPropertyAnimator
						.animate(view)
						.setInterpolator(new AccelerateDecelerateInterpolator())
						.setDuration(200).scaleX(0).scaleY(0)
						.setListener(new AnimatorListener() {
							@Override
							public void onAnimationStart(Animator animation) {
							}

							@Override
							public void onAnimationRepeat(Animator animation) {
							}

							@Override
							public void onAnimationEnd(Animator animation) {
								view.setVisibility(View.GONE);
							}

							@Override
							public void onAnimationCancel(Animator animation) {
							}
						}).start();
			}
		}

	}

	/**
	 * 晃动View
	 * 
	 * @param view
	 */
	public static void notify2shake(View view) {
		Animation shake = AnimationUtils.loadAnimation(view.getContext(),
				R.anim.shake);
		view.startAnimation(shake);
	}

	/**
	 * 关闭软键盘
	 * 
	 * @param context
	 * @param view
	 */
	public static void closeSoftPan(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * 获得按下颜色变化的drawable
	 * 
	 * @param normal
	 * @param pressed
	 * @return
	 */
	public static Drawable getPressColorDrawable(int normal, int pressed) {
		return getPressDrawable(getColorDrawable(normal),
				getColorDrawable(pressed));
	}

	/**
	 * 获得可按下状态
	 * 
	 * @param normal
	 * @param pressed
	 * @return
	 */
	public static Drawable getPressDrawable(Drawable normal, Drawable pressed) {
		StateListDrawable drawable = new StateListDrawable();
		drawable.addState(new int[] { android.R.attr.state_pressed }, pressed);
		drawable.addState(new int[] {}, normal);
		return drawable;
	}

	/**
	 * 获得颜色状态
	 * 
	 * @param color
	 * @return
	 */
	public static Drawable getColorDrawable(int color) {
		return new ColorDrawable(color);
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void setBackground(View view, Drawable drawable){
		if(VERSION.SDK_INT >= 16){
			view.setBackground(drawable);
		}else{
			view.setBackgroundDrawable(drawable);
		}
	}
}
