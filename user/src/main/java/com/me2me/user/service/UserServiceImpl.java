package com.me2me.user.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.me2me.cache.service.CacheService;
import com.me2me.common.Constant;
import com.me2me.common.security.SecurityUtils;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.core.QRCodeUtil;
import com.me2me.io.service.FileTransferService;
import com.me2me.sms.dto.*;
import com.me2me.sms.service.JPushService;
import com.me2me.sms.service.SmsService;
import com.me2me.sms.service.XgPushService;
import com.me2me.user.dao.OldUserJdbcDao;
import com.me2me.user.dao.UserInitJdbcDao;
import com.me2me.user.dao.UserMybatisDao;
import com.me2me.user.dto.*;
import com.me2me.user.model.*;
import com.me2me.user.model.Dictionary;
import com.me2me.user.widget.MessageNotificationAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.security.tools.keytool.Resources_sv;

import java.util.*;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMybatisDao userMybatisDao;

    @Autowired
    private UserInitJdbcDao userInitJdbcDao;

    @Autowired
    private OldUserJdbcDao oldUserJdbcDao;

    @Autowired
    private SmsService smsService;

    @Autowired
    private XgPushService xgPushService;

    @Autowired
   private FileTransferService fileTransferService;

    @Autowired
    private CacheService cacheService;

    @Value("#{app.reg_web}")
    private String reg_web;

    private static final String POWER_KEY = "power:key";

    private static final String AD_KEY = "ad:url:key";

    @Autowired
    private JPushService jPushService;


    /**
     * 用户注册
     * @param userSignUpDto
     * @return
     */
    public Response signUp(UserSignUpDto userSignUpDto) {
        log.info("signUp start ...");
        // 校验手机号码是否注册
        String mobile = userSignUpDto.getMobile();
        log.info("mobile:" + mobile );
        if(userMybatisDao.getUserByUserName(mobile) != null){
            // 该用户已经注册过
            log.info("mobile:" + mobile + " is already register");
            return Response.failure(ResponseStatus.USER_MOBILE_DUPLICATE.status,ResponseStatus.USER_MOBILE_DUPLICATE.message);
        }
        // 检查用户名是否重复
        if(!this.existsNickName(userSignUpDto.getNickName())){
            log.info("nickname:" + userSignUpDto.getNickName() + " is already used");
            return Response.failure(ResponseStatus.NICK_NAME_REQUIRE_UNIQUE.status,ResponseStatus.NICK_NAME_REQUIRE_UNIQUE.message);
        }

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
        log.info("user is create");
        UserProfile userProfile = new UserProfile();
        userProfile.setUid(user.getUid());
        userProfile.setAvatar(Constant.DEFAULT_AVATAR);
        userProfile.setMobile(userSignUpDto.getMobile());
        userProfile.setNickName(userSignUpDto.getNickName());
        userProfile.setIntroduced(userSignUpDto.getIntroduced());

        List<UserAccountBindStatusDto> array = Lists.newArrayList();
        // 添加手机绑定
        array.add(new UserAccountBindStatusDto(Specification.ThirdPartType.MOBILE.index,Specification.ThirdPartType.MOBILE.name,1));
        String mobileBind = JSON.toJSONString(array);
        userProfile.setThirdPartBind(mobileBind);

        userMybatisDao.createUserProfile(userProfile);
        log.info("userProfile is create");
        signUpSuccessDto.setUserName(user.getUserName());
        // 获取用户token
        signUpSuccessDto.setToken(SecurityUtils.getToken());
        signUpSuccessDto.setUid(user.getUid());
        signUpSuccessDto.setNickName(userProfile.getNickName());
        // 设置userNo
        signUpSuccessDto.setMeNumber(userMybatisDao.getUserNoByUid(user.getUid()).getMeNumber().toString());
        signUpSuccessDto.setAvatar(userProfile.getAvatar());
        signUpSuccessDto.setIntroduced(userProfile.getIntroduced());
        // 保存用户token信息
        UserToken userToken = new UserToken();
        userToken.setUid(user.getUid());
        userToken.setToken(signUpSuccessDto.getToken());
        userMybatisDao.createUserToken(userToken);
        log.info("userToken is create");
        signUpSuccessDto.setToken(userToken.getToken());

        //保存用户的设备token和用户平台信息
        UserDevice device = new UserDevice();
        device.setDeviceNo(userSignUpDto.getDeviceNo());
        device.setPlatform(userSignUpDto.getPlatform());
        device.setOs(userSignUpDto.getOs());
        device.setUid(user.getUid());
        userMybatisDao.updateUserDevice(device);
        log.info("userDevice is create");
        // 获取默认值给前端
        UserProfile up = userMybatisDao.getUserProfileByUid(user.getUid());
        signUpSuccessDto.setGender(up.getGender());
        signUpSuccessDto.setYearId(up.getYearsId());
        log.info("signUp end ...");
        //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.REGISTER.index,0,user.getUid()));
        return Response.success(ResponseStatus.USER_SING_UP_SUCCESS.status,ResponseStatus.USER_SING_UP_SUCCESS.message,signUpSuccessDto);
    }

    /**
     * 用户登录
     * @param userLoginDto
     * @return
     */
    public Response login(UserLoginDto userLoginDto) {
        log.info("login start ...");
        //老用户转到新系统中来
        oldUserJdbcDao.moveOldUser2Apps(userLoginDto.getUserName(),userLoginDto.getEncrypt());
        log.info("deal with old user ");
        User user = userMybatisDao.getUserByUserName(userLoginDto.getUserName());
        if(user != null){
            String salt = user.getSalt();
            if(SecurityUtils.md5(userLoginDto.getEncrypt(),salt).equals(user.getEncrypt())){
                // 则用户登录成功
                UserProfile userProfile = userMybatisDao.getUserProfileByUid(user.getUid());
                log.info("get userProfile success");
                //判断用户是否是未激活状态
                if(userProfile.getIsActivate() == Specification.UserActivate.UN_ACTIVATED.index){
                    userProfile.setIsActivate(Specification.UserActivate.ACTIVATED.index);
                    userMybatisDao.modifyUserProfile(userProfile);
                }
                UserToken userToken = userMybatisDao.getUserTokenByUid(user.getUid());
                log.info("get userToken success");
                LoginSuccessDto loginSuccessDto = new LoginSuccessDto();
                loginSuccessDto.setUid(user.getUid());
                loginSuccessDto.setUserName(user.getUserName());
                loginSuccessDto.setNickName(userProfile.getNickName());
                loginSuccessDto.setGender(userProfile.getGender());
                loginSuccessDto.setMeNumber(userMybatisDao.getUserNoByUid(user.getUid()).getMeNumber().toString());
                loginSuccessDto.setAvatar(Constant.QINIU_DOMAIN  + "/" + userProfile.getAvatar());
                log.info("user avatar :" +loginSuccessDto.getAvatar());
                loginSuccessDto.setToken(userToken.getToken());
                loginSuccessDto.setYearId(userProfile.getYearsId());
                loginSuccessDto.setFansCount(userMybatisDao.getUserFansCount(user.getUid()));
                loginSuccessDto.setFollowedCount(userMybatisDao.getUserFollowCount(user.getUid()));
                loginSuccessDto.setIntroduced(userProfile.getIntroduced());
                //保存用户的设备token和用户平台信息
                UserDevice device = new UserDevice();
                device.setDeviceNo(userLoginDto.getDeviceNo());
                device.setPlatform(userLoginDto.getPlatform());
                device.setOs(userLoginDto.getOs());
                device.setUid(user.getUid());
                userMybatisDao.updateUserDevice(device);
                // 保存极光推送
                if(!StringUtils.isEmpty(userLoginDto.getJPushToken())) {
                    // 判断当前用户是否存在JpushToken,如果存在，并且相同我们不做处理，否则修改
                    List<JpushToken> jpushTokens = userMybatisDao.getJpushToken(user.getUid());
                    if(jpushTokens!=null&&jpushTokens.size()>0){
                        // 更新当前JpushToken
                        JpushToken jpushToken = jpushTokens.get(0);
                        jpushToken.setJpushToken(userLoginDto.getJPushToken());
                        userMybatisDao.refreshJpushToken(jpushToken);
                    }else {
                        // 系统兼容扩展
                        JpushToken jpushToken = new JpushToken();
                        jpushToken.setJpushToken(userLoginDto.getJPushToken());
                        jpushToken.setUid(user.getUid());
                        userMybatisDao.createJpushToken(jpushToken);
                    }
                }
                log.info("update user device success");
                log.info("login end ...");
                //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.LOGIN.index,0,user.getUid()));
                return Response.success(ResponseStatus.USER_LOGIN_SUCCESS.status,ResponseStatus.USER_LOGIN_SUCCESS.message,loginSuccessDto);
            }else{
                log.info("user password error");
                // 用户密码不正确
                return Response.failure(ResponseStatus.USER_PASSWORD_ERROR.status,ResponseStatus.USER_PASSWORD_ERROR.message);
            }

        }else{
            log.info("user not exists");
            //用户不存在
            return Response.failure(ResponseStatus.USER_NOT_EXISTS.status,ResponseStatus.USER_NOT_EXISTS.message);
        }
    }

    /**
     * 发送校验验证码
     * @param verifyDto
     * @return
     */
    public Response verify(VerifyDto verifyDto) {
        log.info("verify start ...");
        if(verifyDto.getAction() == Specification.VerifyAction.GET.index){
            // 发送校验码
            User user = userMybatisDao.getUserByUserName(verifyDto.getMobile());
            if(user!=null){
                log.info("user mobile duplicate");
                return Response.failure(ResponseStatus.USER_MOBILE_DUPLICATE.status,ResponseStatus.USER_MOBILE_DUPLICATE.message);
            }
            smsService.send(verifyDto);
            log.info("user signUp get verify code success");
            return Response.success(ResponseStatus.USER_VERIFY_GET_SUCCESS.status,ResponseStatus.USER_VERIFY_GET_SUCCESS.message);
        }else if(verifyDto.getAction() == Specification.VerifyAction.CHECK.index){
            // 验证校验码
            boolean result = smsService.verify(verifyDto);
            if(result) {
                log.info("user verify check success");
                return Response.success(ResponseStatus.USER_VERIFY_CHECK_SUCCESS.status, ResponseStatus.USER_VERIFY_CHECK_SUCCESS.message);
            }else{
                log.info("user verify check error");
                return Response.failure(ResponseStatus.USER_VERIFY_CHECK_ERROR.status,ResponseStatus.USER_VERIFY_CHECK_ERROR.message);
            }
        }else if(verifyDto.getAction() == Specification.VerifyAction.FIND_MY_ENCRYPT.index){
            // 找回密码
            // 判断用户是否已经注册过该手机
            log.info("find my encrypt");
            oldUserJdbcDao.moveOldUser2Apps(verifyDto.getMobile(),Constant.OLD_USER_ENCRYPT);
            User user = userMybatisDao.getUserByUserName(verifyDto.getMobile());
            if(user!=null){
                smsService.send(verifyDto);
                log.info("user verify get success");
                return Response.success(ResponseStatus.USER_VERIFY_GET_SUCCESS.status,ResponseStatus.USER_VERIFY_GET_SUCCESS.message);
            }else{
                log.info("user not exists");
                return Response.failure(ResponseStatus.USER_NOT_EXISTS.status,ResponseStatus.USER_NOT_EXISTS.message);
            }
        }
        log.info("user verify times over");
        return Response.failure(ResponseStatus.USER_VERIFY_ERROR.status,ResponseStatus.USER_VERIFY_ERROR.message);
    }

    /**
     * 修改密码
     * @param modifyEncryptDto
     * @return
     */
    public Response modifyEncrypt(ModifyEncryptDto modifyEncryptDto){
        log.info("modifyEncrypt start ...");
        if(modifyEncryptDto.getOldEncrypt().equals(modifyEncryptDto.getFirstEncrypt())){
            log.info("user the old and new password are the same ");
            return Response.failure(ResponseStatus.USER_MODIFY_ENCRYPT_THE_SAME_ERROR.status,ResponseStatus.USER_MODIFY_ENCRYPT_THE_SAME_ERROR.message);
        }
        if(!modifyEncryptDto.getFirstEncrypt().equals(modifyEncryptDto.getSecondEncrypt())) {
            log.info("user the two passwords are not the same");
            return Response.failure(ResponseStatus.USER_MODIFY_ENCRYPT_PASSWORD_NOT_SAME_ERROR.status,ResponseStatus.USER_MODIFY_ENCRYPT_PASSWORD_NOT_SAME_ERROR.message);
        }else{
            User user = userMybatisDao.getUserByUserName(modifyEncryptDto.getUserName());
            if(user != null){
                if(!SecurityUtils.md5(modifyEncryptDto.getOldEncrypt(),user.getSalt()).equals(user.getEncrypt())){
                    log.info("user old password error");
                    return Response.failure(ResponseStatus.USER_PASSWORD_ERROR.status,ResponseStatus.USER_PASSWORD_ERROR.message);
                }else{
                    user.setEncrypt(SecurityUtils.md5(modifyEncryptDto.getFirstEncrypt(),user.getSalt()));
                    userMybatisDao.modifyUser(user);
                    log.info("user modifyEncrypt success");
                    log.info("modifyEncrypt end ...");
                    return Response.success(ResponseStatus.USER_MODIFY_ENCRYPT_SUCCESS.status, ResponseStatus.USER_MODIFY_ENCRYPT_SUCCESS.message);
                }
            }else {
                log.info("user not exists");
                return Response.failure(ResponseStatus.USER_NOT_EXISTS.status,ResponseStatus.USER_NOT_EXISTS.message);
            }
        }
    }

    /**
     * 找回密码
     * @param findEncryptDto
     * @return
     */
    public Response retrieveEncrypt(FindEncryptDto findEncryptDto){
        log.info("retrieveEncrypt start ...");
        if(!findEncryptDto.getFirstEncrypt().equals(findEncryptDto.getSecondEncrypt())) {
            log.info("user find encrypt password not same error");
            return Response.failure(ResponseStatus.USER_FIND_ENCRYPT_PASSWORD_NOT_SAME_ERROR.status,ResponseStatus.USER_FIND_ENCRYPT_PASSWORD_NOT_SAME_ERROR.message);
        }else{
            User user = userMybatisDao.getUserByUserName(findEncryptDto.getUserName());
            if(user != null){
                user.setEncrypt(SecurityUtils.md5(findEncryptDto.getFirstEncrypt(),user.getSalt()));
                userMybatisDao.modifyUser(user);
                log.info("user find encrypt success");
                log.info("retrieveEncrypt end ...");
                return Response.success(ResponseStatus.USER_FIND_ENCRYPT_SUCCESS.status, ResponseStatus.USER_FIND_ENCRYPT_SUCCESS.message);
            }else {
                log.info("user is not exists");
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
        User user = userMybatisDao.getUserByUid(modifyUserHobbyDto.getUid());
        String hobby = modifyUserHobbyDto.getHobby();
        String [] hobbies = hobby.split(";");
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
        log.info("getBasicDataByType start ... type = " + basicDataDto.getType());
        DictionaryType dictionaryType = userMybatisDao.getDictionaryType(basicDataDto);
        log.info("type name is :" + dictionaryType.getName());
        List<Dictionary> dictionaryList = userMybatisDao.getDictionary(basicDataDto);
        BasicDataSuccessDto basicDataSuccessDto = new BasicDataSuccessDto();
        BasicDataSuccessDto.BasicDataSuccessElement basicDataSuccess = BasicDataSuccessDto.createElement();
        basicDataSuccess.setTid(basicDataDto.getType());
        basicDataSuccess.setType(dictionaryType.getName());
        basicDataSuccess.setList(dictionaryList);
        basicDataSuccessDto.getResults().add(basicDataSuccess);
        log.info("get type data success");
        log.info("getBasicDataByType end ...");
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
        return Response.success(basicDataSuccessDto);
    }

    /**
     * 用户信息修改
     * @param modifyUserProfileDto
     * @return
     */
    public Response modifyUserProfile(ModifyUserProfileDto modifyUserProfileDto){
        log.info("modifyUserProfile start ...");
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(modifyUserProfileDto.getUid());
        if(modifyUserProfileDto.getNickName() != null) {
            if (!this.existsNickName(modifyUserProfileDto.getNickName())) {
                log.info("nick name require unique");
                return Response.failure(ResponseStatus.NICK_NAME_REQUIRE_UNIQUE.status, ResponseStatus.NICK_NAME_REQUIRE_UNIQUE.message);
            }
        }
        userProfile.setNickName(modifyUserProfileDto.getNickName());
        userProfile.setGender(modifyUserProfileDto.getGender());
        userProfile.setBirthday(modifyUserProfileDto.getBirthday());
        userProfile.setAvatar(modifyUserProfileDto.getAvatar());
        userProfile.setIntroduced(modifyUserProfileDto.getIntroduced());
        //修改用户爱好
        if(!StringUtils.isEmpty(modifyUserProfileDto.getHobby())){
            ModifyUserHobbyDto modifyUserHobbyDto = new ModifyUserHobbyDto();
            modifyUserHobbyDto.setUid(modifyUserProfileDto.getUid());
            modifyUserHobbyDto.setHobby(modifyUserProfileDto.getHobby());
            this.modifyUserHobby(modifyUserHobbyDto);
            log.info("modify user hobby");
        }
        userMybatisDao.modifyUserProfile(userProfile);
        log.info("user modify profile success");
        log.info("modifyUserProfile end ...");
        return Response.success(ResponseStatus.USER_MODIFY_PROFILE_SUCCESS.status,ResponseStatus.USER_MODIFY_PROFILE_SUCCESS.message);
    }

    @Override
    public UserProfile getUserProfileByUid(long uid) {
        return userMybatisDao.getUserProfileByUid(uid);
    }

    @Override
    public Response writeTag(PasteTagDto pasteTagDto) {
        UserTags userTag = userMybatisDao.getUserTag(pasteTagDto.getTag());
        long tagId = userMybatisDao.saveUserTag(pasteTagDto.getTag());
        userMybatisDao.saveUserTagDetail(tagId,pasteTagDto);
        userMybatisDao.saveUserTagRecord(pasteTagDto.getFromUid(),pasteTagDto.getTargetUid());
        return Response.success(ResponseStatus.PASTE_TAG_SUCCESS.status,ResponseStatus.PASTE_TAG_SUCCESS.message);
    }

    @Override
    public Response getUserNotice(UserNoticeDto userNoticeDto){
        log.info("getUserNotice start ...");
        ShowUserNoticeDto showUserNoticeDto = new ShowUserNoticeDto();
        List<UserNotice> list = userMybatisDao.userNotice(userNoticeDto);
        log.info("getUserNotice data success");
        for (UserNotice userNotice : list){
            ShowUserNoticeDto.UserNoticeElement userNoticeElement = new ShowUserNoticeDto.UserNoticeElement();
            userNoticeElement.setId(userNotice.getId());
            userNoticeElement.setTag(userNotice.getTag());
            if(!StringUtils.isEmpty( userNotice.getCoverImage())) {
                userNoticeElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + userNotice.getCoverImage());
            }else{
                userNoticeElement.setCoverImage("");
            }
            UserProfile userProfile = getUserProfileByUid(userNotice.getFromUid());
            userNoticeElement.setFromAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            userNoticeElement.setNoticeType(userNotice.getNoticeType());
            userNoticeElement.setFromNickName(userProfile.getNickName());
            userNoticeElement.setFromUid(userNotice.getFromUid());
            userNoticeElement.setReadStatus(userNotice.getReadStatus());
            userNoticeElement.setToNickName(userNotice.getToNickName());
            userNoticeElement.setCreateTime(userNotice.getCreateTime());
            userNoticeElement.setLikeCount(userNotice.getLikeCount());
            userNoticeElement.setSummary(userNotice.getSummary());
            userNoticeElement.setToUid(userNotice.getToUid());
            userNoticeElement.setCid(userNotice.getCid());
            userNoticeElement.setReview(userNotice.getReview());
            showUserNoticeDto.getUserNoticeList().add(userNoticeElement);
        }
        log.info("getUserNotice end ...");
        return Response.success(ResponseStatus.GET_USER_NOTICE_SUCCESS.status,ResponseStatus.GET_USER_NOTICE_SUCCESS.message,showUserNoticeDto);
    }

    @Override
    public Response getUserTips(long uid) {
        List<UserTips> list = userMybatisDao.getUserTips(uid);
        ShowUserTipsDto showUserTipsDto = new ShowUserTipsDto();
        showUserTipsDto.setTips(list);
        return Response.success(ResponseStatus.GET_USER_TIPS_SUCCESS.status,ResponseStatus.GET_USER_TIPS_SUCCESS.message,showUserTipsDto);
    }

    @Override
    public Response cleanUserTips(long uid) {
        List<UserTips> list = userMybatisDao.getUserTips(uid);
        ShowUserTipsDto showUserTipsDto = new ShowUserTipsDto();
        showUserTipsDto.setTips(list);
        for(UserTips userTips : list){
            userTips.setCount(0);
            userMybatisDao.modifyUserTips(userTips);
            // update usernotice
        }
        List<UserNotice> userNotices = userMybatisDao.getUserNotice(uid);
        for(UserNotice userNotice : userNotices) {
            userNotice.setPushStatus(Specification.PushStatus.PUSHED.index);
            userMybatisDao.updateUserNoticePushStatus(userNotice);
        }
        return Response.success(ResponseStatus.CLEAN_USER_TIPS_SUCCESS.status,ResponseStatus.CLEAN_USER_TIPS_SUCCESS.message);
    }

    @Override
    public Response userReport(UserReportDto userReportDto) {
        UserReport userReport = new UserReport();
        userReport.setCid(userReportDto.getCid());
        userReport.setUid(userReportDto.getUid());
        userReport.setReason(userReportDto.getReason());
        userReport.setAttachment(userReportDto.getAttachment());
        userMybatisDao.createUserReport(userReport);
        return Response.success(ResponseStatus.USER_CREATE_REPORT_SUCCESS.status,ResponseStatus.USER_CREATE_REPORT_SUCCESS.message);
    }

    @Override
    public Response showUserTags(long uid) {
        ShowUserTagsDto showUserTagsDto = new ShowUserTagsDto();
        List<UserTagsDetails> list = userMybatisDao.getUserTags(uid);
        for(UserTagsDetails tagsDetails : list){
            ShowUserTagsDto.ShowUserTagElement showUserTagElement = ShowUserTagsDto.createElement();
            showUserTagElement.setUid(tagsDetails.getUid());
            UserTags userTags = userMybatisDao.getUserTagsById(tagsDetails.getTid());
            showUserTagElement.setTag(userTags.getTag());
            showUserTagElement.setLikeCount(tagsDetails.getFrequency());
            showUserTagsDto.getShowTags().add(showUserTagElement);
        }
        return Response.success(ResponseStatus.GET_USER_TAGS_SUCCESS.status,ResponseStatus.GET_USER_TAGS_SUCCESS.message,showUserTagsDto);
    }

    @Override
    public void createUserNotice(UserNotice userNotice) {
        userMybatisDao.createUserNotice(userNotice);
    }

    @Override
    public UserTips getUserTips(UserTips userTips) {
        return userMybatisDao.getUserTips(userTips);
    }

    @Override
    public void createUserTips(UserTips userTips) {
        userMybatisDao.createUserTips(userTips);

    }

    @Override
    public void modifyUserTips(UserTips userTips) {
        userMybatisDao.modifyUserTips(userTips);

    }


    public Response likes(UserLikeDto userLikeDto){
        UserTagsRecord userTagsRecord = new UserTagsRecord();
        userTagsRecord.setFromUid(userLikeDto.getUid());
        userTagsRecord.setToUid(userLikeDto.getCustomerId());
        userTagsRecord.setTagId(userLikeDto.getTid());
        UserTagsRecord u = userMybatisDao.getUserTagsRecord(userTagsRecord);
        if(u != null){
            userMybatisDao.deleteUserTagsRecord(u);
            return Response.success(ResponseStatus.USER_TAGS_LIKES_CANCEL_SUCCESS.status,ResponseStatus.USER_TAGS_LIKES_CANCEL_SUCCESS.message);
        }
        UserTagsDetails userTagsDetails = new UserTagsDetails();
        userTagsDetails.setUid(userLikeDto.getCustomerId());
        userTagsDetails.setTid(userLikeDto.getTid());
        UserTagsDetails details = userMybatisDao.getUserTagByTidAndUid(userLikeDto.getTid(),userLikeDto.getCustomerId());
        userTagsDetails.setFrequency(details.getFrequency() + 1);
        userMybatisDao.updateUserTagDetail(userTagsDetails);
        userMybatisDao.createUserTagsRecord(userTagsRecord);
        return Response.success(ResponseStatus.USER_TAGS_LIKES_SUCCESS.status,ResponseStatus.USER_TAGS_LIKES_SUCCESS.message);
    }
    @Override
    public Response follow(FollowDto followDto) {
        log.info("follow start ...");
        log.info("sourceUid :" + followDto.getSourceUid() + "targetUid :" + followDto.getTargetUid());
        if(followDto.getSourceUid() == followDto.getTargetUid()){
            return Response.failure(ResponseStatus.CAN_NOT_FOLLOW_YOURSELF.status,ResponseStatus.CAN_NOT_FOLLOW_YOURSELF.message);
        }
        // 判断目标对象是否存在
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(followDto.getTargetUid());
        if(userProfile == null){
            log.info("target user not exists");
            return Response.failure(ResponseStatus.USER_NOT_EXISTS.status,ResponseStatus.USER_NOT_EXISTS.message);
        }
        UserFollow userFollow = new UserFollow();
        userFollow.setSourceUid(followDto.getSourceUid());
        userFollow.setTargetUid(followDto.getTargetUid());
        // 判断是否已经关注过了
        if(followDto.getAction() == Specification.UserFollowAction.FOLLOW.index) {
            // 创建关注
            if(userMybatisDao.getUserFollow(followDto.getSourceUid(),followDto.getTargetUid()) != null){
                log.info("can't duplicate follow");
                return Response.failure(ResponseStatus.CAN_NOT_DUPLICATE_FOLLOW.status,ResponseStatus.CAN_NOT_DUPLICATE_FOLLOW.message);
            }
            userMybatisDao.createFollow(userFollow);
            log.info("follow success");
            //关注提醒
            //push(followDto.getTargetUid(),followDto.getSourceUid(),Specification.PushMessageType.FOLLOW.index,null);
            //更换信鸽推送为极光推动
            JpushToken jpushToken = getJpushTokeByUid(followDto.getTargetUid());
            if(jpushToken == null){
                //兼容老版本，如果客户端没有更新则还走信鸽push
                push(followDto.getTargetUid(),followDto.getSourceUid(),Specification.PushMessageType.FOLLOW.index,null);
            }else {
                UserProfile sourceUser = getUserProfileByUid(followDto.getSourceUid());
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("messageType",Specification.PushMessageType.FOLLOW.index);
                String alias = String.valueOf(followDto.getTargetUid());
                jPushService.payloadByIdExtra(alias, sourceUser.getNickName() + "关注了你！", JPushUtils.packageExtra(jsonObject));
            }
            //粉丝数量红点
            log.info("follow fans add push start");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("fansCount","1");
            String alias = String.valueOf(followDto.getTargetUid());
            jPushService.payloadByIdForMessage(alias,jsonObject.toString());
            log.info("follow fans add push end ");

            log.info("follow push success");
            //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.FOLLOW.index,0,followDto.getSourceUid()));
            log.info("monitor success");
            log.info("follow end ...");
            return Response.success(ResponseStatus.USER_FOLLOW_SUCCESS.status, ResponseStatus.USER_FOLLOW_SUCCESS.message);
        }else if(followDto.getAction() == Specification.UserFollowAction.UN_FOLLOW.index){
            // 取消关注
            log.info("cancel follow");
            UserFollow ufw = userMybatisDao.getUserFollow(followDto.getSourceUid(),followDto.getTargetUid());
            if(ufw!=null) {
                userMybatisDao.deleteFollow(ufw.getId());
            }
            //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.UN_FOLLOW.index,0,followDto.getSourceUid()));
            //log.info("monitor success");
            return Response.success(ResponseStatus.USER_CANCEL_FOLLOW_SUCCESS.status, ResponseStatus.USER_CANCEL_FOLLOW_SUCCESS.message);
        }else{
            log.info("illegal request");
            return Response.failure(ResponseStatus.ILLEGAL_REQUEST.status,ResponseStatus.ILLEGAL_REQUEST.message);
        }
    }

    @Override
    public Response getFans(FansParamsDto fansParamsDto) {
        log.info("getFans start ...");
        List<UserFansDto> list = userMybatisDao.getFans(fansParamsDto);
        log.info("getFans getData success");
        for(UserFansDto userFansDto : list){
            userFansDto.setAvatar(Constant.QINIU_DOMAIN + "/" + userFansDto.getAvatar());
            int followMe = this.isFollow(fansParamsDto.getUid(),userFansDto.getUid());
            userFansDto.setIsFollowMe(followMe);
            int followed = this.isFollow(userFansDto.getUid(),fansParamsDto.getUid());
            userFansDto.setIsFollowed(followed);
            UserProfile userProfile = userMybatisDao.getUserProfileByUid(userFansDto.getUid());
            userFansDto.setIntroduced(userProfile.getIntroduced());
        }
        ShowUserFansDto showUserFansDto = new ShowUserFansDto();
        showUserFansDto.setResult(list);
        log.info("getFans end ...");
        return Response.success(ResponseStatus.SHOW_USER_FANS_LIST_SUCCESS.status, ResponseStatus.SHOW_USER_FANS_LIST_SUCCESS.message,showUserFansDto);
    }

    @Override
    public Response getFollows(FollowParamsDto followParamsDto) {
        log.info("getFollows start ...");
        List<UserFollowDto> list = userMybatisDao.getFollows(followParamsDto);
        log.info("getFollows getData success");
        ShowUserFollowDto showUserFollowDto = new ShowUserFollowDto();
        for(UserFollowDto userFollowDto : list){
            userFollowDto.setAvatar(Constant.QINIU_DOMAIN + "/" + userFollowDto.getAvatar());
            int followMe = this.isFollow(followParamsDto.getUid(),userFollowDto.getUid());
            userFollowDto.setIsFollowMe(followMe);
            int followed = this.isFollow(userFollowDto.getUid(),followParamsDto.getUid());
            userFollowDto.setIsFollowed(followed);
            UserProfile userProfile = userMybatisDao.getUserProfileByUid(userFollowDto.getUid());
            userFollowDto.setIntroduced(userProfile.getIntroduced());
        }
        showUserFollowDto.setResult(list);
        log.info("getFollows end ...");
        return Response.success(ResponseStatus.SHOW_USER_FOLLOW_LIST_SUCCESS.status, ResponseStatus.SHOW_USER_FOLLOW_LIST_SUCCESS.message,showUserFollowDto);
    }

    public int isFollow(long targetUid,long sourceUid){
        UserFollow ufw = userMybatisDao.getUserFollow(sourceUid,targetUid);
        if(ufw == null){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public UserToken getUserByUidAndToken(long uid, String token) {
        return userMybatisDao.getUserTokenByUid(uid, token);
    }


    public Response getUser(long targetUid, long sourceUid){
        UserProfile userProfile =  getUserProfileByUid(targetUid);
        UserInfoDto.User user = new UserInfoDto.User();
        user.setNickName(userProfile.getNickName());
        user.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
        user.setGender(userProfile.getGender());
        user.setUid(userProfile.getUid());
        user.setIsFollowed(isFollow(targetUid,sourceUid));
        user.setIsFollowMe(isFollow(sourceUid,targetUid));
        return Response.success(user);
    }

    @Override
    public Response search(String keyword,int page,int pageSize,long uid) {
        List<UserProfile> list =  userMybatisDao.search(keyword,page,pageSize);
        SearchDto searchDto = new SearchDto();
        searchDto.setTotalRecord(userMybatisDao.total(keyword));
        int totalPage = (searchDto.getTotalRecord() + pageSize -1) / pageSize;
        searchDto.setTotalPage(totalPage);
        for(UserProfile userProfile : list){
            SearchDto.SearchElement element = searchDto.createElement();
            element.setUid(userProfile.getUid());
            element.setAvatar(Constant.QINIU_DOMAIN + "/" +userProfile.getAvatar());
            element.setNickName(userProfile.getNickName());
            int follow = this.isFollow(userProfile.getUid(),uid);
            element.setIsFollowed(follow);
            int followMe = this.isFollow(uid,userProfile.getUid());
            element.setIsFollowMe(followMe);
            element.setIntroduced(userProfile.getIntroduced());
            searchDto.getResult().add(element);
        }
        return Response.success(searchDto);
    }

    @Override
    public Response assistant(String keyword) {
        List<UserProfile> list =  userMybatisDao.assistant(keyword);
        SearchAssistantDto searchAssistantDto = new SearchAssistantDto();
        for(UserProfile userProfile : list){
            SearchAssistantDto.SearchAssistantElement element = searchAssistantDto.createElement();
            element.setUid(userProfile.getUid());
            element.setAvatar(Constant.QINIU_DOMAIN + "/" +userProfile.getAvatar());
            element.setNickName(userProfile.getNickName());
            searchAssistantDto.getResult().add(element);
        }
        return Response.success(searchAssistantDto);
    }

    @Override
    public Response checkNickName(String nickName) {
        List<UserProfile> list = userMybatisDao.getByNickName(nickName);
        if(list!=null&&list.size()>0){
            return Response.failure(ResponseStatus.NICK_NAME_REQUIRE_UNIQUE.status,ResponseStatus.NICK_NAME_REQUIRE_UNIQUE.message);
        }else{
            return Response.success();
        }
    }
    public boolean existsNickName(String nickName) {
        List<UserProfile> list = userMybatisDao.getByNickName(nickName);
        if(list!=null&&list.size()>0){
            return false;
        }else{
            return true;
        }
    }
    public UserProfile getUserByNickName(String nickName) {
        List<UserProfile> list = userMybatisDao.getByNickName(nickName);
        return com.me2me.common.utils.Lists.getSingle(list);
    }

    @Override
    public List<Long> getFollowList(long uid) {
        List<Long> result = Lists.newArrayList();
        List<UserFollow> list = userMybatisDao.getUserFollow(uid);
        for(UserFollow userFollow :list){
            result.add(userFollow.getTargetUid());
        }
        return  result;
    }

    @Override
    public Response getUserProfile(long uid) {
        log.info("getUserProfile start ...");
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
        log.info("getUserProfile getUserData success . uid : " + uid);
        ShowUserProfileDto showUserProfileDto = new ShowUserProfileDto();
        showUserProfileDto.setUid(userProfile.getUid());
        showUserProfileDto.setNickName(userProfile.getNickName());
        showUserProfileDto.setAvatar(Constant.QINIU_DOMAIN + "/" +userProfile.getAvatar());
        showUserProfileDto.setBirthday(userProfile.getBirthday());
        showUserProfileDto.setGender(userProfile.getGender());
        showUserProfileDto.setUserName(userProfile.getMobile());
        showUserProfileDto.setIsPromoter(userProfile.getIsPromoter());
        showUserProfileDto.setUgcCount(userInitJdbcDao.getUGCount(uid));
        showUserProfileDto.setLiveCount(userInitJdbcDao.getLiveCount(uid));
        if(!StringUtils.isEmpty(userProfile.getThirdPartBind())) {
            showUserProfileDto.setThirdPartBind(userProfile.getThirdPartBind());
        }
        Set<String> powerKeys = cacheService.smembers(POWER_KEY);
        if(powerKeys!=null && !powerKeys.isEmpty()) {
            if (powerKeys.contains(uid + "")) {
                showUserProfileDto.setPower(1);
            }
        }
        UserToken userToken = userMybatisDao.getUserTokenByUid(uid);
        log.info("get userToken success ");
        showUserProfileDto.setToken(userToken.getToken());
        showUserProfileDto.setMeNumber(userMybatisDao.getUserNoByUid(userProfile.getUid()).getMeNumber().toString());
        log.info("get meNumber success ");
        showUserProfileDto.setFollowedCount(userMybatisDao.getUserFollowCount(uid));
        log.info("get followedCount success ");
        showUserProfileDto.setFansCount(userMybatisDao.getUserFansCount(uid));
        log.info("get fansCount success ");
        showUserProfileDto.setIntroduced(userProfile.getIntroduced());
        log.info("get introduced success ");
        List<UserHobby> list = userMybatisDao.getHobby(uid);
        log.info("get userHobby success ");
        for (UserHobby userHobby : list){
            ShowUserProfileDto.Hobby hobby = showUserProfileDto.createHobby();
            hobby.setHobby(userHobby.getHobby());
            Dictionary dictionary =  userMybatisDao.getDictionaryById(userHobby.getHobby());
            hobby.setValue(dictionary.getValue());
            showUserProfileDto.getHobbyList().add(hobby);
        }
        log.info("getUserProfile end ...");
        return  Response.success(showUserProfileDto);
    }

    @Override
    public ApplicationSecurity getApplicationSecurityByAppId(String appId) {
        return userMybatisDao.getApplicationSecurityByAppId(appId);
    }

    @Override
    public int getFansCount(long uid){
        return userMybatisDao.getUserFansCount(uid);
    }

    /**
     * 该方法暂时注释掉，未来等用户量超出上限再开启
     */
    @Override
    public void initUserNumber(int limit) {
        List<Integer> list = Lists.newArrayList();
        List<Integer> container = Lists.newArrayList();
        int start = 0;
        int end = 0;
        if(limit == 0){
            start = 10000000;
            end =30000000;
        }else if(limit == 1){
            start = 30000000;
            end =60000000;
        } else if(limit == 2){
            start = 60000000;
            end =90000000;
        }
        for(int i = start;i<end;i++){
            list.add(i);
        }
        Collections.shuffle(list);
        for(int i = 0;i<list.size();i++){
            container.add(list.get(i));
            if(i%10000==0){
                userInitJdbcDao.batchInsertMeNumber(container);
                container.clear();
            }
        }
    }

    @Override
    public Response versionControl(String version,int platform) {
        VersionControlDto versionControlDto = new VersionControlDto();
        VersionControl control = userMybatisDao.getVersion(version,platform);
        VersionControl versionControl = userMybatisDao.getNewestVersion(platform);
        if(control == null || (version.compareTo(versionControl.getVersion()) >= 0 && control.getVersion().equals(versionControl.getVersion()))){
            return Response.success(versionControlDto);
        }
        versionControlDto.setId(versionControl.getId());
        versionControlDto.setUpdateDescription(versionControl.getUpdateDescription());
        versionControlDto.setUpdateTime(versionControl.getUpdateTime());
        versionControlDto.setPlatform(versionControl.getPlatform());
        versionControlDto.setVersion(versionControl.getVersion());
        versionControlDto.setUpdateUrl(versionControl.getUpdateUrl());
        if(control == null ||(!control.getVersion().equals(versionControl.getVersion()))){
            if(versionControl.getForceUpdate() == 1){
                versionControlDto.setIsUpdate(Specification.VersionStatus.FORCE_UPDATE.index);
            }else{
                versionControlDto.setIsUpdate(Specification.VersionStatus.UPDATE.index);
            }
        }else{
            versionControlDto.setIsUpdate(Specification.VersionStatus.NEWEST.index);
        }
        //monitorService.post(new MonitorEvent(Specification.MonitorType.BOOT.index,Specification.MonitorAction.BOOT.index,0,0));
        return Response.success(versionControlDto);
    }

    @Override
    public Response updateVersion(VersionDto versionDto) {
        userMybatisDao.updateVersion(versionDto);
        return Response.success(ResponseStatus.VERSION_UPDATE_SUCCESS.status,ResponseStatus.VERSION_UPDATE_SUCCESS.message);
    }

    @Override
    public int getFollowCount(long uid){
        return userMybatisDao.getUserFollowCount(uid);
    }

    @Override
    public String getUserNoByUid(long uid){
        return userMybatisDao.getUserNoByUid(uid).getMeNumber().toString();
    }

    @Override
    public UserNotice getUserNotice(UserNotice userNotice) {
        return userMybatisDao.getUserNotice(userNotice);
    }

    @Override
    public String getUserHobbyByUid(long uid){
        List<UserHobby> list = userMybatisDao.getHobby(uid);
        String result = "";
        for (UserHobby userHobby : list){
            Dictionary dictionary =  userMybatisDao.getDictionaryById(userHobby.getHobby());
            if(dictionary != null && !StringUtils.isEmpty(dictionary.getValue())) {
                result += dictionary.getValue() + ",";
            }
        }
        return result.length() > 0 ? result.substring(0,result.length()-1) : result;
    }

    @Override
    public UserDevice getUserDevice(long uid) {
        return userMybatisDao.getUserDevice(uid);
    }


    @Autowired
    private MessageNotificationAdapter messageNotificationAdapter;

    /**
     * 提醒
     * @param targetUid
     * @param sourceUid
     * @param type
     * @param title
     */
    @Override
    public void push(long targetUid ,long sourceUid ,int type,String title){
//        if(targetUid == sourceUid){
//            return;
//        }
//        UserDevice device = getUserDevice(targetUid);
//        if(device != null) {
//            PushMessageDto pushMessageDto = new PushMessageDto();
//            pushMessageDto.setToken(device.getDeviceNo());
//            pushMessageDto.setDevicePlatform(device.getPlatform());
//            //直播贴标
//            if (type == Specification.PushMessageType.LIVE_TAG.index) {
//                pushMessageDto.setContent("你的直播:" + title + "收到了1个新感受");
//                //日记被贴标
//            } else if (type == Specification.PushMessageType.TAG.index) {
//                pushMessageDto.setContent("你的日记:" + title + "收到了1个新感受");
//                //直播回复
//            } else if (type == Specification.PushMessageType.LIVE_REVIEW.index) {
//                UserProfile userProfile = getUserProfileByUid(sourceUid);
//                pushMessageDto.setContent(userProfile.getNickName() + "评论了你的直播:" + title);
//                //日记被评论
//            } else if (type == Specification.PushMessageType.REVIEW.index) {
//                UserProfile userProfile = getUserProfileByUid(sourceUid);
//                pushMessageDto.setContent(userProfile.getNickName() + "评论了你的日记:" + title);
//                //直播置热
//            } else if (type == Specification.PushMessageType.LIVE_HOTTEST.index) {
//                pushMessageDto.setContent("你的直播：" + title + "上热点啦！");
//                //UGC置热
//            } else if (type == Specification.PushMessageType.HOTTEST.index) {
//                pushMessageDto.setContent("你的日记：" + title + "上热点啦！");
//                //被人关注
//            } else if (type == Specification.PushMessageType.FOLLOW.index) {
//                UserProfile userProfile = getUserProfileByUid(sourceUid);
//                pushMessageDto.setContent(userProfile.getNickName() + "关注了你");
//                //收藏的直播主播更新了
//            } else if (type == Specification.PushMessageType.UPDATE.index) {
//                pushMessageDto.setContent("你订阅的直播：" + title + "更新了");
//                //你关注的直播有了新的更新了
//            } else if (type == Specification.PushMessageType.LIVE.index) {
//                UserProfile userProfile = getUserProfileByUid(sourceUid);
//                pushMessageDto.setContent("你关注的主播" + userProfile.getNickName() + "有了新直播：" + title);
//            }
//            if (device.getPlatform() == 1) {
//                PushMessageAndroidDto pushMessageAndroidDto = new PushMessageAndroidDto();
//                pushMessageAndroidDto.setTitle(pushMessageDto.getContent());
//                pushMessageAndroidDto.setToken(device.getDeviceNo());
//                pushMessageAndroidDto.setMessageType(type);
//                pushMessageAndroidDto.setContent(pushMessageDto.getContent());
//              PushLogDto pushLogDto = xgPushService.pushSingleDevice(pushMessageAndroidDto);
//                if (pushLogDto != null) {
//                    pushLogDto.setMeaageType(type);
//                    userMybatisDao.createPushLog(pushLogDto);
//                }
//            } else {
//                PushMessageIosDto pushMessageIosDto = new PushMessageIosDto();
//                pushMessageIosDto.setTitle(pushMessageDto.getContent());
//                pushMessageIosDto.setToken(device.getDeviceNo());
//                pushMessageIosDto.setContent(pushMessageDto.getContent());
//                PushLogDto pushLogDto = xgPushService.pushSingleDeviceIOS(pushMessageIosDto);
//                if (pushLogDto != null) {
//                    pushLogDto.setMeaageType(type);
//                    userMybatisDao.createPushLog(pushLogDto);
//                }
//            }
//        }
        // new MessageNotificationAdapter(MessageNotificationFactory.getInstance(type)).notice(title,targetUid,sourceUid);
        // fix by peter
        // MessageNotificationAdapter messageNotificationAdapter = SpringContextHolder.getBean(MessageNotificationAdapter.class);
        messageNotificationAdapter.setType(type);
        messageNotificationAdapter.notice(title,targetUid,sourceUid,type);
    }

    @Override
    public List<UserFollow> getFans(long uid) {
        return userMybatisDao.getUserFans(uid);
    }

    @Override
    public Response setUserExcellent(long uid) {
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
        userProfile.setExcellent(1);
        userMybatisDao.modifyUserProfile(userProfile);
        return Response.success(ResponseStatus.SET_USER_EXCELLENT_SUCCESS.status,ResponseStatus.SET_USER_EXCELLENT_SUCCESS.message);
    }

    @Override
    public void createPushLog(PushLogDto pushLogDto) {
        userMybatisDao.createPushLog(pushLogDto);
    }

    @Override
    public Response logout(long uid) {
        userMybatisDao.logout(uid);
        log.info("logout success + uid = " + uid);
        return Response.success(ResponseStatus.LOGOUT_SUCCESS.status,ResponseStatus.LOGOUT_SUCCESS.message);
    }

    @Override
    public Response getSpecialUserProfile(long uid) {
        log.info("getSpecialUserProfile start ... uid = " + uid);
        UserProfile userProfile = getUserProfileByUid(uid);
        log.info("getSpecialUserProfile get userProfile success ");
        SpecialUserDto userDto = new SpecialUserDto();
        userDto.setBirthday(userProfile.getBirthday());
        userDto.setMobilePhone(userProfile.getMobile());
        userDto.setSex(userProfile.getGender().toString());
        userDto.setUserName(userProfile.getNickName());
        String hobbies = getUserHobbyByUid(uid);
        log.info("getSpecialUserProfile get hobby success ");
        userDto.setInterests(hobbies);
        log.info("getSpecialUserProfile end ");
        return Response.success(userDto);
    }

    public List<User> getRobots(int limit){
        List<Map<String,Object>> maps = userInitJdbcDao.getRobots(limit);
        List users = Lists.newArrayList();
        for(Map map : maps){
            User user = new User();
            Long uid = Long.valueOf(map.get("uid").toString());
            user.setUid(uid);
            users.add(user);
        }
        return users;
    }

    /**
     * 该方法已经不用了
     */
    @Override
    public void pushMessage() {
        // 自己发布的日记被评论
        // 自己发布的日记被贴了标签
        // 自己的直播被评论
        // 有用户关注了自己
        // 关注的主播有了新直播
        // 订阅的直播主播有更新
        List<Map<String,Object>> list =  userInitJdbcDao.getUserNoticeCounter("3,4,0,2,6,5,9");
        List<Map<String,Object>> updateList = userInitJdbcDao.getUserNoticeList("3,4,0,2,6,5,9");
        Set<Long> skippedUids = Sets.newConcurrentHashSet();
        for(Map map : list){
            // 获取用户push_token
            int counter = Integer.valueOf(map.get("counter").toString());
            long uid = Long.valueOf(map.get("uid").toString());

            // 新版本极光推送
            // 获取极光推送token
            List<JpushToken> jpushTokens = userMybatisDao.getJpushToken(uid);
            for(JpushToken jpushToken : jpushTokens) {
                log.info("jpush for combination message for {}",uid);
                String alias = String.valueOf(uid);
                jPushService.payloadById(alias,"你有"+counter+"条新消息！");
            }

            UserDevice userDevice = userMybatisDao.getUserDevice(uid);
            if(userDevice==null || StringUtils.isEmpty(userDevice.getDeviceNo())) {
                log.warn("current uid {} user device not find .",uid);
                skippedUids.add(uid);
                continue;
            }
            // 老版本信鸽推送
            int platform = userDevice.getPlatform();
            if(platform == Specification.DevicePlatform.ANDROID.index){
                // android
                PushMessageAndroidDto message = new PushMessageAndroidDto();
                message.setToken(userDevice.getDeviceNo());
                message.setContent("你有"+counter+"条新消息！");
                log.info("push message for android uid is {} and message count {}",uid,counter);
                xgPushService.pushSingleDevice(message);
            }else if(platform == Specification.DevicePlatform.IOS.index){
                // ios
                PushMessageIosDto message = new PushMessageIosDto();
                message.setContent("你有"+counter+"条新消息！");
                message.setToken(userDevice.getDeviceNo());
                log.info("push message for ios uid is {} and message count {}",uid,counter);
                xgPushService.pushSingleDeviceIOS(message);
            }

        }
        // 更新推送状态
        for(Map map : updateList){
            Long id = Long.valueOf(map.get("id").toString());
            UserNotice userNotice = userMybatisDao.getUserNoticeById(id);
//            Long uid = userNotice.getToUid();
//            if(skippedUids.contains(uid)){
//                continue;
//            }
            userNotice.setPushStatus(Specification.PushStatus.PUSHED.index);
            userMybatisDao.updateUserNoticePushStatus(userNotice);
        }
    }

    @Override
    public Response genQRcode(long uid) {
        QRCodeDto qrCodeDto = new QRCodeDto();
        try {
            UserProfile userProfile = getUserProfileByUid(uid);
            if(StringUtils.isEmpty(userProfile.getQrcode())) {
                byte[] image = QRCodeUtil.encode(reg_web + uid);
                String key = UUID.randomUUID().toString();
                fileTransferService.upload(image, key);
                qrCodeDto.setQrCodeUrl(Constant.QINIU_DOMAIN + "/" + key);
                userProfile.setQrcode(key);
                userMybatisDao.modifyUserProfile(userProfile);
            }else{
                qrCodeDto.setQrCodeUrl(Constant.QINIU_DOMAIN + "/" + userProfile.getQrcode());
            }
        }catch (Exception e){
            return Response.failure(ResponseStatus.QRCODE_FAILURE.status,ResponseStatus.QRCODE_FAILURE.message);
        }
        return Response.success(ResponseStatus.QRCODE_SUCCESS.status,ResponseStatus.QRCODE_SUCCESS.message,qrCodeDto);
    }

    @Override
    public Response refereeSignUp(UserRefereeSignUpDto userDto) {
        log.info("refereeSignUp start ...");
        // 校验手机号码是否注册
        String mobile = userDto.getMobile();
        log.info("mobile:" + mobile );
        if(userMybatisDao.getUserByUserName(mobile) != null){
            // 该用户已经注册过
            log.info("mobile:" + mobile + " is already register");
            return Response.failure(ResponseStatus.USER_MOBILE_DUPLICATE.status,ResponseStatus.USER_MOBILE_DUPLICATE.message);
        }
        // 检查用户名是否重复
        if(!this.existsNickName(userDto.getNickName())){
            log.info("nickname:" + userDto.getNickName() + " is already used");
            return Response.failure(ResponseStatus.NICK_NAME_REQUIRE_UNIQUE.status,ResponseStatus.NICK_NAME_REQUIRE_UNIQUE.message);
        }
        User user = new User();
        String salt = SecurityUtils.getMask();
        user.setEncrypt(SecurityUtils.md5(userDto.getEncrypt(),salt));
        user.setSalt(salt);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setStatus(Specification.UserStatus.NORMAL.index);
        user.setUserName(userDto.getMobile());
        userMybatisDao.createUser(user);
        log.info("user is create");
        UserProfile userProfile = new UserProfile();
        userProfile.setUid(user.getUid());
        userProfile.setAvatar(Constant.DEFAULT_AVATAR);
        userProfile.setMobile(userDto.getMobile());
        userProfile.setNickName(userDto.getNickName());
        userProfile.setIntroduced(userDto.getIntroduced());
        userProfile.setIsActivate(Specification.UserActivate.UN_ACTIVATED.index);
        userProfile.setRefereeUid(userDto.getRefereeUid());
        userMybatisDao.createUserProfile(userProfile);
        log.info("userProfile is create");
        // 保存用户token信息
        UserToken userToken = new UserToken();
        userToken.setUid(user.getUid());
        userToken.setToken(SecurityUtils.getToken());
        userMybatisDao.createUserToken(userToken);
        log.info("userToken is create");
        log.info("refereeSignUp end ...");
        return Response.success(ResponseStatus.USER_SING_UP_SUCCESS.status,ResponseStatus.USER_SING_UP_SUCCESS.message);
    }

    @Override
    public UserProfile4H5Dto getUserProfile4H5(long uid) {
        UserProfile4H5Dto dto = new UserProfile4H5Dto();
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
        dto.setUid(uid);
        dto.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
        dto.setNickName(userProfile.getNickName());
        dto.setSummary(userProfile.getIntroduced());
        return dto;
    }

    @Override
    public Response getRefereeProfile(long uid) {
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
        //查询邀请人数
        RefereeProfileDto dto = new RefereeProfileDto();
        dto.setRefereeCount(userMybatisDao.getRefereeCount(uid));
        //查询发布文章数
        dto.setContentCount(userInitJdbcDao.getContentCount(uid));
        //显示粉丝数
        dto.setFansCount(userMybatisDao.getFansCount(uid));
        dto.setUid(uid);
        dto.setIntroduced(userProfile.getIntroduced());
        dto.setAvatar(Constant.QINIU_DOMAIN + "/" +userProfile.getAvatar());
        dto.setNickName(userProfile.getNickName());
        dto.setRegUrl(reg_web+uid);
//        byte[] image = QRCodeUtil.encode(reg_web + uid);
//        String key = UUID.randomUUID().toString();
//        fileTransferService.upload(image,key);
//        dto.setQrCodeUrl(Constant.QINIU_DOMAIN + "/" + key);
        return Response.success(dto);
    }

    @Override
    public int getUserInternalStatus(long uid, long owner) {
        return  oldUserJdbcDao.getUserInternalStatus(uid,owner);
    }

    @Override
    public Response getFansOrderByNickName(FansParamsDto fansParamsDto) {
        log.info("getFans start ...");
        List<UserFansDto> list = userMybatisDao.getFansOrderByNickName(fansParamsDto);
        log.info("getFans getData success");
        for(UserFansDto userFansDto : list){
            userFansDto.setAvatar(Constant.QINIU_DOMAIN + "/" + userFansDto.getAvatar());
            int followMe = this.isFollow(fansParamsDto.getUid(),userFansDto.getUid());
            userFansDto.setIsFollowMe(followMe);
            int followed = this.isFollow(userFansDto.getUid(),fansParamsDto.getUid());
            userFansDto.setIsFollowed(followed);
            UserProfile userProfile = userMybatisDao.getUserProfileByUid(userFansDto.getUid());
            userFansDto.setIntroduced(userProfile.getIntroduced());
        }
        ShowUserFansDto showUserFansDto = new ShowUserFansDto();
        showUserFansDto.setResult(list);
        log.info("getFans end ...");
        return Response.success(ResponseStatus.SHOW_USER_FANS_LIST_SUCCESS.status, ResponseStatus.SHOW_USER_FANS_LIST_SUCCESS.message,showUserFansDto);
    }

    @Override
    public Response getFollowsOrderByNickName(FollowParamsDto followParamsDto) {
        log.info("getFollowsOrderByNickName start ...");
        List<UserFollowDto> list = userMybatisDao.getFollowsOrderByNickName(followParamsDto);
        log.info("getFollowsOrderByNickName getData success");
        ShowUserFollowDto showUserFollowDto = new ShowUserFollowDto();
        for(UserFollowDto userFollowDto : list){
            userFollowDto.setAvatar(Constant.QINIU_DOMAIN + "/" + userFollowDto.getAvatar());
            int followMe = this.isFollow(followParamsDto.getUid(),userFollowDto.getUid());
            userFollowDto.setIsFollowMe(followMe);
            int followed = this.isFollow(userFollowDto.getUid(),followParamsDto.getUid());
            userFollowDto.setIsFollowed(followed);
            UserProfile userProfile = userMybatisDao.getUserProfileByUid(userFollowDto.getUid());
            userFollowDto.setIntroduced(userProfile.getIntroduced());
        }
        showUserFollowDto.setResult(list);
        log.info("getFollowsOrderByNickName end ...");
        return Response.success(ResponseStatus.SHOW_USER_FOLLOW_LIST_SUCCESS.status, ResponseStatus.SHOW_USER_FOLLOW_LIST_SUCCESS.message,showUserFollowDto);
    }

    @Override
    public Response getPromoter(String nickName,String startDate,String endDate) {
        PromoterDto dto = new PromoterDto();
        List<UserProfile> list = userMybatisDao.getPromoter(nickName);
        for(UserProfile userProfile : list){
            PromoterDto.PromoterElement promoterElement = PromoterDto.createElement();
            promoterElement.setUid(userProfile.getUid());
            promoterElement.setNickName(userProfile.getNickName());
            promoterElement.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
            promoterElement.setActivateCount(userMybatisDao.getRefereeCount(userProfile.getUid(),startDate,endDate));
            promoterElement.setRefereeCount(userMybatisDao.getUnactivatedCount(userProfile.getUid(),startDate,endDate));
            dto.getPromoterElementList().add(promoterElement);
        }
        return Response.success(dto);
    }

    @Override
    public Response getPhoto(long sinceId) {
        PhotoDto dto = new PhotoDto();
        List<Map<String, Object>> list = userInitJdbcDao.getPhoto(sinceId);
        for(Map<String,Object> map : list){
            PhotoDto.Photo photo = PhotoDto.create();
            photo.setId(Long.valueOf(map.get("id").toString()));
            photo.setImageUrl(Constant.QINIU_DOMAIN + "/"+map.get("imageUrl").toString());
            photo.setTitle(map.get("title").toString());
            dto.getResult().add(photo);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("messageType",Specification.PushMessageType.LIVE_HOTTEST.index);
            String alias = String.valueOf(sinceId);
            jPushService.payloadByIdExtra(alias,"你发布的内容上热点啦!",JPushUtils.packageExtra(jsonObject));
        }
        log.info("live hottest end");
        return Response.success(dto);
    }

    @Override
    public JpushToken getJpushTokeByUid(long uid) {
        List<JpushToken> jpushTokens = userMybatisDao.getJpushToken(uid);
        return com.me2me.common.utils.Lists.getSingle(jpushTokens);
    }

    @Override
    public Response searchFans(String keyword, int page, int pageSize, long uid) {
        SearchFansDto searchFansDto = new SearchFansDto();
        searchFansDto.setNickName("%"+keyword+"%");
        searchFansDto.setStart((page - 1) * pageSize);
        searchFansDto.setPageSize(pageSize);
        searchFansDto.setUid(uid);
        SearchDto searchDto = new SearchDto();
        searchDto.setTotalRecord(userMybatisDao.totalFans(searchFansDto));
        int totalPage = (searchDto.getTotalRecord() + pageSize -1) / pageSize;
        searchDto.setTotalPage(totalPage);
        List<UserProfile> list = userMybatisDao.searchFans(searchFansDto);
        for(UserProfile userProfile : list){
            SearchDto.SearchElement element = searchDto.createElement();
            element.setUid(userProfile.getUid());
            element.setAvatar(Constant.QINIU_DOMAIN + "/" +userProfile.getAvatar());
            element.setNickName(userProfile.getNickName());
            int follow = this.isFollow(userProfile.getUid(),uid);
            element.setIsFollowed(follow);
            int followMe = this.isFollow(uid,userProfile.getUid());
            element.setIsFollowMe(followMe);
            element.setIntroduced(userProfile.getIntroduced());
            searchDto.getResult().add(element);
        }
        return Response.success(searchDto);
    }

    @Override
    public Response thirdPartLogin(ThirdPartSignUpDto thirdPartSignUpDto) {
        // TODO: 2016/9/12
        LoginSuccessDto loginSuccessDto = new LoginSuccessDto();
            List<ThirdPartUser> users = userMybatisDao.getThirdPartUser(thirdPartSignUpDto.getThirdPartOpenId());
            if (users.size() > 0) {
                long uid = users.get(0).getUid();
                UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
                UserToken userToken = userMybatisDao.getUserTokenByUid(uid);
                loginSuccessDto.setUid(userProfile.getUid());
                loginSuccessDto.setGender(userProfile.getGender());
                loginSuccessDto.setNickName(userProfile.getNickName());
                loginSuccessDto.setToken(userToken.getToken());

                return Response.failure(ResponseStatus.USER_EXISTS.status, ResponseStatus.USER_EXISTS.message, loginSuccessDto);
            } else {
                buildThirdPart(thirdPartSignUpDto, loginSuccessDto);
            }
        return Response.success(ResponseStatus.USER_LOGIN_SUCCESS.status,ResponseStatus.USER_LOGIN_SUCCESS.message,loginSuccessDto);
    }

    //第三方登录公共方法
    public void buildThirdPart(ThirdPartSignUpDto thirdPartSignUpDto ,LoginSuccessDto loginSuccessDto){
        List<UserAccountBindStatusDto> array = Lists.newArrayList();
        User user = new User();
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        String salt = SecurityUtils.getMask();
        user.setEncrypt(SecurityUtils.md5(salt,salt));
        user.setSalt(salt);
        user.setStatus(0);
        user.setUserName(thirdPartSignUpDto.getThirdPartOpenId());
        userMybatisDao.createUser(user);
        log.info("user is create");

        log.info("get user by username");
        User user1 = userMybatisDao.getUserByUserName(thirdPartSignUpDto.getThirdPartOpenId());
        UserProfile userProfile = new UserProfile();
        userProfile.setUid(user1.getUid());
        userProfile.setAvatar(thirdPartSignUpDto.getAvatar());
        userProfile.setNickName(thirdPartSignUpDto.getNickName());
        userProfile.setAvatar(thirdPartSignUpDto.getAvatar());
        userProfile.setGender(thirdPartSignUpDto.getGender());
        userProfile.setCreateTime(new Date());
        if(thirdPartSignUpDto.getThirdPartOpenId().length() > 11) {
            String openId = thirdPartSignUpDto.getThirdPartOpenId();
            userProfile.setMobile(openId.substring(0,11));
        }
        //QQ
        if(thirdPartSignUpDto.getThirdPartType() == Specification.ThirdPartType.QQ.index) {
            array.add(new UserAccountBindStatusDto(Specification.ThirdPartType.QQ.index,Specification.ThirdPartType.QQ.name, 1));
        }//微信
        else if(thirdPartSignUpDto.getThirdPartType() == Specification.ThirdPartType.WEIXIN.index) {
            array.add(new UserAccountBindStatusDto(Specification.ThirdPartType.WEIXIN.index,Specification.ThirdPartType.WEIXIN.name, 1));
        }//微博
        else if(thirdPartSignUpDto.getThirdPartType() == Specification.ThirdPartType.WEIBO.index){
            array.add(new UserAccountBindStatusDto(Specification.ThirdPartType.WEIBO.index,Specification.ThirdPartType.WEIBO.name, 1));
        }
        String thirdPartBind = JSON.toJSONString(array);
        userProfile.setThirdPartBind(thirdPartBind);
        userMybatisDao.createUserProfile(userProfile);
        log.info("UserProfile is create");

        // 保存用户token信息
        UserToken userToken1 = new UserToken();
        userToken1.setUid(user.getUid());
        userToken1.setToken(SecurityUtils.getToken());
        userMybatisDao.createUserToken(userToken1);
        log.info("userToken is create");

        UserProfile userProfile1 = userMybatisDao.getUserProfileByUid(user1.getUid());
        UserToken userToken = userMybatisDao.getUserTokenByUid(user1.getUid());
        loginSuccessDto.setUid(userProfile1.getUid());
        loginSuccessDto.setNickName(userProfile1.getNickName());
        loginSuccessDto.setGender(userProfile1.getGender());
        loginSuccessDto.setToken(userToken.getToken());

        ThirdPartUser thirdPartUser = new ThirdPartUser();
        thirdPartUser.setUid(user1.getUid());
        thirdPartUser.setThirdPartToken(thirdPartSignUpDto.getThirdPartToken());
        thirdPartUser.setThirdPartOpenId(thirdPartSignUpDto.getThirdPartOpenId());
        thirdPartUser.setCreateTime(new Date());
        thirdPartUser.setThirdPartType(thirdPartSignUpDto.getThirdPartType());
        userMybatisDao.creatThirdPartUser(thirdPartUser);
        log.info("ThirdPartUser is create");

        if(!StringUtils.isEmpty(thirdPartSignUpDto.getJPushToken())) {
            List<JpushToken> jpushTokens = userMybatisDao.getJpushToken(user1.getUid());
            if(jpushTokens!=null&&jpushTokens.size()>0){
                // 更新当前JpushToken
                JpushToken jpushToken = jpushTokens.get(0);
                jpushToken.setJpushToken(thirdPartSignUpDto.getJPushToken());
                userMybatisDao.refreshJpushToken(jpushToken);
            }else {
                JpushToken jpushToken = new JpushToken();
                jpushToken.setJpushToken(thirdPartSignUpDto.getJPushToken());
                jpushToken.setUid(user1.getUid());
                userMybatisDao.createJpushToken(jpushToken);
            }
        }

    }

    //第三方绑定方法
    public void buildThirdPart2(ThirdPartSignUpDto thirdPartSignUpDto){

        ThirdPartUser thirdPartUser = new ThirdPartUser();
        thirdPartUser.setUid(thirdPartSignUpDto.getUid());
        thirdPartUser.setThirdPartToken(thirdPartSignUpDto.getThirdPartToken());
        thirdPartUser.setThirdPartOpenId(thirdPartSignUpDto.getThirdPartOpenId());
        thirdPartUser.setCreateTime(new Date());
        thirdPartUser.setThirdPartType(thirdPartSignUpDto.getThirdPartType());
        userMybatisDao.creatThirdPartUser(thirdPartUser);
        log.info("ThirdPartUser is create");

    }

    @Override
    public Response activityModel(ActivityModelDto activityModelDto) {
        String url = cacheService.get(AD_KEY);
        activityModelDto.setActivityUrl(url);
        return Response.success(ResponseStatus.GET_ACTIVITY_MODEL_SUCCESS.status,ResponseStatus.GET_ACTIVITY_MODEL_SUCCESS.message,activityModelDto);
    }

    @Override
    public Response checkNameOpenId(UserNickNameDto userNickNameDto) {
        if(!StringUtils.isEmpty(userNickNameDto.getOpenid())) {
            ThirdPartUser thirdPartUser = userMybatisDao.checkOpenId(userNickNameDto.getOpenid());
            if(thirdPartUser!=null) {
                return Response.success(ResponseStatus.USER_EXISTS.status,ResponseStatus.USER_EXISTS.message);
            }else{
                return  Response.success(ResponseStatus.OPENID_DONT_EXISTS.status,ResponseStatus.OPENID_DONT_EXISTS.message);
            }
        }else{
            String nickName = userNickNameDto.getNickName();
            List<UserProfile> list = userMybatisDao.checkUserNickName(nickName);
        if(list.size()>0){
            return Response.failure(ResponseStatus.USER_NICKNAME_EXISTS.status,ResponseStatus.USER_NICKNAME_EXISTS.message);
        }
        }
        return Response.success(ResponseStatus.USER_NICKNAME_DONT_EXISTS.status,ResponseStatus.USER_NICKNAME_DONT_EXISTS.message);
    }

    @Override
    public Response bind(ThirdPartSignUpDto thirdPartSignUpDto) {
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(thirdPartSignUpDto.getUid());
        String bindJsonData = userProfile.getThirdPartBind();
        List<UserAccountBindStatusDto> bindStatusDtoList = JSON.parseArray(bindJsonData,UserAccountBindStatusDto.class);
        if(!StringUtils.isEmpty(thirdPartSignUpDto.getMobile())){

            //判断手机号是否注册过了，注册过了不能绑定
            User mobile = userMybatisDao.getUserByUserName(thirdPartSignUpDto.getMobile());
            if(mobile!=null){
                return Response.failure(ResponseStatus.MOBILE_BIND_EXISTS.status,ResponseStatus.MOBILE_BIND_EXISTS.message);
            }

            User user = userMybatisDao.getUserByUid(thirdPartSignUpDto.getUid());
            String salt = SecurityUtils.getMask();
            user.setUserName(thirdPartSignUpDto.getMobile());
            user.setEncrypt(SecurityUtils.md5(thirdPartSignUpDto.getEncrypt(),salt));
            user.setSalt(salt);
            userMybatisDao.modifyUser(user);
            userProfile.setMobile(thirdPartSignUpDto.getMobile());
            // 手机绑定
            bindStatusDtoList.add(new UserAccountBindStatusDto(Specification.ThirdPartType.MOBILE.index,Specification.ThirdPartType.MOBILE.name,1));

        }else{
            //判断第三方账号是否存在
            ThirdPartUser thirdUser = userMybatisDao.thirdPartIsExist(thirdPartSignUpDto.getThirdPartOpenId() ,thirdPartSignUpDto.getThirdPartType());
            // 第三方账号绑定(qq,weixin,weibo)
            String thirdPartName = null;
            if(thirdPartSignUpDto.getThirdPartType()==Specification.ThirdPartType.QQ.index){
                if(thirdUser != null){
                    return Response.success(ResponseStatus.QQ_BIND_EXISTS.status,ResponseStatus.QQ_BIND_EXISTS.message);
                }
                buildThirdPart2(thirdPartSignUpDto);
                thirdPartName = Specification.ThirdPartType.QQ.name;
            }else if(thirdPartSignUpDto.getThirdPartType()==Specification.ThirdPartType.WEIXIN.index){
                if(thirdUser != null){
                    return Response.success(ResponseStatus.WEIXIN_BIND_EXISTS.status,ResponseStatus.WEIXIN_BIND_EXISTS.message);
                }
                buildThirdPart2(thirdPartSignUpDto);
                thirdPartName = Specification.ThirdPartType.WEIXIN.name;
            }else if(thirdPartSignUpDto.getThirdPartType()==Specification.ThirdPartType.WEIBO.index){
                if(thirdUser != null){
                    return Response.success(ResponseStatus.WEIBO_BIND_EXISTS.status,ResponseStatus.WEIBO_BIND_EXISTS.message);
                }
                buildThirdPart2(thirdPartSignUpDto);
                thirdPartName = Specification.ThirdPartType.WEIBO.name;
            }
            bindStatusDtoList.add(new UserAccountBindStatusDto(thirdPartSignUpDto.getThirdPartType(),thirdPartName,1));
        }
        String bindJson = JSON.toJSONString(bindStatusDtoList);
        userProfile.setThirdPartBind(bindJson);
        userMybatisDao.modifyUserProfile(userProfile);
        return Response.success();
    }

}
