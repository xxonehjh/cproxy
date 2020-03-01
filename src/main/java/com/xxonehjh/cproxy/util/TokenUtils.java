package com.xxonehjh.cproxy.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TokenUtils {

	public static String encrypt(String token) {
		return encrypt(token, new Date());
	}

	public static String encrypt(String token, Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
		return MD5.md5(token + df.format(date));
	}

	public static boolean validate(String tokenEncryption, String token) {
		if (encrypt(token, new Date()).equals(tokenEncryption)) { // 当前时间
			return true;
		}
		return false;
	}

}
