package com.me2me.user.service;

import com.me2me.common.security.SecurityUtils;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.user.dao.UserMybatisDao;
import com.me2me.user.dto.*;
import com.me2me.user.model.Dictionary;
import com.me2me.user.model.DictionaryExample;
import com.me2me.user.model.User;
import com.me2me.user.model.UserHobby;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMybatisDao userMybatisDao;

    /**
     * 用户注册
     * @param userSignUpDto
     * @return
     */
    public Response signUp(UserSignUpDto userSignUpDto) {
        SignUpSuccessDto signUpSuccessDto = new SignUpSuccessDto();
        User user = new User();
        String salt = SecurityUtils.getMask();
        user.setEncrypt(SecurityUtils.md5(userSignUpDto.getEncrypt(),salt));
        user.setSalt(salt);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setStatus(Specification.UserStatus.NORMAL.index);
        user.setUserName(userSignUpDto.getMobile());
        userMybatisDao.createUser(user);
        signUpSuccessDto.setUserName(user.getUserName());
        // tudo 需要获取用户token
        signUpSuccessDto.setToken("fdsfdsfds");
        signUpSuccessDto.setUid(user.getUid());
        return Response.success(ResponseStatus.USER_SING_UP_SUCCESS.status,ResponseStatus.USER_SING_UP_SUCCESS.message,signUpSuccessDto);
    }

    /**
     * 用户登录
     * @param userLoginDto
     * @return
     */
    public Response login(UserLoginDto userLoginDto) {
        User user = userMybatisDao.getUserByUserName(userLoginDto.getUserName());
        if(user != null){
            if(userLoginDto.getEncrypt().equals(user.getEncrypt())){
                // 则用户登录成功
                LoginSuccessDto loginSuccessDto = new LoginSuccessDto();
                loginSuccessDto.setUid(user.getUid());
                loginSuccessDto.setUserName(user.getUserName());
                return Response.success(ResponseStatus.USER_LOGIN_SUCCESS.status,ResponseStatus.USER_LOGIN_SUCCESS.message,loginSuccessDto);
            }else{
                // 用户密码不正确
                return Response.failure(ResponseStatus.USER_PASSWORD_ERROR.status,ResponseStatus.USER_PASSWORD_ERROR.message);
            }

        }else{
            return Response.failure(ResponseStatus.USER_NOT_EXISTS.status,ResponseStatus.USER_NOT_EXISTS.message);
        }
    }

    /**
     * 发送校验验证码
     * @param verifyDto
     * @return
     */
    public Response verify(VerifyDto verifyDto) {
        return null;
    }

    /**
     * 修改密码
     * @param modifyEncryptDto
     * @return
     */
    public Response modifyEncrypt(ModifyEncryptDto modifyEncryptDto){
        User user = userMybatisDao.getUserByUserName(modifyEncryptDto.getUserName());
        if(user != null){
            if(!modifyEncryptDto.getOldEncrypt().equals(user.getEncrypt())){
                return Response.failure(ResponseStatus.USER_PASSWORD_ERROR.status,ResponseStatus.USER_PASSWORD_ERROR.message);
            }else{
                user.setEncrypt(modifyEncryptDto.getFirstEncrypt());
                userMybatisDao.modifyUser(user);
                return Response.failure(ResponseStatus.USER_MODIFY_ENCRYPT_SUCCESS.status,ResponseStatus.USER_MODIFY_ENCRYPT_SUCCESS.message);
            }
        }else {
            return Response.failure(ResponseStatus.USER_NOT_EXISTS.status,ResponseStatus.USER_NOT_EXISTS.message);
        }
    }

    /**
     * 修改用户爱好
     * @param modifyUserHobbyDto
     * @return
     */
    public Response modifyUserHobby(ModifyUserHobbyDto modifyUserHobbyDto){
        String userName = modifyUserHobbyDto.getUserName();
        User user = userMybatisDao.getUserByUserName(userName);
        String hobby = modifyUserHobbyDto.getHobby();
        String [] hobbies = hobby.split(",");
        UserHobby deleteUserHobby = new UserHobby();
        deleteUserHobby.setUid(user.getUid());
        userMybatisDao.deleteUserHobby(deleteUserHobby);
        for (String h : hobbies){
            UserHobby userHobby = new UserHobby();
            userHobby.setHobby(Long.parseLong(h));
            userHobby.setUid(user.getUid());
            userMybatisDao.createUserHobby(userHobby);
        }
        return Response.success(ResponseStatus.USER_MODIFY_HOBBY_SUCCESS.status,ResponseStatus.USER_MODIFY_HOBBY_SUCCESS.message);
    }

    public Response getBasicData(BasicDataDto basicDataDto){
        List<Dictionary> dictionaryList = userMybatisDao.getDictionary(basicDataDto);
        BasicDataSuccessDto basicDataSuccessDto = new BasicDataSuccessDto();
        basicDataSuccessDto.setResult(dictionaryList);
        return Response.success(basicDataSuccessDto);
    }

}
