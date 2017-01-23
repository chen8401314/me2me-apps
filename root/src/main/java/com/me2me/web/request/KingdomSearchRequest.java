package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class KingdomSearchRequest extends Request {

	@Getter
    @Setter
	private long updateTime;
	@Getter
    @Setter
	private long searchUid;
	@Getter
    @Setter
	private int allowCore;
	@Getter
    @Setter
	private long exceptTopicId;
	@Getter
    @Setter
	private long topicId;
	@Getter
    @Setter
	private int topicType;
	@Getter
    @Setter
	private int searchType;
	@Getter
    @Setter
	private int searchRights;
	@Getter
    @Setter
	private String keyword;
}
