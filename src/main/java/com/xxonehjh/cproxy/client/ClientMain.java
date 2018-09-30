package com.xxonehjh.cproxy.client;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.client.core.Client;

public class ClientMain {

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			ClientContext context = new ClientContext(Constants.CLINET_CONFIG);
			new Client().start(context);
		} else {
			for (String item : args) {
				if (item.endsWith(".properties")) {
					final String config = item;
					new Thread() {
						public void run() {
							ClientContext context = new ClientContext(config);
							try {
								new Client().start(context);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}.start();
				}
			}
		}
	}

}
