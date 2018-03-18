import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;


public class EchoServerHandler extends ChannelHandlerAdapter{
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
