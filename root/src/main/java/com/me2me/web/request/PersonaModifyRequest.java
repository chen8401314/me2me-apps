package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class PersonaModifyRequest extends Request {

	@Getter
    @Setter
	private int type;
	@Getter
    @Setter
	private String params;
}
