package com.me2me.user.service;

import com.me2me.common.page.PageBean;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseWapx;
import com.me2me.sms.dto.AwardXMDto;
import com.me2me.sms.dto.PushLogDto;
import com.me2me.sms.dto.VerifyDto;
import com.me2me.user.dto.*;
import com.me2me.user.model.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    //七天活动报名成功通知短信
    Response sendQIMessage(AwardXMDto awardXMDto);

    //七天活动审核通过通知短信
    Response sendQIauditMessage(AwardXMDto awardXMDto);

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
    Response userNotice(UserNoticeDto userNoticeDto);


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

    long createUserNoticeAndReturnId(UserNotice userNotice);
    
    void createUserNoticeUnread(UserNoticeUnread userNoticeUnread);

    UserTips getUserTips(UserTips userTips);

    void createUserTips(UserTips userTips);

    void modifyUserTips( UserTips userTips);

    Response likes(UserLikeDto userLikeDto);

    Response follow(FollowDto followDto);

    Response getFans(FansParamsDto fansParamsDto);

    Response getFollows(FollowParamsDto followParamsDto);

    int isFollow(long targetUid,long sourceUid);
    
    List<UserFollow> getAllFollows(long uid, List<Long> uids);

    UserToken getUserByUidAndToken(long uid, String token);
    
    UserToken getUserTokenByUid(long uid);
    
    List<UserToken> getUserTokenByUids(List<Long> uids);

    Response getUser(long targetUid, long sourceUid);

    Response search(String keyword,int page,int pageSize,long uid);

    Response assistant(String keyword);

    Response checkNickName(String nickName);

    boolean existsNickName(String nickName);

    List<Long> getFollowList(long uid);

    Response getUserProfile(long uid, int vFlag);

    UserProfile getUserProfileByMobile(String mobile);

    ApplicationSecurity getApplicationSecurityByAppId(String appId);

    int getFollowCount(long uid);

    int getFansCount(long uid);

    void initUserNumber(int limit);
    
    void updateUserSex(long uid, int sex);

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

    Response searchUserPage(String nickName, String mobile, int vLv, int status, String startTime, String endTime, int page, int pageSize);
    
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

    boolean isAdmin(long uid);



    Response getEntryPageConfig(EntryPageDto dto);

    Response getVersionList(String version, int platform);
    
    Response getVersion(String version, int platform);

    Response getVersionById(long id);
    
    Response saveOrUpdateVersion(VersionControlDto dto);
    
    Response getGagUserPageByTargetUid(long targetUid, int page, int pageSize);
    
    Response deleteGagUserById(long id);
    
    Response addGagUser(UserGag gag);
    
    Response updateSystemConfig(SystemConfig config);

    Response touristLogin();
    
    List<Long> getAllUids();
    
    Response optionDisableUser(int action, long uid);
    
    Response testPush(long uid, String msg, String jsonData);
    
    void noticeCountPush(long uid);
    
    void noticeMessagePush(long targetUid, String message, int level);
    
    void pushWithExtra(String uid,String message,Map<String,String> extraMaps);
    
    List<UserFamous> getUserFamousPage(int page, int pageSize);
    
    Response userRecomm(long uid ,long targetUid ,int action);
    
    boolean isUserFamous(long uid);

    ResponseWapx iosWapxUserRegist(WapxIosDto dto);
    
    List<VersionChannelDownload> queryVersionChannelDownloads(String channel);
    
    VersionChannelDownload getVersionChannelDownloadByChannel(String channel);
    
    VersionChannelDownload getVersionChannelDownloadById(long id);
    
    void saveVersionChannelDownload(VersionChannelDownload vcd);
    
    void updateVersionChannelDownload(VersionChannelDownload vcd);
    
    void deleteVersionChannelDownload(long id);

    int spreadCheckUnique(int spreadChannel,String idfa);

    Response click(int type ,DaoDaoDto daoDaoDto);

    Response getIMUsertoken(long uid);

    Response registAllIMtoken();

    /**
     * 搜索用户，供管理后台使用。
     * @author zhangjiwei
     * @date Mar 22, 2017
     * @param page
     * @param queries
     * @return
     */
    PageBean<SearchUserDto> searchUserPage(PageBean page,Map<String,Object> queries);
    /**
     * 统计用户信息，每日一次
     * @author zhangjiwei
     * @date Apr 10, 2017
     */
    void countUserByDay();
    
    Response mobileQuery(String mobiles, long uid);
    
    Response contacts(int page, String mobiles, long uid);
    
    /**
     * 初始化所有用户的昵称所属的组
     */
    void initNameGroup();
    
    Response seekFollowsQuery(long uid, long sinceId);
    
    Response seekFollow(long uid);
    
    /**
     * 清除超过{hour}小时的求关注记录
     * @param hisDay
     */
    void cleanSeekFollow(int hour);
    
    Response myFollowsQuery(long uid, String name, int page);
    
    Response batchFollow(long uid, String targetUids);
    
    Response personaModify(long uid, int type, String params);
    
    Response testSendMessage(long templateId, String mobiles);
    
    void deleteUserProfile(long id);
    
    Response noticeReddotQuery(long uid);
    
    void clearUserNoticeUnreadByCid(long uid, int contentType, long cid);
    /**
     * 获取用户mbti数据
     * @author zhangjiwei
     * @date May 23, 2017
     * @param uid
     * @return
     */
    Response<MBTIDto> getMBTIResult(long uid);
    /**
     * 保存mbti测试结果。
     * @author zhangjiwei
     * @date May 23, 2017
     * @param uid
     * @param mbti
     * @return
     */
    Response<MBTIDto> saveMBTIResult(long uid,String mbti);
    /**
     * 记录用户分享mbti历史
     * @author zhangjiwei
     * @date May 23, 2017
     * @param uid
     * @return
     */
    Response saveMBTIShareResult(long uid);
    
    List<EmotionRecord> getUserEmotionRecords(long uid, int pageSize);
    List<EmotionInfo> getEmotionInfosByIds(List<Long> ids);
    
    void addMBTIMapping(MbtiMapping mapping);
    
    void delMBTIMapping(long mappingId);
    
    void modifyMBTIMapping(MbtiMapping mapping);
    
    List<MbtiMapping> getMBTIMappingPage();
    
    MbtiMapping getMBTIMappingById(long id);
    
     List<EmotionInfo> getEmotionInfoList();
     
 	 EmotionInfo getEmotionInfoByKey(Long id);
 	 
 	 
 	 void updateEmotionInfoByKey(EmotionInfo emotionInfo);
 	
 	 Integer addEmotionInfo(EmotionInfo emotionInfo);
 	 
 	 EmotionInfo getEmotionInfoByValue(int happyValue,int freeValue); 
 	 
 	Response addEmotionRecord(EmotionRecord emotionRecord);
 	 
}
