package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class GameUserInfoRequest extends Request {

	@Getter
    @Setter
	private long gameUid;
	
	@Getter
	@Setter
	private int gameChannel;
}
