package com.me2me.mgmt.request;

import lombok.Getter;
import lombok.Setter;

public class SystemUserDTO {

	@Getter
	@Setter
	private long id = 0;
	@Getter
	@Setter
	private long uuid;
	@Getter
	@Setter
	private String uname;
	@Getter
	@Setter
	private long appUid;
}
