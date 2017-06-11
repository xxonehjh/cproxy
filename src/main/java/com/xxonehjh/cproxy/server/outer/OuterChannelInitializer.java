package com.xxonehjh.cproxy.server.outer;

import com.xxonehjh.cproxy.protocol.util.DecoderForBytes;
import com.xxonehjh.cproxy.protocol.util.EncoderForBytes;
import com.xxonehjh.cproxy.server.ServerContext;
import com.xxonehjh.cproxy.util.LoggingHandlerUtil;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class OuterChannelInitializer extends ChannelInitializer<SocketChannel> {

	private ServerContext context;

	public OuterChannelInitializer(ServerContext serverContext) {
		this.context = serverContext;
	}

	@Override
	public void initChannel(SocketChannel ch) {
		int port = ch.localAddress().getPort();
		ChannelPipeline pipe = ch.pipeline();
		if (context.getConfig().isDebug()) {
			pipe.addLast(LoggingHandlerUtil.getInstance());
		}
		pipe.addLast(EncoderForBytes.getInstance(), DecoderForBytes.getInstance(), context.getInnerChannelManage(port).getOuterChannelHandler());
	}

}