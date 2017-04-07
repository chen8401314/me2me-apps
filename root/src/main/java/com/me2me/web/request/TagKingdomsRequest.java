package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class TagKingdomsRequest extends Request {

	@Getter
    @Setter
	private String tag;
	@Getter
    @Setter
	private long sinceId;
}
