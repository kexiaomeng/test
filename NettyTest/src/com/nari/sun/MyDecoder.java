package com.nari.sun;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class MyDecoder extends ByteToMessageDecoder{

	@Override
	protected void decode(ChannelHandlerContext arg0, ByteBuf buf,
			List<Object> out) throws Exception {
		
		byte[] msg = new byte[buf.readableBytes()];
		buf.readBytes(msg);
		int msgLength = msg[7];
		
		System.out.println("消息体长度: "+msgLength+" 消息长度: "+msg.length);
		if(msg.length <  msgLength){
			return ;
		}
		
		
		
		
		
		if(msg.length - 8 >= msgLength){
			/**
			 * 解决粘包问题，父类会循环调用decode方法
			 */
			buf.readerIndex(msgLength+8);
			buf.discardReadBytes();
			/**
			 * 
			 */
			/**
			 * 必须使buf被读取，必须使用中间变量获取当前ByteBUF netty机制防止decode无限生成
			 */
			ByteBuf tmp  = buf.slice(0,msgLength+8);
			tmp.retain();
			
//			= Unpooled.buffer();
//			tmp.writeBytes(msg);
			
			System.out.println("数据合法,"+tmp.refCnt());
			out.add(tmp);
		}else {
			return ;
		}
		
	}

}
