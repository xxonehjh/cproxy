package com.xxonehjh.cproxy.util;

import io.netty.buffer.ByteBuf;

public class ByteUtils {

	public static final int BYTES_OF_BYTE = 1;

	public static final int BYTES_OF_INTEGER = 4;

	public static final int BYTES_OF_LONG = 8;

	public static byte[] read(ByteBuf buf) {
		int length = buf.readableBytes();
		byte[] datas = new byte[length];
		buf.readBytes(datas);
		return datas;
	}

}
