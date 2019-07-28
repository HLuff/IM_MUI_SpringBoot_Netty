/**  
* <p>Title: UserService.java</p>   
* @author Luff
* @date 2019年4月13日  
*/  
package com.imooc.service;

import java.util.List;

import com.imooc.netty.ChatMsg;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.FriendRequestVO;
import com.imooc.pojo.vo.MyFriendsVO;

public interface UserService {
	
	/**
	 * 判断用户名是否存在
	 * <p>Title: queryUsernameIsExist</p>  
	 * @param username
	 * @return
	 */
	public boolean queryUsernameIsExist(String username);
	
	/**
	 * 查询用户是否登录
	 * <p>Title: queryUserForLogin</p>  
	 * @param username
	 * @param pwd
	 * @return
	 */
	public Users queryUserForLogin(String username, String pwd);
	
	/**
	 * 用户注册
	 * <p>Title: saveUser</p>  
	 * @param user
	 * @return
	 */
	public Users saveUser(Users user);
	
	/**
	 * 修改用户记录
	 * <p>Title: updateUserInfo</p>  
	 * @param usr
	 */
	public Users updateUserInfo(Users user);
	
	/**
	 * 搜索朋友的前置条件
	 * <p>Title: preconditionSearchFriends</p>  
	 * @param myUserId
	 * @param friendUsername
	 * @return
	 */
	public Integer preconditionSearchFriends(String myUserId,String friendUsername);
	
	/**
	 * 根据用户名查询用户对象
	 * <p>Title: queryUserInfoByUsername</p>  
	 * @param username
	 * @return
	 */
	public Users queryUserInfoByUsername(String username);
	
	/**
	 * 添加好友请求记录，保存到数据库
	 * <p>Title: sendFriendRequest</p>  
	 * @param myUserId
	 * @param friendUsername
	 */
	public void sendFriendRequest(String myUserId,String friendUsername);
	
	/**
	 * 查询好友请求
	 * <p>Title: queryFriendRequestList</p>  
	 * @param acceptUserId
	 * @return
	 */
	public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);
	
	/**
	 * 删除好友记录
	 * <p>Title: deleteFirendRequest</p>  
	 * @param sendUserId
	 * @param acceptUserId
	 */
	public void deleteFirendRequest(String sendUserId, String acceptUserId);
	
	/**
	 * 通过好友请求：1.保存好友 2.逆向保存好友 3.删除好友请求
	 * 
	 * <p>Title: deleteFirendRequest</p>  
	 * @param sendUserId
	 * @param acceptUserId
	 */
	public void passFirendRequest(String sendUserId, String acceptUserId);
	
	/**
	 * 查询好友列表
	 * <p>Title: queryMyFriends</p>  
	 * @param userId
	 * @return
	 */
	public List<MyFriendsVO> queryMyFriends(String userId);
	
	/**
	 * 保存聊天消息到数据库
	 * <p>Title: saveMsg</p>  
	 * @param chatMsg
	 * @return
	 */
	public String saveMsg(ChatMsg chatMsg);
	
	/**
	 * 批量签收消息
	 * <p>Title: updateMsgSigned</p>  
	 * @param msgIdList
	 */
	public void updateMsgSigned(List<String> msgIdList);
	
	/**
	 * 获取未签收列表
	 * <p>Title: getUnReadMsgList</p>  
	 * @param acceptUserId
	 * @return
	 */
	public List<com.imooc.pojo.ChatMsg> getUnReadMsgList(String acceptUserId);
	

}
