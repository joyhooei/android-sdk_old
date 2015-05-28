package com.paojiao.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.DownloadListener;
import android.widget.Toast;


/**
 * 
* @ClassName: MyWebViewDownLoadListener 
* @Description: TODO(webView下载的类 ) 使用方法为     webview.setDownloadListener(new MyWebViewDownLoadListener());
* @author zgt
* @date 2014-8-29 上午11:49:22 
*
 */
public class MyWebViewDownLoadListener implements DownloadListener {
	
	private Context context;
	
	/**
	 * 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context
	 */
	public MyWebViewDownLoadListener( Context context){
		this.context=context;
	}

     @Override
     public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                 long contentLength) {
    	 if(TextUtils.isEmpty(url)){
    		 Toast.makeText(context, "该应无法下载，链接地址为空", Toast.LENGTH_SHORT).show();
    	 }else {
    		Intent intent = new Intent();        
	        intent.setAction(Intent.ACTION_VIEW);    
	        Uri content_url = Uri.parse(url);   
	        intent.setData(content_url);  
	        context.startActivity(intent);
		}
    	 
     }

 }
	
	
	