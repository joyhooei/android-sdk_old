package org.yunyue;

import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.muzhiwan.sdk.login.MzwApiCallback;
import com.muzhiwan.sdk.login.MzwApiFactory;
import com.muzhiwan.sdk.pay.domain.Order;
import com.muzhiwan.sdk.utils.CallbackCode;

public class GameProxyImpl extends GameProxy{
    private PayCallback payCallBack;

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    public boolean supportLogout() {
        return true;
    }

    public void applicationInit(Activity activity) {
        MzwApiFactory.getInstance().init(activity,
				MzwApiFactory.ORIENTATION_VERTICAL, new MzwApiCallback() {

					@Override
					public void onResult(final int code, Object arg1) {

						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								if (!isFinishing() && pd.isShowing()) {
									pd.dismiss();
								}
								if (code == 1) {
									//Toast.makeText(getApplicationContext(),
									//		"init success", Toast.LENGTH_LONG)
									//		.show();
									/**
									 * 登录
									 */
									//doLogin();
								} else {
									//Toast.makeText(getApplicationContext(),
									//		"init error", Toast.LENGTH_LONG)
									//		.show();
								}
							}
						});
					}
				});
    }

    /**
	 * 登录回调
	 */
	private class LoginCallback implements MzwApiCallback {

		@Override
		public void onResult(final int code, final Object data) {
			Log.i("mzw_net_modify", "data:" + data);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (code == CallbackCode.SUCCESS) {
                        User u = new User();
						userListerner.onLoginSuccess(u, null);
					} else if (code == CallbackCode.ERROR) {
                        userListerner.onLoginFailed("", null);
					} else if (code == CallbackCode.CANCEL) {
                        userListerner.onLoginFailed("", null);
					}

				}
			});

		}

	}

    public void login(Activity activity, Object customParams) {
        MzwApiFactory.getInstance().doLogin(activity, new LoginCallback());
    }

    /**
	 * 支付回调
	 */
	private class PayCallback implements MzwApiCallback {

		@Override
		public void onResult(final int code, final Object data) {
			
			runOnUiThread(new Runnable() {
				@Override
				public void run() {

					if (code == CallbackCode.SUCCESS) {
						payCallBack.onSuccess("");
					} else if (code == CallbackCode.PROCESSING) {
						//Toast.makeText(getApplicationContext(), "processing",
						//		Toast.LENGTH_LONG).show();
					}else if (code == CallbackCode.CANCEL) {
						payCallBack.onFail("");
						//Toast.makeText(getApplicationContext(), "pay cancel",
						//		Toast.LENGTH_LONG).show();
					}
                    else if (code==CallbackCode.FINISH) {
						payCallBack.onFail("");
                        //Toast.makeText(getApplicationContext(), "pay finish",
                        //    Toast.LENGTH_LONG).show();
					}else {
						payCallBack.onFail("");
						//Toast.makeText(getApplicationContext(), "pay error",
						//		Toast.LENGTH_LONG).show();
					}
					
				}
			});
			
		}
	}

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        this.payCallBack = payCallBack;

        Order order = new Order();
		order.setExtern(callBackInfo + "_" + orderID);
		order.setMoney(price);
		order.setProductdesc("");
		order.setProductid(ID);
		order.setProductname(name);
        MzwApiFactory.getInstance().doPay(activity, order, new PayCallback());
    }

    @Override
	public void onDestroy(Activity activity) {
		super.onDestroy(activity);
		/**
		 * 销毁SDK资源
		 */
		MzwApiFactory.getInstance().destroy(activity);
	}

    public void logout(Activity activity,Object customParams) {
        MzwApiFactory.getInstance().doLogout(activity);
        userListerner.onLogout(customParams);
    }
}
