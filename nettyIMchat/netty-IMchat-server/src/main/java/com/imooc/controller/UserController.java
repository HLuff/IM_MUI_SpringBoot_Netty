/**  
* <p>Title: HelloController.java</p>   
* @author Luff
* @date 2019年4月13日  
*/
package com.imooc.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.imooc.enums.OperatorFriendRequestTypeEnum;
import com.imooc.enums.SearchFriendsStatusEnum;
import com.imooc.pojo.ChatMsg;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UsersBO;
import com.imooc.pojo.vo.FriendRequestVO;
import com.imooc.pojo.vo.MyFriendsVO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.FastDFSClient;
import com.imooc.utils.FileUtils;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;

@RestController
@RequestMapping("u")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private FastDFSClient fastDFSClient;

	@PostMapping("/registOrLogin")
	public IMoocJSONResult registOrLogin(@RequestBody Users user) throws Exception {
		System.out.println("登录中");

		// 0.判断用户名密码不能为空
		if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
			return IMoocJSONResult.errorMsg("用户名或密码不能为空");

		}
		// 1.判断用户名是否存在，如果存在就登录，如果不存在就注册
		boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());

		Users userResult = null;
		if (usernameIsExist) {
			// 1.1登录
			userResult = userService.queryUserForLogin(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
			if (userResult == null) {
				return IMoocJSONResult.errorMsg("用户名或密码不正确");
			}

		} else {
			// 1.2注册
			user.setNickname(user.getUsername());
			user.setFaceImage("");
			user.setFaceImageBig("");
			user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
			userResult = userService.saveUser(user);

		}

		UsersVO usersVO = new UsersVO();
		BeanUtils.copyProperties(userResult, usersVO);

		return IMoocJSONResult.ok(usersVO);
	}

	@PostMapping("/uploadFaceBase64")
	public IMoocJSONResult uploadFaceBase64(@RequestBody UsersBO usersBo) throws Exception {
		System.out.println("修改头像");

		// 获取前端传过来的base64字符串，然后转换为文件对象再上传
		String base64Data = usersBo.getFaceData();
		String userFacePath = "D:\\" + usersBo.getUserId() + "userface64.png";
		FileUtils.base64ToFile(userFacePath, base64Data);

		// 上传文件到fastdfs
		MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
		String url = fastDFSClient.uploadBase64(faceFile);
		System.out.println(url);

		// 获取缩略图的url
		String thump = "_80x80.";
		String arr[] = url.split("\\.");
		String thumpImgUrl = arr[0] + thump + arr[1];

		// 更细用户头像
		Users users = new Users();
		users.setId(usersBo.getUserId());
		users.setFaceImage(thumpImgUrl);
		users.setFaceImageBig(url);
		System.out.println(thumpImgUrl);

		Users result = userService.updateUserInfo(users);

		return IMoocJSONResult.ok(result);
	}

	/**
	 * 设置用户昵称
	 * <p>
	 * Title: setNickname
	 * </p>
	 * 
	 * @param usersBo
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/setNickname")
	public IMoocJSONResult setNickname(@RequestBody UsersBO usersBo) throws Exception {
		System.out.println("修改昵称");

		Users users = new Users();
		users.setId(usersBo.getUserId());
		users.setNickname(usersBo.getNickname());

		Users result = userService.updateUserInfo(users);

		return IMoocJSONResult.ok(result);
	}

	/**
	 * 搜索好友接口，根据账号做匹配查询而不是模糊查询
	 * <p>
	 * Title: setNickname
	 * </p>
	 */
	@PostMapping("/search")
	public IMoocJSONResult searchUser(String myUserId, String friendUsername) throws Exception {
		System.out.println("搜索好友");

		// 0.判断用户名密码不能为空
		if (StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUsername)) {
			return IMoocJSONResult.errorMsg("");

		}
		// 前置条件-1.搜索的用户如果不存在，返回[无此用户]
		// 前置条件-2.搜索的账号是你自己，返回[不能添加自己]
		// 前置条件-3.搜索的用户已经是你的好友，返回[该用户已经是你的好友]
		Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);
		if (status == SearchFriendsStatusEnum.SUCCESS.status) {

			Users user = userService.queryUserInfoByUsername(friendUsername);

			UsersVO userVO = new UsersVO();
			BeanUtils.copyProperties(user, userVO);
			return IMoocJSONResult.ok(userVO);
		} else {
			String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
			return IMoocJSONResult.errorMsg(errorMsg);
		}
	}

	/**
	 * 发送添加好友的请求
	 * <p>
	 * Title: setNickname
	 * </p>
	 */
	@PostMapping("/addFriendRequest")
	public IMoocJSONResult addFriendRequest(String myUserId, String friendUsername) throws Exception {
		System.out.println("发送添加好友的请求");

		// 0.判断用户名密码不能为空
		if (StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUsername)) {
			return IMoocJSONResult.errorMsg("");

		}
		// 前置条件-1.搜索的用户如果不存在，返回[无此用户]
		// 前置条件-2.搜索的账号是你自己，返回[不能添加自己]
		// 前置条件-3.搜索的用户已经是你的好友，返回[该用户已经是你的好友]
		Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);
		if (status == SearchFriendsStatusEnum.SUCCESS.status) {
			userService.sendFriendRequest(myUserId, friendUsername);
		} else {
			String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
			return IMoocJSONResult.errorMsg(errorMsg);
		}
		return IMoocJSONResult.ok();

	}

	/**
	 * 查询好友请求
	 * <p>Title: queryFriendRequests</p>  
	 * @param myUserId
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/queryFriendRequests")
	public IMoocJSONResult queryFriendRequests(String myUserId) throws Exception {
		System.out.println("查询好友请求");

		// 0.判断用户名密码不能为空
		if (StringUtils.isBlank(myUserId)) {
			return IMoocJSONResult.errorMsg("");
		}
		List<FriendRequestVO> a = userService.queryFriendRequestList(myUserId);
		return IMoocJSONResult.ok(userService.queryFriendRequestList(myUserId));
	}
	
	/**
	 * 接受方通过或者忽略朋友请求
	 * <p>Title: queryFriendRequests</p>  
	 * @param myUserId
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/operFriendRequest")
	public IMoocJSONResult operFriendRequest(String acceptUserId,String sendUserId,
			Integer operType) throws Exception {
		System.out.println("接受方通过或者忽略朋友请求");

		// 0.判断acceptUserId、sendUserId、operType不能为空
		if (StringUtils.isBlank(acceptUserId) 
				|| StringUtils.isBlank(sendUserId) 
				|| operType == null) {
			return IMoocJSONResult.errorMsg("");
		}
		//1.如果operType没有对应的枚举值，则抛出空错误信息
		if (StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))) {
			return IMoocJSONResult.errorMsg("");
		}
		
		if(operType == OperatorFriendRequestTypeEnum.IGNORE.type){
			//2.判断如果忽略好友请求，则直接删除好友请求的数据库记录
			userService.deleteFirendRequest(sendUserId, acceptUserId);
		}else if(operType == OperatorFriendRequestTypeEnum.PASS.type){
			//3.判断如果是通过，则相互增加好友记录到数据库对应的表，然后删除好友请求数据库记录
			userService.passFirendRequest(sendUserId, acceptUserId);
		}
		
		//4.数据库查询好友列表
		List<MyFriendsVO> myFriends = userService.queryMyFriends(acceptUserId);
		
		return IMoocJSONResult.ok(myFriends);
	}
	
	/**
	 * 查询我的好友列表
	 * <p>Title: queryFriendRequests</p>  
	 * @param myUserId
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/myFriends")
	public IMoocJSONResult myFriends(String userId) throws Exception {
		System.out.println("查询我的好友列表");
		
		// 0.判断acceptUserId、sendUserId、operType不能为空
		if (StringUtils.isBlank(userId)) {
			return IMoocJSONResult.errorMsg("");
		}
		
		//1.数据库查询好友列表
		List<MyFriendsVO> myFriends = userService.queryMyFriends(userId);

		return IMoocJSONResult.ok(myFriends);
	}
	
	/**
	 * 
	 * @Description: 用户手机端获取未签收的消息列表
	 */
	@PostMapping("/getUnReadMsgs")
	public IMoocJSONResult getUnReadMsgs(String acceptUserId) {
		
		System.out.println("查询未签收的消息列表");
		// 0. userId 判断不能为空
		if (StringUtils.isBlank(acceptUserId)) {
			return IMoocJSONResult.errorMsg("");
		}
		
		// 查询列表
		List<com.imooc.pojo.ChatMsg> unreadMsgList = userService.getUnReadMsgList(acceptUserId);
		
		return IMoocJSONResult.ok(unreadMsgList);
	}

}
