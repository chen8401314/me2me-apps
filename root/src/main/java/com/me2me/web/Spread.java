package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.user.dto.DaoDaoDto;
import com.me2me.user.dto.WapxIosDto;
import com.me2me.user.service.UserService;
import com.me2me.web.request.DaoDaoRequest;
import com.me2me.web.request.WapxIosRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;

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
        // 1 刀刀排重 0不存在 1存在
        return userService.spreadCheckUnique(1,idfa);
    }

    @ResponseBody
    @RequestMapping(value = "/click",produces = MediaType.APPLICATION_JSON_VALUE)
    public Response click(DaoDaoRequest request){
        DaoDaoDto daoDaoDto = new DaoDaoDto();
        BeanUtils.copyProperties(request ,daoDaoDto);
        return userService.click(1,daoDaoDto);
    }

}
