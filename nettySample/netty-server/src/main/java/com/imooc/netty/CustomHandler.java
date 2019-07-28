/**  
* <p>Title: customHandler.java</p>   
* @author Luff
* @date 2019年4月12日  
*/  
package com.imooc.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * @Description:创建自定义助手类
 */

//SimpleChannelInboundHandler:对于请求来讲，其实相当于[入站，入境]
public class CustomHandler extends SimpleChannelInboundHandler<HttpObject>{

	/* (non-Javadoc)
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		//获取channel
		Channel channel = ctx.channel();
		
		
		if(msg instanceof HttpRequest){
			
			//显示客户端的远程地址
			System.out.println(channel.remoteAddress());
			
			//定义发送的数据消息
			ByteBuf content  = Unpooled.copiedBuffer("Hello netty~",CharsetUtil.UTF_8);
			
			//构建一个http response
			FullHttpResponse response = 
					new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
							HttpResponseStatus.OK,
							content);
			//为响应增加数据类型和长度
			response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
			response.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());
			
			//把响应刷到客户端
			ctx.writeAndFlush(response);
			
		}
		
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRegistered(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channel...注册");
		super.channelRegistered(ctx);
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelUnregistered(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channel...移除");
		super.channelUnregistered(ctx);
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channel...活跃");
		super.channelActive(ctx);
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelInactive(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channel...不活跃");
		super.channelInactive(ctx);
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelReadComplete(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channel读取完毕...");
		super.channelReadComplete(ctx);
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#userEventTriggered(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		System.out.println("用户事件触发...");
		super.userEventTriggered(ctx, evt);
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelWritabilityChanged(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channel可写更改");
		super.channelWritabilityChanged(ctx);
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("捕获到异常");
		super.exceptionCaught(ctx, cause);
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerAdapter#handlerAdded(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		System.out.println("助手类添加");
		super.handlerAdded(ctx);
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerAdapter#handlerRemoved(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		System.out.println("助手类移除");
		super.handlerRemoved(ctx);
	}

}
