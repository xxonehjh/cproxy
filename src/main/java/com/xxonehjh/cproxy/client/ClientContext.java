package com.xxonehjh.cproxy.client;

import com.xxonehjh.cproxy.client.core.ClientChannelManage;
import com.xxonehjh.cproxy.client.core.ClientHandler;
import com.xxonehjh.cproxy.client.target.TargetChannelManage;
import com.xxonehjh.cproxy.client.target.TargetHandler;
import com.xxonehjh.cproxy.util.LoggingHandlerUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ClientContext {

	private ClientConfig config;
	private TargetChannelManage targetChannelManage;
	private ClientChannelManage clientChannelManage;
	private ClientHandler clientHandler;
	private TargetHandler targetHandler;
	private Bootstrap targetBootstrap;

	public ClientContext(String configPath) {
		config = new ClientConfig(configPath);
		targetChannelManage = new TargetChannelManage(this);
		clientChannelManage = new ClientChannelManage(this);
		clientHandler = ClientHandler.create(this);
		targetHandler = TargetHandler.create(this);
	}

	public ClientConfig getConfig() {
		return config;
	}

	public ClientHandler getClientHandler() {
		return clientHandler;
	}

	public TargetChannelManage getTargetChannelManage() {
		return targetChannelManage;
	}

	public ClientChannelManage getClientChannelManage() {
		return clientChannelManage;
	}

	public Bootstrap getTargetBootstrap(ChannelHandlerContext ctx) {
		if (null == this.targetBootstrap) {
			synchronized (this) {
				if (null == this.targetBootstrap) {
					final Channel clientChannel = ctx.channel();
					targetBootstrap = new Bootstrap();
					targetBootstrap.group(clientChannel.eventLoop()).channel(ctx.channel().getClass())
							.handler(new ChannelInitializer<SocketChannel>() {
								@Override
								public void initChannel(SocketChannel ch) throws Exception {
									ChannelPipeline pipe = ch.pipeline();
									pipe.addLast(LoggingHandlerUtil.getInstance(getConfig().isDebug()));
									pipe.addLast(targetHandler);
								}
							}).option(ChannelOption.AUTO_READ, false);
				}
			}
		}
		return targetBootstrap;
	}

}
