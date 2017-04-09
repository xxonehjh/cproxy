package com.xxonehjh.cproxy.server.inner;

import com.xxonehjh.cproxy.protocol.util.Decoder;
import com.xxonehjh.cproxy.protocol.util.Encoder;
import com.xxonehjh.cproxy.server.ServerContext;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class InnerInitializer extends ChannelInitializer<SocketChannel> {

	private ServerContext context;

	public InnerInitializer(ServerContext context) {
		this.context = context;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline pipe = ch.pipeline();
		if (context.getConfig().isDebug()) {
			pipe.addLast(new LoggingHandler(LogLevel.INFO));
		}
		pipe.addLast(new Encoder(), new Decoder(), new InnerHandler(context));
	}
}