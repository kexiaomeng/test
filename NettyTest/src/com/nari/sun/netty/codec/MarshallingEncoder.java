package com.nari.sun.netty.codec;

import java.io.IOException;

import org.jboss.marshalling.Marshaller;
import io.netty.buffer.ByteBuf;

public class MarshallingEncoder {
	
	private final static byte[] LENGTHHOLDER = new byte[4];
	private Marshaller marshaller;
	
	public MarshallingEncoder() throws IOException{
		marshaller = MarshallingCodeCFactory.buildMarshalling();
	}
	public void encoder(Object obj,ByteBuf buf) throws IOException{
		int lengthPos = buf.writerIndex();
		
		buf.writeBytes(LENGTHHOLDER);
		
		ChannelBufferByteOutput outPut = new ChannelBufferByteOutput(buf);
		marshaller.start(outPut);
		marshaller.writeObject(obj);
		marshaller.finish();
		buf.setInt(lengthPos, buf.writerIndex()-lengthPos-4);
		
		marshaller.close();
		
	}
}
