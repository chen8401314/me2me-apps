package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.user.dto.*;
import com.me2me.user.service.UserService;
import com.me2me.web.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/25.
 */
@Controller
@RequestMapping(value = "/api/user")
public class Users {

    @Autowired
    private UserService userService;

    /**
     * 用户注册接口
     * @return
     */
    @RequestMapping(value = "/signUp",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response signUp(SignUpRequest request){
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        userSignUpDto.setMobile(request.getMobile());
        userSignUpDto.setGander(request.getGander());
        userSignUpDto.setConstellation(request.getConstellation());
        userSignUpDto.setEncrypt(request.getEncrypt());
        // todo add request params
        return userService.signUp(userSignUpDto);
    }

    /**
     * 用户登录
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response login(LoginRequest request){
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUserName(request.getUserName());
        userLoginDto.setEncrypt(request.getEncrypt());
        return userService.login(userLoginDto);
    }

    /**
     * 修改密码接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/modifyEncrypt",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response modifyEncrypt(ModifyEncryptRequest request){
        ModifyEncryptDto modifyEncryptDto = new ModifyEncryptDto();
        modifyEncryptDto.setUserName(request.getUserName());
        modifyEncryptDto.setOldEncrypt(request.getOldEncrypt());
        modifyEncryptDto.setSecondEncrypt(request.getSecondEncrypt());
        return userService.modifyEncrypt(modifyEncryptDto);
    }

    /**
     * 获取验证码接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/verify",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response verify(VerifyRequest request){
        //// TODO: 2016/2/29 get verify code
        return null;
    }

    /**
     * 用户资料修改
     * @param request
     * @param avatar
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/modifyUserProfile",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response modifyUserProfile(ModifyUserProfileRequest request, MultipartFile avatar){
       return null;
    }

    /**
     * 修改用户爱好
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/modifyUserHobby",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response modifyUserHobby(ModifyUserHobbyRequest request){
        ModifyUserHobbyDto modifyUserHobbyDto = new ModifyUserHobbyDto();
        modifyUserHobbyDto.setUserName(request.getUserName());
        modifyUserHobbyDto.setHobby(request.getHobby());
        userService.modifyUserHobby(modifyUserHobbyDto);
        return null;
    }


    /**
     * 获取用户基础数据
     *
     */
    @ResponseBody
    @RequestMapping(value = "/getBasicData",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getBasicData(BasicDataRequest request){
        BasicDataDto basicDataDto = new BasicDataDto();
        return userService.getBasicData(basicDataDto);
    }

    /**
     * 第三方登录
     */
    @ResponseBody
    @RequestMapping(value = "/thirdPartAuth",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response thirdPartAuth(ThirdPartAuthRequest request){
        return null;
    }



}
