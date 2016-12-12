package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class TestPushRequest extends Request {

	@Getter
    @Setter
	private String msg;
	@Getter
    @Setter
	private String jsonData;
}
