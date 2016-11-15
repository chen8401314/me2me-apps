package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class DelReviewRequest extends Request {

	@Getter
    @Setter
    private long cid;
	
	@Getter
    @Setter
	private long rid;
	
	@Setter
    @Getter
    private int type;
}
