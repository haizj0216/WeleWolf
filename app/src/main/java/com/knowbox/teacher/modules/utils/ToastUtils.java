package com.knowbox.teacher.modules.utils;

import android.content.Context;

public class ToastUtils {
	public static void showShortToast(Context context, String text) {
		ToastInstance toast = ToastInstance.getInstance(context);
		toast.showShortToast(context, text);
	}

	public static void showShortToast(Context context, int strId) {
		ToastInstance.getInstance(context).showShortToast(context, strId);
	}

	public static void showShortToast(Context context, int resid, String str) {
		String message = context.getString(resid, str);
		ToastInstance.getInstance(context).showShortToast(context, message);
	}


	public static void showPopToast(Context context, String text) {
		showShortToast(context, text);
	}

	public static void showPopToast(Context context, int strId) {
		showShortToast(context, strId);
	}

	public static void showPopToast(Context context, int resid, String str) {
		showShortToast(context, resid);
	}

	public static void showLongToast(Context context, int strId) {
		ToastInstance.getInstance(context).showLongToast(context, strId);
	}

	public static void showLongToast(Context context, String text) {
		ToastInstance.getInstance(context).showLongToast(context, text);
	}
	public static void showLongCenterToast(Context context, String text) {
//		ToastInstance.getInstance(context).showLongCenterToast(context, text);
		ToastInstance.getInstance(context).showLongToast(context, text);
	}
}