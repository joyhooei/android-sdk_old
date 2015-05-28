package com.paojiao.sdk.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.os.Build;

public class APKUtils {

	/**
	 * 
	 * @Title: currentIsMyApplication
	 * @Description: TODO(获取当前运行的是 我们的apk 吗 true 是 )
	 * @param @param context
	 * @return boolean
	 * @throws
	 */
	public static boolean currentIsMyApplication(Context context) {
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> infos = mActivityManager.getRunningTasks(1);
		if (infos != null && infos.size() > 0) {
			RunningTaskInfo info = infos.get(0);
			String currenPackage = context.getPackageName();
			String apkPackageName = info.topActivity.getPackageName();
			if (currenPackage.equals(apkPackageName)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static String getSystemProperty(String propName) {
		String line;
		BufferedReader input = null;
		try {
			Process p = Runtime.getRuntime().exec("getprop " + propName);
			input = new BufferedReader(
					new InputStreamReader(p.getInputStream()), 1024);
			line = input.readLine();
			input.close();
		} catch (IOException ex) {
			// Log.e(TAG, "Unable to read sysprop " + propName, ex);
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// Log.e(TAG, "Exception while closing InputStream", e);
				}
			}
		}
		return line;
	}
	
	
	 public static  boolean isEmulator() {
         return (Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk"));
     }
	
	
}
