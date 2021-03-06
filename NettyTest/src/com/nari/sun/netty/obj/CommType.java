package com.nari.sun.netty.obj;

public enum CommType {
	
	TYPE_0(0,"业务请求消息"),
	TYPE_1(1,"业务响应消息"),
	TYPE_2(2,"业务ONE-WAY消息"),
	TYPE_3(3,"握手请求消息"),
	TYPE_4(4,"握手应答消息"),
	TYPE_5(5,"心跳请求消息"),
	TYPE_6(6,"心跳应答消息");


	private int    id;
	private String name;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private CommType(int id,String name){
		this.id   = id;
		this.name = name;
	}
	
}
