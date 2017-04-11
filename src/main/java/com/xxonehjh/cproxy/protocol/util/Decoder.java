package com.xxonehjh.cproxy.protocol.util;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.xxonehjh.cproxy.protocol.IMsg;
import com.xxonehjh.cproxy.protocol.MsgType;
import com.xxonehjh.cproxy.util.ChannelUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 每个通道对应一个
 * 
 * @author xxonehjh
 *
 */
@Sharable
public class Decoder extends ByteToMessageDecoder {

	private static Logger logger = LogManager.getLogger(Decoder.class);

	public static final Decoder INSTANCE = new Decoder();

	private Decoder() {
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		try {
			if (in.readableBytes() == 0) {
				return;
			}
			in.markReaderIndex();
			byte type = in.readByte();
			MsgType mtype = MsgType.valueOf(type);
			if (mtype == null) {
				logger.error("未能识别消息类型:{}", type);
				ctx.close();
				return;
			}
			IMsg msg = mtype.decode(in);
			if (null == msg) {
				in.resetReaderIndex();
				return;
			}
			logger.info("{}读取{}", ctx.channel(), msg);
			out.add(msg);
			decode(ctx, in, out);
		} catch (Exception e) {
			ChannelUtils.exceptionCaught(ctx, e, logger);
		}
	}

}