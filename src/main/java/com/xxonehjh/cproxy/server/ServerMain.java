package com.xxonehjh.cproxy.server;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.server.inner.InnerServer;
import com.xxonehjh.cproxy.server.outer.OuterServer;

public class ServerMain {

	public static void main(String[] args) {
		String config = args.length > 0 && args[0].endsWith(".properties") ? args[0] : Constants.SERVER_CONFIG;
		final ServerContext context = new ServerContext(config);

		new Thread() {
			public void run() {
				new InnerServer().start(context);
			}
		}.start();

		new OuterServer().start(context);

	}

}
