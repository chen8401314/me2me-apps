package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class RecommendTagRequest extends Request {

	@Getter
	@Setter
	private String content;
	@Getter
	@Setter
	private int count=10;
}
