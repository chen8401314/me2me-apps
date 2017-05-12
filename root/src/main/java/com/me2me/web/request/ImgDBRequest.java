package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class ImgDBRequest extends Request {

	@Getter
    @Setter
	private long topicId;
	@Getter
    @Setter
	private int direction;
	@Getter
    @Setter
	private long fragmentId;
	
	@Getter
	@Setter
	private int type=0;	//0 按数据分页加载，1按月份加载
	
}
