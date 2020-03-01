package com.xxonehjh.cproxy.protocol;

import io.netty.buffer.ByteBuf;

public interface IMsg {

	MsgType getType();

	void encode(ByteBuf buf) throws Exception;
	
}
