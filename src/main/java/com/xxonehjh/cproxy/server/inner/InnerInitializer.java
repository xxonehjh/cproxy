package com.xxonehjh.cproxy.server.inner;

import com.xxonehjh.cproxy.protocol.util.Decoder;
import com.xxonehjh.cproxy.protocol.util.Encoder;
import com.xxonehjh.cproxy.server.ServerContext;
import com.xxonehjh.cproxy.util.LoggingHandlerUtil;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class InnerInitializer extends ChannelInitializer<SocketChannel> {

	private ServerContext context;

	public InnerInitializer(ServerContext context) {
		this.context = context;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline pipe = ch.pipeline();
		if(context.getConfig().isDebug()){
			pipe.addLast(LoggingHandlerUtil.getInstance());
		}
		pipe.addLast(Encoder.getInstance(), Decoder.getInstance(), context.getInnerHandler());
	}
}