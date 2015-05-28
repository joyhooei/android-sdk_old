package com.paojiao.sdk.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	public static String MD5(String user,String token,String psw){
		
		return Md5(Md5(user) + Md5(token) + psw);
	}
	public static String MD52(String user,String token){
		
		return Md5(Md5(user) + Md5(token));
	}
	public static String TwiceMD5(String plainText){
		
		return Md5(Md5(plainText));
	}
	
	public static String Md5(String plainText) {
		StringBuffer buf = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}


		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

}
