package com.xxonehjh.cproxy.protocol;

import com.xxonehjh.cproxy.util.MyByteBufUtil;

import io.netty.buffer.ByteBuf;

public class MsgConnect implements IMsg {

	/**
	 * 代理端口
	 */
	private int port;
	/**
	 * 权限token
	 */
	private String token;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public MsgType getType() {
		return MsgType.CONNECT;
	}

	@Override
	public void encode(ByteBuf buf) throws Exception {
		if (null == token || token.length() > Byte.MAX_VALUE) {
			throw new java.lang.IllegalArgumentException("token 参数格式错误:" + token);
		}
		MyByteBufUtil.writeDatas(buf, port, token);
	}

	public static IMsg decode(ByteBuf buf) {
		Object[] datas = MyByteBufUtil.readDatas(buf, int.class, String.class);
		if (null == datas) {
			return null;
		}
		MsgConnect result = new MsgConnect();
		result.setPort((int) datas[0]);
		result.setToken((String) datas[1]);
		return result;
	}
	
	public String toString(){
		return "[MsgConnect]" + port + ":" + token;
	}

}
