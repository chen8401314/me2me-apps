package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class RecommendHistoryRequest extends Request {

	@Getter
    @Setter
	private long auid;
	@Getter
    @Setter
	private int page;
	@Getter
    @Setter
	private int pageSize;
}
