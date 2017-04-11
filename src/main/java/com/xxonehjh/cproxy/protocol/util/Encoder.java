package com.xxonehjh.cproxy.protocol.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.protocol.IMsg;
import com.xxonehjh.cproxy.util.ChannelUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 每个通道对应一个
 * 
 * @author xxonehjh
 *
 */
@Sharable
public class Encoder extends MessageToByteEncoder<IMsg> {

	private static final Logger logger = LogManager.getLogger(Encoder.class);

	public static final Encoder INSTANCE = new Encoder();

	private Encoder() {
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, IMsg msg, ByteBuf out) throws Exception {
		try {
			out.writeByte(msg.getType().getValue());
			msg.encode(out);
			logger.info("{}写入{}",ctx.channel(),msg);
		} catch (Exception e) {
			ChannelUtils.exceptionCaught(ctx, e, logger);
		}
	}

}