package com.paojiao.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.paojiao.sdk.CallbackListener;
import com.paojiao.sdk.PJApi;
import com.paojiao.sdk.activity.WebActivity;
import com.paojiao.sdk.config.ConfigurationInfo;
import com.paojiao.sdk.config.Route;
import com.paojiao.sdk.config.URL;
import com.paojiao.sdk.dialog.SimpleDialog;
import com.paojiao.sdk.dialog.UCDialog;
import com.paojiao.sdk.res.StringGlobal;
import com.paojiao.sdk.service.OpenUDID_service;
import com.paojiao.sdk.widget.FloatUtils;
import com.paojiao.sdk.widget.MiuiFloatView;
import com.paojiao.sdk.widget.MyFloatView;

public class DialogUtils {
//	static MiuiFloatView miuiFloatView = null;

	/**
	 * 显示个人中心对话框
	 * 
	 * @param context
	 */
	public static void showUserCenter(Context context) {
		if(Utils.isMiuiV5(context)){
			UCDialog userCenterDialog = new UCDialog(context, ResourceUtil.getStyleId(
					context, "PJDialog"));//UCDialog.newInstance(context);
			/*userCenterDialog.getWindow().setType(
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			userCenterDialog.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);*/
			userCenterDialog.show();
		}else {
			UCDialog userCenterDialog = UCDialog.newInstance(context);
			userCenterDialog.getWindow().setType(
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			userCenterDialog.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			userCenterDialog.show();
		}
		
	}

	/**
	 * 登录成功后弹出提示绑定电话对话框
	 * 
	 * @param mContext
	 */
	public static void showSimpleDialog(final Activity mContext) {
		Prints.i("tag-tag", "isBundle:" + PJApi.IS_BUNDEL_TEL);
		if (PJApi.IS_BUNDEL_TEL)
			return;
		final SimpleDialog simpleDialog = new SimpleDialog(mContext, null);
		simpleDialog.setMessage(String.format(StringGlobal.NOTICE_BIND_MOBILE,
				mContext.getString(mContext.getApplicationInfo().labelRes),
				ConfigurationInfo.getNickName(mContext)));
		simpleDialog.setTitle(StringGlobal.WARM_TIPS);
		simpleDialog.setPositiveButton(StringGlobal.BIND_RIGHT_NOW,
				new View.OnClickListener() {

					public void onClick(View v) {
						Bundle params = Route.creatUrlParams(mContext);
						simpleDialog.dismiss();
						PJApi.startActivity(WebActivity.class, URL.BIND_MOBILE,
								params);
					}
				});
		simpleDialog.setNegativeButton(StringGlobal.BIND_LATTER,
				new View.OnClickListener() {

					public void onClick(View v) {
						simpleDialog.dismiss();
					}
				});
		simpleDialog.show();
	}

	/**
	 * 此处为第一次调用显示悬浮窗
	 * 
	 * @param con
	 */
	public static void showFloat(Activity con) {
		if (!Utils.isMiuiV5(con)) {
			MyFloatView.newInstance().show();
			if (MyFloatView.newInstance().isAddWindow() == false) {
				con.startService(new Intent(con, OpenUDID_service.class));
			}
		} else {
			showMiuiFloat(con);
		}

	}
	
	/**
	 * 此处为第一次调用显示悬浮窗
	 * 
	 * @param con
	 */
	public static void showFloat(Activity con,boolean second) {
		if (!Utils.isMiuiV5(con)) {
			MyFloatView.newInstance().show();
			if (MyFloatView.newInstance().isAddWindow() == false) {
				con.startService(new Intent(con, OpenUDID_service.class));
			}
		} else {
			showMiuiFloat(con,second);
		}

	}
	

	static int rawX, rawY, XX, YY;

	/**
	 * 小米手机悬浮窗消失后再次弹出
	 * 
	 * @param mContext
	 */
	public static void tempShowFloat(Context mContext) {
		new CallbackListener(mContext) {
			public void onGetCoordinate(Bundle info) {
				super.onGetCoordinate(info);
				rawX = info.getInt("rawX");
				rawY = info.getInt("rawY");
				XX = info.getInt("x");
				YY = info.getInt("y");
			}
		};
		//System.out.println(rawX + "\n" + rawY + "\n" + "\n" + XX + "\n" + YY);
		View view = ((Activity) mContext).getWindow().getDecorView();
		Display display = ((Activity) mContext).getWindowManager()
				.getDefaultDisplay();
		int screenWidth = display.getWidth();
		if(mContext instanceof Activity){
			if (MiuiFloatView.newInstance((Activity)mContext) != null && !MiuiFloatView.newInstance((Activity)mContext).isShowing()) {
				if (rawX < screenWidth / 2) {
					MiuiFloatView.newInstance((Activity)mContext).showAtLocation(view, Gravity.TOP | Gravity.LEFT,
							0, rawY - YY);
				} else {
					MiuiFloatView.newInstance((Activity)mContext).showAtLocation(view, Gravity.TOP | Gravity.LEFT,
							screenWidth - XX / 2, rawY - YY);
				}
			}
		}
		
	}

  public static class concreteIBack implements IBack {
		public void getCordinates(int xx, int yy, int rx, int ry) {
			rawX = rx;
			rawY = ry;
			XX = xx;
			YY = yy;
		}
	}

	public static interface IBack {
		void getCordinates(int xx, int yy, int rx, int ry);
	}

	/**
	 * 退出游戏时移除掉view
	 */
	public static void removeMiuiFloatView() {
		/*if (miuiFloatView != null) {
			miuiFloatView = null;
		}*/
	}

	/**
	 * 小米手机显示个人中心对话框
	 */
	private static void showMiuiFloat(Activity mContext) {
		View view = mContext.getWindow().getDecorView();
		Display display = mContext.getWindowManager().getDefaultDisplay();
		
		
		if (MiuiFloatView.newInstance((Activity)mContext) != null && !MiuiFloatView.newInstance((Activity)mContext).isShowing()) {
			MiuiFloatView.newInstance((Activity)mContext).showAtLocation(view, Gravity.TOP | Gravity.LEFT, 0,display.getHeight() / 2);
		}
	}
	
	
	/**
	 * 小米手机显示个人中心对话框
	 */
	private static void showMiuiFloat(Activity mContext, boolean second) {
		if (second) {
			if (FloatUtils.getnewInstall().getMiuiFloatView(mContext) == null) {
				View view = mContext.getWindow().getDecorView();
				MiuiFloatView miuiFloatView = new MiuiFloatView(mContext,false);
				Display display = mContext.getWindowManager()
						.getDefaultDisplay();
				if (!miuiFloatView.isShowing()) {
					miuiFloatView.showAtLocation(view, Gravity.TOP
							| Gravity.LEFT, 0, display.getHeight() / 2);
				}
				FloatUtils.getnewInstall().addMiuiFloatView(mContext,miuiFloatView);
			}
		}

	}
	
}
