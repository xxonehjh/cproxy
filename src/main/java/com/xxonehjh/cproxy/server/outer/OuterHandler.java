package com.xxonehjh.cproxy.server.outer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.protocol.IMsg;
import com.xxonehjh.cproxy.protocol.MsgProxyData;
import com.xxonehjh.cproxy.server.ServerContext;
import com.xxonehjh.cproxy.util.ChannelUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class OuterHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LogManager.getLogger(OuterHandler.class);
	private ServerContext serverContext;
	private Channel innerChannel;
	private Channel outerChannel;
	private ChannelFutureListener listener = new ChannelFutureListener() {
		@Override
		public void operationComplete(ChannelFuture future) {
			if (future.isSuccess()) {
				outerChannel.read();
			} else {
				logger.error("写入失败,端口:{},外部请求:{},ex:({})", port, outerChannel, future.cause());
				close();
			}
		}
	};

	private int port;

	public OuterHandler(ServerContext serverContext, Channel innerChannel, int port) {
		this.serverContext = serverContext;
		this.innerChannel = innerChannel;
		this.port = port;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		outerChannel = ctx.channel();
		if (innerChannel.isActive()) {
			serverContext.getOuterChannelManage().reg(ctx.channel(), port);
			ctx.channel().read();
			
			
			IMsg obj = new MsgProxyData(outerChannel.attr(Constants.ATTR_KEY_ID).get(), Constants.EMPTY_BYTES);
			innerChannel.writeAndFlush(obj).addListener(listener);
		} else {
			logger.error("服务不可用,端口:{},外部请求:{}", port, ctx.channel());
			close();
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (!innerChannel.isActive()) {
			logger.error("内部服务不可用,端口:{},外部请求:{}", port, outerChannel);
			close();
		} else {
			byte[] datas = (byte[])msg; // 这里如果没有设置编解码-直接读ByteBuf对象的值，会导致内存泄漏 【单个外部请求过来的数据会一直堆积，如果是用同一个channl上传文件，会导致内存不断加大】
			logger.info("读取外部通道{}:数据长度:{}", outerChannel, datas.length);
			IMsg obj = new MsgProxyData(outerChannel.attr(Constants.ATTR_KEY_ID).get(), datas);
			innerChannel.writeAndFlush(obj).addListener(listener);
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

	private void close() {
		ChannelUtils.closeOnFlush(innerChannel);
		if (null != outerChannel) {
			ChannelUtils.closeOnFlush(outerChannel);
		}
	}

}