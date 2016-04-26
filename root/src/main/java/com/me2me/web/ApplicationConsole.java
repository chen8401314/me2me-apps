package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.common.web.Specification;
import com.me2me.content.service.ContentService;
import com.me2me.user.dto.*;
import com.me2me.user.service.UserService;
import com.me2me.web.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/25.
 */
@Controller
@RequestMapping(value = "/api/console")
public class ApplicationConsole extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private ContentService contentService;

    /**
     * 用户注册接口
     * @return
     */
    @RequestMapping(value = "/bindAccount",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response bindAccount(BindAccountRequest bindAccountRequest){
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        userSignUpDto.setMobile("18000000000");
        userSignUpDto.setGender(0);
        userSignUpDto.setEncrypt("123456");
        userSignUpDto.setNickName(bindAccountRequest.getNickName());
        return userService.signUp(userSignUpDto);
    }

    /**
     * 用户注册接口
     * @return
     */
    @RequestMapping(value = "/showPGCList",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response showPGCList(BindAccountRequest bindAccountRequest){
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        userSignUpDto.setMobile("18000000000");
        userSignUpDto.setGender(0);
        userSignUpDto.setEncrypt("123456");
        userSignUpDto.setNickName(bindAccountRequest.getNickName());
        return userService.signUp(userSignUpDto);
    }

    /**
     * 用户注册接口
     * @return
     */
    @RequestMapping(value = "/showUGCList",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response showUGCList(BindAccountRequest bindAccountRequest){
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        userSignUpDto.setMobile("18000000000");
        userSignUpDto.setGender(0);
        userSignUpDto.setEncrypt("123456");
        userSignUpDto.setNickName(bindAccountRequest.getNickName());
        return userService.signUp(userSignUpDto);
    }


}
