//package com.paojiao.sdk.utils;
//
//import android.content.Context;
//import android.os.Handler;
//import android.util.Log;
//import cn.paypalm.pppayment.PPInterface;
//import cn.paypalm.pppayment.global.ResponseDataToMerchant;
//
///**
// * 掌上支付功能类
// * 
// * @author paojiao
// * 
// */
//public class PPPayUtil implements ResponseDataToMerchant {
//	private static final String TAG = "Merchant_PPPaymentActivity";
//
//	private String mCurrSelectPayMethod = "";
//	private static final String MERCHANT_ID = "1000002153";// 默认商户ID
////	private String key="FzXlpODHLVMNZtvwTYExzr6kq76JCw37";
//	private String strMerId = "";// 商户ID
//	private String strOrderDesc = "彩票充值";// 订单描述
//	private String strPhone = "";// 手机号
//	private String strUsername = "lisan";// 用户名
//	private String strAmt = "";// 金额
//	private String strOrders = "";// 订单号
//	private String strProductId = "100001"; // 产品ID,移动支付的专用ProductId
////	private String strNotifyUrl = "http://www.test.com";
//	private String strNotifyUrl = "http://test.sdk.paojiao.cn/wap/paypalm/sdkNotifyHandler.do";
//	private String strReserve = "http://124.193.184.93:8060"; // 保留字段
//	Context context;
//	/**
//	 * 测试服务器IP组
//	 */
//
//	Handler handler = new Handler();
//
//	public PPPayUtil(Context c) {
//		this.context = c;
//		PPInterface.startSafe(context, MERCHANT_ID);
//
//	}
//
//	public void payPrepare(String orderSubjectStr, String orderNumberStr,
//			String orderAmountStr) {
//		strOrderDesc = orderSubjectStr;
//
//		int price = Integer.valueOf(orderAmountStr) * 100;
//		strOrders = orderNumberStr;
//		strAmt = String.valueOf(price);// 订单金额 单位：分
//		strMerId = MERCHANT_ID;// 商户使用PP支付开实际的商户编码
//		new Thread() {
//			@Override
//			public void run() {
//				handler.post(new Runnable() {
//					public void run() {
//						startPP(context, strOrders, strPhone, strMerId,
//								strUsername, strAmt, strProductId,
//								strNotifyUrl, strOrderDesc, strReserve,
//								PPPayUtil.this);
//					}
//				});
//			}
//		}.start();
//
//	}
//
//	/**
//	 * 
//	 * @param order
//	 *            订单号 注:非空
//	 * @param phone
//	 *            手机号 注:非空
//	 * @param merchantId
//	 *            商户编号 注:非空
//	 * @param merchantUserId
//	 *            商户用户ID 注:非空
//	 * @param payAmt
//	 *            支付金额 注:单位是 分 , 非空
//	 * @param productId
//	 *            产品ID 注: 固定传 100001
//	 * @param payMethod
//	 *            支付方式 CREDIT_PAY:无卡支付; BANKCARD_PAY: 银行卡支付; PHONEACCOUNT_PAY:
//	 *            话费支付; RECHARGECARD_PAY: 充值卡、点卡支付 。 注: 可为空，为空为当前商户编码下配置的所有支付方式
//	 * @param notifyUrl
//	 *            回调接口 注:非空 特别注意!这个必须填写且正确，这个是用于异步通知回调你们商户的，如果填写错误没有回调成功就是你们的问题!
//	 * @param orderDesc
//	 *            订单描述(传入商品名称) 注:非空
//	 * @param reserve
//	 *            保留字段
//	 * @param responseDataToMerchant
//	 *            回调接口(用于向商户返回支付结果) 注:非空
//	 */
//
//	private void startPP(Context context, String order, String phone,
//			String merchantId, String merchantUserId, String payAmt,
//			String productId, String notifyUrl, String orderDesc,
//			String reserve, ResponseDataToMerchant responseDataToMerchant) {
//
//		// 调用PP支付
//		PPInterface.startPPPayment(context, order, phone, merchantId,
//				merchantUserId, payAmt, productId, mCurrSelectPayMethod,
//				notifyUrl, orderDesc, reserve, responseDataToMerchant);
//		Log.d(TAG, "01--" + order + "02--" + phone + "03--" + merchantId
//				+ "04--" + merchantUserId + "05--" + payAmt + "06--"
//				+ productId + "07--" + mCurrSelectPayMethod + "08--"
//				+ notifyUrl + "09--" + orderDesc + "10--" + reserve);
//
//	}
//
//	public void responseData(int arg0, String arg1) {
//		switch (arg0) {
//		case 0:
//			break;
//		case ResponseDataToMerchant.RESULT_PAYCODE_OK:
//			Log.d(TAG, "支付成功");
//			// Toast.makeText(this, "用户支付成功", Toast.LENGTH_LONG).show();
//			break;
//		case ResponseDataToMerchant.RESULT_PAYCODE_ERROR:
//			Log.d(TAG, "支付失败");
//			// Toast.makeText(this, "用户支付失败", Toast.LENGTH_LONG).show();
//			break;
//		}
//	}
//
//}