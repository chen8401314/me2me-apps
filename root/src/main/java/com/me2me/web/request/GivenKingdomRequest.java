package com.me2me.web.request;

import lombok.Getter;
import lombok.Setter;

import com.me2me.common.web.Request;
/**
 * 赠送王国请求。
 * @author zhangjiwei
 * @date Jul 19, 2017
 */
public class GivenKingdomRequest extends Request {

	@Getter
	@Setter
	private String action;

	@Getter
	@Setter
	private long givenKingdomId;
}
