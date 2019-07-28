/**  
* <p>Title: DataContent.java</p>   
* @author Luff
* @date 2019年6月23日  
*/  
package com.imooc.netty;

import java.io.Serializable;

public class ChatMsg implements Serializable {

	private static final long serialVersionUID = -5433993826757088277L;
	
	private String senderId;      //发送者的用户Id
	private String receiverId;    //接受者的用户Id
	private String msg;           //聊天内容
	private String msgId;		  //用于消息的签收
	/**
	 * @return the senderId
	 */
	public String getSenderId() {
		return senderId;
	}
	/**
	 * @param senderId the senderId to set
	 */
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	/**
	 * @return the receiverId
	 */
	public String getReceiverId() {
		return receiverId;
	}
	/**
	 * @param receiverId the receiverId to set
	 */
	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}
	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}
	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
	/**
	 * @return the msgId
	 */
	public String getMsgId() {
		return msgId;
	}
	/**
	 * @param msgId the msgId to set
	 */
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}



}
