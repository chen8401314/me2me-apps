package com.me2me.content.dto;

import lombok.Data;

import com.me2me.common.web.BaseEntity;

@Data
public class ReviewDelDTO implements BaseEntity {

	private static final long serialVersionUID = -8530085981823516836L;

	private long uid;
	private long cid;
	private long rid;
	private int type;
}
