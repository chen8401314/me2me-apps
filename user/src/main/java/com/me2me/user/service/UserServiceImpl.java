package com.me2me.user.service;

import com.me2me.common.Constant;
import com.me2me.common.security.SecurityUtils;
import com.me2me.common.sms.YunXinSms;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.user.dao.UserMybatisDao;
import com.me2me.user.dto.*;
import com.me2me.user.event.VerifyEvent;
import com.me2me.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ApplicationEventBus applicationEventBus;

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
        // 校验验证码是否正确
        boolean result = YunXinSms.verify(userSignUpDto.getMobile(),userSignUpDto.getVerifyCode());
        if(!result){
            Response.failure(ResponseStatus.USER_VERIFY_GET_ERROR.status,ResponseStatus.USER_VERIFY_GET_ERROR.message);
        }
        userMybatisDao.createUser(user);
        UserProfile userProfile = new UserProfile();
        userProfile.setUid(user.getUid());
        userProfile.setAvatar(Constant.DEFAULT_AVATAR);
        userProfile.setNickName(userSignUpDto.getNickName());
        userMybatisDao.createUserProfile(userProfile);
        signUpSuccessDto.setUserName(user.getUserName());
        // 获取用户token
        signUpSuccessDto.setToken(SecurityUtils.getToken());
        signUpSuccessDto.setUid(user.getUid());
        // 保存用户token信息
        UserToken userToken = new UserToken();
        userToken.setUid(user.getUid());
        userToken.setToken(signUpSuccessDto.getToken());
        userMybatisDao.createUserToken(userToken);
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
            String salt = user.getSalt();
            if(SecurityUtils.md5(userLoginDto.getEncrypt(),salt).equals(user.getEncrypt())){
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
        if(verifyDto.getAction() == Specification.VerifyAction.GET.index){
            applicationEventBus.post(new VerifyEvent(verifyDto.getMobile(),null));
            return Response.success(ResponseStatus.USER_VERIFY_GET_SUCCESS.status,ResponseStatus.USER_VERIFY_GET_SUCCESS.message);
        }else if(verifyDto.getAction() == Specification.VerifyAction.CHECK.index){
            boolean result = YunXinSms.verify(verifyDto.getMobile(),verifyDto.getVerifyCode());
            if(result) {
                return Response.success(ResponseStatus.USER_VERIFY_CHECK_SUCCESS.status, ResponseStatus.USER_VERIFY_CHECK_SUCCESS.message);
            }else{
                Response.failure(ResponseStatus.USER_VERIFY_CHECK_ERROR.status,ResponseStatus.USER_VERIFY_CHECK_ERROR.message);
            }
            }
        return Response.failure(ResponseStatus.USER_VERIFY_ERROR.status,ResponseStatus.USER_VERIFY_ERROR.message);
    }

    /**
     * 修改密码
     * @param modifyEncryptDto
     * @return
     */
    public Response modifyEncrypt(ModifyEncryptDto modifyEncryptDto){
        if(!modifyEncryptDto.getFirstEncrypt().equals(modifyEncryptDto.getSecondEncrypt())) {
            return Response.failure(ResponseStatus.USER_MODIFY_ENCRYPT_PASSWORD_NOT_SAME_ERROR.status,ResponseStatus.USER_MODIFY_ENCRYPT_PASSWORD_NOT_SAME_ERROR.message);
        }else{
            User user = userMybatisDao.getUserByUserName(modifyEncryptDto.getUserName());
            if(user != null){
                if(!SecurityUtils.md5(modifyEncryptDto.getOldEncrypt(),user.getSalt()).equals(user.getEncrypt())){
                    return Response.failure(ResponseStatus.USER_PASSWORD_ERROR.status,ResponseStatus.USER_PASSWORD_ERROR.message);
                }else{
                    user.setEncrypt(SecurityUtils.md5(modifyEncryptDto.getFirstEncrypt(),user.getSalt()));
                    userMybatisDao.modifyUser(user);
                    return Response.success(ResponseStatus.USER_MODIFY_ENCRYPT_SUCCESS.status, ResponseStatus.USER_MODIFY_ENCRYPT_SUCCESS.message);
                }
            }else {
                return Response.failure(ResponseStatus.USER_NOT_EXISTS.status,ResponseStatus.USER_NOT_EXISTS.message);
            }
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
        for(String h : hobbies){
            UserHobby userHobby = new UserHobby();
            userHobby.setHobby(Long.parseLong(h));
            userHobby.setUid(user.getUid());
            userMybatisDao.createUserHobby(userHobby);
        }
        return Response.success(ResponseStatus.USER_MODIFY_HOBBY_SUCCESS.status,ResponseStatus.USER_MODIFY_HOBBY_SUCCESS.message);
    }


    /**
     * 获取基础数据
     * @param basicDataDto
     * @return
     */
    public Response getBasicDataByType(BasicDataDto basicDataDto){
        List<Dictionary> dictionaryList = userMybatisDao.getDictionary(basicDataDto);
        BasicDataSuccessDto basicDataSuccessDto = new BasicDataSuccessDto();
        Map<Long,List<Dictionary>> result = new HashMap<Long, List<Dictionary>>();
        result.put(basicDataDto.getType(),dictionaryList);
        basicDataSuccessDto.setResult(result);
        return Response.success(basicDataSuccessDto);
    }

    public Response getBasicData() {
        BasicDataSuccessDto basicDataSuccessDto = new BasicDataSuccessDto();
        Map<Long,List<Dictionary>> result = new HashMap<Long, List<Dictionary>>();
        BasicDataDto basicDataDto = new BasicDataDto();
        basicDataDto.setType(Specification.UserBasicData.START.index);
        List<Dictionary> dictionaryList = userMybatisDao.getDictionary(basicDataDto);
        result.put(basicDataDto.getType(),dictionaryList);

        basicDataDto.setType(Specification.UserBasicData.INDUSTRY.index);
        dictionaryList = userMybatisDao.getDictionary(basicDataDto);
        result.put(basicDataDto.getType(),dictionaryList);

        basicDataDto.setType(Specification.UserBasicData.BEAR_STATUS.index);
        dictionaryList = userMybatisDao.getDictionary(basicDataDto);
        result.put(basicDataDto.getType(),dictionaryList);

        basicDataDto.setType(Specification.UserBasicData.SOCIAL_CLASS.index);
        dictionaryList = userMybatisDao.getDictionary(basicDataDto);
        result.put(basicDataDto.getType(),dictionaryList);

        basicDataDto.setType(Specification.UserBasicData.YEARS.index);
        dictionaryList = userMybatisDao.getDictionary(basicDataDto);
        result.put(basicDataDto.getType(),dictionaryList);

        basicDataDto.setType(Specification.UserBasicData.MARRIAGE_STATUS.index);
        dictionaryList = userMybatisDao.getDictionary(basicDataDto);
        result.put(basicDataDto.getType(),dictionaryList);

        basicDataSuccessDto.setResult(result);
        return Response.success(basicDataSuccessDto);
    }

    /**
     * 用户信息修改
     * @param modifyUserProfileDto
     * @return
     */
    public Response modifyUserProfile(ModifyUserProfileDto modifyUserProfileDto){
        if(modifyUserProfileDto.getAction() == Specification.ModifyUserProfileAction.AVATAR.index){
            userMybatisDao.modifyUserAvatar(modifyUserProfileDto);
            return Response.success(ResponseStatus.USER_MODIFY_AVATAR_SUCCESS.status,ResponseStatus.USER_MODIFY_AVATAR_SUCCESS.message,modifyUserProfileDto);
        }else if(modifyUserProfileDto.getAction() == Specification.ModifyUserProfileAction.NICKNAME.index){
            userMybatisDao.modifyNickName(modifyUserProfileDto);
            return Response.success(ResponseStatus.USER_MODIFY_NICK_NAME_SUCCESS.status,ResponseStatus.USER_MODIFY_NICK_NAME_SUCCESS.message,modifyUserProfileDto);
        }else if(modifyUserProfileDto.getAction() == Specification.ModifyUserProfileAction.USER_PROFILE.index){
            userMybatisDao.modifyUserProfile(modifyUserProfileDto);
            return Response.success(ResponseStatus.USER_MODIFY_PROFILE_SUCCESS.status,ResponseStatus.USER_MODIFY_PROFILE_SUCCESS.message,modifyUserProfileDto);
        }
        return Response.failure(ResponseStatus.USER_MODIFY_USER_PROFILE_ERROR.status,ResponseStatus.USER_MODIFY_USER_PROFILE_ERROR.message);
    }

    @Override
    public UserProfile getUserProfileByUid(long uid) {
        return userMybatisDao.getUserProfileByUid(uid);
    }

}
