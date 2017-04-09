package com.xxonehjh.cproxy.server.outer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.protocol.IMsg;
import com.xxonehjh.cproxy.protocol.MsgProxyData;
import com.xxonehjh.cproxy.server.ServerContext;
import com.xxonehjh.cproxy.server.inner.InnerChannelManage;
import com.xxonehjh.cproxy.util.ChannelUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class OuterHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LogManager.getLogger(OuterHandler.class);
	private ServerContext serverContext;
	private InnerChannelManage innerChannelManage;
	private int port;

	public OuterHandler(ServerContext serverContext, int port) {
		this.serverContext = serverContext;
		this.innerChannelManage = serverContext.getInnerChannelManage(port);
		this.port = port;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		if (innerChannelManage.isEnable()) {
			serverContext.getOuterChannelManage().reg(ctx.channel(), port);
		} else {
			logger.error("服务不可用,端口{}", innerChannelManage.getPort());
			ChannelUtils.closeOnFlush(ctx.channel());
		}
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg) {
		final Channel channel = innerChannelManage.getChannel();
		if (null == channel) {
			logger.error("服务不可用,端口:{}:msg:{}", innerChannelManage.getPort(), msg);
			ChannelUtils.closeOnFlush(ctx.channel());
		} else {
			ByteBuf m = (ByteBuf) msg;
			byte[] datas = new byte[m.readableBytes()];
			m.readBytes(datas);
			logger.info("读取外部通道{}:{}",ctx.channel(),datas.length);
			IMsg obj = new MsgProxyData(ctx.channel().attr(Constants.ATTR_KEY_ID).get(), datas);
			channel.writeAndFlush(obj).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) {
					if (future.isSuccess()) {
						ctx.channel().read();
					} else {
						logger.error("写入失败,端口:{},ex:({})", innerChannelManage.getPort(),future.cause());
						ChannelUtils.closeOnFlush(channel);
						ChannelUtils.closeOnFlush(ctx.channel());
					}
				}
			});
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		serverContext.getOuterChannelManage().remove(ctx.channel());
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		ChannelUtils.exceptionCaught(ctx, cause, logger);
	}

}