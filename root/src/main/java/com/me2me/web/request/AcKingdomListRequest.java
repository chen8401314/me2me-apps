package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class AcKingdomListRequest extends Request {

	@Getter
	@Setter
	private long ceTopicId;
	
	@Getter
	@Setter
	private int resultType;
	
	@Getter
	@Setter
	private int page;
}
