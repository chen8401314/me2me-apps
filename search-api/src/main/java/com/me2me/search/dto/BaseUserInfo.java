package com.me2me.search.dto;

import com.me2me.common.web.BaseEntity;

import lombok.Data;
@Data
public class BaseUserInfo implements BaseEntity{
	private static final long serialVersionUID = -1042380710098678702L;
	
	private long uid;
	private String nickName;
	private String avatar;
	private int isFollowed;
	private int isFollowMe;
	private String introduced;
	private int v_lv;
}
