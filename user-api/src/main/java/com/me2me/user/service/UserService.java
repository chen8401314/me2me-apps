package com.me2me.user.service;

import com.me2me.common.web.Response;
import com.me2me.sms.dto.AwardXMDto;
import com.me2me.sms.dto.PushLogDto;
import com.me2me.sms.dto.VerifyDto;
import com.me2me.user.dto.*;
import com.me2me.user.model.*;

import java.util.Date;
import java.util.List;

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

    Response sendAwardMessage(AwardXMDto awardXMDto);

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

    User getUserByUidAndTime(long uid , Date startDate , Date endDate);

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

    Response follow(FollowDto followDto);

    Response getFans(FansParamsDto fansParamsDto);

    Response getFollows(FollowParamsDto followParamsDto);

    int isFollow(long targetUid,long sourceUid);

    UserToken getUserByUidAndToken(long uid, String token);

    Response getUser(long targetUid, long sourceUid);

    Response search(String keyword,int page,int pageSize,long uid);

    Response assistant(String keyword);

    Response checkNickName(String nickName);

    boolean existsNickName(String nickName);

    List<Long> getFollowList(long uid);

    Response getUserProfile(long uid);

    ApplicationSecurity getApplicationSecurityByAppId(String appId);

    int getFollowCount(long uid);

    int getFansCount(long uid);

    void initUserNumber(int limit);

    Response versionControl(String version,int platform,String ip,String channel,String device);

    Response updateVersion(VersionDto versionDto);

    String getUserNoByUid(long uid);

    UserNotice getUserNotice(UserNotice userNotice);

    String getUserHobbyByUid(long uid);

    UserDevice getUserDevice(long uid);

    void push(long targetUid ,long sourceUid ,int type,String title);

    List<UserFollow> getFans(long uid);

    Response setUserExcellent(long uid);

    void createPushLog(PushLogDto pushLogDto);

    Response logout(long uid);

    Response getSpecialUserProfile(long uid);

    UserProfile getUserByNickName(String nickName);

    List<User> getRobots(int limit);

    void pushMessage();

    Response genQRcode(long uid);

    Response refereeSignUp(UserRefereeSignUpDto userRefereeSignUpDto);

    UserProfile4H5Dto getUserProfile4H5(long uid);

    Response getRefereeProfile(long uid);

    int getUserInternalStatus(long uid,long owner);

    Response getFansOrderByNickName(FansParamsDto fansParamsDto);

    Response getFollowsOrderByNickName(FollowParamsDto followParamsDto);

    Response getFansOrderByTime(FansParamsDto fansParamsDto);

    Response getFollowsOrderByTime(FollowParamsDto followParamsDto);

    Response getPromoter(String nickNam,String startDate,String endDate);

    Response getPhoto(long sinceId);

    JpushToken getJpushTokeByUid(long uid);

    Response searchFans(String keyword,int page,int pageSize,long uid);

    Response thirdPartLogin(ThirdPartSignUpDto thirdPartSignUpDto);

    Response activityModel(ActivityModelDto activityModelDto);

    Response checkNameOpenId(UserNickNameDto userNickNameDto);

    Response bind(ThirdPartSignUpDto thirdPartSignUpDto);

    //上V接口 提供给运营
    Response addV(UserVDto userVDto);

    List<UserProfile> getUserProfilesByUids(List<Long> uids);

    Response searchPageByNickNameAndvLv(String nickName, String mobile, int vLv, int page, int pageSize);
    
    /**
     * 操作大V
     * @param action	1：上大V； 其他：取消大V
     * @param uid		待操作的用户UID
     * @return
     */
    Response optionV(int action, long uid);

    Response gag(GagDto dto);


    boolean checkGag(UserGag gag);

    SystemConfig getSystemConfig();



    Response getEntryPageConfig(EntryPageDto dto);

    SystemConfig getSystemConfig();


}
