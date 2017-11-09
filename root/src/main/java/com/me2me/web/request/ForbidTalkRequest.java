package com.me2me.web.request;

import com.me2me.common.web.Request;

import lombok.Getter;
import lombok.Setter;

public class ForbidTalkRequest extends Request {
	
	@Getter
	@Setter
	private long forbidUid;
	
	@Getter
	@Setter
	private long topicId;
	
	@Getter
	@Setter
	private int action;
	
}
