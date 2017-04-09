package com.xxonehjh.cproxy.protocol;

import io.netty.buffer.ByteBuf;

public enum MsgType {
	
	CONNECT((byte)1){
		public IMsg decode(ByteBuf b){
			return MsgConnect.decode(b);
		}
	},
	PINGREQ((byte)2){
		public IMsg decode(ByteBuf b){
			return MsgPingReq.decode(b);
		}
	},
	PINGRESP((byte)3){
		public IMsg decode(ByteBuf b){
			return MsgPingResp.decode(b);
		}
	},
	PROXYCLOSE((byte)4){
		public IMsg decode(ByteBuf b){
			return MsgProxyClose.decode(b);
		}
	},
	PROXYDATA((byte)5){
		public IMsg decode(ByteBuf b){
			return MsgProxyData.decode(b);
		}
	};
	
	private  final byte value;
	
	private MsgType(byte value){
		this.value = value;
	}

	public byte getValue() {
		return value;
	}
	
	public IMsg decode(ByteBuf b){
		return null;
	}
	
	public static MsgType valueOf(byte value){
		if(value == MsgType.CONNECT.getValue()){
			return MsgType.CONNECT;
		}
		if(value == MsgType.PINGREQ.getValue()){
			return MsgType.PINGREQ;
		}
		if(value == MsgType.PINGRESP.getValue()){
			return MsgType.PINGRESP;
		}
		if(value == MsgType.PROXYCLOSE.getValue()){
			return MsgType.PROXYCLOSE;
		}
		if(value == MsgType.PROXYDATA.getValue()){
			return MsgType.PROXYDATA;
		}
		return null;
	}
	
}
