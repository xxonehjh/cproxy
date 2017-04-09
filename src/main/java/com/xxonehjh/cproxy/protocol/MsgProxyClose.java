package com.xxonehjh.cproxy.protocol;

import com.xxonehjh.cproxy.util.ByteUtils;
import com.xxonehjh.cproxy.util.MyByteBufUtil;

import io.netty.buffer.ByteBuf;

public class MsgProxyClose extends MsgProxy {

	public MsgProxyClose(int id) {
		super(id);
	}
	
	@Override
	public MsgType getType() {
		return MsgType.PROXYCLOSE;
	}

	@Override
	public void encode(ByteBuf buf) throws Exception {
		MyByteBufUtil.writeDatas(buf,this.getId());
	}
	
	public static MsgProxyClose decode(ByteBuf b) {
		if(b.readableBytes()>=ByteUtils.BYTES_OF_INTEGER){
			return new MsgProxyClose(b.readInt());
		}
		return null;
	}

	

}
