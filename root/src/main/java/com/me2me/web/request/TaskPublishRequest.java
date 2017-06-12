package com.me2me.web.request;

import com.me2me.common.web.Request;

import lombok.Getter;
import lombok.Setter;

public class TaskPublishRequest extends Request {

	@Getter
	@Setter
	private long tid;
	@Getter
	@Setter
	private int type;
}
