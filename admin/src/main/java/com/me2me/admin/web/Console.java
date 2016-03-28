package com.me2me.admin.web;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/25.
 */
@Controller
@RequestMapping("/console")
public class Console  {

    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String index(){
        System.out.println("Hello");
        return "index";
    }


}
