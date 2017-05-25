package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class EmotionRecordRequest extends Request {

	@Getter
	@Setter
	private long emotionId;
	
	@Getter
	@Setter
	private int happyValue;
	
	@Getter
	@Setter
	private int freeValue;
	
	@Setter
    @Getter
	private int source;
}
