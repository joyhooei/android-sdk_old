package com.paojiao.sdk.utils;

import android.content.res.ColorStateList;

public class ColorState {
	/**
	 * 
	 * @param colorPress
	 *            tv被按下去显示颜色
	 * @param colorNormal
	 *            tv常态显示颜色
	 * @return
	 */
	public static ColorStateList SetTextColor(int colorPress, int colorNormal) {
		int statePressed = android.R.attr.state_pressed;
		int stateFocesed = android.R.attr.state_focused;
		int[][] states = { { statePressed }, { -statePressed },
				{ stateFocesed }, { -stateFocesed } };
		int[] colors = { colorPress, colorNormal, colorPress, colorNormal };
		ColorStateList colorState = new ColorStateList(states, colors);
		return colorState;
	}
}
