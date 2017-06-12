package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class FragmentForwardRequest extends Request {

	@Getter
    @Setter
	private long fid;
	@Getter
    @Setter
	private long sourceTopicId;
	@Getter
    @Setter
	private long targetTopicId;
}
