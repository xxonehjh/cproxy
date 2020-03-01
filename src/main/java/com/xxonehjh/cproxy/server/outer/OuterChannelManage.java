package com.xxonehjh.cproxy.server.outer;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.server.ServerContext;

import io.netty.channel.Channel;

public class OuterChannelManage {

	private static final Logger logger = LogManager.getLogger(OuterChannelManage.class);
	private AtomicInteger idSeed;
	private Map<Integer, Channel> clients;

	public OuterChannelManage(ServerContext serverContext) {
		idSeed = new AtomicInteger();
		clients = new java.util.concurrent.ConcurrentHashMap<>();
	}

	public void reg(Channel channel, int port) {
		if (idSeed.get() == Integer.MAX_VALUE) {
			idSeed.compareAndSet(Integer.MAX_VALUE, 0);
		}
		int id = idSeed.incrementAndGet();
		logger.info("注册【reg】外部服务:{}:id:{}:port:{}", channel, id, port);
		channel.attr(Constants.ATTR_KEY_ID).set(id);
		channel.attr(Constants.ATTR_KEY_PROXY_PORT).set(port);
		clients.put(id, channel);
	}

	public void remove(Channel channel) {
		if (channel.hasAttr(Constants.ATTR_KEY_ID)) {
			logger.info("注销【remove】外部服务:{}:id:{}", channel, channel.attr(Constants.ATTR_KEY_ID).get());
			clients.remove(channel.attr(Constants.ATTR_KEY_ID).get());
		}
	}

	public Channel get(int id) {
		return clients.get(id);
	}

}
