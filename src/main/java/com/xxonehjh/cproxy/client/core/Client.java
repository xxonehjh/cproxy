package com.xxonehjh.cproxy.client.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.client.ClientContext;
import com.xxonehjh.cproxy.protocol.util.Decoder;
import com.xxonehjh.cproxy.protocol.util.Encoder;
import com.xxonehjh.cproxy.util.LoggingHandlerUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {

	private static final Logger logger = LogManager.getLogger(Client.class);

	public void start(final ClientContext context) throws InterruptedException {
		EventLoopGroup workerGroup = new NioEventLoopGroup(context.getConfig().getWorkThreads());
		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipe = ch.pipeline();
					pipe.addLast(LoggingHandlerUtil.getInstance(context.getConfig().isDebug()));
					pipe.addLast(Encoder.INSTANCE, Decoder.INSTANCE, context.getClientHandler());
				}
			});
			final String host = context.getConfig().getServerInnerHost();
			final int port = context.getConfig().getServerInnerPort();
			final int connectCount = context.getConfig().getConnectCount();
			while (true) {
				try {
					if (context.getClientChannelManage().size() < connectCount) {
						for (int i = context.getClientChannelManage().size(); i < connectCount; i++) {
							b.connect(host, port).sync();
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				try {
					Thread.sleep(Constants.CLIENT_CHECK_INTERVAL);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				try {
					context.getClientChannelManage().heartbeat();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		} finally {
			workerGroup.shutdownGracefully();
		}
	}
}
