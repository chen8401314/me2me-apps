package com.me2me.mgmt.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.me2me.mgmt.request.LoginForm;

@Controller
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@RequestMapping("/index")
	public ModelAndView index(HttpServletRequest request){
		return new ModelAndView("redirect:/dashboard");
	}
	
	@RequestMapping("/login")
    public ModelAndView login(LoginForm form){
		logger.info("=="+form.getName());
		logger.info("==="+form.getPassword());
		return new ModelAndView("redirect:/dashboard");
	}
}
