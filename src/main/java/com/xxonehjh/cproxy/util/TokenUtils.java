package com.xxonehjh.cproxy.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.xxonehjh.cproxy.Constants;

public class TokenUtils {

	public static String encrypt(String token) {
		return encrypt(token, new Date());
	}

	public static String encrypt(String token, Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
		return MD5.md5(token + df.format(date));
	}

	public static boolean validate(String tokenEncryption, String token) {
		if (encrypt(token, new Date()).equals(tokenEncryption)) { // 当前时间
			return true;
		}
		if (encrypt(token, new Date(System.currentTimeMillis() - Constants.ONE_MINUTE)).equals(tokenEncryption)) {// 一分钟之前
			return true;
		}
		return false;
	}

}
