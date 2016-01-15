package org.weilan;

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

import com.jolo.account.Jolo.onAccountResult;
import com.jolo.jolopay.JoloPay.onPayResult;
import com.jolo.sdk.JoloSDK;
import com.joloplay.sdk.demo.R;

public class GameProxyImpl extends GameProxy implements onAccountResult, onPayResult {
	private string userid; // 用户id
	private string session; // 用户登录session
    private Object loginCustomParams;
    private PayCallBack mPayCallBack;

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    public void applicationInit(Activity activity) {
        // SDK初始化
		// 注意：在调用JoloSDK其他接口之前，请调用初始化接口
		// 可以把游戏的gameCode先替换PartnerConfig.CP_GAME_CODE值来检测是否能获取UserId, username,
		// session, accountSign, account
		JoloSDK.initJoloSDK(activity, "${CP_GAME_CODE}");
		//注册 onPay回调和onAccount回调，一般建议CP使用onActivityResult回调，两个回调返回的数据是相同的。
		//两者选一个即可
		JoloSDK.initCallBack(activity, this);
    }

    public void login(Activity activity,Object customParams) {
        // 登录，customParams透传给回调
        loginCustomParams = customParams;
		JoloSDK.login(activity);
    }

    public void logout(Activity activity,Object customParams) {
		// 调用注销接口，会重新进入登录页，进行用户的切换
		JoloSDK.logoff(activity);
        userListerner.onLogout(customParams);
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        mPayCallBack = payCallBack;
        Order or = new Order();
        //注意：参数里，不要出现类似“1元=10000个金币”的字段，因为“=”原因，会导致微信支付校验失败
        or.setAmount("" + (int()(price*100))); // 设置支付金额，单位分
        or.setGameCode("${CP_GAME_CODE}"); // 设置游戏唯一ID,由Jolo提供
        or.setGameName("${APPNAME}"); // 设置游戏名称
        or.setGameOrderid("" + System.currentTimeMillis()); // 设置游戏订单号
        or.setNotifyUrl("${PAY_URL}"); // 设置支付通知
        or.setProductDes(name); // 设置产品描述
        or.setProductID(ID); // 设置产品ID
        or.setProductName(name); // 设置产品名称
        or.setSession(session); // 设置用户session
        or.setUsercode(userid); // 设置用户ID
        order = or.toJsonOrder(); // 生成Json字符串订单
        String sign = RsaSign.sign(order, "${CP_PRIVATE_KEY_PKCS8}"); // 签名
        JoloSDK.startPay(MainActivity.this, order, sign); // 启动支付
    }

    public void onDestroy(Activity activity) {
		// 销毁SDK所使用的资源
		JoloSDK.releaseJoloSDK();
		super.onDestroy(activity);
	}

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK || data == null){
			if(requestCode == JoloSDK.PAY_REQUESTCODE){
                mPayCallBack.onFail("支付取消");
			}
			return;
		}

		switch (requestCode) {
		case JoloSDK.ACCOUNT_REQUESTCODE: {
			// 用户账号名
			//userName = data.getStringExtra(JoloSDK.USER_NAME);
			// 用户账号ID
			userid = data.getStringExtra(JoloSDK.USER_ID);
			// 账号的session，支付时使用
			session = data.getStringExtra(JoloSDK.USER_SESSION);
			// 用户帐号信息签名(聚乐公钥验签)，密文，CP对该密文用公钥进行校验
			String accountSign = data.getStringExtra(JoloSDK.ACCOUNT_SIGN);
			// 用户帐号信息，明文，用户加密的字符串
			String account = data.getStringExtra(JoloSDK.ACCOUNT);

            User usr   = new User();
            usr.token  = accountSign;
            usr.userId = account;
            userListerner.onLoginSuccess(usr, loginCustomParams);
		}
			break;
		case JoloSDK.PAY_REQUESTCODE: {
            mPayCallBack.onSuccess("支付成功");
		}
			break;
		default:
			break;
		}
	};


}

