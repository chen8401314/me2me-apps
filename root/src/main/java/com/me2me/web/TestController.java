package com.me2me.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.me2me.common.web.Response;
import com.me2me.web.request.AwardRequest;

/**
 * Created by 马秀成 on 2016/10/18.
 */
@Controller
public class TestController extends BaseController {
    @ResponseBody
    @RequestMapping(value = "/**/*")
    public Response luckAward(AwardRequest request , HttpServletRequest rq){
       
    	return Response.failure(-1, "no data");
    }

}
