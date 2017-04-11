package com.xxonehjh.cproxy.server.outer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.util.ChannelUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@io.netty.channel.ChannelHandler.Sharable
public class OuterHandlerForClose extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LogManager.getLogger(OuterHandlerForClose.class);
	private int port;

	public OuterHandlerForClose(int port) {
		this.port = port;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.error("内部服务不可用,端口:{},外部通道:{}", port, ctx.channel());
		ChannelUtils.closeOnFlush(ctx.channel());
	}
}
