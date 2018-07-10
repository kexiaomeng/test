package com.nari.sun.eventListen;

public enum EventType {
	MSG_SEND("发送消息"),
	MSG_RECEIVE("接收消息");
	
	private String name;
	EventType(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
}
