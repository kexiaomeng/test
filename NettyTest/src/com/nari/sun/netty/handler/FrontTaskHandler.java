package com.nari.sun.netty.handler;

import kafkaListen.object.Archives;
import kafkaListen.process.Producter;
import kafkaListen.util.SerialUtil;

import com.nari.sun.netty.obj.NettyMessage;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class FrontTaskHandler extends ChannelHandlerAdapter{

	
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object obj){
		NettyMessage msg = (NettyMessage)obj;
			
		Archives archives = new Archives();
		Producter producter = null;
		try {
			producter = new Producter();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		archives.setTerminalAddr(msg.getHeader().getCrcCode()+"");
		archives.setOptType(msg.getHeader().getType());
		
		
		producter.send("test", SerialUtil.getBytes(archives));
		
	}
	
}
