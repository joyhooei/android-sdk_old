package com.paojiao.sdk.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.paojiao.sdk.config.BridgeMediation;
import com.paojiao.sdk.config.ConfigurationInfo;
import com.paojiao.sdk.dialog.UCDialog;
import com.paojiao.sdk.utils.APKUtils;
import com.paojiao.sdk.utils.DialogUtils;
import com.paojiao.sdk.utils.Utils;
import com.paojiao.sdk.widget.MyFloatView;

/*
 * You have to add this in your manifest

 <service android:name="org.OpenUDID.OpenUDID_service">
 <intent-filter>
 <action android:name="org.OpenUDID.GETUDID" />
 </intent-filter>
 </service>

 */

public class OpenUDID_service extends Service {
	@Override
	public IBinder onBind(Intent arg0) {
		return new Binder() {
			@Override
			public boolean onTransact(int code, android.os.Parcel data,
					android.os.Parcel reply, int flags) {
				final SharedPreferences preferences = getSharedPreferences(
						OpenUDID_manager.PREFS_NAME, Context.MODE_PRIVATE);

				reply.writeInt(data.readInt()); // Return to the sender the
												// input random number
				reply.writeString(preferences.getString(
						OpenUDID_manager.PREF_KEY, null));
				return true;
			}
		};
	}

	private Timer timer;
	private Handler handler = new Handler();

	@Override
	public void onCreate() {
		super.onCreate();
		if (!Utils.isMiuiV5(this)) {
			MyFloatView myFloatView = MyFloatView.newInstance();
			myFloatView.showFloatView(this);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle bundle = null;
		if (!Utils.isMiuiV5(this)) {
			if (!MyFloatView.newInstance().isAddWindow()) {
				MyFloatView myFloatView = MyFloatView.newInstance();
				myFloatView.showFloatView(this);
				myFloatView.show();
			}
			if (!ConfigurationInfo.getCurrentToken(this).equals("unknown")
					&& BridgeMediation.getfMenu() != null
					&& BridgeMediation.getfMenu().size() != 0) {
				MyFloatView.newInstance().show();
			}

			if (timer == null) {
				timer = new Timer();
				timer.scheduleAtFixedRate(new RefreshTask(), 0, 800);
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//System.out.println("90000000000000");
		UCDialog.newInstance(this).removeUserCenter(this);
		/*if(UCDialog.mUserCenter!=null){
			UCDialog.mUserCenter.removeUserCenter(this);
		}*/
		if (!Utils.isMiuiV5(this)) {
			MyFloatView myFloatView = MyFloatView.newInstance();
			myFloatView.removeFloatView(this);
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
		} else {
			DialogUtils.removeMiuiFloatView();
		}
	}

	private class RefreshTask extends TimerTask {

		@Override
		public void run() {
			final MyFloatView myFloatView = MyFloatView.newInstance();
			boolean current = APKUtils
					.currentIsMyApplication(getApplicationContext());
			if (current && myFloatView.isShow()) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						myFloatView.showNoSetShow();
					}
				});
			} else if (current == false && myFloatView.isShow()) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						myFloatView.hideNoSetShow();
					}
				});
			}
		}

	}

}
