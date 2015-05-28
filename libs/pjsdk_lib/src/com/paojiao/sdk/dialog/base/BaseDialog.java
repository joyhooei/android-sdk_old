/**
 * 
 */
package com.paojiao.sdk.dialog.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.paojiao.sdk.CallbackListener;
import com.paojiao.sdk.utils.ResourceUtil;

/**
 * 项目中部分Dialog的父类
 * 
 * @author zhounan
 * @version 2014-8-5 上午11:53:32
 */
public abstract class BaseDialog extends Dialog {

	protected CallbackListener callbackListener;
	protected Activity activity;

	protected BaseDialog(Activity activity, CallbackListener callbackListener) {
		super(activity, ResourceUtil.getStyleId(activity, "  "));
		// 无title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 背景色透明
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		this.activity = activity;
		this.callbackListener = callbackListener;
		setCanceledOnTouchOutside(false);
	}

	protected BaseDialog(Context context, CallbackListener callbackListener) {
		super(context, ResourceUtil.getStyleId(context, "  "));
		// 无title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 背景色透明
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		this.callbackListener = callbackListener;
		setCanceledOnTouchOutside(false);
	}

	protected BaseDialog(Context context, int theme) {
		super(context, theme);
		// 无title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 背景色透明
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		setCanceledOnTouchOutside(false);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		findView();
		setListener();
		initData();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		findView();
		setListener();
		initData();
	}

	protected abstract void initData();

	protected abstract void setListener();

	protected abstract void findView();

}
