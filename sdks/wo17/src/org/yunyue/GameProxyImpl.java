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

import com.future.playgame.coin.listenter.PlayGCInitSDKListener;
import com.future.playgame.coin.listenter.PlayGCPayListener;
import com.future.playgame.coin.listenter.PlayGCQueryInfoListener;
import com.future.playgame.coin.manager.PlayGCConfigManager;
import com.future.playgame.coin.manager.PlayGCManager;
import com.future.playgame.coin.manager.PlayGCMessageUtils;

public class GameProxyImpl extends GameProxy{

    private static String appid = "${APPID}";
    private static String appkey = "${APPNAME}";

    public boolean supportLogin() {
        return false;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    /**
	 * 初始化SDK
	 */
	private void initSDK(Activity activity) {
		PlayGCManager.instance().initSDK(activity,
				new PlayGCInitSDKListener() {

					@Override
					public void onInitRstCode(int rstCode) {
						if (rstCode == PlayGCMessageUtils.INIT_SDK_SUCCESS) {
							resultText.setText("初始化成功");
						} else if (rstCode == PlayGCMessageUtils.INIT_SDK_FAIL) {
							resultText.setText("初始化失败");
						} else if (rstCode == PlayGCMessageUtils.INIT_SDK_REPEAT) {
							resultText.setText("已初始化成功");
						}
					}
				});

	}

    public void applicationInit(Activity activity) {
        initSDK(activity);
    }

    public void onDestroy(Activity activity) {
        PlayGCManager.instance().closeSDK();
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        PlayGCManager.instance().intentPayActivity(
                new PlayGCPayListener() {
                    @Override
                    public void onPayResult(int resultCode, Object payObj,int type) {
                        Log.i("tag", "code:"+resultCode+" result:"+payObj.toString()+" type:"+type);
                        if (resultCode == PlayGCMessageUtils.PAY_SUCCESS) {
                            payCallBack.onSuccess("");
                        } else {
                            payCallBack.onFail("");
                        }
                    }
                }, PlayGCManager.ACTIVITY_MMUNIPAY_FLAG, orderID, appid, "${APPNAME}",
                    appkey, name, "1", String.valueOf((int)price), ID, "可用于购买道具");
    }
}
