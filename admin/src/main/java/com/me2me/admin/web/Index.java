package com.me2me.admin.web;

import com.me2me.admin.web.request.TimelineRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/25.
 */
@RequestMapping("/index")
@Controller
public class Index {

    public ModelAndView index(TimelineRequest request){
        ModelAndView mv = new ModelAndView("/home/index");
        return mv;
    }

}
