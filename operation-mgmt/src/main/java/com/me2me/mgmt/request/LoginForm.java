package com.me2me.mgmt.request;

import lombok.Getter;
import lombok.Setter;

public class LoginForm {

	@Getter
    @Setter
	private String name;
	
	@Getter
    @Setter
	private String password;
}
