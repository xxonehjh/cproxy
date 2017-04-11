package com.xxonehjh.cproxy.server.inner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.protocol.IMsg;
import com.xxonehjh.cproxy.protocol.MsgConnect;
import com.xxonehjh.cproxy.protocol.MsgPingReq;
import com.xxonehjh.cproxy.protocol.MsgPingResp;
import com.xxonehjh.cproxy.protocol.MsgProxy;
import com.xxonehjh.cproxy.protocol.MsgProxyClose;
import com.xxonehjh.cproxy.protocol.MsgProxyData;
import com.xxonehjh.cproxy.server.ServerContext;
import com.xxonehjh.cproxy.util.ChannelUtils;
import com.xxonehjh.cproxy.util.TokenUtils;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InnerHandler extends ChannelInboundHandlerAdapter {

	private static final Logger logger = LogManager.getLogger(InnerHandler.class);

	private ServerContext context;

	public InnerHandler(ServerContext context) {
		this.context = context;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		ChannelUtils.updateActiveTime(ctx.channel());
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object obj) {
		IMsg msg = (IMsg) obj;
		if (msg instanceof MsgPingReq) {
			ChannelUtils.updateActiveTime(ctx.channel());
			ctx.channel().writeAndFlush(MsgPingResp.INSTANCE);
		} else if (ctx.channel().hasAttr(Constants.ATTR_KEY_PROXY_PORT)) {
			if (msg instanceof MsgProxy) {
				MsgProxy msgProxy = (MsgProxy) msg;
				Channel client = context.getOuterChannelManage().get(msgProxy.getId());
				if (null == client) {
					logger.error("未能找到客户通道:{}:{}", msgProxy.getId(), obj);
				} else if (!client.isWritable()) {
					logger.error("客户通道未能写入数据:{}:{}:{}", client, msgProxy.getId(), obj);
					ChannelUtils.closeOnFlush(client);
				} else {
					if (obj instanceof MsgProxyData) {
						logger.info("写入外部通道{}:{}", client, msg);
						client.writeAndFlush(Unpooled.copiedBuffer(((MsgProxyData) obj).getData()));
					} else if (obj instanceof MsgProxyClose) {
						ChannelUtils.closeOnFlush(client);
					}
				}
			} else {
				logger.error("收到错误消息:{}:{}", ctx.channel(), obj);
				ChannelUtils.closeOnFlush(ctx.channel());
			}
		} else {
			if (msg instanceof MsgConnect) {
				MsgConnect conn = (MsgConnect) msg;
				if (TokenUtils.validate(conn.getToken(), context.getConfig().getServerToken())) {
					context.getInnerChannelManage(conn.getPort()).reg(ctx.channel());
				} else {
					logger.error("权限验证失败:{}:{}", ctx.channel(), obj);
					ChannelUtils.closeOnFlush(ctx.channel());
				}
			} else {
				logger.error("收到错误消息:{}:{}", ctx.channel(), obj);
				ChannelUtils.closeOnFlush(ctx.channel());
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		ChannelUtils.exceptionCaught(ctx, cause, logger);
	}

}