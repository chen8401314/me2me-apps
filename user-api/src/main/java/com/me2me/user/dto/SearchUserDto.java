package com.me2me.user.dto;

import java.io.Serializable;
import java.util.Date;
/**
 * 用户信息
 * @author zhangjiwei
 * @date Mar 21, 2017
 */
public class SearchUserDto implements Serializable{
	private static final long serialVersionUID = 1L;
	private int kingdomCount;
	private int focusCount;
	private int fansCount;
	private int uid;
	private String mobile;
	private String nickName;
	private int gender;
	private int vLv;
	private Date createTime;
	private Date updateTime;

	public int getvLv() {
		return vLv;
	}
	public void setvLv(int vLv) {
		this.vLv = vLv;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
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
