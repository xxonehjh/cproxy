package com.xxonehjh.cproxy.protocol;

import com.xxonehjh.cproxy.Constants;
import com.xxonehjh.cproxy.util.MyByteBufUtil;

import io.netty.buffer.ByteBuf;

public class MsgProxyData extends MsgProxy {

	private byte[] data;

	public MsgProxyData(int id, byte[] data) {
		super(id);
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	@Override
	public MsgType getType() {
		return MsgType.PROXYDATA;
	}

	@Override
	public void encode(ByteBuf buf) throws Exception {
		if (null == data) {
			data = Constants.EMPTY_BYTES;
		}
		MyByteBufUtil.writeDatas(buf, this.getId(), this.getData());
	}

	public static MsgProxyData decode(ByteBuf buf) {
		Object[] datas = MyByteBufUtil.readDatas(buf, int.class, byte[].class);
		if (null == datas) {
			return null;
		}
		return new MsgProxyData((int) datas[0], (byte[]) datas[1]);
	}
	
	public String toString(){
		return "["+this.getClass().getSimpleName()+"]" + this.getId() + ":" + (null==this.getData()?0:this.getData().length);
	}

}
