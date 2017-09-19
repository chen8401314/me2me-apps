package com.me2me.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.me2me.common.web.Response;
import com.me2me.web.request.AwardRequest;

/**
 * 拦截所有没有映射的请求，不然请求不会进入dev拦截器。
 * 
 * @author zhangjiwei
 */
@Controller
public class DevController extends BaseController {
	@ResponseBody
	@RequestMapping(value = "/api/**/*")
	public Response luckAward(AwardRequest request, HttpServletRequest rq) {

		return Response.failure(-1, "no data");
	}

}
