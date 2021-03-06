package com.nari.sun.netty.main;

import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.nari.sun.netty.codec.NettyMessageDecoder;
import com.nari.sun.netty.codec.NettyMessageEncoder;
import com.nari.sun.netty.handler.FrontTaskHandler;
import com.nari.sun.netty.handler.HeartBeatRespHandler;
import com.nari.sun.netty.handler.LoginAuthReqHandler;
import com.nari.sun.netty.handler.LoginAuthRespHandler;
import com.sun.corba.se.impl.orbutil.closure.Future;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyServer {
	
	private EventLoopGroup parent = new NioEventLoopGroup();
	private EventLoopGroup child  = new NioEventLoopGroup();
	private ServerBootstrap serverBootstrap = new ServerBootstrap();
	
	public NettyServer(){
		
	}
	public void bind(int port) throws InterruptedException{
		try{
		serverBootstrap.group(parent, child).
		option(ChannelOption.SO_BACKLOG, 1024).channel(NioServerSocketChannel.class)
		.handler(new LoggingHandler(LogLevel.DEBUG))
		.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel arg0) throws Exception{
					arg0.pipeline().addLast("messageDecoder",new NettyMessageDecoder(1024, 4, 4));
					arg0.pipeline().addLast("messageEncoder",new NettyMessageEncoder());
					arg0.pipeline().addLast("loginAUTH",new LoginAuthRespHandler());
					arg0.pipeline().addLast("heartBeat",new HeartBeatRespHandler());
					arg0.pipeline().addLast("taskHandler",new FrontTaskHandler());
									}
			});
		
			ChannelFuture future = null;
	
			 future = serverBootstrap.bind(port).sync();
			 System.out.println("server start success");
			 future.channel().closeFuture().sync();
	
			// TODO Auto-generated catch block
		
		}finally{
			parent.shutdownGracefully();
			child.shutdownGracefully();
		}
	}
	public static void main(String[] args) throws InterruptedException {
		new NettyServer().bind(8080);
	}
}
