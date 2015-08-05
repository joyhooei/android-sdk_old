package org.yunyue;

import java.util.UUID;
import java.net.URLEncoder;
import java.net.URLDecoder;
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

import android.text.TextUtils;
import com.iapppay.interfaces.callback.IPayResultCallback;
import com.iapppay.sdk.main.IAppPay;
import com.iapppay.ui.widget.ProgressDialog;
import com.iapppay.utils.RSAHelper;

public class GameProxyImpl extends GameProxy{
    private String appId = "${APPID}";
    private String cppKey = "${CPPKEY}";
    private String publicKey = "${PUBLICKEY}";

    public boolean supportLogin() {
        return false;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    public void applicationInit(final Activity activity) {
		IAppPay.init(activity, IAppPay.LANDSCAPE, appId);
    }

	/**
	 * 获取收银台参数
	 */
	private String genUrl( String appuserid, String cpprivateinfo, int waresid, double price, String cporderid) {
		String json = "";

		JSONObject obj = new JSONObject();
		try {
			obj.put("appid", appId);
			obj.put("waresid", waresid);
			obj.put("cporderid", cporderid);
			obj.put("appuserid", appuserid);
			obj.put("price", price);//单位 元
			obj.put("currency", "RMB");//如果做服务器下单 该字段必填
			obj.put("waresname", "客户端传入名称");//开放价格名称(用户可自定义，如果不传以后台配置为准)

			/*CP私有信息，选填*/
			String cpprivateinfo0 = cpprivateinfo;
			if(!TextUtils.isEmpty(cpprivateinfo0)){
				obj.put("cpprivateinfo", cpprivateinfo0);
			}

			json = obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String sign = "";
		try {
			sign = RSAHelper.signForPKCS1(json, cppKey);
		} catch (Exception e) {
		}
		
		return  "transdata=" + URLEncoder.encode(json) + "&sign=" + URLEncoder.encode(sign) + "&signtype=" + "RSA";
	}

    public void pay(final Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        String appuserid = "";
        try {
            String entity_id = roleInfo.getString("id");
            String server_id = roleInfo.getString("serverID");
            appuserid = entity_id + "#" + server_id;
        } catch (JSONException e) {
            Log.e("sdk", "roleInfo parse failed, ignore");
        }

        String param = genUrl(appuserid, callBackInfo , Integer.parseInt( ID ), price , orderID);
        Log.e( "moli", "param : " + param );

        IAppPay.startPay(activity , param, new IPayResultCallback() {
			@Override
			public void onPayResult(int resultCode, String signvalue, String resultInfo) {
				// TODO Auto-generated method stub
				switch (resultCode) {
				case IAppPay.PAY_SUCCESS:
					if( dealPaySuccess(signvalue) ) {
                        payCallBack.onSuccess(null);
                    } else {
                        payCallBack.onFail(null);
                    }
					break;
				case IAppPay.PAY_ING:
					Toast.makeText(activity, "成功下单", Toast.LENGTH_LONG).show();
					break;
				default:
                    Log.e("failure pay", "failure pay, callback cp errorinfo : " + resultCode + "," + resultInfo);
                    Toast.makeText(activity, "payfail:["+ "resultCode:"+resultCode + "," + (TextUtils.isEmpty(resultInfo) ? "unkown error" : resultInfo) + "]", Toast.LENGTH_LONG).show();
                    payCallBack.onFail(null);
					break;
				}
				Log.d("GoodsActivity", "requestCode:"+resultCode + ",signvalue:" + signvalue + ",resultInfo:"+resultInfo);
			}
			
			
			/*4.支付成功。
			 *  需要对应答返回的签名做验证，只有在签名验证通过的情况下，才是真正的支付成功
			 * 
			 * */
			private boolean dealPaySuccess(String signValue) {
				Log.i("signValue", "sign = " + signValue);
				if (TextUtils.isEmpty(signValue)) {
					/**
					 *  没有签名值
					 */
					Log.e("pay error", "pay success,but it's signValue is null");
					Toast.makeText(activity, "pay success, but sign value is null", Toast.LENGTH_LONG).show();
					return false;
				}

				boolean isvalid = false;
				try {
					isvalid = signCpPaySuccessInfo(signValue);
				} catch (Exception e) {
					isvalid = false;
				}
				if (isvalid) {
					Toast.makeText(activity, "pay success", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(activity, "pay success, sign error", Toast.LENGTH_LONG).show();
				}

                return isvalid;
			}

			/**
			 * valid cp callback sign
			 * @param signValue
			 * @return
			 * @throws Exception
			 * 
			 * transdata={"cporderid":"1","transid":"2","appid":"3","waresid":31,
			 * "feetype":4,"money":5,"count":6,"result":0,"transtype":0,
			 * "transtime":"2012-12-12 12:11:10","cpprivate":"7",
			 * "paytype":1}&sign=xxxxxx&signtype=RSA
			 */
			private boolean signCpPaySuccessInfo(String signValue) throws Exception {
				int transdataLast = signValue.indexOf("&sign=");
				String transdata = URLDecoder.decode(signValue.substring("transdata=".length(), transdataLast));
				
				int signLast = signValue.indexOf("&signtype=");
				String sign = URLDecoder.decode(signValue.substring(transdataLast+"&sign=".length(),signLast));
				
				String signtype = signValue.substring(signLast+"&signtype=".length());
				
				if (signtype.equals("RSA") && RSAHelper.verify(transdata, publicKey, sign)) {
					return true;
				}else{
					Log.e("wrong type", "wrong type ");
				}
				return false;
			}
		});
    }
}
