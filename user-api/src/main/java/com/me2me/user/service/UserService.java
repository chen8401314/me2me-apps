package com.me2me.user.service;

import com.me2me.common.web.Response;
import com.me2me.user.dto.ModifyEncryptDto;
import com.me2me.user.dto.UserLoginDto;
import com.me2me.user.dto.UserSignUpDto;
import com.me2me.user.dto.VerifyDto;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
public interface UserService {


     /**
     * 用户注册接口
     * @param userDto
     * @return
     */
    Response signUp(UserSignUpDto userDto);


    /**
     * 用户登录接口
     * @param userLoginDto
     * @return
     */
    Response login(UserLoginDto userLoginDto);


    /**
     * 验证码和校验验证码接口
     * @return
     */
    Response verify(VerifyDto verifyDto);

    /**
     * 用户修改密码
     * @param modifyEncryptDto
     * @return
     */
    Response modifyEncrypt(ModifyEncryptDto modifyEncryptDto);

    /**
     * 用户端获取基础数据
     * @param type
     * @return
     */
    Response getBasicData(int type);

    Response modifyUserHobby(ModifyEncryptDto modifyEncryptDto);



}
