package com.xxonehjh.cproxy.client;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.client.core.Client;

public class ClientMain {

	public static void main(String[] args) throws Exception {
		String config = args.length>0?args[0]:Constants.CLINET_CONFIG;
		ClientContext context = new ClientContext(config);
		new Client().start(context);
	}

}
