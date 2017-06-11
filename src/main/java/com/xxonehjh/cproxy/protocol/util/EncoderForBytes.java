package com.xxonehjh.cproxy.protocol.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.util.ChannelUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class EncoderForBytes extends MessageToByteEncoder<byte[]> {

	private static final Logger logger = LogManager.getLogger(EncoderForBytes.class);

	public static final EncoderForBytes getInstance() {
		return new EncoderForBytes();
	}

	private EncoderForBytes() {
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, byte[] bytes, ByteBuf out) throws Exception {
		try {
			out.writeBytes(bytes);
		} catch (Exception e) {
			ChannelUtils.exceptionCaught(ctx, e, logger);
		}
	}

}
