package com.xxonehjh.cproxy.protocol;

import io.netty.buffer.ByteBuf;

public class MsgPingResp implements IMsg{

	public static final MsgPingResp INSTANCE= new MsgPingResp();
	private MsgPingResp(){};
	
	@Override
	public MsgType getType() {
		return MsgType.PINGRESP;
	}

	public static MsgPingResp decode(ByteBuf b) {
		return INSTANCE;
	}

	@Override
	public void encode(ByteBuf out) throws Exception {
	}
	
	public String toString(){
		return "[MsgPingResp]";
	}
}
