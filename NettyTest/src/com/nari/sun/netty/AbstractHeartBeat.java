package com.nari.sun.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public abstract class AbstractHeartBeat  extends ChannelHandlerAdapter{

	
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object obj){
		
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx,Object obj){
		
	}
	public abstract void handleData(ChannelHandlerContext ctx,ByteBuf obj);
	
	public void dealIdleRead(){
		
	}
	public void dealIdleWrite(){
		
	}
}
