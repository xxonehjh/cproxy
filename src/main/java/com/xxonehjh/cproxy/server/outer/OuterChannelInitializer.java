package com.xxonehjh.cproxy.server.outer;

import com.xxonehjh.cproxy.server.ServerContext;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class OuterChannelInitializer extends ChannelInitializer<SocketChannel> {

	private ServerContext serverContext;

	public OuterChannelInitializer(ServerContext serverContext) {
		this.serverContext = serverContext;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline pipe = ch.pipeline();
		if (serverContext.getConfig().isDebug()) {
			pipe.addLast(new LoggingHandler(LogLevel.INFO));
		}
		int port = ch.localAddress().getPort();
		pipe.addLast(serverContext.getInnerChannelManage(port).getOuterChannelHandler());
	}
	
}