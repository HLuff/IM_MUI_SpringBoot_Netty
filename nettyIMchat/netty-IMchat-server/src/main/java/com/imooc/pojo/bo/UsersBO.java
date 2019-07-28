package com.imooc.pojo.bo;

import javax.persistence.*;

public class UsersBO {

    private String userId;

    private String faceData;
    
    private String nickname;

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the faceData
	 */
	public String getFaceData() {
		return faceData;
	}

	/**
	 * @param faceData the faceData to set
	 */
	public void setFaceData(String faceData) {
		this.faceData = faceData;
	}
    
    

    
}