package com.nari.sun.netty;

import java.nio.ByteBuffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ClientHandler extends ChannelHandlerAdapter{
	
	@Override
	 public void channelActive(ChannelHandlerContext ctx) throws Exception{
		String bufferString = "00010001000100020100";
//    	for(int i = 0;i<10;i++){
    		byte[] req = transHexStrToBuff(bufferString);
    	
    		ByteBuf buf = Unpooled.directBuffer();
    		buf.writeBytes(req);
    		
    		ctx.writeAndFlush(buf);
    		if (!buf.isWritable())
    		    System.out.println("Send order 2 server succeed.");
//    	}
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object obj){
		String msg = (String)obj;
		System.out.println("received from Server："+msg);
		String bufferString = "0001000100010002010000";
//    	for(int i = 0;i<10;i++){
    		byte[] req = transHexStrToBuff(bufferString);
    	
    		ByteBuf buf = Unpooled.directBuffer();
    		buf.writeBytes(req);
    		
    		ctx.writeAndFlush(buf);
    		if (!buf.isWritable())
    		    System.out.println("Send order 2 server succeed.");
    	}
//	}
	
	   public static byte[] transHexStrToBuff(String hexString){
			int length = hexString.length();
			byte[] buffer = new byte[length / 2];

			for (int i = 0; i < length; i += 2) {
				buffer[i / 2] = (byte) ((toByte(hexString.charAt(i)) << 4) | toByte(hexString
						.charAt(i + 1)));
			}

			return buffer;
		}
	    
		private static int toByte(char c) {
			if (c >= '0' && c <= '9')
				return (c - '0');
			if (c >= 'A' && c <= 'F')
				return (c - 'A' + 10);
			if (c >= 'a' && c <= 'f')
				return (c - 'a' + 10);

			throw new RuntimeException("Invalid hex char '" + c + "'");
		}
		
}
