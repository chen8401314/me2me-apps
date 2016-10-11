package com.me2me.mgmt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by 王一武 on 2016/9/20.
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @RequestMapping("")
    public ModelAndView dashboard(){
        ModelAndView view = new ModelAndView("Dashboard");
        return view;
    }
}
