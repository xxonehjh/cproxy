package com.xxonehjh.cproxy.client.target;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.client.ClientContext;
import com.xxonehjh.cproxy.protocol.IMsg;
import com.xxonehjh.cproxy.protocol.MsgProxyData;
import com.xxonehjh.cproxy.util.ChannelUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TargetHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LogManager.getLogger(TargetHandler.class);

	private final ClientContext context;

	public TargetHandler(ClientContext context) {
		this.context = context;
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg) {
		ByteBuf m = (ByteBuf) msg;
		byte[] datas = new byte[m.readableBytes()];
		m.readBytes(datas);
		logger.info("从目标通道读取{}:{}",ctx.channel(),datas.length);
		final IMsg obj = new MsgProxyData(ctx.channel().attr(Constants.ATTR_KEY_ID).get(), datas);
		context.getClientChannelManage().getChannel().writeAndFlush(obj).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (future.isSuccess()) {
					ctx.channel().read();
				} else {
					logger.info("写入数据失败:{}:ex({})", obj, future.cause());
					future.channel().close();
				}
			}
		});
	}

	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		if (ctx.channel().hasAttr(Constants.ATTR_KEY_ID)) {
			logger.info("目标通道失效:{}",ctx.channel());
			context.getTargetChannelManage().remove(ctx.channel().attr(Constants.ATTR_KEY_ID).get());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		ChannelUtils.exceptionCaught(ctx, cause, logger);
	}

}
