package com.paojiao.sdk.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.paojiao.sdk.dialog.UCDialog;
import com.paojiao.sdk.widget.MiuiFloatView;

/**
 * @author zhounan
 * @version 2014-7-30 上午10:26:38
 */
public class ImageUtils {
	/**
	 * 将bitmap裁切成圆形bitmap并返回。
	 * 
	 * @author zhounan
	 * @param
	 */
	public static Bitmap getRoundBitmap(Bitmap orignalMap, Context context) {
		if (orignalMap == null) {
			orignalMap = BitmapFactory.decodeResource(context.getResources(),
					ResourceUtil.getDrawableId(context,
							"pj_default_avatar_icon"));
		}
		Bitmap lastMap = Bitmap.createBitmap(orignalMap.getWidth(),
				orignalMap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(lastMap);
		int w = orignalMap.getWidth();
		int h = orignalMap.getHeight();
		final int color = 0xffff4242;
		final Paint paint = new Paint();
		final Rect rect;
		int r;
		if (w > h) {
			r = h / 2;
			rect = new Rect((w - h) / 2, 0, (w + h) / 2, h);
		} else {
			r = w / 2;
			rect = new Rect(0, (h - w) / 2, w, (w + h) / 2);
		}

		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, r, r, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(orignalMap, rect, rect, paint);
		return lastMap;
	}


	/**
	 * @param avatarUrl
	 * 
	 */
	public static void savaPicToLocal(Context context, String avatarUrl) {
		File cache = new File(Environment.getExternalStorageDirectory(),
				"cache/pj");
		if (!cache.exists()) {
			cache.mkdirs();
		}

	}

	public static Bitmap GetLocalOrNetBitmap(String url) {
		Bitmap bitmap = null;
		InputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(new URL(url).openStream(), 2 * 1024);
			final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			out = new BufferedOutputStream(dataStream, 2 * 1024);
			// copy(in, out);
			out.flush();
			byte[] data = dataStream.toByteArray();
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			data = null;
			return bitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将bitmap存储到本地sdk目录下
	 */
	public static void writeFileToSD(Bitmap bm) {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			Log.d("TestFile", "SD card is not avaiable/writeable right now.");
			return;
		}
		byte[] byteImg = bitmap2Bytes(bm);
		try {
			String pathName = "/sdcard/pjsdk/";
			String fileName = "myAva.png";
			File path = new File(pathName);
			File file = new File(pathName + fileName);
			if (!path.exists()) {
				Log.d("TestFile", "Create the path:" + pathName);
				path.mkdir();
			}
			if (!file.exists()) {
				Log.d("TestFile", "Create the file:" + fileName);
				file.createNewFile();
			}
			FileOutputStream stream = new FileOutputStream(file);
			stream.write(byteImg);
			stream.flush();
			stream.close();

		} catch (Exception e) {
			Log.e("TestFile", "Error on writeFilToSD.");
			e.printStackTrace();
		}
	}

	// Bitmap转byte数组
	private static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);// png类型
		return baos.toByteArray();
	}

	public static void initImageLoader(Activity activity) {
	}
}
