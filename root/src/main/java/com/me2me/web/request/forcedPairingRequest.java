package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class forcedPairingRequest extends Request {

	@Getter
	@Setter
	private int isTest;
	@Getter
	@Setter
	private long testUid1;
	@Getter
	@Setter
	private long testUid2;
}
