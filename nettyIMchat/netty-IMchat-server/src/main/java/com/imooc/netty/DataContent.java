/**  
* <p>Title: DataContent.java</p>   
* @author Luff
* @date 2019年6月23日  
*/  
package com.imooc.netty;

import java.io.Serializable;

public class DataContent implements Serializable {

	private static final long serialVersionUID = -2459444157511262346L;
	private Integer action; 	// 动作类型
	private ChatMsg chatMsg;	// 用户的聊天内容entity
	private String extend;		//扩展字段
	/**
	 * @return the action
	 */
	public Integer getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(Integer action) {
		this.action = action;
	}
	/**
	 * @return the chatMsg
	 */
	public ChatMsg getChatMsg() {
		return chatMsg;
	}
	/**
	 * @param chatMsg the chatMsg to set
	 */
	public void setChatMsg(ChatMsg chatMsg) {
		this.chatMsg = chatMsg;
	}
	/**
	 * @return the extend
	 */
	public String getExtend() {
		return extend;
	}
	/**
	 * @param extend the extend to set
	 */
	public void setExtend(String extend) {
		this.extend = extend;
	}
	
	
	

}
