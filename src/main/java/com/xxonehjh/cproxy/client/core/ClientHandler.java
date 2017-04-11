package com.xxonehjh.cproxy.client.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.client.ClientContext;
import com.xxonehjh.cproxy.client.target.TargetHandlerContext;
import com.xxonehjh.cproxy.protocol.IMsg;
import com.xxonehjh.cproxy.protocol.MsgConnect;
import com.xxonehjh.cproxy.protocol.MsgPingResp;
import com.xxonehjh.cproxy.protocol.MsgProxyData;
import com.xxonehjh.cproxy.util.ChannelUtils;
import com.xxonehjh.cproxy.util.TokenUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

@Sharable
public class ClientHandler extends ChannelInboundHandlerAdapter {
	
	public static ClientHandler create(ClientContext context){
		return new ClientHandler(context);
	}

	private static final Logger logger = LogManager.getLogger(ClientHandler.class);
	private ClientContext context;

	private ClientHandler(ClientContext context) {
		this.context = context;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		ChannelUtils.updateActiveTime(ctx.channel());
		Channel channel = ctx.channel();
		context.getClientChannelManage().reg(channel);
		MsgConnect connect = new MsgConnect();
		connect.setPort(context.getConfig().getServerOuterPort());
		connect.setToken(TokenUtils.encrypt(context.getConfig().getServerToken()));
		channel.writeAndFlush(connect).addListener(ChannelUtils.LOG_AND_CLOSE);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) {
		IMsg msg = (IMsg) obj;
		if (msg instanceof MsgPingResp) {
			ChannelUtils.updateActiveTime(ctx.channel());
		} else {
			if (msg instanceof MsgProxyData) {
				MsgProxyData data = (MsgProxyData) msg;
				TargetHandlerContext handler = context.getTargetChannelManage().get(ctx, data.getId());
				handler.write(data.getData());
			} else {
				logger.error("收到错误消息:{}:{}", ctx.channel(), obj);
				ChannelUtils.closeOnFlush(ctx.channel());
			}
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		context.getClientChannelManage().remove(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		ChannelUtils.exceptionCaught(ctx, cause, logger);
	}

}