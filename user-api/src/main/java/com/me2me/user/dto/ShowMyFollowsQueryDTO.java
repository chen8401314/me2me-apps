package com.me2me.user.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowMyFollowsQueryDTO implements BaseEntity {
	private static final long serialVersionUID = 1004115103884095646L;

	private int totalPage;
	
	private List<MyFollowElement> myFollowData = Lists.newArrayList();
	
	@Data
	public static class MyFollowElement implements BaseEntity {
		private static final long serialVersionUID = -1330086331625078074L;
		
		private long uid;
		private String avatar;
		private String nickName;
		private String introduced;
		private int v_lv;
		private String group;
		private int isFollowed;
		private int isFollowMe;
	}
}
