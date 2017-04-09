package com.xxonehjh.cproxy.client;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.client.core.Client;

public class ClientMain {

	public static void main(String[] args) throws Exception {
		ClientContext context = new ClientContext(Constants.CLINET_CONFIG);
		new Client().start(context);
	}

}
