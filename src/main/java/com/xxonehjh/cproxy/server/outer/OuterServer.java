package com.xxonehjh.cproxy.server.outer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.server.ServerContext;
import com.xxonehjh.cproxy.util.LoggingHandlerUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class OuterServer {

	private static final Logger logger = LogManager.getLogger(OuterServer.class);

	public void start(final ServerContext context) {
		int[] ports = context.getConfig().getServerOuterPorts();
		EventLoopGroup bossGroup = new NioEventLoopGroup(context.getConfig().getServerOuterNettyBossThreads());
		EventLoopGroup workerGroup = new NioEventLoopGroup(context.getConfig().getServerOuterNettyWorkThreads());
		try {
			final ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup);
			b.channel(NioServerSocketChannel.class);
			if(context.getConfig().isDebug()){
				b.handler(LoggingHandlerUtil.getInstance());
			}
			b.childHandler(new OuterChannelInitializer(context));
			b.childOption(ChannelOption.AUTO_READ, false);
			for (int i = 1; i < ports.length; i++) {
				final int current = ports[i];
				new Thread() {
					public void run() {
						ChannelFuture channel = b.bind(current);
						logger.info("启动外部服务,端口:{}", current);
						try {
							channel.sync().channel().closeFuture().sync();
						} catch (Exception e) {
							logger.error("启动外部服务失败,端口:" + current, e);
							System.exit(-1);
						}
					}
				}.start();
			}

			try {
				ChannelFuture channel = b.bind(ports[0]);
				logger.info("启动外部服务,端口:{}", ports[0]);
				channel.sync().channel().closeFuture().sync();
			} catch (Exception e) {
				logger.error("启动外部服务失败,端口:" + ports[0], e);
				System.exit(-1);
			}
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}
