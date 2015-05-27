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

import mm.purchasesdk.OnPurchaseListener;
import mm.purchasesdk.Purchase;

public class GameProxyImpl extends GameProxy{

	public static Purchase purchase;
    private IAPListener mListener;
    public PayCallBack payCallBack;
	private static final String APPID = "${APPID}";
	private static final String APPKEY = "${APPKEY}";

    @Override
    public boolean supportLogin() {
        return false;
    }

    @Override
    public boolean supportCommunity() {
        return false;
    }

    @Override
    public boolean supportPay() {
        return true;
    }

    @Override
    public void applicationInit(Activity activity) {
        IAPHandler iapHandler = new IAPHandler();
        mListener = new IAPListener(this, iapHandler);
        purchase = Purchase.getInstance();

        try {
            purchase.setAppInfo(APPID, APPKEY);

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        try {
            purchase.init(activity, mListener);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        this.payCallBack = payCallBack;
        purchase.order(activity, ID, 1, name, false, mListener, callBackInfo);
    }
}
