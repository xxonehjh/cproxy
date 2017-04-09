package com.xxonehjh.cproxy.server;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.server.inner.InnerServer;
import com.xxonehjh.cproxy.server.outer.OuterServer;

public class ServerMain {

	public static void main(String[] args) {

		final ServerContext context = new ServerContext(Constants.SERVER_CONFIG);

		new Thread() {
			public void run() {
				new InnerServer().start(context);
			}
		}.start();

		new OuterServer().start(context);

	}

}
