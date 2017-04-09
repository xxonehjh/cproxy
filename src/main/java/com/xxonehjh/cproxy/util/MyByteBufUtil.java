package com.xxonehjh.cproxy.util;

import java.io.UnsupportedEncodingException;

import com.xxonehjh.cproxy.Constants;

import io.netty.buffer.ByteBuf;

public class MyByteBufUtil {

	public static void writeDatas(ByteBuf buf, Object... datas) {
		for (Object item : datas) {
			if (null == item) {
				throw new RuntimeException("参数不能为空");
			}
			if (item.getClass() == Byte.class) {
				buf.writeByte((byte) item);
			} else if (item.getClass() == Integer.class) {
				buf.writeInt((int) item);
			} else if (item.getClass() == Long.class) {
				buf.writeLong((long) item);
			} else if (item.getClass() == String.class) {
				String str = (String) item;
				byte[] data;
				try {
					data = str.getBytes(Constants.UTF8);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
				buf.writeInt(data.length);
				buf.writeBytes(data);
			} else if (item.getClass() == byte[].class) {
				byte[] data = (byte[]) item;
				buf.writeInt(data.length);
				buf.writeBytes(data);
			} else {
				throw new RuntimeException("不支持类型:" + item.getClass().getName());
			}
		}
	}

	public static Object[] readDatas(ByteBuf buf, Class<?>... types) {
		Object[] datas = new Object[types.length];
		int index = 0;
		for (Class<?> type : types) {
			if (type == byte.class) {
				if (buf.readableBytes() < ByteUtils.BYTES_OF_BYTE) {
					return null;
				} else {
					datas[index++] = buf.readByte();
				}
			} else if (type == int.class) {
				if (buf.readableBytes() < ByteUtils.BYTES_OF_INTEGER) {
					return null;
				} else {
					datas[index++] = buf.readInt();
				}
			} else if (type == long.class) {
				if (buf.readableBytes() < ByteUtils.BYTES_OF_LONG) {
					return null;
				} else {
					datas[index++] = buf.readLong();
				}
			} else if (type == String.class) {
				if (buf.readableBytes() < ByteUtils.BYTES_OF_INTEGER) {
					return null;
				} else {
					int len = buf.readInt();
					if (buf.readableBytes() < len) {
						return null;
					}
					byte[] arr = new byte[len];
					buf.readBytes(arr);
					try {
						datas[index++] = new String(arr, Constants.UTF8);
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(e);
					}
				}
			} else if (type == byte[].class) {
				if (buf.readableBytes() < ByteUtils.BYTES_OF_INTEGER) {
					return null;
				} else {
					int len = buf.readInt();
					if (buf.readableBytes() < len) {
						return null;
					}
					byte[] arr = new byte[len];
					buf.readBytes(arr);
					datas[index++] = arr;
				}
			} else {
				throw new RuntimeException("不支持类型:" + type.getName());
			}
		}
		return datas;
	}

}
