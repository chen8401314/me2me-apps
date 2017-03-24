package com.me2me.web;

import com.me2me.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/11
 * Time :18:09
 */
@Controller
@RequestMapping(value = "/api/spread")
public class Spread extends BaseController {

    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping(value = "/check",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer check(String idfa){
        // 1 刀刀排重
        return userService.spreadCheckUnique(1,idfa);
    }
}
