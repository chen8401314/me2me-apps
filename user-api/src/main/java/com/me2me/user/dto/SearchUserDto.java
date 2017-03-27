package com.me2me.user.dto;

import java.io.Serializable;

import com.me2me.user.model.UserProfile;
/**
 * 用户信息
 * @author zhangjiwei
 * @date Mar 21, 2017
 */
public class SearchUserDto implements Serializable{
	private static final long serialVersionUID = 1L;
	private UserProfile userProfile;
	private int kingdomCount;
	private int focusCount;
	private int fansCount;
	
	public UserProfile getUserProfile() {
		return userProfile;
	}
	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}
	public int getKingdomCount() {
		return kingdomCount;
	}
	public void setKingdomCount(int kingdomCount) {
		this.kingdomCount = kingdomCount;
	}
	public int getFocusCount() {
		return focusCount;
	}
	public void setFocusCount(int focusCount) {
		this.focusCount = focusCount;
	}
	public int getFansCount() {
		return fansCount;
	}
	public void setFansCount(int fansCount) {
		this.fansCount = fansCount;
	}
	
}
