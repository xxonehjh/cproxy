package com.xxonehjh.cproxy.client.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.client.ClientContext;
import com.xxonehjh.cproxy.protocol.MsgPingReq;

import io.netty.channel.Channel;

public class ClientChannelManage {

	private static final Logger logger = LogManager.getLogger(ClientChannelManage.class);
	private List<Channel> channels;
	private AtomicLong rand;

	public ClientChannelManage(ClientContext clientContext) {
		this.channels = new CopyOnWriteArrayList<>();
		this.rand = new AtomicLong(0);
	}

	public Channel getChannel() {
		return channels.get((int) (this.rand.incrementAndGet() % channels.size()));
	}

	public void reg(Channel channel) {
		logger.info("注册【reg】通道:{}", channel);
		channel.attr(Constants.ATTR_KEY_TIME).set(System.currentTimeMillis());
		channels.add(channel);
	}

	public void remove(Channel channel) {
		logger.info("注销【remove】通道:{}", channel);
		channels.remove(channel);
	}

	public int size() {
		return channels.size();
	}

	public void heartbeat() {
		for (Channel channel : channels) {
			if (System.currentTimeMillis() - channel.attr(Constants.ATTR_KEY_TIME).get() > Constants.CLIENT_TIMEOUT) {
				logger.info("连接超时:{}", channel);
				channel.close();
			} else {
				channel.writeAndFlush(MsgPingReq.INSTANCE);
			}
		}
	}

}
