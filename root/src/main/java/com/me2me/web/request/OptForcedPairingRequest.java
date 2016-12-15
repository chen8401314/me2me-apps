package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;

public class OptForcedPairingRequest extends Request {

	@Getter
    @Setter
	private int action;//1接受强配  2不接受强配
}
