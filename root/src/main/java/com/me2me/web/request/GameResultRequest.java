package com.me2me.web.request;

import com.me2me.common.web.Request;

import lombok.Getter;
import lombok.Setter;

public class GameResultRequest extends Request {
	
	@Setter
	@Getter
	private long gameId;
	
	@Setter
	@Getter
	private int record;
}
