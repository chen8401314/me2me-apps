package com.me2me.search.dto;

import com.me2me.common.web.BaseEntity;

import lombok.Data;

@Data
public class BaseKingdomInfo implements BaseEntity{
	private static final long serialVersionUID = 1L;
	private BaseUserInfo userInfo;
	private String title;
	private long topicId;
	private String cover;
	private String tags;
}
