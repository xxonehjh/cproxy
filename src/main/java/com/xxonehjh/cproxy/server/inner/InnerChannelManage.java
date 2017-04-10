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

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandler;

public class InnerChannelManage {

	private static final Logger logger = LogManager.getLogger(InnerChannelManage.class);

	private ServerContext serverContext;
	private AtomicLong rand;
	private List<InnerChannelHolder> channels;
	private OuterHandlerForClose outerHandlerForClose;
	private int port;

	public InnerChannelManage(ServerContext serverContext, int port) {
		this.serverContext = serverContext;
		this.rand = new AtomicLong(0);
		this.channels = new CopyOnWriteArrayList<>();
		this.outerHandlerForClose = new OuterHandlerForClose(port);
		this.port = port;
	}

	public boolean isEnable() {
		return channels.size() > 0;
	}

	public ChannelInboundHandler getOuterHandler() {
		if (!isEnable()) {
			return outerHandlerForClose;
		}
		InnerChannelHolder c = channels.get((int) (this.rand.incrementAndGet() % channels.size()));
		return c.getOuterHandler();
	}

	public void remove(Channel channel) {
		logger.info("注销【remove】内部服务:{}:代理端口:{}", channel, port);
		channels.remove(channel);
	}

	public void reg(Channel channel) {
		logger.info("注册【reg】内部服务:{}:代理端口:{}", channel, port);
		channel.attr(Constants.ATTR_KEY_PROXY_PORT).set(port);
		OuterHandler handler = new OuterHandler(this.serverContext, channel, port);
		InnerChannelHolder holder = new InnerChannelHolder(channel, handler);
		channels.add(holder);
	}

	public int getPort() {
		return port;
	}

}
