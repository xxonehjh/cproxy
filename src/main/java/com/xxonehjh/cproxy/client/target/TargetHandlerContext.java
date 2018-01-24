package com.xxonehjh.cproxy.client.target;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.client.ClientContext;
import com.xxonehjh.cproxy.util.ByteArrayUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class TargetHandlerContext {

	private static final Logger logger = LogManager.getLogger(TargetHandlerContext.class);
	private final int id;
	private List<byte[]> cache;
	private Channel targetChannel;
	private Channel clientChannel;

	public TargetHandlerContext(final ClientContext context, ChannelHandlerContext clientChannelHandlerContext,
			int id) {
		this.id = id;
		this.cache = new LinkedList<byte[]>();
		ChannelFuture f = context.getTargetBootstrap(clientChannelHandlerContext)
				.connect(context.getConfig().getTargetHost(), context.getConfig().getTargetPort());
		this.clientChannel = clientChannelHandlerContext.channel();
		this.targetChannel = f.channel();
		this.targetChannel.attr(Constants.ATTR_KEY_ID).set(id);
		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (future.isSuccess()) {
					logger.error("创建目标通道成功:{}:id:{}", targetChannel, getId());
					writeCache();
					future.channel().read();
				} else {
					logger.error("创建目标通道失败:{}:id:{}: ex:({})", targetChannel, getId(), future.cause());
					context.getTargetChannelManage().remove(getId());
				}
			}
		});
	}

	private void writeCache() {
		if (cache.size() > 0) {
			byte[] datas = null;
			synchronized (cache) {
				datas = ByteArrayUtil.concat(cache);
				cache.clear();
			}
			if (null != datas && datas.length > 0) {
				logger.info("写入目标通道:{}:数据长度:{}", targetChannel, datas.length);
				//targetChannel.writeAndFlush(Unpooled.copiedBuffer(datas)).addListener(READ_OR_CLOSE);
				targetChannel.writeAndFlush(datas).addListener(READ_OR_CLOSE);
			}
		}
	}

	public void write(byte[] datas) {
		if (targetChannel.isActive()) {
			writeCache();
			logger.info("写入目标通道:{}:数据长度:{}", targetChannel, datas.length);
			//targetChannel.writeAndFlush(Unpooled.copiedBuffer(datas)).addListener(READ_OR_CLOSE);
			targetChannel.writeAndFlush(datas).addListener(READ_OR_CLOSE);
		} else {
			synchronized (cache) {
				cache.add(datas);
			}
		}
	}

	public int getId() {
		return this.id;
	}

	public Channel getTargetChannel() {
		return this.targetChannel;
	}

	public Channel getClientChannel() {
		return clientChannel;
	}

	private static final ChannelFutureListener READ_OR_CLOSE = new ChannelFutureListener() {
		@Override
		public void operationComplete(ChannelFuture future) {
			if (future.isSuccess()) {
				future.channel().read();
			} else {
				logger.error("发送数据失败:{}:id:{}:ex:({})", future.channel(),
						future.channel().attr(Constants.ATTR_KEY_ID).get(), future.cause());
				future.channel().close();
			}
		}
	};

}
