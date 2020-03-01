package com.xxonehjh.cproxy.client.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.client.ClientContext;
import com.xxonehjh.cproxy.protocol.MsgPingReq;
import com.xxonehjh.cproxy.util.ChannelUtils;

import io.netty.channel.Channel;

public class ClientChannelManage {

	private static final Logger logger = LogManager.getLogger(ClientChannelManage.class);
	private List<Channel> channels;

	public ClientChannelManage(ClientContext clientContext) {
		this.channels = new CopyOnWriteArrayList<>();
	}

	public void reg(Channel channel) {
		logger.info("注册【reg】通道:{}", channel);
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
			if (ChannelUtils.isTimeout(channel)) {
				logger.info("连接超时:{}", channel);
				channel.close();
			} else {
				channel.writeAndFlush(MsgPingReq.INSTANCE);
			}
		}
	}

}
