package com.nari.sun.netty.codec;

import java.io.IOException;
import java.util.List;




import com.nari.sun.netty.obj.Header;
import com.nari.sun.netty.obj.NettyMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

public class NettyMessageEncoder extends MessageToByteEncoder<NettyMessage>{

	private MarshallingEncoder marshallingEncoder;
	
	public NettyMessageEncoder() throws IOException{
		this.marshallingEncoder = new MarshallingEncoder();
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, NettyMessage msg,
			ByteBuf buf) throws Exception {
		// TODO Auto-generated method stub
		if(msg == null || msg.getHeader() == null){
			throw new Exception("msg is null");
		}
		
		/**
		 * 将msg对象转成bytebuf进行编码
		 */
		
		System.out.println("信息编码");
		
		
		buf.writeInt(msg.getHeader().getCrcCode());
		buf.writeInt(msg.getHeader().getLength());
		buf.writeLong(msg.getHeader().getSessionId());
		buf.writeByte(msg.getHeader().getType());
		buf.writeByte(msg.getHeader().getPriority());
		
		if(msg.getBody() != null){
			marshallingEncoder.encoder(msg.getBody(),buf);
		}else {
			buf.writeInt(0);
		}
		buf.setInt(4, buf.readableBytes()-8);

		
	}

	

}
