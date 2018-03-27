package proto;

import java.nio.ByteBuffer;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqClientHandler extends ChannelHandlerAdapter{

	@Override 
	public void channelActive(ChannelHandlerContext ctx)
    throws Exception{
		for(int i =0 ;i<5;i++){
			ctx.write(req(i));
		}
		ctx.flush();
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
		SubScribeResp.SubScribeReq req = (SubScribeResp.SubScribeReq)msg;
		System.out.println(req.toString());
		ByteBuffer buffer = ByteBuffer.allocate(20);
	}
	
	private SubScribeReq1.SubScribeReq req(int i){
		SubScribeReq1.SubScribeReq.Builder build = SubScribeReq1.SubScribeReq.newBuilder();
		build.setSubId(i);
		build.setName("sunmeng");
		build.setAddr("hello world");
		build.setProductName("book");
		return build.build();
	}
	@Override 
	 public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
		cause.printStackTrace();
		ctx.close();
	}
}
