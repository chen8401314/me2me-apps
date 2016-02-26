package com.me2me.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/25.
 */
@Controller
@RequestMapping(value = "/api/user")
public class Users {

    @RequestMapping(value = "/say")
    @ResponseBody
    public String say(){
        return "Hello";
    }

}
