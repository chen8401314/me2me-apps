package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class CreateLotteryRequest extends Request {

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
	private String summary;
	@Setter
    @Getter
	private long endTime;
	@Setter
    @Getter
	private int winNumber;
}
