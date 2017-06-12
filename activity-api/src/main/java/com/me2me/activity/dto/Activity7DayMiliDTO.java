package com.me2me.activity.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class Activity7DayMiliDTO implements BaseEntity {
	private static final long serialVersionUID = 7921369778840616343L;

	private long auid;//报名用户ID
	private int isApp;//是否APP内，0APP外(默认)，1APP内
	private int isFirst;//是否首次请求，0首次(默认)，1非首次
	
}
