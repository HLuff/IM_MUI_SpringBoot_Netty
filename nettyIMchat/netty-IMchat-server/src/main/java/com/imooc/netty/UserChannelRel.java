/**  
* <p>Title: UserChannelRel.java</p>   
* @author Luff
* @date 2019年6月23日  
*/  
package com.imooc.netty;

import java.util.HashMap;

import io.netty.channel.Channel;

/**
 * 用户Id和channel的关联关系处理
 */
public class UserChannelRel {
	
	private static HashMap<String,Channel> manager = new HashMap<>();
	
	public static void put(String senderId, Channel channel){
		manager.put(senderId, channel);
	}
	
	public static Channel get(String senderId){
		return manager.get(senderId);
	}
	
	public static void output(){
		for(HashMap.Entry<String,Channel> entry:manager.entrySet()){
			System.out.println("UserId:" + entry.getKey()
							+ ",ChannelId" + entry.getValue().id().asLongText());
		}
	}
}
