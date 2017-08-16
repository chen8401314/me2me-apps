package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class GetLotteryRequest extends Request {

	@Setter
    @Getter
	private long lotteryId;
	
	@Setter
    @Getter
	private int outApp;
}
