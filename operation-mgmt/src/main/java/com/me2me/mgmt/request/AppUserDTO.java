package com.me2me.mgmt.request;

import lombok.Getter;
import lombok.Setter;

public class AppUserDTO {

	@Getter
    @Setter
	private long mobile;
	@Getter
    @Setter
	private int count;
	@Getter
    @Setter
	private String pwd;
}
