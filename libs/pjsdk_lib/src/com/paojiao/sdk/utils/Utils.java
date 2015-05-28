package com.paojiao.sdk.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;

import com.paojiao.sdk.PJApi;

public class Utils {

//	/**
//	 * 获取 meta-data 对应的 value 值 meta-data是在 AndroidManifest.xml中配置的
//	 * 
//	 * @param context
//	 * @param metaKey
//	 * @return
//	 */
//	public static String getMetaValue(Context context, String metaKey) {
//		Bundle metaData = null;
//		String apiKey = "default";
//		if (context == null || metaKey == null) {
//			return apiKey;
//		}
//		try {
//			ApplicationInfo ai = context.getPackageManager()
//					.getApplicationInfo(context.getPackageName(),
//							PackageManager.GET_META_DATA);
//			if (null != ai) {
//				metaData = ai.metaData;
//			}
//			if (null != metaData) {
//				apiKey = metaData.getString(metaKey);
//			}
//		} catch (Exception e) {
//		}
//		if (apiKey != null) {
//			return apiKey;
//		} else {
//			return "default";
//		}
//	}
	
	
	
	
	
	
	
	/**
	 * 获取 meta-data 对应的 value 值 meta-data是在 AndroidManifest.xml中配置的
	 * 
	 * @param context
	 * @param metaKey
	 * @return
	 */
	public static String getMetaValue2(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = "default";
		if (context == null || metaKey == null) {
			return apiKey;
		}
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
			if(TextUtils.isEmpty(apiKey)){
				apiKey = Integer.toString(metaData.getInt(metaKey));
			}
		} catch (Exception e) {
		}
		if (apiKey != null) {
			return apiKey;
		} else {
			return "default";
		}
	}
	
	/**
	 * 获取 meta-data 对应的 value 值 meta-data是在 AndroidManifest.xml中配置的
	 * 
	 * @param context
	 * @param metaKey
	 * @return
	 */
	public static String getMetaValue(Context context, String metaKey) {
		if(PJApi.channel){
			return ZipUtils.getChannel(context,metaKey);
		}else {
			return getMetaValue2(context, metaKey);
		}
	}
	

	public static String signForUrl(String url) {
		if (url.contains("?")) {
			url = url.substring(url.indexOf('?') + 1);
			String str[] = url.split("&");
			if (str.length > 0) {
				List<String> list = new ArrayList<String>();
				Set<String> set = new HashSet<String>();
				for (int i = 0; i < str.length; i++) {
					set.add(str[i]);
				}
				list.addAll(set);
				Collections.sort(list);
				StringBuffer stringBuffer = new StringBuffer();
				for (String string : list) {
					stringBuffer.append(string);
				}
				stringBuffer.append("mduSfA5TgCe8xxpAqxsIFpPeWWRqCVH4");
				String md5 = MD5Util.Md5(stringBuffer.toString());
				return md5;
			}
		}
		return null;
	}

	/**
	 * 返回圆角图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 5;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	/**
	 * 判断手机是否为MIUI v5系统
	 * @param mContext
	 * @return
	 */
	public static boolean isMiuiV5(Context mContext) {
		String str = APKUtils.getSystemProperty("ro.miui.ui.version.name");
		if(!TextUtils.isEmpty(str)){
			if (str.contains("V")) { // TODO 判断是不是MIUI
				PJApi.IS_MIUI = true;
			} else
				// 测试环境，作假判断
				PJApi.IS_MIUI = false;
		}else {
			PJApi.IS_MIUI = false;
		}
		
		return PJApi.IS_MIUI;
	}
}
