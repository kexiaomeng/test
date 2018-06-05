package com.nari.sun.netty;


import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.org.apache.xalan.internal.xsltc.dom.MultiValuedNodeHeapIterator.HeapNode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


public class TestHandler extends ChannelHandlerAdapter{
	
	
	private   int count = 0;
	private NettyClient client;
	public TestHandler(){
		
	}
	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg){
		
		try {
//			ByteBuf buffer =  (ByteBuf)msg;
//			byte[] bu = new byte[buffer.readableBytes()];
//			buffer.readBytes(bu);
////			System.out.println(buffer.release());
//			System.out.println(ByteBufUtil.hexDump(bu));
			
			String mesg = (String)msg;
			System.out.println(msg);
			
		} catch (Exception e) {
			System.out.println(e);
			// TODO: handle exception
		}
		
//		ctx.writeAndFlush("back to client");
		System.out.println("received from client"+ctx.channel().remoteAddress());
//		System.out.println("message: "+(String)msg);
//		ByteBuf mes = Unpooled.directBuffer();
//		
//		ctx.writeAndFlush(mes.writeBytes("HEART_BEAT".getBytes()));

//		ctx.writeAndFlush("hello world 1");
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx){
		System.out.println("client "+ count++);
		
	}
	
	

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx,Object obj){
		if(obj instanceof IdleStateEvent){
			IdleStateEvent tmp = (IdleStateEvent)obj;
			if(tmp.state() == IdleState.READER_IDLE){
				System.out.println("lose CONNECTION");
				count++;
				if(count>2){
					System.out.println("timeout over 2");
					ctx.channel().close();
				}
			}else {
				try {
					super.userEventTriggered(ctx, obj);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
      throws Exception
  {
//		  ctx.close();
		  System.out.println("errrrrrrrrrrr");
		  System.out.println(cause);
      ctx.fireExceptionCaught(cause);
  }
		
}
