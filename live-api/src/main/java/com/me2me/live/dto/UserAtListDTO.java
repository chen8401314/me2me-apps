package com.me2me.live.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class UserAtListDTO implements BaseEntity {
	private static final long serialVersionUID = -4994164626206402607L;

	private long uid;
	private String keyword;
	private int page;
	private long topicId;
}
