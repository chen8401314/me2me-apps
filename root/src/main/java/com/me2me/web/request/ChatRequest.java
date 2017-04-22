package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class ChatRequest extends Request {

	@Getter
    @Setter
	private long chatUid;
	@Getter
    @Setter
	private String message;
}
