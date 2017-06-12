package com.me2me.user.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowMobileDTO implements BaseEntity {
	private static final long serialVersionUID = -695671575642051657L;
	
	private int totalAppUser = 0;
	
	private List<MobileElement> mobileContactData = Lists.newArrayList();
	
	@Data
	public static class MobileElement implements BaseEntity {
		private static final long serialVersionUID = 3786885853886132483L;
		
		private String mobile;
		private int isAppUser;
		private long uid;
		private String avatar;
		private String nickName;
		private String introduced;
		private int v_lv;
		private int isFollowed;
		private int isFollowMe;
	}
}
