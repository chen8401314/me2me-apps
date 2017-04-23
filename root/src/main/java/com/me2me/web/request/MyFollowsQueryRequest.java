package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class MyFollowsQueryRequest extends Request {

	@Getter
    @Setter
	private int page;
	@Getter
    @Setter
	private String name;
}
