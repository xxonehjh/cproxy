package com.xxonehjh.cproxy.client.target;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.client.ClientContext;
import com.xxonehjh.cproxy.util.ByteArrayUtil;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class TargetHandlerContext {

	private static final Logger logger = LogManager.getLogger(TargetHandlerContext.class);
	private final int id;
	private List<byte[]> cache;
	private Channel targetChannel;

	public TargetHandlerContext(final ClientContext context, ChannelHandlerContext ctx, int id) {
		this.id = id;
		this.cache = new LinkedList<byte[]>();
		ChannelFuture f = context.getTargetBootstrap(ctx).connect(context.getConfig().getTargetHost(),
				context.getConfig().getTargetPort());
		this.targetChannel = f.channel();
		this.targetChannel.attr(Constants.ATTR_KEY_ID).set(id);
		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (future.isSuccess()) {
					writeCache();
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
				logger.info("写入目标通道{}:{}", targetChannel,datas.length);
				targetChannel.writeAndFlush(Unpooled.copiedBuffer(datas)).addListener(READ_OR_CLOSE);
			}
		}
	}

	public void write(byte[] datas) {
		if (targetChannel.isActive()) {
			writeCache();
			logger.info("写入目标通道{}:{}", targetChannel,datas.length);
			targetChannel.writeAndFlush(Unpooled.copiedBuffer(datas)).addListener(READ_OR_CLOSE);
		} else {
			synchronized (cache) {
				cache.add(datas);
			}
		}
	}

	public int getId() {
		return this.id;
	}

	public Channel getChannel() {
		return this.targetChannel;
	}

	private static final ChannelFutureListener READ_OR_CLOSE = new ChannelFutureListener() {
		@Override
		public void operationComplete(ChannelFuture future) {
			if (future.isSuccess()) {
				future.channel().read();
			} else {
				logger.error("发送数据失败{}:id:{}:ex:({})", future.channel(),
						future.channel().attr(Constants.ATTR_KEY_ID).get(), future.cause());
				future.channel().close();
			}
		}
	};

}
