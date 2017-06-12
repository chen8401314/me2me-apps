package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class ActivityMiliRequest extends Request {

	@Setter
    @Getter
	private long auid;//报名用户ID
	@Setter
    @Getter
	private int isApp;//是否APP内，0APP外(默认)，1APP内
	@Setter
    @Getter
	private int isFirst;//是否首次请求，0首次(默认)，1非首次
	@Setter
    @Getter
	private long activityId;
}
