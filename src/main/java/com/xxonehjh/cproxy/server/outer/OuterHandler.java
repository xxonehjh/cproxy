package com.xxonehjh.cproxy.server.outer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.protocol.IMsg;
import com.xxonehjh.cproxy.protocol.MsgProxyData;
import com.xxonehjh.cproxy.server.ServerContext;
import com.xxonehjh.cproxy.util.ChannelUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@io.netty.channel.ChannelHandler.Sharable
public class OuterHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LogManager.getLogger(OuterHandler.class);
	private ServerContext serverContext;
	private Channel innerChannel;
	private int port;

	public OuterHandler(ServerContext serverContext, Channel innerChannel, int port) {
		this.serverContext = serverContext;
		this.innerChannel = innerChannel;
		this.port = port;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		if (innerChannel.isActive()) {
			serverContext.getOuterChannelManage().reg(ctx.channel(), port);
		} else {
			logger.error("服务不可用,端口{}", port);
			ChannelUtils.closeOnFlush(ctx.channel());
		}
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg) {
		final Channel currentChannel = ctx.channel();
		if (!innerChannel.isActive()) {
			logger.error("内部服务不可用,端口:{},外部请求:{}", port,currentChannel);
			ChannelUtils.closeOnFlush(currentChannel);
		} else {
			ByteBuf m = (ByteBuf) msg;
			byte[] datas = new byte[m.readableBytes()];
			m.readBytes(datas);
			logger.info("读取外部通道{}:{}", currentChannel, datas.length);
			IMsg obj = new MsgProxyData(currentChannel.attr(Constants.ATTR_KEY_ID).get(), datas);
			innerChannel.writeAndFlush(obj).addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) {
					if (future.isSuccess()) {
						currentChannel.read();
					} else {
						logger.error("写入失败,端口:{},ex:({})", port, future.cause());
						ChannelUtils.closeOnFlush(innerChannel);
						ChannelUtils.closeOnFlush(currentChannel);
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