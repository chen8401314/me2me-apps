package com.me2me.user.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class SearchUserProfileDto implements BaseEntity {

	private static final long serialVersionUID = -2207948916524216927L;

	private int totalPage;

    private int totalRecord;
	
	private List<UserProfileElement> result = Lists.newArrayList();
	
	public UserProfileElement createUserProfileElement(){
		return new UserProfileElement();
	}
	
	@Data
	public static class UserProfileElement implements BaseEntity{

		private static final long serialVersionUID = 9204369954350041021L;
		
		private Long uid;
		private String mobile;
		private String nickName;
		private Integer gender;
		private String avatar;
		private String birthday;
		private Date createTime;
		private String thirdPartBind;
		private Integer vlv;
	}
}