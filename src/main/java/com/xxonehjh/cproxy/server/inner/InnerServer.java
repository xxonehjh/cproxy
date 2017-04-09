package com.xxonehjh.cproxy.server.inner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.server.ServerContext;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class InnerServer {

	private static final Logger logger = LogManager.getLogger(InnerServer.class);

	public void start(ServerContext context) {
		int port = context.getConfig().getServerInnerPort();
		EventLoopGroup bossGroup = new NioEventLoopGroup(context.getConfig().getServerInnerNettyBossThreads());
		EventLoopGroup workerGroup = new NioEventLoopGroup(context.getConfig().getServerInnerNettyWorkThreads());
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup);
			b.channel(NioServerSocketChannel.class);
			if(context.getConfig().isDebug()){
				b.handler(new LoggingHandler(LogLevel.INFO));
			}
			b.childHandler(new InnerInitializer(context));
			b.childOption(ChannelOption.AUTO_READ, true);
			try {
				ChannelFuture channel = b.bind(port);
				logger.info("启动内部服务,端口:{}", port);
				channel.sync().channel().closeFuture().sync();
			} catch (InterruptedException e) {
				logger.error("启动内部服务失败:" + port, e);
				System.exit(-1);
			}
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}
