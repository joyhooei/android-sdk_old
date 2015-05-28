/**
 * 
 */
package com.paojiao.sdk.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.paojiao.sdk.CallbackListener;
import com.paojiao.sdk.dialog.base.BaseDialog;
import com.paojiao.sdk.utils.ResourceUtil;

/**
 * @author liurenqiu520
 * 
 */
public class SplashDialog extends BaseDialog {

	private Handler mHandler;
	private CallbackListener callbackListener;
	private Runnable hRunnable = new Runnable() {

		@Override
		public void run() {
			SplashDialog.this.cancel();
		}
	};

	private Activity activity;

	public SplashDialog(Context context) {
		super(context, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		this.activity = (Activity) context;
		setOwnerActivity((Activity) context);
		setContentView(ResourceUtil.getLayoutId(context, "pj_layout_splash"));
		mHandler = new Handler();
	}

	public void show(CallbackListener callbackListener, long delay) {
		this.callbackListener = callbackListener;
		if(!activity.isFinishing()){
			super.show();
			if (delay > 0) {
				mHandler.postDelayed(hRunnable, delay);
			}
		}
	}

	@Override
	public void cancel() {
		try {
			mHandler.removeCallbacks(hRunnable);
		} catch (Exception ex) {
		}
		if (this.isShowing())
			super.cancel();
		if (this.callbackListener != null) {
			this.callbackListener.onSplashComplete();
		}

	}

	@Override
	protected void initData() {

	}

	@Override
	protected void setListener() {

	}

	@Override
	protected void findView() {

	}
}
