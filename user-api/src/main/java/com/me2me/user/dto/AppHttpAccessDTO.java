package com.me2me.user.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class AppHttpAccessDTO implements BaseEntity {
	private static final long serialVersionUID = -4949064935929272846L;

	private long uid;
	private String requestUri;
	private String requestMethod;
	private String requestParams;
	private long startTime;
	private long endTime;
	
}
