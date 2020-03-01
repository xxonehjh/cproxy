package com.xxonehjh.cproxy.protocol;

import io.netty.buffer.ByteBuf;

public class MsgPingReq implements IMsg{
	
	public static final MsgPingReq INSTANCE= new MsgPingReq();
	private MsgPingReq(){};
	
	@Override
	public MsgType getType() {
		return MsgType.PINGREQ;
	}

	public static IMsg decode(ByteBuf b) {
		return INSTANCE;
	}

	@Override
	public void encode(ByteBuf out) throws Exception {
	}
	
	public String toString(){
		return "[MsgPingReq]";
	}
}
