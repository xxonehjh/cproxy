package com.xxonehjh.cproxy;

import io.netty.util.AttributeKey;

public class Constants {

	public static final String UTF8 = "utf-8";

	public static final byte[] EMPTY_BYTES = new byte[0];

	public static final String CLINET_CONFIG = "client.properties";

	public static final String SERVER_CONFIG = "server.properties";

	public static final AttributeKey<Integer> ATTR_KEY_ID = AttributeKey.newInstance("id");

	public static final AttributeKey<Integer> ATTR_KEY_PROXY_PORT = AttributeKey.newInstance("port");

	public static final AttributeKey<Long> ATTR_KEY_TIME = AttributeKey.newInstance("time");

	public static final int ONE_MINUTE = 1000 * 60;

	public static final int CLIENT_CHECK_INTERVAL = 60 * 1000;

	public static final int CLIENT_TIMEOUT = 3 * CLIENT_CHECK_INTERVAL;

}
