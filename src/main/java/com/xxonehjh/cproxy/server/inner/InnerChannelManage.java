package com.xxonehjh.cproxy.server.inner;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.server.ServerContext;
import com.xxonehjh.cproxy.server.outer.OuterHandler;
import com.xxonehjh.cproxy.server.outer.OuterHandlerForClose;
import com.xxonehjh.cproxy.util.ChannelUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInboundHandler;

public class InnerChannelManage {

	private static final Logger logger = LogManager.getLogger(InnerChannelManage.class);

	private ServerContext serverContext;
	private List<InnerChannelHolder> channelHolders;
	private OuterHandlerForClose outerHandlerForClose;
	private AtomicLong randomIndex;
	private int port;

	public InnerChannelManage(ServerContext serverContext, int port) {
		this.serverContext = serverContext;
		this.channelHolders = new CopyOnWriteArrayList<>();
		this.outerHandlerForClose = new OuterHandlerForClose(port);
		this.randomIndex = new AtomicLong(0);
		this.port = port;
	}

	public ChannelInboundHandler getOuterChannelHandler() {
		if (channelHolders.size() == 0) {
			return outerHandlerForClose;
		}
		InnerChannelHolder holder = channelHolders
				.get((int) (this.randomIndex.incrementAndGet() % channelHolders.size()));
		if (!holder.getInnerChannel().isActive()) {
			logger.info("内部服务不可用:{}:代理端口:{}", holder.getInnerChannel(), port);
			closeAndRemove(holder);
			return getOuterChannelHandler();
		} else if (ChannelUtils.isTimeout(holder.getInnerChannel())) {
			logger.info("内部服务心跳超时:{}:代理端口:{}", holder.getInnerChannel(), port);
			closeAndRemove(holder);
			return getOuterChannelHandler();
		}
		return holder.getOuterHandler();
	}

	public void reg(Channel channel) {
		logger.info("注册【reg】内部服务:{}:代理端口:{}", channel, port);
		channel.attr(Constants.ATTR_KEY_PROXY_PORT).set(port);
		InnerChannelHolder holder = new InnerChannelHolder(channel);
		channel.closeFuture().addListener(holder);
		channelHolders.add(holder);
	}

	public int getPort() {
		return port;
	}

	void closeAndRemove(InnerChannelHolder holder) {
		holder.getInnerChannel().close();
		remove(holder);
	}

	void remove(InnerChannelHolder holder) {
		if (channelHolders.remove(holder)) {
			logger.info("注销【remove】内部服务:{}:代理端口:{}", holder.getInnerChannel(), port);
		} else {
			logger.debug("注销失败【remove】内部服务:{}:代理端口:{}", holder.getInnerChannel(), port);
		}
	}

	ServerContext getContext() {
		return serverContext;
	}

	class InnerChannelHolder implements ChannelFutureListener {

		private Channel innerChannel;

		public InnerChannelHolder(Channel innerChannel) {
			this.innerChannel = innerChannel;
		}

		public Channel getInnerChannel() {
			return innerChannel;
		}

		public OuterHandler getOuterHandler() {
			return new OuterHandler(getContext(), innerChannel, getPort());
		}

		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			remove(this);
		}
	}

}
