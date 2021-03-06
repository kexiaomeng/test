package com.nari.sun.netty.codec;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;

import com.nari.sun.netty.obj.Header;
import com.nari.sun.netty.obj.NettyMessage;



import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;

public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder{
	
	public NettyMessageDecoder(int maxFrameLength,
			int lengthFieldOffset,int lengthFileldLength) throws IOException {
		super(maxFrameLength, lengthFieldOffset,lengthFileldLength);
		// TODO Auto-generated constructor stub
		marshallingDecoder  = new MarshallingDecoder();
	}

	private MarshallingDecoder marshallingDecoder;
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
    throws Exception{
		
		System.out.println("信息解码");
		ByteBuf buf = (ByteBuf)super.decode(ctx, in);
		if(buf == null){
			System.out.println("收到信息为空");
			return null;
		}
		NettyMessage msg = new NettyMessage();
		Header header = new Header();
		
		header.setCrcCode(buf.readInt());
		header.setLength(buf.readInt());
		header.setSessionId(buf.readLong());
		header.setType(buf.readByte());
		header.setPriority(buf.readByte());
		
		if(buf.readableBytes() > 4){
			msg.setBody(marshallingDecoder.decode(buf));
		}
		msg.setHeader(header);
		return msg;
	}

	

}
