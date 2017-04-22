package com.me2me.user.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

import lombok.Data;

@Data
public class ShowContactsDTO implements BaseEntity {
	private static final long serialVersionUID = 8446059805040970105L;

	private List<MobileContactElement> mobileContactData = Lists.newArrayList();
	
	private List<SeekFollowElement> seekFollowData = Lists.newArrayList();
	
	private int totalPage = 0;//我的关注的总页数
	private List<MyFollowElement> myFollowData = Lists.newArrayList();
	
	@Data
	public static class MobileContactElement implements BaseEntity {
		private static final long serialVersionUID = 1752141254645108175L;
		
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
	
	@Data
	public static class SeekFollowElement implements BaseEntity {
		private static final long serialVersionUID = 2149340956444472961L;
		
		private long uid;
		private String avatar;
		private String nickName;
		private String introduced;
		private int v_lv;
		private int isFollowed;
		private int isFollowMe;
	}
	
	@Data
	public static class MyFollowElement implements BaseEntity {
		private static final long serialVersionUID = 8196331151033556815L;
		
		private long uid;
		private String avatar;
		private String nickName;
		private String introduced;
		private int v_lv;
		private String group;
	}
}
