package com.xxonehjh.cproxy.protocol;

public abstract class MsgProxy implements IMsg{

	private int id;

    public MsgProxy(int id) {
    	this.id = id;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String toString(){
		return "["+this.getClass().getSimpleName()+"]" + id;
	}
}
