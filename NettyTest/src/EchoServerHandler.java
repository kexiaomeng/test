import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nari.sun.TestHandler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;


public class EchoServerHandler extends ChannelHandlerAdapter{
	private static Logger test  = LoggerFactory.getLogger(TestHandler.class);

	@Override 
	public void channelRead(ChannelHandlerContext ctx, Object msg){
		String context  = (String)msg;
		System.out.println("Receive message : "+context);
		ctx.writeAndFlush("back to client");
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)throws Exception
    {
		ctx.close();
		ctx.fireExceptionCaught(cause);
    }

}
