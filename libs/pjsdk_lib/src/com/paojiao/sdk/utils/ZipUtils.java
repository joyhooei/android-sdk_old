package com.paojiao.sdk.utils;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.paojiao.sdk.PJApi;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class ZipUtils {
	
	private static final String Channel_Channel="Channel_Channel";
	/**
	 * 获取渠道号
	 * @param mContext
	 * @param packageName
	 * @param metaKey  获取 meta-data 对应的 value 值 meta-data是在 AndroidManifest.xml中配置的
	 * @return
	 */
	public static String getChannel(Context mContext,String metaKey){

		String channel = null;
		channel=PreferencesUtils.getString(mContext, Channel_Channel,null);
		if(!TextUtils.isEmpty(channel)){
			return channel;
		}
		String packageName = PJApi.packageName;//mContext.getPackageName();
		if(TextUtils.isEmpty(packageName)){
			packageName=mContext.getPackageName();
		}
		String apkUrl = "/data/app/"+packageName+"-1.apk";
		
		File file = new File(apkUrl);
		if(file.exists()){
			channel = unZip(apkUrl);
			
			if(!TextUtils.isEmpty(channel)){
				PreferencesUtils.putString(mContext, Channel_Channel,channel);
			}
			
		}else {
			String apkUrl2 = "/data/app/"+packageName+"-2.apk";
			File file2 = new File(apkUrl2);
			if(file2.exists()){
				channel = unZip(apkUrl);
				if(!TextUtils.isEmpty(channel)){
					PreferencesUtils.putString(mContext, Channel_Channel,channel);
				}
				
			}else {
				channel = Utils.getMetaValue2(mContext, metaKey);
			}
		}
		if(TextUtils.isEmpty(channel)){
			channel = Utils.getMetaValue2(mContext, metaKey);
		}
	   return channel;
	}
	
	
	
//	public static String getChannel2(Context mContext,String metaKey){
//
//		String channel = null;
//		channel=PreferencesUtils.getString(mContext, Channel_Channel,null);
//		if(!TextUtils.isEmpty(channel)){
//			return channel;
//		}
//		String packageName = PJApi.packageName;
//		if(TextUtils.isEmpty(packageName)){
//			packageName=mContext.getPackageName();
//		}
//		String apkUrl = "/data/app/"+packageName+"-1.apk";
//		
//		Toast.makeText(mContext, apkUrl, Toast.LENGTH_LONG).show();
//		Log.e("zgt", apkUrl); 
//		
//		File file = new File(apkUrl);
//		if(file.exists()){
//			channel = unZip(apkUrl);
//			
//			if(!TextUtils.isEmpty(channel)){
//				PreferencesUtils.putString(mContext, Channel_Channel,channel);
//			}
//			
//		}else {
//			String apkUrl2 = "/data/app/"+packageName+"-2.apk";
//			File file2 = new File(apkUrl2);
//			if(file2.exists()){
//				channel = unZip(apkUrl);
//				if(!TextUtils.isEmpty(channel)){
//					PreferencesUtils.putString(mContext, Channel_Channel,channel);
//				}
//				
//			}else {
//				channel = Utils.getMetaValue2(mContext, metaKey);
//			}
//		}
//		if(TextUtils.isEmpty(channel)){
//			channel = Utils.getMetaValue2(mContext, metaKey);
//		}
//	   return channel;
//	}

	@SuppressWarnings({ "rawtypes" })
	private  static String unZip(String apkUrl) {  
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(new File(apkUrl));
			Enumeration enumeration = zipFile.entries();
			ZipEntry zipEntry = null;
			while (enumeration.hasMoreElements()) {
				zipEntry = (ZipEntry) enumeration.nextElement();
				if (!zipEntry.isDirectory()) {
					if (zipEntry.getName().contains("META-INF/channel_")) {
						String channel = zipEntry.getName().substring(
								zipEntry.getName().indexOf("_") + 1);
						return channel;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
