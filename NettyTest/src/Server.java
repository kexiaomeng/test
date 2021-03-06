import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;


public class Server {
	public void bind(int port) throws InterruptedException{
		EventLoopGroup parent = new NioEventLoopGroup();
		EventLoopGroup child  = new NioEventLoopGroup();
		
		ServerBootstrap serBootstrap = new ServerBootstrap();
		try{
			serBootstrap.group(parent, child).channel(NioServerSocketChannel.class).
				childHandler(new ChannelInitializer<SocketChannel>() {
	
					@Override
					protected void initChannel(SocketChannel arg0) throws Exception {
						// TODO Auto-generated method stub
//						 ByteBuf delimiter = Unpooled.copiedBuffer("$"
//								    .getBytes());
//						 
//						arg0.pipeline().addLast(new DelimiterBasedFrameDecoder(20,delimiter));
						arg0.pipeline().addLast(new FixedLengthFrameDecoder(10));
						arg0.pipeline().addLast(new StringDecoder());
						arg0.pipeline().addLast(new EchoServerHandler());
						
					}
			}).option(ChannelOption.SO_BACKLOG,1024);
			
			ChannelFuture f = serBootstrap.bind(port).sync();
			
			
			f.channel().closeFuture().sync();
		}finally{
			parent.shutdownGracefully();
			child.shutdownGracefully();
		}
		
	}
	public static void main(String[] args) throws InterruptedException {
		new Server().bind(8080);	
	}
}
