package com.nari.sun.netty.obj;

public class NettyMessage {
	
	private Header header;
	private Object body;
	
//	public NettyMessage(Header header,Object body){
//		this.setBody(body);
//		this.setHeader(header);
//	}
	
	
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
	
	public String toString(){
		return "nettyMessage:"+"["+header+"]";
	}
	
}
