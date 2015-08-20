package com.ninegame.game;

import android.util.Log;

public class GLog {
	public static void d(String tag, String msg, Object... args) {
		Log.d(tag, String.format(msg, args));
	}
}
