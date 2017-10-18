package com.me2me.web.request;

import com.me2me.common.web.Request;

import lombok.Getter;
import lombok.Setter;

public class UserLikeRequest extends Request {
	@Getter
	@Setter
	private int type;
	@Getter
	@Setter
	private int isLike;
	@Getter
	@Setter
	private long data;
	@Getter
	@Setter
	private int needNew;
	@Getter
	@Setter
	private String tagIds;
	
}
