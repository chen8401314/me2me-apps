package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class CreateVoteRequest extends Request {

	@Setter
    @Getter
	private long topicId;
	@Setter
    @Getter
	private int source;
	@Setter
    @Getter
	private String title;
	@Setter
    @Getter
	private String option;
	@Setter
    @Getter
	private long uid;
	@Setter
    @Getter
	private int type;
}
