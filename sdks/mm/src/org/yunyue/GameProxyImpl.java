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
    private static final String PARTNER = "${PARTNER}";
    private static final String SELLER = "${SELLER}";

	//商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "${RSA_PRIVATE}";
	//支付宝公钥
	public static final String RSA_PUBLIC = "";

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
        if (price >= 30) {
            alipay_pay(activity, ID, name, orderID, price, callBackInfo, roleInfo);
        }
        else {
            purchase.order(activity, ID, 1, name, false, mListener, callBackInfo, "");
        }
    }

    public void alipay_pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        String orderInfo = getOrderInfo(orderID, name, "可用于购买道具", price.toString());
        // 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

        Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(PayDemoActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
    }

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}
    /**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String orderID, String subject, String body, String price) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + orderID + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "${PAY_URL}"
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}
}
