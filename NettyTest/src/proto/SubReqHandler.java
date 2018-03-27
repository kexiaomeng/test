package proto;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;


public class SubReqHandler extends ChannelHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
		SubScribeReq1.SubScribeReq req = (SubScribeReq1.SubScribeReq)msg;
		if("sunmeng".equals(req.getName())){
			System.out.println(req.toString());
		}
		ctx.writeAndFlush(resp(req.getSubId()));
	}
	
	private SubScribeResp.SubScribeReq resp(int id){
		SubScribeResp.SubScribeReq.Builder build = SubScribeResp.SubScribeReq.newBuilder();
		build.setSubReqId(1);
		build.setResqCode(id);
		build.setDesc("hello world");
		return build.build();
	}
	@Override 
	 public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
		cause.printStackTrace();
		ctx.close();
	}
}
