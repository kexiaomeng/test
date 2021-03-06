package com.nari.sun;

import com.nari.sun.netty.obj.Header;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public abstract class AbstractHeartBeat  extends ChannelHandlerAdapter{

	
	@Override
	public void channelRead(ChannelHandlerContext/*产生更短的事件流 可以用来获得更多的性能*/ ctx,Object obj){
//		ByteBuf buf = (ByteBuf)obj;
		Header header = (Header)obj;
		System.out.println(obj.toString());
//		switch (buf.charAt(0) ) {
//		case '2':
//			
//			break;
//		case '1':
//			
//			break;
//		default:
//			handleData(ctx, buf);
//			break;
//		}
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx,Object obj){
		if(obj instanceof IdleStateEvent){
			IdleStateEvent tmpEvent = (IdleStateEvent)obj;
			switch (tmpEvent.state()) {
			case READER_IDLE:
				dealIdleRead(ctx);
				break;
			case WRITER_IDLE:
				dealIdleWrite(ctx);
				break;

			default:
				break;
			}
		}
	}
	public abstract void handleData(ChannelHandlerContext ctx,Object obj);
	
	public abstract void dealIdleRead(ChannelHandlerContext ctx);
	public abstract void dealIdleWrite(ChannelHandlerContext ctx);
}
