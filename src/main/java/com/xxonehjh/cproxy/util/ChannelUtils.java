package com.xxonehjh.cproxy.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.Constants;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class ChannelUtils {

	private static final Logger logger = LogManager.getLogger(ChannelUtils.class);

	public static void exceptionCaught(ChannelHandlerContext ctx, Throwable cause, Logger logger) throws Exception {
		ctx.channel().close();
		logger.error(ctx.channel(), cause);
	}

	public static void closeOnFlush(Channel ch) {
		if (ch.isActive()) {
			ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
	}

	public static boolean isTimeout(Channel c) {
		return System.currentTimeMillis() - c.attr(Constants.ATTR_KEY_TIME).get() > Constants.CLIENT_TIMEOUT;
	}

	public static void updateActiveTime(Channel c) {
		c.attr(Constants.ATTR_KEY_TIME).set(System.currentTimeMillis());
	}

	public static final ChannelFutureListener LOG_AND_CLOSE = new ChannelFutureListener() {
		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			if (null != future.cause()) {
				logger.error(future.channel(), future.cause());
			}
			if (!future.isSuccess()) {
				future.channel().close();
			}
		}
	};

}
