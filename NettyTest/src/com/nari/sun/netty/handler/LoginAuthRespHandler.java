package com.nari.sun.netty.handler;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sun.org.mozilla.javascript.internal.Token.CommentType;

import com.nari.sun.netty.obj.CommType;
import com.nari.sun.netty.obj.Header;
import com.nari.sun.netty.obj.NettyMessage;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
/**
 * 服务端响应握手认证
 * @author lenovo
 *
 */
public class LoginAuthRespHandler extends ChannelHandlerAdapter{
	private Map<String, Boolean> ipCheck = new ConcurrentHashMap<String, Boolean>(); 
	private static List<String> whiteList = new ArrayList<String>();
	//登陆白名单设置
	static{
		whiteList.add("127.0.0.1");
		
	}
	@Override
	 public void channelActive(ChannelHandlerContext ctx)throws Exception{
		System.out.println("客户端接入"+ctx.channel().remoteAddress().toString());
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object obj)throws Exception{
    	System.out.println("接收客户端信息");
    	
		NettyMessage msg = (NettyMessage)obj;
    	Header header = msg.getHeader();
    	System.out.println(msg.toString());

    	
    	if(header != null && header.getType() == (byte)CommType.TYPE_3.getId()){
    	   	String ipAddr = ctx.channel().remoteAddress().toString();
        	NettyMessage loginResp = null;
        	
        	if(ipCheck.containsKey(ipAddr)){
        		System.err.println("重复登陆，拒绝");
        		loginResp = buildResp((byte)-1);
        	}else {
				InetSocketAddress address = (InetSocketAddress)ctx.channel().remoteAddress();
				String ip = address.getAddress().getHostAddress();
				if(whiteList.contains(ip)){
					loginResp = buildResp((byte)0);
					ipCheck.put(ipAddr, true);
				}else {
					System.out.println("不属于白名单的远程地址");
					loginResp = buildResp((byte)-1);
				}
				
			}
			System.out.println("login result is："+loginResp.getBody());

    		ctx.writeAndFlush(loginResp);
    	}else{
    		ctx.fireChannelRead(obj);
    	}
	}
	private NettyMessage buildResp(byte b) {
	// TODO Auto-generated method stub
		NettyMessage msg = new NettyMessage();
		Header header = new Header();
		
		header.setType((byte)CommType.TYPE_4.getId());
		msg.setBody(b);
		msg.setHeader(header);
		
		return msg;
}
	@Override 
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
        throws Exception{
		ipCheck.remove(ctx.channel().remoteAddress().toString());
		ctx.close();
		cause.printStackTrace();
        ctx.fireExceptionCaught(cause);
    }
	
	
	
}
