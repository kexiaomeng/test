package com.nari.sun.netty.handler;

import com.nari.sun.netty.obj.CommType;
import com.nari.sun.netty.obj.NettyMessage;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class HeartBeatRespHandler extends ChannelHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx,Object obj) {
		
		NettyMessage msg = (NettyMessage)obj;
		System.out.println(msg.toString());

		if(msg.getHeader() != null && msg.getHeader().getType() == (byte)CommType.TYPE_5.getId()){
			msg.getHeader().setType((byte)CommType.TYPE_6.getId());
			System.out.println("收到终端心跳，回复");
			ctx.writeAndFlush(msg);
		}else {
			ctx.fireChannelRead(obj);
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		System.out.println("发生异常，链路断开");
		ctx.channel().close();
		
	}
	
}
