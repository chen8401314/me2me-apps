package com.me2me.user.service;

import com.google.common.collect.Lists;
import com.me2me.common.Constant;
import com.me2me.common.security.SecurityUtils;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.Specification;
import com.me2me.sms.dto.*;
import com.me2me.sms.service.SmsService;
import com.me2me.sms.service.XgPushService;
import com.me2me.user.dao.OldUserJdbcDao;
import com.me2me.user.dao.UserInitJdbcDao;
import com.me2me.user.dao.UserMybatisDao;
import com.me2me.user.dto.*;
import com.me2me.user.model.*;
import com.me2me.user.model.Dictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
@Service
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


    /**
     * 用户注册
     * @param userSignUpDto
     * @return
     */
    public Response signUp(UserSignUpDto userSignUpDto) {
        // 校验手机号码是否注册
        String mobile = userSignUpDto.getMobile();
        if(userMybatisDao.getUserByUserName(mobile)!=null){
            // 该用户已经注册过
            return Response.failure(ResponseStatus.USER_MOBILE_DUPLICATE.status,ResponseStatus.USER_MOBILE_DUPLICATE.message);
        }
        // 检查用户名是否重复
        if(!this.existsNickName(userSignUpDto.getNickName())){
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
        UserProfile userProfile = new UserProfile();
        userProfile.setUid(user.getUid());
        userProfile.setAvatar(Constant.DEFAULT_AVATAR);
        userProfile.setMobile(userSignUpDto.getMobile());
        userProfile.setNickName(userSignUpDto.getNickName());
        userMybatisDao.createUserProfile(userProfile);
        signUpSuccessDto.setUserName(user.getUserName());
        // 获取用户token
        signUpSuccessDto.setToken(SecurityUtils.getToken());
        signUpSuccessDto.setUid(user.getUid());
        signUpSuccessDto.setNickName(userProfile.getNickName());
        // 设置userNo
        signUpSuccessDto.setMeNumber(userMybatisDao.getUserNoByUid(user.getUid()).getMeNumber().toString());
        signUpSuccessDto.setAvatar(userProfile.getAvatar());
        // 保存用户token信息
        UserToken userToken = new UserToken();
        userToken.setUid(user.getUid());
        userToken.setToken(signUpSuccessDto.getToken());
        userMybatisDao.createUserToken(userToken);
        signUpSuccessDto.setToken(userToken.getToken());
        //保存用户的设备token和用户平台信息
        UserDevice device = new UserDevice();
        device.setDeviceNo(userSignUpDto.getDeviceNo());
        device.setPlatform(userSignUpDto.getPlatform());
        device.setOs(userSignUpDto.getOs());
        device.setUid(user.getUid());
        userMybatisDao.updateUserDevice(device);
        // 获取默认值给前端
        UserProfile up = userMybatisDao.getUserProfileByUid(user.getUid());
        signUpSuccessDto.setGender(up.getGender());
        signUpSuccessDto.setYearId(up.getYearsId());
        return Response.success(ResponseStatus.USER_SING_UP_SUCCESS.status,ResponseStatus.USER_SING_UP_SUCCESS.message,signUpSuccessDto);
    }

    /**
     * 用户登录
     * @param userLoginDto
     * @return
     */
    public Response login(UserLoginDto userLoginDto) {
        //老用户转到新系统中来
        oldUserJdbcDao.moveOldUser2Apps(userLoginDto.getUserName(),userLoginDto.getEncrypt());
        User user = userMybatisDao.getUserByUserName(userLoginDto.getUserName());
        if(user != null){
            String salt = user.getSalt();
            if(SecurityUtils.md5(userLoginDto.getEncrypt(),salt).equals(user.getEncrypt())){
                // 则用户登录成功
                UserProfile userProfile = userMybatisDao.getUserProfileByUid(user.getUid());
                UserToken userToken = userMybatisDao.getUserTokenByUid(user.getUid());
                LoginSuccessDto loginSuccessDto = new LoginSuccessDto();
                loginSuccessDto.setUid(user.getUid());
                loginSuccessDto.setUserName(user.getUserName());
                loginSuccessDto.setNickName(userProfile.getNickName());
                loginSuccessDto.setGender(userProfile.getGender());
                loginSuccessDto.setMeNumber(userMybatisDao.getUserNoByUid(user.getUid()).getMeNumber().toString());
                loginSuccessDto.setAvatar(Constant.QINIU_DOMAIN  + "/" + userProfile.getAvatar());
                loginSuccessDto.setToken(userToken.getToken());
                loginSuccessDto.setYearId(userProfile.getYearsId());
                loginSuccessDto.setFansCount(userMybatisDao.getUserFansCount(user.getUid()));
                loginSuccessDto.setFollowedCount(userMybatisDao.getUserFollowCount(user.getUid()));
                //保存用户的设备token和用户平台信息
                UserDevice device = new UserDevice();
                device.setDeviceNo(userLoginDto.getDeviceNo());
                device.setPlatform(userLoginDto.getPlatform());
                device.setOs(userLoginDto.getOs());
                device.setUid(user.getUid());
                userMybatisDao.updateUserDevice(device);
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
            // 发送校验码
            User user = userMybatisDao.getUserByUserName(verifyDto.getMobile());
            if(user!=null){
                return Response.failure(ResponseStatus.USER_MOBILE_DUPLICATE.status,ResponseStatus.USER_MOBILE_DUPLICATE.message);
            }
            smsService.send(verifyDto);
            return Response.success(ResponseStatus.USER_VERIFY_GET_SUCCESS.status,ResponseStatus.USER_VERIFY_GET_SUCCESS.message);
        }else if(verifyDto.getAction() == Specification.VerifyAction.CHECK.index){
            // boolean result = YunXinSms.verify(verifyDto.getMobile(),verifyDto.getVerifyCode());
            // 验证校验码
            boolean result = smsService.verify(verifyDto);
            if(result) {
                return Response.success(ResponseStatus.USER_VERIFY_CHECK_SUCCESS.status, ResponseStatus.USER_VERIFY_CHECK_SUCCESS.message);
            }else{
                return Response.failure(ResponseStatus.USER_VERIFY_CHECK_ERROR.status,ResponseStatus.USER_VERIFY_CHECK_ERROR.message);
            }
        }else if(verifyDto.getAction() == Specification.VerifyAction.FIND_MY_ENCRYPT.index){
            // 找回密码
            // 判断用户是否已经注册过该手机
            oldUserJdbcDao.moveOldUser2Apps(verifyDto.getMobile(),"123456");
            User user = userMybatisDao.getUserByUserName(verifyDto.getMobile());
            if(user!=null){
                // applicationEventBus.post(new VerifyEvent(verifyDto.getMobile(),null));
                smsService.send(verifyDto);
                return Response.success(ResponseStatus.USER_VERIFY_GET_SUCCESS.status,ResponseStatus.USER_VERIFY_GET_SUCCESS.message);
            }else{
                return Response.failure(ResponseStatus.USER_NOT_EXISTS.status,ResponseStatus.USER_NOT_EXISTS.message);
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
        String mobile = modifyEncryptDto.getUserName();
        if(modifyEncryptDto.getOldEncrypt().equals(modifyEncryptDto.getFirstEncrypt())){
            return Response.failure(ResponseStatus.USER_MODIFY_ENCRYPT_THE_SAME_ERROR.status,ResponseStatus.USER_MODIFY_ENCRYPT_THE_SAME_ERROR.message);
        }
        if(userMybatisDao.getUserByUserName(mobile) == null){
            // 该用户已经注册过
            return Response.failure(ResponseStatus.USER_MOBILE_DUPLICATE.status,ResponseStatus.USER_MOBILE_DUPLICATE.message);
        }
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
     * 找回密码
     * @param findEncryptDto
     * @return
     */
    public Response retrieveEncrypt(FindEncryptDto findEncryptDto){

        if(!findEncryptDto.getFirstEncrypt().equals(findEncryptDto.getSecondEncrypt())) {
            return Response.failure(ResponseStatus.USER_FIND_ENCRYPT_PASSWORD_NOT_SAME_ERROR.status,ResponseStatus.USER_FIND_ENCRYPT_PASSWORD_NOT_SAME_ERROR.message);
        }else{
            User user = userMybatisDao.getUserByUserName(findEncryptDto.getUserName());
            if(user != null){
                user.setEncrypt(SecurityUtils.md5(findEncryptDto.getFirstEncrypt(),user.getSalt()));
                userMybatisDao.modifyUser(user);
                return Response.success(ResponseStatus.USER_FIND_ENCRYPT_SUCCESS.status, ResponseStatus.USER_FIND_ENCRYPT_SUCCESS.message);
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
        DictionaryType dictionaryType = userMybatisDao.getDictionaryType(basicDataDto);
        List<Dictionary> dictionaryList = userMybatisDao.getDictionary(basicDataDto);
        BasicDataSuccessDto basicDataSuccessDto = new BasicDataSuccessDto();
        BasicDataSuccessDto.BasicDataSuccessElement basicDataSuccess = BasicDataSuccessDto.createElement();
        basicDataSuccess.setTid(basicDataDto.getType());
        basicDataSuccess.setType(dictionaryType.getName());
        basicDataSuccess.setList(dictionaryList);
        basicDataSuccessDto.getResults().add(basicDataSuccess);
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
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(modifyUserProfileDto.getUid());
        if(modifyUserProfileDto.getNickName()!=null) {
            if (!this.existsNickName(modifyUserProfileDto.getNickName())) {
                return Response.failure(ResponseStatus.NICK_NAME_REQUIRE_UNIQUE.status, ResponseStatus.NICK_NAME_REQUIRE_UNIQUE.message);
            }
        }
        userProfile.setNickName(modifyUserProfileDto.getNickName());
        userProfile.setGender(modifyUserProfileDto.getGender());
        userProfile.setBirthday(modifyUserProfileDto.getBirthday());
        userProfile.setAvatar(modifyUserProfileDto.getAvatar());
        //修改用户爱好
        if(!StringUtils.isEmpty(modifyUserProfileDto.getHobby())){
            ModifyUserHobbyDto modifyUserHobbyDto = new ModifyUserHobbyDto();
            modifyUserHobbyDto.setUid(modifyUserProfileDto.getUid());
            modifyUserHobbyDto.setHobby(modifyUserProfileDto.getHobby());
            this.modifyUserHobby(modifyUserHobbyDto);
        }
        userMybatisDao.modifyUserProfile(userProfile);
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
        ShowUserNoticeDto showUserNoticeDto = new ShowUserNoticeDto();
        List<UserNotice> list = userMybatisDao.userNotice(userNoticeDto);
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
        if(followDto.getSourceUid() == followDto.getTargetUid()){
            return Response.failure(ResponseStatus.CAN_NOT_FOLLOW_YOURSELF.status,ResponseStatus.CAN_NOT_FOLLOW_YOURSELF.message);
        }
        // 判断目标对象是否存在
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(followDto.getTargetUid());
        if(userProfile==null){
            return Response.failure(ResponseStatus.USER_NOT_EXISTS.status,ResponseStatus.USER_NOT_EXISTS.message);
        }

        if(followDto.getSourceUid()==followDto.getTargetUid()){
            return Response.failure(ResponseStatus.ILLEGAL_REQUEST.status,ResponseStatus.ILLEGAL_REQUEST.message);
        }
        UserFollow userFollow = new UserFollow();
        userFollow.setSourceUid(followDto.getSourceUid());
        userFollow.setTargetUid(followDto.getTargetUid());
        // 判断是否已经关注过了
        if(followDto.getAction()==Specification.UserFollowAction.FOLLOW.index) {
            // 创建关注
            if(userMybatisDao.getUserFollow(followDto.getSourceUid(),followDto.getTargetUid()) != null){
                return Response.failure(ResponseStatus.CAN_NOT_DUPLICATE_FOLLOW.status,ResponseStatus.CAN_NOT_DUPLICATE_FOLLOW.message);
            }
            userMybatisDao.createFollow(userFollow);
            //关注提醒
            push(followDto.getTargetUid(),followDto.getSourceUid(),Specification.PushMessageType.FOLLOW.index,null);
            return Response.success(ResponseStatus.USER_FOLLOW_SUCCESS.status, ResponseStatus.USER_FOLLOW_SUCCESS.message);
        }else if(followDto.getAction()==Specification.UserFollowAction.UN_FOLLOW.index){
            // 取消关注
            UserFollow ufw = userMybatisDao.getUserFollow(followDto.getSourceUid(),followDto.getTargetUid());
            if(ufw!=null) {
                userMybatisDao.deleteFollow(ufw.getId());
            }
            return Response.success(ResponseStatus.USER_CANCEL_FOLLOW_SUCCESS.status, ResponseStatus.USER_CANCEL_FOLLOW_SUCCESS.message);
        }else{
            return Response.failure(ResponseStatus.ILLEGAL_REQUEST.status,ResponseStatus.ILLEGAL_REQUEST.message);
        }
    }

    @Override
    public Response getFans(FansParamsDto fansParamsDto) {
        List<UserFansDto> list = userMybatisDao.getFans(fansParamsDto);
        for(UserFansDto userFansDto : list){
            userFansDto.setAvatar(Constant.QINIU_DOMAIN + "/" + userFansDto.getAvatar());
            int followMe = this.isFollow(fansParamsDto.getUid(),userFansDto.getUid());
            userFansDto.setIsFollowMe(followMe);
            int followed = this.isFollow(userFansDto.getUid(),fansParamsDto.getUid());
            userFansDto.setIsFollowed(followed);
        }
        ShowUserFansDto showUserFansDto = new ShowUserFansDto();
        showUserFansDto.setResult(list);
        return Response.success(ResponseStatus.SHOW_USER_FANS_LIST_SUCCESS.status, ResponseStatus.SHOW_USER_FANS_LIST_SUCCESS.message,showUserFansDto);
    }

    @Override
    public Response getFollows(FollowParamsDto followParamsDto) {
        List<UserFollowDto> list = userMybatisDao.getFollows(followParamsDto);
        ShowUserFollowDto showUserFollowDto = new ShowUserFollowDto();
        for(UserFollowDto userFollowDto : list){
            userFollowDto.setAvatar(Constant.QINIU_DOMAIN + "/" + userFollowDto.getAvatar());
            int followMe = this.isFollow(followParamsDto.getUid(),userFollowDto.getUid());
            userFollowDto.setIsFollowMe(followMe);
            int followed = this.isFollow(userFollowDto.getUid(),followParamsDto.getUid());
            userFollowDto.setIsFollowed(followed);
        }
        showUserFollowDto.setResult(list);
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
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
        ShowUserProfileDto showUserProfileDto = new ShowUserProfileDto();
        showUserProfileDto.setUid(userProfile.getUid());
        showUserProfileDto.setNickName(userProfile.getNickName());
        showUserProfileDto.setAvatar(Constant.QINIU_DOMAIN + "/" +userProfile.getAvatar());
        showUserProfileDto.setBirthday(userProfile.getBirthday());
        showUserProfileDto.setGender(userProfile.getGender());
        showUserProfileDto.setUserName(userProfile.getMobile());
        UserToken userToken = userMybatisDao.getUserTokenByUid(uid);
        showUserProfileDto.setToken(userToken.getToken());
        showUserProfileDto.setMeNumber(userMybatisDao.getUserNoByUid(userProfile.getUid()).getMeNumber().toString());
        showUserProfileDto.setFollowedCount(userMybatisDao.getUserFollowCount(uid));
        showUserProfileDto.setFansCount(userMybatisDao.getUserFansCount(uid));
        List<UserHobby> list = userMybatisDao.getHobby(uid);
        for (UserHobby userHobby : list){
            ShowUserProfileDto.Hobby hobby = showUserProfileDto.createHobby();
            hobby.setHobby(userHobby.getHobby());
            Dictionary dictionary =  userMybatisDao.getDictionaryById(userHobby.getHobby());
            hobby.setValue(dictionary.getValue());
            showUserProfileDto.getHobbyList().add(hobby);
        }
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


    /**
     * 提醒
     * @param targetUid
     * @param sourceUid
     * @param type
     * @param title
     */
    @Override
    public void push(long targetUid ,long sourceUid ,int type,String title){
        if(targetUid == sourceUid){
            return;
        }
        UserProfile userProfile = getUserProfileByUid(sourceUid);
        UserDevice device = getUserDevice(targetUid);
        if(device != null) {
            PushMessageDto pushMessageDto = new PushMessageDto();
            pushMessageDto.setToken(device.getDeviceNo());
            pushMessageDto.setDevicePlatform(device.getPlatform());
            //直播贴标
            if (type == Specification.PushMessageType.LIVE_TAG.index) {
                pushMessageDto.setContent("你的直播:" + title + "收到了1个新感受");
                //日记被贴标
            } else if (type == Specification.PushMessageType.TAG.index) {
                pushMessageDto.setContent("你的日记:" + title + "收到了1个新感受");
                //直播回复
            } else if (type == Specification.PushMessageType.LIVE_REVIEW.index) {
                pushMessageDto.setContent(userProfile.getNickName() + "评论了你的直播:" + title);
                //日记被评论
            } else if (type == Specification.PushMessageType.REVIEW.index) {
                pushMessageDto.setContent(userProfile.getNickName() + "评论了你的日记:" + title);
                //直播置热
            } else if (type == Specification.PushMessageType.LIVE_HOTTEST.index) {
                pushMessageDto.setContent("你的直播：" + title + "上热点啦！");
                //UGC置热
            } else if (type == Specification.PushMessageType.HOTTEST.index) {
                pushMessageDto.setContent("你的日记：" + title + "上热点啦！");
                //被人关注
            } else if (type == Specification.PushMessageType.FOLLOW.index) {
                pushMessageDto.setContent(userProfile.getNickName() + "关注了你");
                //收藏的直播主播更新了
            } else if (type == Specification.PushMessageType.UPDATE.index) {
                pushMessageDto.setContent("你订阅的直播：" + title + "更新了");
                //你关注的直播有了新的更新了
            } else if (type == Specification.PushMessageType.LIVE.index) {
                pushMessageDto.setContent("你关注的主播" + userProfile.getNickName() + "有了新直播：" + title);
            }
            if (device.getPlatform() == 1) {
                PushMessageAndroidDto pushMessageAndroidDto = new PushMessageAndroidDto();
                pushMessageAndroidDto.setTitle(pushMessageDto.getContent());
                pushMessageAndroidDto.setToken(device.getDeviceNo());
                pushMessageAndroidDto.setMessageType(type);
                pushMessageAndroidDto.setTitle("米汤友情提示");
                pushMessageAndroidDto.setContent(pushMessageDto.getContent());
                PushLogDto pushLogDto = xgPushService.pushSingleDevice(pushMessageAndroidDto);
                if (pushLogDto != null) {
                    pushLogDto.setMeaageType(type);
                    userMybatisDao.createPushLog(pushLogDto);
                }
            } else {
                PushMessageIosDto pushMessageIosDto = new PushMessageIosDto();
                pushMessageIosDto.setTitle(pushMessageDto.getContent());
                pushMessageIosDto.setToken(device.getDeviceNo());
                pushMessageIosDto.setTitle("米汤友情提示");
                pushMessageIosDto.setContent(pushMessageDto.getContent());
                PushLogDto pushLogDto = xgPushService.pushSingleDeviceIOS(pushMessageIosDto);
                if (pushLogDto != null) {
                    pushLogDto.setMeaageType(type);
                    userMybatisDao.createPushLog(pushLogDto);
                }
            }
        }
    }

    @Override
    public List<UserFollow> getFans(long uid) {
        return userMybatisDao.getUserFollow(uid);
    }

    @Override
    public Response setUserExcellent(long uid) {
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
        userProfile.setExcellent(1);
        userMybatisDao.modifyUserProfile(userProfile);
        return Response.success(ResponseStatus.SET_USER_EXCELLENT_SUCCESS.status,ResponseStatus.SET_USER_EXCELLENT_SUCCESS.message);
    }


}
