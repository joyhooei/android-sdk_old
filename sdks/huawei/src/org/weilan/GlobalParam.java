package org.weilan;


/**
 * 存放联盟参数和插件对象
 * 
 * @author h00193325
 * 
 */
public class GlobalParam
{
    /**
     * 联盟为应用分配的应用ID
     */
    public static final String APP_ID = "${APPID}";
    
    /**
     * 浮标密钥，CP必须存储在服务端，然后通过安全网络（如https）获取下来，存储到内存中，否则存在私钥泄露风险
     */
    public static String BUO_SECRET = "";
    
    /**
     * 支付ID
     */
    public static final String PAY_ID = "${PAYID}";
    
    /**
     * 支付私钥，CP必须存储在服务端，然后通过安全网络（如https）获取下来，存储到内存中，否则存在私钥泄露风险
     */
    public static String PAY_RSA_PRIVATE = "";
    
    /**
     * 支付公钥
     */
    public static final String PAY_RSA_PUBLIC = "${PUBLIC_KEY}";
    
    /*
     * 支付页面横竖屏参数：1表示竖屏，2表示横屏，默认竖屏
     */
    public static final int PAY_ORI = 1;
    

    
    /**
     * Demo校验accessToken的地址，此地址为华为服务端Demo的地址，CP不能使用，需要自己实现服务端代码并部署，然后修改地址为自己的URL
     */
     public static final String VALID_TOKEN_ADDR = "https://ip:port/HuaweiServerDemo/validtoken";
    
    /**
     * 生成签名时需要使用RSA的私钥，安全考虑，必须放到服务端，通过此接口使用安全通道获取
     */
     public static final String GET_PAY_PRIVATE_KEY = "${GET_PAY_PRIVATE_KEY}";
    
    /**
     * 调用浮标时需要使用浮标的私钥，安全考虑，必须放到服务端，通过此接口使用安全通道获取
     */
     public static final String GET_BUOY_PRIVATE_KEY = "${GET_BUOY_PRIVATE_KEY}";
    
    public interface PayParams
    {
        public static final String USER_ID = "userID";
        
        public static final String APPLICATION_ID = "applicationID";
        
        public static final String AMOUNT = "amount";
        
        public static final String PRODUCT_NAME = "productName";
        
        public static final String PRODUCT_DESC = "productDesc";
        
        public static final String REQUEST_ID = "requestId";
        
        public static final String USER_NAME = "userName";
        
        public static final String SIGN = "sign";
        
        public static final String NOTIFY_URL = "notifyUrl";
        
        public static final String SERVICE_CATALOG = "serviceCatalog";
        
        public static final String SHOW_LOG = "showLog";
        
        public static final String SCREENT_ORIENT = "screentOrient";
        
        public static final String SDK_CHANNEL = "sdkChannel";
        
        public static final String URL_VER = "urlver";
    }
    
}
