package com.me2me.mgmt.request;

import lombok.Getter;
import lombok.Setter;

public class UserRegisterDetailQueryDTO {

	@Getter
	@Setter
	private String startTime;
	@Getter
	@Setter
	private String endTime;
	@Getter
	@Setter
	private String channelCode;
	@Getter
	@Setter
	private String nickName;
}
