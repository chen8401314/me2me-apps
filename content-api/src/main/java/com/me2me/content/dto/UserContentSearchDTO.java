package com.me2me.content.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class UserContentSearchDTO implements BaseEntity {

	private static final long serialVersionUID = -7425397029087436938L;

	private long uid;
	private int searchType;
	
	private int page;
	private int pageSize;
}
