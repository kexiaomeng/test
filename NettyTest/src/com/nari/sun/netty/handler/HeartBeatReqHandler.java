package com.nari.sun.netty.handler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.nari.sun.netty.obj.CommType;
import com.nari.sun.netty.obj.Header;
import com.nari.sun.netty.obj.NettyMessage;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class HeartBeatReqHandler extends ChannelHandlerAdapter{

	private volatile ScheduledFuture<?> heartBeat;
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object obj){
		
		NettyMessage msg = (NettyMessage)obj;

		if(msg != null && msg.getHeader().getType() == (byte)CommType.TYPE_4.getId()){
			System.out.println("启动心跳定时任务");
			//发送心跳帧定时任务
			heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeat(ctx), 0, 30000, TimeUnit.MILLISECONDS);	
		}else if(msg.getHeader() != null && msg.getHeader().getType() == (byte)CommType.TYPE_6.getId()){
			System.out.println(msg.toString());

			System.out.println("心跳响应帧");
		}else {
			ctx.fireChannelRead(obj);
		}
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)throws Exception{
		cause.printStackTrace();
		if(heartBeat != null){
			heartBeat.cancel(true);
			heartBeat = null;
		}
		ctx.fireExceptionCaught(cause);
	}
	
	
	/**
	 * 
	 * 心跳帧请求
	 *
	 */
	class HeartBeat implements Runnable{
		private final ChannelHandlerContext ctx;
		public HeartBeat(ChannelHandlerContext ctx){
			this.ctx = ctx;
		}
		@Override
		public void run(){
			NettyMessage msg = buildHeartBeat();
			ctx.writeAndFlush(msg);
		}
		private NettyMessage buildHeartBeat() {
			NettyMessage msg = new NettyMessage();
			Header header = new Header();
			header.setType((byte)CommType.TYPE_1.getId());
			msg.setHeader(header);
			return msg;
		}
	}
}
