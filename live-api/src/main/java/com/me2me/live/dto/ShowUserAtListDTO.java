package com.me2me.live.dto;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.me2me.common.web.BaseEntity;

@Data
public class ShowUserAtListDTO implements BaseEntity {
	private static final long serialVersionUID = 5045396022631656733L;

	private int totalPage;
	private List<UserElement> userData = Lists.newArrayList();
	
	@Data
	public static class UserElement implements BaseEntity {
		private static final long serialVersionUID = -4168638535555033788L;
		
		private long uid;
		private String avatar;
		private String nickName;
		private int v_lv;
		private int level;
		private int internalStatus;
	}
}
