package com.xxonehjh.cproxy.server.inner;

import com.xxonehjh.cproxy.server.outer.OuterHandler;

import io.netty.channel.Channel;

public class InnerChannelHolder {

	private Channel channel;
	private OuterHandler outerHandler;
	
	public InnerChannelHolder(Channel channel,OuterHandler outerHandler){
		this.channel = channel;
		this.outerHandler = outerHandler;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public OuterHandler getOuterHandler() {
		return outerHandler;
	}

	public void setOuterHandler(OuterHandler outerHandler) {
		this.outerHandler = outerHandler;
	}
	
	
}
