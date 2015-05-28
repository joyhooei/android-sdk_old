package com.paojiao.sdk.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Message;

/**
 * 安卓端请求服务器端参数；
 * @author Van
 * @version 1.0
 */
public class HttpPost {
	
	
	private static final String KEY="mduSfA5TgCe8xxpAqxsIFpPeWWRqCVH4";
	
	//线程池
	private static ThreadPoolExecutor postPool = new ThreadPoolExecutor(5,
            15,
            10 * 60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
	
	/**
	 * 对现有的参数列表进行排序验签
	 * @param params
	 * @return
	 */
	private String sign(Map<String, String> params) {
		StringBuffer sb = new StringBuffer();
		List<String> keys = new ArrayList<String>();
		keys.addAll(params.keySet());
		
		Collections.sort(keys);
		for (String key:keys) {
			sb.append(key)
			.append("=")
			.append(params.get(key));
		}
		sb.append(KEY);
		String md5 = MD5Util.Md5(sb.toString());
		return md5;

	}
	
	
	
	/**
	 * 将Map中的数据组装成一个字符，用于Post
	 * @param params
	 * @return
	 */
    public String processData(Map<String, String> params) {
        StringBuffer content = new StringBuffer();
        for(String key:params.keySet()){
        	 String value = params.get(key);
        	 
        	 //只要有空白的参数也传过去
        	 if(value==null){
        		 value="";
        	 }
    		 if(!content.equals("")){
    			 content.append("&");
    		 }
    		 content.append(key);
    		 content.append("=");
    		 content.append(value);
        }
        
        return content.toString();
    }

	/**
	 * 提交数据工具，后台线程提交，不返回结果，一般用于统计；
	 * @param url
	 * @param params
	 */
	public void postWithoutResult(String url,Map<String,String> params){
		postPool.execute(new PostTask(url,params));
	}
	
	
	/**
	 * 提交数据工具，后台线程提交，不返回结果，一般用于统计；
	 * @param url
	 * @param params
	 */
	public void postWithoutResult(String url,Map<String,String> params,HttpPostResponse response){
		postPool.execute(new PostTask(url,params,response));
	}
	
	
	/**
	 * 提交数据方法
	 * @param url
	 * @param params
	 */
	private void postData(String url,Map<String,String> params,Handler handler){
		
		HttpURLConnection conn=null;
		//加入签名验证
		params.put("sign", sign(params));
		StringBuffer buffer = new StringBuffer();
		try{
			URL serverUrl = new URL(url);
			conn = (HttpURLConnection) serverUrl.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.connect();
			//将参数处理后写入数据
			conn.getOutputStream().write(processData(params).getBytes("utf-8"));
			InputStream is = conn.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			
			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
			//System.out.println("返回结果为："+buffer.toString());
			
		}catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(conn!=null){
				conn.disconnect();
			}
			handler.sendMessage(handler.obtainMessage(0, buffer.toString()));
		}
	}
		

	
	private class PostTask implements Runnable{
		
		private String url;
		private Map<String,String> params;
		
		private Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what==0){
					if(response!=null){
						response.response((String)msg.obj);
					}
				}
			}
			
		};
		
		public PostTask(String url,Map<String,String> params){
			this.url=url;
			this.params=params;
		}
		
		private HttpPostResponse response;
		
		public PostTask(String url,Map<String,String> params,HttpPostResponse response){
			this.url=url;
			this.params=params;
			this.response=response;
		}

		@Override
		public void run() {
			postData(url,params,handler);
		}
	}
	
	public static interface HttpPostResponse{
		public void response(String msg);
	}

}
