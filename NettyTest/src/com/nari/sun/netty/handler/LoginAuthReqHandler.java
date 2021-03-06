package com.nari.sun.netty.handler;

import sun.org.mozilla.javascript.internal.Token.CommentType;

import com.nari.sun.netty.obj.CommType;
import com.nari.sun.netty.obj.Header;
import com.nari.sun.netty.obj.NettyMessage;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
/**
 * 客户端请求握手认证
 * @author lenovo
 *
 */
public class LoginAuthReqHandler extends ChannelHandlerAdapter{
	
	@Override
	 public void channelActive(ChannelHandlerContext ctx)throws Exception{
		System.out.println("链路激活");
    	ctx.writeAndFlush(buildLoginReq());
    	
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object obj)throws Exception{
    	NettyMessage msg = (NettyMessage)obj;
    	Header header = msg.getHeader();
		System.out.println(msg.toString());

    	if(header != null && header.getType() == (byte)CommType.TYPE_4.getId()){
    		byte loginResult = (Byte)msg.getBody();
    		if(loginResult != (byte)0){
    			System.out.println("握手失败，断开连接");
    			ctx.channel().close().sync();
    		}else {
				System.out.println("握手成功，连接成功");
				ctx.fireChannelRead(obj);
			}
    	}else{
    		ctx.fireChannelRead(obj);
    	}
	}
	@Override 
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
        throws Exception{
        ctx.fireExceptionCaught(cause);
    }
	
	/**
	 * 创建握手信息
	 * 
	 * @return
	 */
	private NettyMessage buildLoginReq() {
		
		NettyMessage msg = new NettyMessage();
		
		Header header = new Header();
		
		header.setType((byte)CommType.TYPE_3.getId());
		msg.setHeader(header);
		
		return msg;
	}
	
}
