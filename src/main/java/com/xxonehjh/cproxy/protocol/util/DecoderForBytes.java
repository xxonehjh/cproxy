package com.xxonehjh.cproxy.protocol.util;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.util.ByteUtils;
import com.xxonehjh.cproxy.util.ChannelUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class DecoderForBytes  extends ByteToMessageDecoder {

	private static Logger logger = LogManager.getLogger(DecoderForBytes.class);

	public static final DecoderForBytes getInstance(){
		return new DecoderForBytes();
	}

	private DecoderForBytes() {
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		try {
			if(in.readableBytes()>0){
				out.add(ByteUtils.read((ByteBuf) in));
			}
		} catch (Exception e) {
			ChannelUtils.exceptionCaught(ctx, e, logger);
		}
	}

}
