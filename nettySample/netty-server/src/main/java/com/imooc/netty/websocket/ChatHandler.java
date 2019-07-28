/**  
* <p>Title: ChatHandler.java</p>   
* @author Luff
* @date 2019年4月12日  
*/  
package com.imooc.netty.websocket;

import java.time.LocalDateTime;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 处理消息的handler
 * TextWebSocketFrame：在netty中，是用于为websocket转门处理文本对象，frame是消息的载体
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{
	//用于记录和管理所有客户端的channel
	private static ChannelGroup clients = 
			new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	/* (non-Javadoc)
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		//获取客户端传输过来的消息
		String content = msg.text();
		System.out.println("接受到的数据" + content);
		
//		for(Channel channel:clients){
//			channel.writeAndFlush(new TextWebSocketFrame(
//					"服务器在" + LocalDateTime.now()
//					+ "接受到消息，消息为" + content));
//			System.out.println(111);
//		}

		//下面这个方法，和上面for循环一致
		clients.writeAndFlush(
		       new TextWebSocketFrame(
				      "服务器在" + LocalDateTime.now()
				      + "接受到消息，消息为" + content));
				
		
	}

	/**
	 * 当客户端连接服务端之后
	 * 获取客户端的channel，并且放入ChannelGroup中去管理
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		clients.add(ctx.channel());
	}

	/* (non-Javadoc)
	 * @see io.netty.channel.ChannelHandlerAdapter#handlerRemoved(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		//当触发handleRemoved,ChannelGroup会自动移除对应的客户端的channel
		//clients.remove(ctx.channel());
		System.out.println("客户端断开，channel对应的长id：" + ctx.channel().id().asLongText());
		System.out.println("客户端断开，channel对应的短id：" + ctx.channel().id().asShortText());
	}
	

}
