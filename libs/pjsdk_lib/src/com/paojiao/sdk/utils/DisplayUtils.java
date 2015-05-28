package com.paojiao.sdk.utils;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;

public class DisplayUtils {
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	public static int getShortSide(Context context) {
		if (getScreenHeight(context) > getScreenWidth(context))
			return getScreenWidth(context);
		else
			return getScreenHeight(context);
	}

	/**
	 * 用以获取手机屏幕高度。宽p.x，高p.y
	 * 
	 * @author zhounan
	 * @param
	 */
	@SuppressLint("NewApi")
	public static Point getSizePoint(Activity context) {
		Point point = new Point();
		Display display = context.getWindowManager().getDefaultDisplay();
		display.getSize(point);
		return point;
	}

	/** 获取手机状态栏高度 */
	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	public static int DipToPixels(Context context, int dip) {
		final float SCALE = context.getResources().getDisplayMetrics().density;

		float valueDips = dip;
		int valuePixels = (int) (valueDips * SCALE + 0.5f);

		return valuePixels;

	}

	// 像素转dip
	public static float PixelsToDip(Context context, int Pixels) {
		final float SCALE = context.getResources().getDisplayMetrics().density;

		float dips = Pixels / SCALE;

		return dips;

	}

}
