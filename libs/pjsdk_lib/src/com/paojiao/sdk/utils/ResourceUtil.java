package com.paojiao.sdk.utils;

import android.content.Context;

public class ResourceUtil {

	/**
	 * 获取 layout 布局文件
	 * @param paramContext Context
	 * @param paramString  layout xml 的文件名
	 * @return layout 
	 */
	public static int getLayoutId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "layout",
				paramContext.getPackageName());
	}

	/**
	 * 获取 string 值 
	 * @param paramContext  Context
	 * @param paramString   string name的名称
	 * @return string
	 */
	public static int getStringId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "string",
				paramContext.getPackageName());
	}

	/**
	 * 获取 drawable 布局文件 或者 图片的 
	 * @param paramContext  Context
	 * @param paramString drawable 的名称
	 * @return drawable
	 */
	public static int getDrawableId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString,
				"drawable", paramContext.getPackageName());
	}

	
	/**
	 * 获取 style 
	 * @param paramContext Context 
	 * @param paramString  style的名称
	 * @return style
	 */
	public static int getStyleId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "style",
				paramContext.getPackageName());
	}
	
	/**
	 * 获取 styleable
	 * @param paramContext  Context 
	 * @param paramString  styleable 的名称
	 * @return styleable
	 */
	public static Object getStyleableId(Context paramContext, String paramString){
		return paramContext.getResources().getIdentifier(paramString, "styleable",
				paramContext.getPackageName());
	}
	
	
	/**
	 * 获取 anim 
	 * @param paramContext  Context 
	 * @param paramString  anim xml 文件名称
	 * @return anim
	 */
	public static int getAnimId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "anim",
				paramContext.getPackageName());
	}

	/**
	 * 获取 id 
	 * @param paramContext Context
	 * @param paramString id 的名称
	 * @return
	 */
	public static int getId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "id",
				paramContext.getPackageName());
	}

	/**
	 * color
	 * @param paramContext  Context
	 * @param paramString  color 名称
	 * @return
	 */
	public static int getColorId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "color",
				paramContext.getPackageName());
	}

}