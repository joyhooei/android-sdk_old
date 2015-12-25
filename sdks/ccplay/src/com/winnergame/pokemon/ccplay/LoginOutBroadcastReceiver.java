package com.winnergame.pokemon.ccplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

import org.weilan.sdk;

/**
 * 登出广播
 * 
 * @author 巍
 * 
 */
public class LoginOutBroadcastReceiver extends BroadcastReceiver {

    private final static int CB_LOGOUT_SUCCESS  = 2;
	public static final String LOGINOUT_ACTION = "CCPAY_LOGINOUT_ACTION";

	@Override
	public void onReceive(Context context, Intent intent) {
        sdk.platformCallback(CB_LOGOUT_SUCCESS, "");
	}
}
