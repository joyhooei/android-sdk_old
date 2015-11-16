package org.weilan;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import com.android.huawei.pay.plugin.IHuaweiPay;
import com.android.huawei.pay.plugin.IPayHandler;
import com.android.huawei.pay.plugin.MobileSecurePayHelper;
import com.android.huawei.pay.util.HuaweiPayUtil;
import com.android.huawei.pay.util.Rsa;
import com.huawei.gamebox.buoy.sdk.util.DebugConfig;

/**
 * 公共类，提供加载插件和支付接口
 * 
 * @author h00193325
 * 
 */
public class GameBoxUtil
{
    
    // 日志标签
    public static final String TAG = GameBoxUtil.class.getSimpleName();
    
    public static void pay(
        final Activity activity,
        final String price,
        final String productName,
        final String productDesc,
        final String requestId,
        final IPayHandler handler)
    {
        
        Map<String, String> params = new HashMap<String, String>();
        // 必填字段，不能为null或者""，请填写从联盟获取的支付ID
        params.put(GlobalParam.PayParams.USER_ID, GlobalParam.PAY_ID);
        // 必填字段，不能为null或者""，请填写从联盟获取的应用ID
        params.put(GlobalParam.PayParams.APPLICATION_ID, GlobalParam.APP_ID);
        // 必填字段，不能为null或者""，单位是元，精确到小数点后两位，如1.00
        params.put(GlobalParam.PayParams.AMOUNT, price);
        // 必填字段，不能为null或者""，道具名称
        params.put(GlobalParam.PayParams.PRODUCT_NAME, productName);
        // 必填字段，不能为null或者""，道具描述
        params.put(GlobalParam.PayParams.PRODUCT_DESC, productDesc);
        // 必填字段，不能为null或者""，最长30字节，不能重复，否则订单会失败
        params.put(GlobalParam.PayParams.REQUEST_ID, requestId);
        
        String noSign = HuaweiPayUtil.getSignData(params);
        DebugConfig.d("startPay", "签名参数noSign：" + noSign);
        
        // CP必须把参数传递到服务端，在服务端进行签名，然后把sign传递下来使用；服务端签名的代码和客户端一致
        String sign = Rsa.sign(noSign, GlobalParam.PAY_RSA_PRIVATE);
        DebugConfig.d("startPay", "签名： " + sign);
        
        Map<String, Object> payInfo = new HashMap<String, Object>();
        // 必填字段，不能为null或者""
        payInfo.put(GlobalParam.PayParams.AMOUNT, price);
        // 必填字段，不能为null或者""
        payInfo.put(GlobalParam.PayParams.PRODUCT_NAME, productName);
        // 必填字段，不能为null或者""
        payInfo.put(GlobalParam.PayParams.REQUEST_ID, requestId);
        // 必填字段，不能为null或者""
        payInfo.put(GlobalParam.PayParams.PRODUCT_DESC, productDesc);
        // 必填字段，不能为null或者""，请填写自己的公司名称
        payInfo.put(GlobalParam.PayParams.USER_NAME, "深圳市微蓝互动有限公司");
        // 必填字段，不能为null或者""
        payInfo.put(GlobalParam.PayParams.APPLICATION_ID, GlobalParam.APP_ID);
        // 必填字段，不能为null或者""
        payInfo.put(GlobalParam.PayParams.USER_ID, GlobalParam.PAY_ID);
        // 必填字段，不能为null或者""
        payInfo.put(GlobalParam.PayParams.SIGN, sign);
        
        // 必填字段，不能为null或者""，此处写死X6
        payInfo.put(GlobalParam.PayParams.SERVICE_CATALOG, "X6");
        
        // 调试期可打开日志，发布时注释掉
        payInfo.put(GlobalParam.PayParams.SHOW_LOG, true);
        
        // 设置支付界面横竖屏，默认竖屏
        payInfo.put(GlobalParam.PayParams.SCREENT_ORIENT, GlobalParam.PAY_ORI);
        
        DebugConfig.d("startPay", "支付请求参数 : " + payInfo.toString());
        
        // 开始支付
        IHuaweiPay payHelper = new MobileSecurePayHelper();
        payHelper.startPay(activity, payInfo, handler);
    }
    
    /**
     * 支付方法，实现参数签名与调起支付服务
     * 
     * @param activity
     * @param price
     * @param productName
     * @param productDesc
     * @param requestId
     * @param handler
     */
    public static void startPay(
        final Activity activity,
        final String price,
        final String productName,
        final String productDesc,
        final String requestId,
        final IPayHandler handler)
    {
        pay( activity, price, productName, productDesc, requestId, handler );
    }
}
