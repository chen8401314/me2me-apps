package com.me2me.web.request;

import com.me2me.common.web.Request;

import lombok.Getter;
import lombok.Setter;

public class TagKingdomRequest extends Request {
	@Getter
	@Setter
	private int page;
	@Getter
	@Setter
	private int pageSize;
	@Getter
	@Setter
	private String tagName;
	@Getter
	@Setter
	private String order;
}
