package com.me2me.web.request;

import com.me2me.common.web.Request;

import lombok.Getter;
import lombok.Setter;

public class StealKingdomCoinRequest extends Request {
	@Getter
	@Setter
	private long topicId;
}
