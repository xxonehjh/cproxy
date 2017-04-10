package com.xxonehjh.cproxy.client.target;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.client.ClientContext;
import com.xxonehjh.cproxy.protocol.MsgProxyClose;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class TargetChannelManage {

	private static final Logger logger = LogManager.getLogger(TargetHandlerContext.class);

	private ClientContext context;
	private Map<Integer, TargetHandlerContext> targets;

	public TargetChannelManage(ClientContext context) {
		this.context = context;
		this.targets = new java.util.concurrent.ConcurrentHashMap<>();
	}

	public TargetHandlerContext get(int id) {
		return targets.get(id);
	}

	public TargetHandlerContext get(ChannelHandlerContext clientChannelHandlerContext, int id) {
		TargetHandlerContext handler = targets.get(id);
		if (null == handler) {
			synchronized (this) {
				handler = targets.get(id);
				if (null == handler) {
					handler = new TargetHandlerContext(context, clientChannelHandlerContext, id);
					targets.put(id, handler);
					logger.info("注册【reg】目标通道:{}:id:{}", handler.getChannel(), id);
				}
			}
		}
		return handler;
	}

	public void remove(final int id) {
		final TargetHandlerContext targetHandler = targets.get(id);
		if (null != targetHandler) {
			targetHandler.getClientChannel().writeAndFlush(new MsgProxyClose(id))
					.addListener(new ChannelFutureListener() {
						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							targets.remove(id);
							logger.info("注销【remove】目标通道:{}:id:{}:ex({})", targetHandler.getChannel(),
									targetHandler.getId(), future.cause());
						}
					});
		}
	}

}
