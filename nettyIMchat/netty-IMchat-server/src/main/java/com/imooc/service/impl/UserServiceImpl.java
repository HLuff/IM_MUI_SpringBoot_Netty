/**  
* <p>Title: UserServiceImpl.java</p>   
* @author Luff
* @date 2019年4月13日  
*/  
package com.imooc.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.imooc.enums.MsgActionEnum;
import com.imooc.enums.MsgSignFlagEnum;
import com.imooc.enums.SearchFriendsStatusEnum;
import com.imooc.mapper.ChatMsgMapper;
import com.imooc.mapper.FriendsRequestMapper;
import com.imooc.mapper.MyFriendsMapper;
import com.imooc.mapper.UsersMapper;
import com.imooc.mapper.UsersMapperCustom;
import com.imooc.netty.ChatMsg;
import com.imooc.netty.DataContent;
import com.imooc.netty.UserChannelRel;
import com.imooc.pojo.FriendsRequest;
import com.imooc.pojo.MyFriends;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.FriendRequestVO;
import com.imooc.pojo.vo.MyFriendsVO;
import com.imooc.service.UserService;
import com.imooc.utils.FastDFSClient;
import com.imooc.utils.FileUtils;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.QRCodeUtils;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UsersMapper usersMapper;
	
	@Autowired
	private UsersMapperCustom usersMapperCustom;
	
	@Autowired
	private Sid sid;
	
	@Autowired
	private QRCodeUtils qrCodeUtils;
	
	@Autowired
	private FastDFSClient fastDFSClient;
	
	@Autowired
	private MyFriendsMapper myFriendsMapper;
	
	@Autowired
	private ChatMsgMapper chatMsgMapper;
	
	@Autowired
	private FriendsRequestMapper friendsRequestMapper;


	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean queryUsernameIsExist(String username) {
		
		Users user = new Users();
		user.setUsername(username);
		
		Users result =  usersMapper.selectOne(user);
		
		return result != null ? true : false;
	}


	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Users queryUserForLogin(String username, String pwd) {
		
		Example userExample = new Example(Users.class);
		Criteria criteria = userExample.createCriteria();
		
		criteria.andEqualTo("username",username);
		criteria.andEqualTo("password",pwd);
		
		Users result = usersMapper.selectOneByExample(userExample);
		
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public Users saveUser(Users user) {
		
		String userId = sid.nextShort();
		
		//为每个用户生成一个唯一的二维码
		String qrCodePath = "D://user" + userId + "qrcode.png";
		//muxin_qrcode:[username]
		qrCodeUtils.createQRCode(qrCodePath, "muxin_qrcode:" + user.getUsername());
		
		//上传文件到fastdfs
		MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);
		
		String qrCodeUrl = "";
		try {
			qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		user.setQrcode(qrCodeUrl);
		
		user.setId(userId);
		usersMapper.insert(user);
		
		return user;
	}


	/* (non-Javadoc)
	 * @see com.imooc.service.UserService#updateUserInfo(com.imooc.pojo.Users)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public Users updateUserInfo(Users user) {
		usersMapper.updateByPrimaryKeySelective(user);
		return queryUserById(user.getId());
		
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	private Users queryUserById(String userId){
		return usersMapper.selectByPrimaryKey(userId);
	}


	/* (non-Javadoc)
	 * @see com.imooc.service.UserService#preconditionSearchFriends(java.lang.String, java.lang.String)
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Integer preconditionSearchFriends(String myUserId, String friendUsername) {

		Users user = queryUserInfoByUsername(friendUsername);
		
		//1.搜索的用户如果不存在，返回[无此用户]
		if(user == null){
			return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
		}
		//2.搜索的账号是你自己，返回[不能添加自己]
		if(user.getId().equals(myUserId))
			return SearchFriendsStatusEnum.NOT_YOURSELF.status;
		
		//3.搜索的用户已经是你的好友，返回[该用户已经是你的好友]
		Example mfe = new Example(MyFriends.class);
		Criteria mfc = mfe.createCriteria();
		mfc.andEqualTo("myUserId",myUserId);
		mfc.andEqualTo("myFriendUserId",user.getId());
		MyFriends myFriendsRel = myFriendsMapper.selectOneByExample(mfe);
		if(myFriendsRel != null){
			return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
		}
		
		return SearchFriendsStatusEnum.SUCCESS.status;
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Users queryUserInfoByUsername(String username){
		Example ue = new Example(Users.class);
		Criteria uc = ue.createCriteria();
		uc.andEqualTo("username",username);
		
		return usersMapper.selectOneByExample(ue); 
	}


	/* (non-Javadoc)
	 * @see com.imooc.service.UserService#sendFriendRequest(java.lang.String, java.lang.String)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void sendFriendRequest(String myUserId, String friendUsername) {
		
		//根据用户名把朋友信息查询出来
		Users friend = queryUserInfoByUsername(friendUsername);
		
		//1.查询发送好友请求记录
		Example fre = new Example(FriendsRequest.class);
		Criteria frc = fre.createCriteria();
		frc.andEqualTo("sendUserId",myUserId);
		frc.andEqualTo("acceptUserId",friend.getId());
		FriendsRequest friendRequest =  friendsRequestMapper.selectOneByExample(fre);
		if(friendRequest == null){
			//2.如果不是你的好友，并且好友记录没有添加，则新添好友记录请求
			String requestId = sid.nextShort();
			
			FriendsRequest request = new FriendsRequest();
			request.setId(requestId);
			request.setSendUserId(myUserId);
			request.setAcceptUserId(friend.getId());
			request.setRequestDateTime(new Date());
			friendsRequestMapper.insert(request);
		}
	}


	/* (non-Javadoc)
	 * @see com.imooc.service.UserService#queryFriendRequestList(java.lang.String)
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<FriendRequestVO> queryFriendRequestList(String acceptUserId) {
		
		return usersMapperCustom.queryFriendRequestList(acceptUserId);
	}


	/* (non-Javadoc)
	 * @see com.imooc.service.UserService#deleteFirendRequest(java.lang.String, java.lang.String)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void deleteFirendRequest(String sendUserId, String acceptUserId) {
		
		Example fre = new Example(FriendsRequest.class);
		Criteria frc = fre.createCriteria();
		frc.andEqualTo("sendUserId",sendUserId);
		frc.andEqualTo("acceptUserId",acceptUserId);
		friendsRequestMapper.deleteByExample(fre);
	}


	/* (non-Javadoc)
	 * @see com.imooc.service.UserService#passFirendRequest(java.lang.String, java.lang.String)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void passFirendRequest(String sendUserId, String acceptUserId) {
		//1.保存好友 2.逆向保存好友 3.删除好友请求
		saveFriends(sendUserId,acceptUserId);
		saveFriends(acceptUserId,sendUserId);
		deleteFirendRequest(sendUserId,acceptUserId);
		
		Channel sendChannel = UserChannelRel.get(sendUserId);
		if(sendChannel != null){
			//使用websocket主动推送消息到请求发起者，更新他的通讯录为最新
			DataContent dataContent = new DataContent();
			dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);
			
			sendChannel.writeAndFlush(
					new TextWebSocketFrame(
							JsonUtils.objectToJson(dataContent)));
		}
		
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	private void saveFriends(String sendUserId,String acceptUserId){
		
		MyFriends myFriends = new MyFriends();
		String recordId = sid.nextShort();
		myFriends.setId(recordId);
		myFriends.setMyFriendUserId(acceptUserId);
		myFriends.setMyUserId(sendUserId);
		myFriendsMapper.insert(myFriends);
		
	}


	/* (non-Javadoc)
	 * @see com.imooc.service.UserService#queryMyFriends(java.lang.String)
	 */
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<MyFriendsVO> queryMyFriends(String userId) {
		List<MyFriendsVO> myFriends = usersMapperCustom.queryMyFriends(userId);
		return myFriends;
	}


	/* (non-Javadoc)
	 * @see com.imooc.service.UserService#saveMsg(com.imooc.netty.ChatMsg)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String saveMsg(ChatMsg chatMsg) {
		
		com.imooc.pojo.ChatMsg msgDB = new com.imooc.pojo.ChatMsg();
		String msgId = sid.nextShort();
		msgDB.setId(msgId);
		msgDB.setAcceptUserId(chatMsg.getReceiverId());
		msgDB.setSendUserId(chatMsg.getSenderId());
		msgDB.setCreateTime(new Date());
		msgDB.setSignFlag(MsgSignFlagEnum.unsign.type);
		msgDB.setMsg(chatMsg.getMsg());
		
		chatMsgMapper.insert(msgDB);
		
		return msgId;
	}


	/* (non-Javadoc)
	 * @see com.imooc.service.UserService#updateMsgSigned(java.util.List)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void updateMsgSigned(List<String> msgIdList) {
		// TODO Auto-generated method stub
		usersMapperCustom.batchUpdateMsgSigned(msgIdList);
	}



	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<com.imooc.pojo.ChatMsg> getUnReadMsgList(String acceptUserId) {
		
		Example chatExample = new Example(com.imooc.pojo.ChatMsg.class);
		Criteria chatCriteria = chatExample.createCriteria();
		chatCriteria.andEqualTo("signFlag", 0);
		chatCriteria.andEqualTo("acceptUserId", acceptUserId);
		
		List<com.imooc.pojo.ChatMsg> result = chatMsgMapper.selectByExample(chatExample);
		
		return result;
	}
	

}
