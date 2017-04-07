package com.me2me.search.dto;

import java.io.Serializable;

import com.me2me.search.enums.ApplicationType;

/**
 * 用户信息搜索结果。
 * @author zhangjiwei
 * @date 2016年10月9日
 *
 */
public class SearchResultUser  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userName;	
	private String sex;
	private String mobile;
	private String photo;
	private String uid;
	private String typeName;
	private Integer vLv;
	
	public Integer getvLv() {
		return vLv;
	}
	public void setvLv(Integer vLv) {
		this.vLv = vLv;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public SearchResultUser() {
		this.setTypeName(ApplicationType.PERSONA.getCode());
	}

	public String getUserName() {
		return userName;
	}
	
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
}
