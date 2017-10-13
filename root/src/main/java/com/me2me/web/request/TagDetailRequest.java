package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class TagDetailRequest extends Request {

	@Getter
	@Setter
	private long tagId;
	
	@Getter
	@Setter
	private String tagName;
	
	@Getter
	@Setter
	private int page;
}
