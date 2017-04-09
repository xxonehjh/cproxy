package com.xxonehjh.cproxy.server.inner;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.server.ServerContext;

import io.netty.channel.Channel;

public class InnerChannelManage {

	private static final Logger logger = LogManager.getLogger(InnerChannelManage.class);

	private ServerContext serverContext;
	private AtomicLong rand;
	private List<Channel> channels;
	private int port;

	public InnerChannelManage(ServerContext serverContext, int port) {
		this.serverContext = serverContext;
		this.rand = new AtomicLong(0);
		this.channels = new CopyOnWriteArrayList<>();
		this.port = port;
	}

	public boolean isEnable() {
		return channels.size() > 0;
	}

	public Channel getChannel() {
		if (!isEnable()) {
			return null;
		}
		return channels.get((int) (this.rand.incrementAndGet() % channels.size()));
	}

	public void remove(Channel channel) {
		logger.info("注销【remove】内部服务:{}:代理端口:{}", channel, port);
		channels.remove(channel);
		if (channels.size() == 0) {
			serverContext.getOuterChannelManage().closeByPort(port);
		}
	}

	public void reg(Channel channel) {
		logger.info("注册【reg】内部服务:{}:代理端口:{}", channel, port);
		channel.attr(Constants.ATTR_KEY_PROXY_PORT).set(port);
		channels.add(channel);
	}

	public int getPort() {
		return port;
	}

}
