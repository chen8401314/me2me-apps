package com.me2me.user.service;

import com.me2me.common.web.Response;
import com.me2me.user.dto.*;
import com.me2me.user.model.UserNotice;
import com.me2me.user.model.UserProfile;
import com.me2me.user.model.UserTips;

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
     * 修改爱好
     * @param modifyUserHobbyDto
     * @return
     */
    Response modifyUserHobby(ModifyUserHobbyDto modifyUserHobbyDto);

    /**
     * 用户端获取基础数据
     * @param basicDataDto
     * @return
     */
    Response getBasicDataByType(BasicDataDto basicDataDto);

    /**
     * 用户端获取基础数据
     * @return
     */
    Response getBasicData();

    /**
     * 用户信息修改
     * @param modifyUserProfileDto
     * @return
     */
    Response modifyUserProfile(ModifyUserProfileDto modifyUserProfileDto);

    UserProfile getUserProfileByUid(long uid);

    /**
     * 找回密码
     * @param findEncryptDto
     * @return
     */
    Response retrieveEncrypt(FindEncryptDto findEncryptDto);

    /**
     * 给指定的用户贴标签
     * @param pasteTagDto
     * @return
     */
    Response writeTag(PasteTagDto pasteTagDto);


    /**
     * 消息提醒列表
     * @param userNoticeDto
     * @return
     */
    Response getUserNotice(UserNoticeDto userNoticeDto);


    /**
     * 获取用户消息提醒-数量
     * @param uid
     * @return
     */
    Response getUserTips(long uid);

    /**
     * 清空userTips
     * @param uid
     * @return
     */
    Response cleanUserTips(long uid);

    /**
     * 用户举报接口
     * @param userReportDto
     * @return
     */
    Response userReport(UserReportDto userReportDto);

    Response showUserTags(long uid);

    void createUserNotice(UserNotice userNotice);

    UserTips getUserTips(UserTips userTips);

    void createUserTips(UserTips userTips);

    void modifyUserTips( UserTips userTips);

    Response likes(UserLikeDto userLikeDto);

    Response follow(long uid);

    Response getFans(long uid);

    Response getFollows(long uid);


}
