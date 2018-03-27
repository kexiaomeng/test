package com.nari.sun.netty;

import java.nio.charset.Charset;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class NettyClient implements Runnable{
	
	private String host;
	private int port;
	private EventLoopGroup parent = null;

	public NettyClient(String host,int port){
		this.host = host;
		this.port = port;
	}
	
	public void connect(String host,int port) throws InterruptedException{
		
		parent  = new NioEventLoopGroup();
		Bootstrap client = new Bootstrap();
		
		try {
			client.group(parent).option(ChannelOption.SO_BACKLOG, 1024).channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel arg0) throws Exception {
					arg0.pipeline().addLast(new LineBasedFrameDecoder(20));
					arg0.pipeline().addLast(new StringDecoder());
					arg0.pipeline().addLast(new ClientHandler());
				}
				
				
				
			});
			
			ChannelFuture  future  = client.connect(host, port).sync();
			future.channel().closeFuture().sync();
			
		}finally{
			parent.shutdownGracefully();
		}
		
		
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			connect(host, port);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
