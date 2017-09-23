package com.me2me.user.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.me2me.core.KeysManager;
import com.me2me.user.dto.*;
import com.me2me.user.model.*;
import com.me2me.user.model.Dictionary;
import com.me2me.user.rule.CoinRule;
import com.me2me.user.rule.Rules;

import lombok.Getter;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.me2me.cache.CacheConstant;
import com.me2me.cache.service.CacheService;
import com.me2me.common.Constant;
import com.me2me.common.page.PageBean;
import com.me2me.common.security.SecurityUtils;
import com.me2me.common.utils.CommonUtils;
import com.me2me.common.utils.JPushUtils;
import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.common.web.ResponseWapx;
import com.me2me.common.web.Specification;
import com.me2me.core.QRCodeUtil;
import com.me2me.core.event.ApplicationEventBus;
import com.me2me.io.service.FileTransferService;
import com.me2me.sms.dto.AwardXMDto;
import com.me2me.sms.dto.ImUserInfoDto;
import com.me2me.sms.dto.PushLogDto;
import com.me2me.sms.dto.PushMessageAndroidDto;
import com.me2me.sms.dto.PushMessageIosDto;
import com.me2me.sms.dto.VerifyDto;
import com.me2me.sms.service.JPushService;
import com.me2me.sms.service.SmsService;
import com.me2me.sms.service.XgPushService;
import com.me2me.user.cache.ContactsReddot;
import com.me2me.user.cache.EmotionSummaryModel;
import com.me2me.user.dao.ActivityJdbcDao;
import com.me2me.user.dao.LiveForUserJdbcDao;
import com.me2me.user.dao.OldUserJdbcDao;
import com.me2me.user.dao.UgcForUserJdbcDao;
import com.me2me.user.dao.UserInitJdbcDao;
import com.me2me.user.dao.UserMybatisDao;
import com.me2me.user.event.BatchFollowEvent;
import com.me2me.user.event.ContactsMobileEvent;
import com.me2me.user.event.ContactsMobilePushEvent;
import com.me2me.user.event.FollowEvent;
import com.me2me.user.event.NoticeCountPushEvent;
import com.me2me.user.event.NoticeMessagePushEvent;
import com.me2me.user.event.PushExtraEvent;
import com.me2me.user.event.WapxIosEvent;
import com.me2me.user.widget.MessageNotificationAdapter;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/26.
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Getter
    private Map<Integer,CoinRule> coinRules = Maps.newConcurrentMap();

    @PostConstruct
    public void init(){
        coinRules.put(Rules.SPEAK_KEY,new CoinRule(Rules.SPEAK_KEY,"发言",Integer.valueOf(getAppConfigByKey("SPEAK_KEY")),true));
        coinRules.put(Rules.PUBLISH_UGC_KEY,new CoinRule(Rules.PUBLISH_UGC_KEY,"发布UGC",Integer.valueOf(getAppConfigByKey("PUBLISH_UGC_KEY")),true));
        coinRules.put(Rules.REVIEW_UGC_KEY,new CoinRule(Rules.REVIEW_UGC_KEY,"回复UGC",Integer.valueOf(getAppConfigByKey("REVIEW_UGC_KEY")),true));
        coinRules.put(Rules.LIKES_UGC_KEY,new CoinRule(Rules.LIKES_UGC_KEY,"点赞UGC",Integer.valueOf(getAppConfigByKey("LIKES_UGC_KEY")),true));
        coinRules.put(Rules.FOLLOW_USER_KEY,new CoinRule(Rules.FOLLOW_USER_KEY,"关注一个新用户",Integer.valueOf(getAppConfigByKey("FOLLOW_USER_KEY")),true));
        coinRules.put(Rules.JOIN_KING_KEY,new CoinRule(Rules.JOIN_KING_KEY,"加入一个新王国",Integer.valueOf(getAppConfigByKey("JOIN_KING_KEY")),true));
        coinRules.put(Rules.SHARE_KING_KEY,new CoinRule(Rules.SHARE_KING_KEY,"对外分享王国/UGC",Integer.valueOf(getAppConfigByKey("SHARE_KING_KEY")),true));
        coinRules.put(Rules.CREATE_KING_KEY,new CoinRule(Rules.CREATE_KING_KEY,"建立王国/更新王国",Integer.valueOf(getAppConfigByKey("CREATE_KING_KEY")),false));
        coinRules.put(Rules.LOGIN_KEY,new CoinRule(Rules.LOGIN_KEY,"登录",Integer.valueOf(getAppConfigByKey("LOGIN_KEY")),false));
    }


    @Autowired
    private UserMybatisDao userMybatisDao;

    @Autowired
    private UserInitJdbcDao userInitJdbcDao;

    @Autowired
    private OldUserJdbcDao oldUserJdbcDao;

    @Autowired
    private LiveForUserJdbcDao liveForUserJdbcDao;

    @Autowired
    private UgcForUserJdbcDao ugcForUserJdbcDao;

    @Autowired
    private SmsService smsService;

    @Autowired
    private XgPushService xgPushService;

    @Autowired
   private FileTransferService fileTransferService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ActivityJdbcDao activityJdbcDao;


    @Value("#{app.reg_web}")
    private String reg_web;

    private static final String POWER_KEY = "power:key";

    private static final String AD_KEY = "ad:url:key";

    private static final String USER_PERMISSIONS = "USER_PERMISSIONS";

    private  static  final  String LEVEL_DEFINITION = "LEVEL_DEFINITION";



    @Autowired
    private JPushService jPushService;

    @Autowired
    private ApplicationEventBus applicationEventBus;

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

        // 第三方推广数据
        spreadChannelCount(userSignUpDto.getParams(),userSignUpDto.getSpreadChannel(), user);

        //IM新用户获得token
        try {
            ImUserInfoDto imUserInfoDto = smsService.getIMUsertoken(user.getUid(),userSignUpDto.getNickName(),"");
            if(imUserInfoDto != null){
                ImConfig imConfig = new ImConfig();
                imConfig.setUid(user.getUid());
                imConfig.setToken(imUserInfoDto.getToken());
                userMybatisDao.createImConfig(imConfig);
                log.info("create IM Config success");
            }
        } catch (Exception e) {
            log.error("get im token failure", e);
        }

        Date now = new Date();
        log.info("user is create");
        UserProfile userProfile = new UserProfile();
        userProfile.setUid(user.getUid());
        userProfile.setAvatar(Constant.DEFAULT_AVATAR);
        userProfile.setMobile(userSignUpDto.getMobile());
        userProfile.setNickName(userSignUpDto.getNickName());
        userProfile.setIntroduced(userSignUpDto.getIntroduced());
        //性别默认给-1
        userProfile.setGender(-1);
        //生日默认给一个不可能的值
        userProfile.setBirthday("1800-1-1");
        userProfile.setCreateTime(now);
        userProfile.setUpdateTime(now);
        userProfile.setChannel(userSignUpDto.getChannel());
        userProfile.setPlatform(userSignUpDto.getPlatform());
        userProfile.setRegisterVersion(userSignUpDto.getRegisterVersion());
        //判断是否有推广员
        this.processReferee(userSignUpDto.getOpeninstallData(), user.getUid(), userProfile);

        List<UserAccountBindStatusDto> array = Lists.newArrayList();
        // 添加手机绑定
        array.add(new UserAccountBindStatusDto(Specification.ThirdPartType.MOBILE.index,Specification.ThirdPartType.MOBILE.name,1));
        String mobileBind = JSON.toJSONString(array);
        userProfile.setThirdPartBind(mobileBind);

        userMybatisDao.createUserProfile(userProfile);
        log.info("userProfile is create");
        //添加默认关注v2.1.4
        defaultFollow(user.getUid());

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
        
        //添加设备相关信息new
        this.saveUserDeviceInfo(user.getUid(), userSignUpDto.getIp(), 1, userSignUpDto.getDeviceData());
        
        log.info("userDevice is create");
        // 获取默认值给前端
        UserProfile up = userMybatisDao.getUserProfileByUid(user.getUid());
        signUpSuccessDto.setGender(up.getGender());
        signUpSuccessDto.setYearId(up.getYearsId());
        log.info("signUp end ...");
        
        //检测手机通讯录推送V2.2.5
        ContactsMobilePushEvent event = new ContactsMobilePushEvent();
        event.setUid(user.getUid());
        event.setMobile(userSignUpDto.getMobile());
        this.applicationEventBus.post(event);
        //不管你爽不爽，就是要卖你3个王国
        give3Kingdoms(userProfile);
        //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.REGISTER.index,0,user.getUid()));

        //新用户注册放入cach,机器人自动回复用
        String key = KeysManager.SEVEN_DAY_REGISTER_PREFIX+signUpSuccessDto.getUid();
        cacheService.setex(key,signUpSuccessDto.getUid()+"",7*24*60*60);
        return Response.success(ResponseStatus.USER_SING_UP_SUCCESS.status,ResponseStatus.USER_SING_UP_SUCCESS.message,signUpSuccessDto);
    }
    
    private void processReferee(String openinstallData, long currentUid, UserProfile userProfile){
    	if(StringUtils.isEmpty(openinstallData)){
    		return;
    	}
    	try {
			long refereeUid = 0;
			long fromTopicId = 0;
			if(openinstallData.contains("=")){//IOS的坑，尽然不是json，这里给其擦屁股
				Map<String, String> paramMap = new HashMap<String, String>();
				openinstallData = CommonUtils.replaceBlank(openinstallData);//去除空格、制表符、回车、换行
				openinstallData = openinstallData.substring(1, openinstallData.length()-1).trim();//去除{}
				String[] arr = openinstallData.split(",");
				if(null != arr && arr.length > 0){
					for(String param : arr){
						if(!StringUtils.isEmpty(param)){
							String[] pArr = param.split("=");
							if(null != pArr && pArr.length > 1 && !StringUtils.isEmpty(pArr[0]) 
									&& !StringUtils.isEmpty(pArr[1])){
								paramMap.put(pArr[0], pArr[1]);
							}
						}
					}
				}
				if(null != paramMap.get("uid")){
					refereeUid = Long.valueOf(paramMap.get("uid"));
				}
				if(null != paramMap.get("topicId")){
					fromTopicId = Long.valueOf(paramMap.get("topicId"));
				}
			}else{
				JSONObject openinstallDataJson = JSONObject.parseObject(openinstallData);
				if(null != openinstallDataJson.get("uid")){
					refereeUid = openinstallDataJson.getLongValue("uid");
				}
				if(null != openinstallDataJson.get("topicId")){
					fromTopicId = openinstallDataJson.getLongValue("topicId");
				}
			}
			
			if (refereeUid > 0) {
				// 判断推广员是否存在
				UserProfile refereeUserProfile = userMybatisDao.getUserProfileByUid(refereeUid);
				if (refereeUserProfile != null) {
					userProfile.setRefereeUid(refereeUid);
					// 判断是否已经关注过了
					if (userMybatisDao.getUserFollow(currentUid, refereeUid) == null) {
						// 创建关注
						UserFollow userFollow = new UserFollow();
						userFollow.setSourceUid(currentUid);
						userFollow.setTargetUid(refereeUid);
						userMybatisDao.createFollow(userFollow);
						applicationEventBus.post(new FollowEvent(currentUid, refereeUid));
					}
					StringBuffer msg = new StringBuffer();
					msg.append("你的好友").append(userProfile.getNickName()).append("已经入驻啦！赶快打开米汤一起畅聊吧！");		
				    JsonObject jsonObject = new JsonObject();
		            jsonObject.addProperty("messageType", 0);
		            jsonObject.addProperty("type", -1);
		            pushWithExtra(refereeUid+"", msg.toString(), JPushUtils.packageExtra(jsonObject));
		            
		            //保存邀请历史
		            int invitingCoins = 0;//邀请的奖励
		            int invitedCoins = 0;//被邀请的奖励
		            try{
		            	String invitingCoinsStr = this.getAppConfigByKey("INVITING_COINS");
		            	if(!StringUtils.isEmpty(invitingCoinsStr)){
		            		invitingCoins = Integer.valueOf(invitingCoinsStr);
		            		if(invitingCoins<0){
		            			invitingCoins = 0;
		            		}
		            	}
		            	String invitedCoinsStr = this.getAppConfigByKey("INVITED_COINS");
		            	if(!StringUtils.isEmpty(invitedCoinsStr)){
		            		invitedCoins = Integer.valueOf(invitedCoinsStr);
		            		if(invitedCoins<0){
		            			invitedCoins = 0;
		            		}
		            	}
		            }catch(Exception e){
		            	log.error("邀请或被邀请的配置错误", e);
		            }
		            //保存邀请奖励
		            Date now = new Date();
		            UserInvitationHis invitingHis = new UserInvitationHis();
		            invitingHis.setCoins(invitingCoins);
		            invitingHis.setCreateTime(now);
		            invitingHis.setFromCid(fromTopicId);
		            invitingHis.setFromUid(currentUid);
		            invitingHis.setStatus(0);
		            invitingHis.setType(Specification.UserInvitationType.INVITING.index);
		            invitingHis.setUid(refereeUid);
		            userMybatisDao.saveUserInvitationHis(invitingHis);
		            
		            //保存被邀请奖励
		            UserInvitationHis invitedHis = new UserInvitationHis();
		            invitedHis.setCoins(invitedCoins);
		            invitedHis.setCreateTime(now);
		            invitedHis.setFromCid(fromTopicId);
		            invitedHis.setFromUid(refereeUid);
		            invitedHis.setStatus(0);
		            invitedHis.setType(Specification.UserInvitationType.INVITED.index);
		            invitedHis.setUid(currentUid);
		            userMybatisDao.saveUserInvitationHis(invitedHis);
				}
			}
		} catch (Exception e) {
			log.error("openinstallData json failure", e);
		}
    }

    @Override
    public Response signUpByVerify(UserSignUpDto userSignUpDto) {
        VerifyDto verifyDto = new VerifyDto();
        verifyDto.setAction(1);
        verifyDto.setChannel(1);
        verifyDto.setVerifyCode(userSignUpDto.getVerifyCode());
        verifyDto.setMobile(userSignUpDto.getMobile());
        boolean result = smsService.verify(verifyDto);
        if(result){
        log.info("signUp start ...");
        // 校验手机号码是否注册
        String mobile = userSignUpDto.getMobile();
        log.info("mobile:" + mobile );
        if(userMybatisDao.getUserByUserName(mobile) != null){
            // 该用户已经注册过
            log.info("mobile:" + mobile + " is already register");
            return Response.failure(ResponseStatus.USER_MOBILE_DUPLICATE.status,ResponseStatus.USER_MOBILE_DUPLICATE.message);
        }
        log.info("user not exists");
        //用户不存在
        SignUpSuccessDto signUpSuccessDto = new SignUpSuccessDto();
        User newUser = new User();
        String salt = SecurityUtils.getMask();
        //验证码注册,密码给默认值0
        newUser.setEncrypt("0");
        newUser.setSalt(salt);
        newUser.setCreateTime(new Date());
        newUser.setUpdateTime(new Date());
        newUser.setStatus(Specification.UserStatus.NORMAL.index);
        newUser.setUserName(userSignUpDto.getMobile());
        userMybatisDao.createUser(newUser);

        // 第三方推广数据
        // spreadChannelCount(userLoginDto.getParams(),userLoginDto.getSpreadChannel(), newUser);

        //IM新用户获得token
        try {
            ImUserInfoDto imUserInfoDto = smsService.getIMUsertoken(newUser.getUid(),"用户"+signUpSuccessDto.getMeNumber(),"");
            if(imUserInfoDto != null){
                ImConfig imConfig = new ImConfig();
                imConfig.setUid(newUser.getUid());
                if (ObjectUtils.isEmpty(imConfig.getToken())){
                    imConfig.setToken("测试环境的token满了.");
                }else {
                    imConfig.setToken(imUserInfoDto.getToken());
                }
                userMybatisDao.createImConfig(imConfig);
                log.info("create IM Config success");
            }
        } catch (Exception e) {
            log.error("get im token failure", e);
        }

        // 设置userNo
        signUpSuccessDto.setMeNumber(userMybatisDao.getUserNoByUid(newUser.getUid()).getMeNumber().toString());
        log.info("user is create");
        UserProfile userProfile = new UserProfile();
        userProfile.setUid(newUser.getUid());
        userProfile.setAvatar(Constant.DEFAULT_AVATAR);
        userProfile.setMobile(userSignUpDto.getMobile());
        userProfile.setNickName("用户"+signUpSuccessDto.getMeNumber());
        //介绍默认没有
        userProfile.setIntroduced("");
        //性别默认给-1
        userProfile.setGender(-1);
        //生日默认给一个不可能的值
        userProfile.setBirthday("1800-1-1");
        userProfile.setCreateTime(new Date());
        userProfile.setUpdateTime(new Date());
        userProfile.setPlatform(userSignUpDto.getPlatform());
        userProfile.setChannel(userSignUpDto.getChannel());
        userProfile.setRegisterVersion(userSignUpDto.getRegisterVersion());
        List<UserAccountBindStatusDto> array = Lists.newArrayList();
        // 添加手机绑定
        array.add(new UserAccountBindStatusDto(Specification.ThirdPartType.MOBILE.index,Specification.ThirdPartType.MOBILE.name,1));
        String mobileBind = JSON.toJSONString(array);
        userProfile.setThirdPartBind(mobileBind);
        //判断是否有推广员
        this.processReferee(userSignUpDto.getOpeninstallData(), newUser.getUid(), userProfile);
        
        userMybatisDao.createUserProfile(userProfile);
        log.info("userProfile is create");
        //添加默认关注v2.1.4
        defaultFollow(newUser.getUid());

        signUpSuccessDto.setUserName(newUser.getUserName());
        // 获取用户token
        signUpSuccessDto.setToken(SecurityUtils.getToken());
        signUpSuccessDto.setUid(newUser.getUid());
        signUpSuccessDto.setNickName(userProfile.getNickName());
        signUpSuccessDto.setAvatar(userProfile.getAvatar());
        signUpSuccessDto.setIntroduced(userProfile.getIntroduced());

        // 保存用户token信息
        UserToken userToken = new UserToken();
        userToken.setUid(newUser.getUid());
        userToken.setToken(signUpSuccessDto.getToken());
        userMybatisDao.createUserToken(userToken);
        log.info("userToken is create");
        signUpSuccessDto.setToken(userToken.getToken());

        //保存用户的设备token和用户平台信息
        UserDevice device = new UserDevice();
        device.setDeviceNo(userSignUpDto.getDeviceNo());
        device.setPlatform(userSignUpDto.getPlatform());
        device.setOs(userSignUpDto.getOs());
        device.setUid(newUser.getUid());
        userMybatisDao.updateUserDevice(device);
        
        //添加设备相关信息new
        this.saveUserDeviceInfo(newUser.getUid(), userSignUpDto.getIp(), 1, userSignUpDto.getDeviceData());
        
        log.info("userDevice is create");
        // 获取默认值给前端
        UserProfile up = userMybatisDao.getUserProfileByUid(newUser.getUid());
        signUpSuccessDto.setGender(up.getGender());
        signUpSuccessDto.setYearId(up.getYearsId());
        log.info("signUp end ...");

        //检测手机通讯录推送V2.2.5
        ContactsMobilePushEvent event = new ContactsMobilePushEvent();
        event.setUid(newUser.getUid());
        event.setMobile(userSignUpDto.getMobile());
        this.applicationEventBus.post(event);

        //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.REGISTER.index,0,user.getUid()));

        //新用户注册放入cach,机器人自动回复用
        String key = KeysManager.SEVEN_DAY_REGISTER_PREFIX+signUpSuccessDto.getUid();
        cacheService.setex(key,signUpSuccessDto.getUid()+"",7*24*60*60);
        //不管你爽不爽，就是要卖你3个王国
        give3Kingdoms(userProfile);
        
        return Response.success(ResponseStatus.USER_SING_UP_SUCCESS.status,ResponseStatus.USER_SING_UP_SUCCESS.message,signUpSuccessDto);
        }else{
            log.info("user verify check error");
            return Response.failure(ResponseStatus.USER_VERIFY_CHECK_ERROR.status,ResponseStatus.USER_VERIFY_CHECK_ERROR.message);
        }
    }

    private void spreadChannelCount(String params,int spreadChannel, User user) {
        if(!StringUtils.isEmpty(params)) {
            WapxParams wapxParams = JSON.parseObject(params, WapxParams.class);
            WapxIosEvent event = new WapxIosEvent(wapxParams.getIdfa(),user.getUid(),spreadChannel);
            applicationEventBus.post(event);
        }
    }

    private void defaultFollow(long uid){
        SystemConfig config = userMybatisDao.getSystemConfig();
        if(null == config){
        	return;
        }
        
        if(!StringUtils.isEmpty(config.getDefaultFollow())){
            String[] arrayDefaultFollow = config.getDefaultFollow().split(",");
            for(String defaultFollow:arrayDefaultFollow) {
                long tid = Long.parseLong(defaultFollow);
                UserFollow userFollow = new UserFollow();
                userFollow.setSourceUid(uid);
                userFollow.setTargetUid(tid);

                userMybatisDao.createFollow(userFollow);

                //不需要关注他的王国..modify by zcl 20170216 version 2.2.0
                //关注所有王国
//                List<Map<String,Object>> topics = liveForUserJdbcDao.getTopicByUid(tid);
//                for(Map<String,Object> topic : topics){
//                    liveForUserJdbcDao.favoriteTopic((Long)topic.get("id"),uid);
//                }

                //加入圈外人
//                liveForUserJdbcDao.addToSnsCircle(tid,uid);
            }
        }
        if(!StringUtils.isEmpty(config.getDefaultSubscribe())){//默认订阅王国
        	String[] topicIds = config.getDefaultSubscribe().split(",");
        	for(String topicId : topicIds){
        		if(!StringUtils.isEmpty(topicId)){
        			long tid = Long.valueOf(topicId);
        			liveForUserJdbcDao.favoriteTopic(tid, uid);
        		}
        	}
        }
    }
    
    /**
     * 校验密码
     * @param pwd
     * @param salt
     * @param encrypt
     * @return
     */
    private boolean checkPwd(String pwd, String salt, String encrypt){
    	if(SecurityUtils.md5(pwd,salt).equals(encrypt)){
    		return true;
    	}
    	if(pwd.length() > 12){
    		pwd = pwd.substring(0, 12);
    		if(SecurityUtils.md5(pwd,salt).equals(encrypt)){
        		return true;
        	}
    	}
    	
    	return false;
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
//            if(SecurityUtils.md5(userLoginDto.getEncrypt(),salt).equals(user.getEncrypt())){
            if(this.checkPwd(userLoginDto.getEncrypt(), salt, user.getEncrypt())){
                //如果status为1则已被禁用
                if(user.getDisableUser() == 1){
                    return Response.failure(ResponseStatus.USER_ACCOUNT_DISABLED.status,ResponseStatus.USER_ACCOUNT_DISABLED.message);
                }
                // 则用户登录成功
                UserProfile userProfile = userMybatisDao.getUserProfileByUid(user.getUid());
                log.info("get userProfile success");
                //判断用户是否是未激活状态
                if(userProfile.getIsActivate() == Specification.UserActivate.UN_ACTIVATED.index){
                    userProfile.setIsActivate(Specification.UserActivate.ACTIVATED.index);
                    List<UserAccountBindStatusDto> array = Lists.newArrayList();
                    // 添加手机绑定
                    array.add(new UserAccountBindStatusDto(Specification.ThirdPartType.MOBILE.index,Specification.ThirdPartType.MOBILE.name,1));
                    String mobileBind = JSON.toJSONString(array);
                    userProfile.setThirdPartBind(mobileBind);
                    userProfile.setChannel(userLoginDto.getChannel());
                    userProfile.setPlatform(userLoginDto.getPlatform());
                    userProfile.setRegisterVersion(userLoginDto.getRegisterVersion());
                    userMybatisDao.modifyUserProfile(userProfile);
                }
                UserToken userToken = userMybatisDao.getUserTokenByUid(user.getUid());
                log.info("get userToken success");
                LoginSuccessDto loginSuccessDto = new LoginSuccessDto();
                loginSuccessDto.setUid(user.getUid());
                UserProfile profile = userMybatisDao.getUserProfileByUid(user.getUid());
                loginSuccessDto.setV_lv(profile.getvLv());
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
                loginSuccessDto.setLevel(userProfile.getLevel());
                //保存用户的设备token和用户平台信息
                UserDevice device = new UserDevice();
                device.setDeviceNo(userLoginDto.getDeviceNo());
                device.setPlatform(userLoginDto.getPlatform());
                device.setOs(userLoginDto.getOs());
                device.setUid(user.getUid());
                userMybatisDao.updateUserDevice(device);
                
                //保存登录设备信息new
                this.saveUserDeviceInfo(userProfile.getUid(), userLoginDto.getIp(), 2, userLoginDto.getDeviceData());
                
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

    @Override
    public Response loginByVerify(UserLoginDto userLoginDto) {
        // 验证校验码
        VerifyDto verifyDto = new VerifyDto();
        verifyDto.setAction(1);
        verifyDto.setChannel(1);
        verifyDto.setVerifyCode(userLoginDto.getVerifyCode());
        verifyDto.setMobile(userLoginDto.getUserName());
        boolean result = smsService.verify(verifyDto);
        if(result) {
            User user = userMybatisDao.getUserByUserName(userLoginDto.getUserName());
            if(user != null){
                //如果status为1则已被禁用
                if(user.getDisableUser() == 1){
                    return Response.failure(ResponseStatus.USER_ACCOUNT_DISABLED.status,ResponseStatus.USER_ACCOUNT_DISABLED.message);
                }
                // 则用户登录成功
                UserProfile userProfile = userMybatisDao.getUserProfileByUid(user.getUid());
                log.info("get userProfile success");
                //判断用户是否是未激活状态
                if(userProfile.getIsActivate() == Specification.UserActivate.UN_ACTIVATED.index){
                    userProfile.setIsActivate(Specification.UserActivate.ACTIVATED.index);
                    List<UserAccountBindStatusDto> array = Lists.newArrayList();
                    // 添加手机绑定
                    array.add(new UserAccountBindStatusDto(Specification.ThirdPartType.MOBILE.index,Specification.ThirdPartType.MOBILE.name,1));
                    String mobileBind = JSON.toJSONString(array);
                    userProfile.setThirdPartBind(mobileBind);
                    userProfile.setChannel(userLoginDto.getChannel());
                    userProfile.setPlatform(userLoginDto.getPlatform());
                    userProfile.setRegisterVersion(userLoginDto.getRegisterVersion());
                    userMybatisDao.modifyUserProfile(userProfile);
                }
                UserToken userToken = userMybatisDao.getUserTokenByUid(user.getUid());
                log.info("get userToken success");
                LoginSuccessDto loginSuccessDto = new LoginSuccessDto();
                loginSuccessDto.setUid(user.getUid());
                UserProfile profile = userMybatisDao.getUserProfileByUid(user.getUid());
                loginSuccessDto.setV_lv(profile.getvLv());
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
                loginSuccessDto.setLevel(userProfile.getLevel());
                //保存用户的设备token和用户平台信息
                UserDevice device = new UserDevice();
                device.setDeviceNo(userLoginDto.getDeviceNo());
                device.setPlatform(userLoginDto.getPlatform());
                device.setOs(userLoginDto.getOs());
                device.setUid(user.getUid());
                userMybatisDao.updateUserDevice(device);
                
                //保存登录设备信息new
                this.saveUserDeviceInfo(userProfile.getUid(), userLoginDto.getIp(), 2, userLoginDto.getDeviceData());
                
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
            }else {
          /*  log.info("user not exists");
            //用户不存在
            SignUpSuccessDto signUpSuccessDto = new SignUpSuccessDto();
            User newUser = new User();
            String salt = SecurityUtils.getMask();
            //验证码注册,密码给默认值0
            newUser.setEncrypt("0");
            newUser.setSalt(salt);
            newUser.setCreateTime(new Date());
            newUser.setUpdateTime(new Date());
            newUser.setStatus(Specification.UserStatus.NORMAL.index);
            newUser.setUserName(userLoginDto.getUserName());
            userMybatisDao.createUser(newUser);

            // 第三方推广数据
           // spreadChannelCount(userLoginDto.getParams(),userLoginDto.getSpreadChannel(), newUser);

            //IM新用户获得token
            try {
                ImUserInfoDto imUserInfoDto = smsService.getIMUsertoken(newUser.getUid());
                if(imUserInfoDto != null){
                    ImConfig imConfig = new ImConfig();
                    imConfig.setUid(newUser.getUid());
                    imConfig.setToken(imUserInfoDto.getToken());
                    userMybatisDao.createImConfig(imConfig);
                    log.info("create IM Config success");
                }
            } catch (Exception e) {
                log.error("get im token failure", e);
            }

            // 设置userNo
            signUpSuccessDto.setMeNumber(userMybatisDao.getUserNoByUid(newUser.getUid()).getMeNumber().toString());
            log.info("user is create");
            UserProfile userProfile = new UserProfile();
            userProfile.setUid(newUser.getUid());
            userProfile.setAvatar(Constant.DEFAULT_AVATAR);
            userProfile.setMobile(userLoginDto.getUserName());
            userProfile.setNickName(userLoginDto.getUserName()+signUpSuccessDto.getMeNumber());
            //介绍默认没有
            userProfile.setIntroduced("");
            //性别默认给-1
            userProfile.setGender(-1);
            //生日默认给一个不可能的值
            userProfile.setBirthday("1800-1-1");
            userProfile.setCreateTime(new Date());
            userProfile.setUpdateTime(new Date());
            userProfile.setPlatform(userLoginDto.getPlatform());
            List<UserAccountBindStatusDto> array = Lists.newArrayList();
            // 添加手机绑定
            array.add(new UserAccountBindStatusDto(Specification.ThirdPartType.MOBILE.index,Specification.ThirdPartType.MOBILE.name,1));
            String mobileBind = JSON.toJSONString(array);
            userProfile.setThirdPartBind(mobileBind);

            userMybatisDao.createUserProfile(userProfile);
            log.info("userProfile is create");
            //添加默认关注v2.1.4
            defaultFollow(newUser.getUid());

            signUpSuccessDto.setUserName(newUser.getUserName());
            // 获取用户token
            signUpSuccessDto.setToken(SecurityUtils.getToken());
            signUpSuccessDto.setUid(newUser.getUid());
            signUpSuccessDto.setNickName(userProfile.getNickName());
            signUpSuccessDto.setAvatar(userProfile.getAvatar());
            signUpSuccessDto.setIntroduced(userProfile.getIntroduced());

            // 保存用户token信息
            UserToken userToken = new UserToken();
            userToken.setUid(newUser.getUid());
            userToken.setToken(signUpSuccessDto.getToken());
            userMybatisDao.createUserToken(userToken);
            log.info("userToken is create");
            signUpSuccessDto.setToken(userToken.getToken());

            //保存用户的设备token和用户平台信息
            UserDevice device = new UserDevice();
            device.setDeviceNo(userLoginDto.getDeviceNo());
            device.setPlatform(userLoginDto.getPlatform());
            device.setOs(userLoginDto.getOs());
            device.setUid(newUser.getUid());
            userMybatisDao.updateUserDevice(device);
            log.info("userDevice is create");
            // 获取默认值给前端
            UserProfile up = userMybatisDao.getUserProfileByUid(newUser.getUid());
            signUpSuccessDto.setGender(up.getGender());
            signUpSuccessDto.setYearId(up.getYearsId());
            log.info("signUp end ...");

            //检测手机通讯录推送V2.2.5
            ContactsMobilePushEvent event = new ContactsMobilePushEvent();
            event.setUid(newUser.getUid());
            event.setMobile(userLoginDto.getUserName());
            this.applicationEventBus.post(event);

            //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.REGISTER.index,0,user.getUid()));

            //新用户注册放入cach,机器人自动回复用
            String key = KeysManager.SEVEN_DAY_REGISTER_PREFIX+signUpSuccessDto.getUid();
            cacheService.setex(key,signUpSuccessDto.getUid()+"",7*24*60*60);
            return Response.success(ResponseStatus.USER_SING_UP_SUCCESS.status,ResponseStatus.USER_SING_UP_SUCCESS.message,signUpSuccessDto);*/
                return Response.failure("该用户还没有注册,请完成注册");
            }
        }else{
            log.info("user verify check error");
            return Response.failure(ResponseStatus.USER_VERIFY_CHECK_ERROR.status,ResponseStatus.USER_VERIFY_CHECK_ERROR.message);
        }
    }

    /**
     * 发送校验验证码
     * @param verifyDto
     * @return
     */
    public Response verify(VerifyDto verifyDto) {
        log.info("verify start ...");
        //验证码登录获取验证码
        if(verifyDto.getAction() == Specification.VerifyAction.LOGIN.index){
            User user = userMybatisDao.getUserByUserName(verifyDto.getMobile());
            if(user == null){
                log.info("user mobile duplicate");
                return Response.failure(ResponseStatus.USER_MOBILE_NO_SIGN_UP.status,ResponseStatus.USER_MOBILE_NO_SIGN_UP.message);
            }else {
                return smsService.send(verifyDto);}
        }


        if(verifyDto.getAction() == Specification.VerifyAction.SIGNUP.index){
            User user = userMybatisDao.getUserByUserName(verifyDto.getMobile());
            if(user != null){
                log.info("user mobile duplicate");
                return Response.failure(ResponseStatus.USER_MOBILE_DUPLICATE.status,ResponseStatus.USER_MOBILE_DUPLICATE.message);
            }else {
                return smsService.send(verifyDto);}
        }

        if(verifyDto.getAction() == Specification.VerifyAction.GET.index){
            // 发送校验码
            User user = userMybatisDao.getUserByUserName(verifyDto.getMobile());
            if(user!=null){
                log.info("user mobile duplicate");
                return Response.failure(ResponseStatus.USER_MOBILE_DUPLICATE.status,ResponseStatus.USER_MOBILE_DUPLICATE.message);
            }
            return smsService.send(verifyDto);
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
            	return smsService.send(verifyDto);
            }else{
                log.info("user not exists");
                return Response.failure(ResponseStatus.USER_NOT_EXISTS.status,ResponseStatus.USER_NOT_EXISTS.message);
            }
        }else if(verifyDto.getAction() == Specification.VerifyAction.SEND_MESSAGE.index){
        	//纯发短信
        	return smsService.send(verifyDto);
        }
        log.info("user verify times over");
        return Response.failure(ResponseStatus.USER_VERIFY_ERROR.status,ResponseStatus.USER_VERIFY_ERROR.message);
    }

    @Override
    public Response sendAwardMessage(AwardXMDto awardXMDto) {
        //运维人员手机号
        List<Map<String, Object>> mapList = userInitJdbcDao.getLuckStatusOperateMobile();
        Map<String, Object> map = mapList.get(0);
        String OperateMobile = (String) map.get("operate_mobile");
        boolean isTrue = smsService.sendMessage(awardXMDto.getNickName(),awardXMDto.getAwardName(),awardXMDto.getMobile() ,OperateMobile);
        if(isTrue){
            log.info("award message success");
            return Response.success(ResponseStatus.AWARD_MESSAGE_SUCCESS.status,ResponseStatus.AWARD_MESSAGE_SUCCESS.message);
        }else{
            log.info("award message failure");
            return Response.success(ResponseStatus.AWARD_MESSAGE_FAILURE.status,ResponseStatus.AWARD_MESSAGE_FAILURE.message);
        }
    }

    @Override
    public Response sendQIMessage(AwardXMDto awardXMDto) {
        boolean isTrue = smsService.sendQIMessage(awardXMDto.getMobile());
        if(isTrue){
            log.info("qi message success");
            return Response.success(ResponseStatus.AWARD_MESSAGE_SUCCESS.status,ResponseStatus.AWARD_MESSAGE_SUCCESS.message);
        }else{
            log.info("qi message failure");
            return Response.success(ResponseStatus.AWARD_MESSAGE_FAILURE.status,ResponseStatus.AWARD_MESSAGE_FAILURE.message);
        }
    }

    @Override
    public Response sendQIauditMessage(AwardXMDto awardXMDto) {
        boolean isTrue = smsService.sendQIauditMessage(awardXMDto.getMobileList());
        if(isTrue){
            log.info("qi message success");
            return Response.success(ResponseStatus.AWARD_MESSAGE_SUCCESS.status,ResponseStatus.AWARD_MESSAGE_SUCCESS.message);
        }else{
            log.info("qi message failure");
            return Response.success(ResponseStatus.AWARD_MESSAGE_FAILURE.status,ResponseStatus.AWARD_MESSAGE_FAILURE.message);
        }
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

    @Override
    public Response setEncrypt(SetEncryptDto setEncryptDto) {
      //通过用户名获取用户信息
            User user = userMybatisDao.getUserByUserName(setEncryptDto.getUserName());
            if(user != null ){
                    user.setEncrypt(SecurityUtils.md5(setEncryptDto.getEncrypt(),user.getSalt()));
                    userMybatisDao.modifyUser(user);
                    log.info("user modifyEncrypt success");
                    log.info("modifyEncrypt end ...");
                    return Response.success(ResponseStatus.USER_MODIFY_ENCRYPT_SUCCESS.status, ResponseStatus.USER_SET_ENCRYPT_SUCCESS.message);
            }else {
                log.info("user not exists");
                return Response.failure(ResponseStatus.USER_NOT_EXISTS.status,ResponseStatus.USER_NOT_EXISTS.message);
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
		UserHobby deleteUserHobby = new UserHobby();
		deleteUserHobby.setUid(user.getUid());
		userMybatisDao.deleteUserHobby(deleteUserHobby);
		Set<Long> userHobbyTags = new LinkedHashSet<>();
		if (!StringUtils.isEmpty(hobby)) {
			String[] hobbies = hobby.split(";");
			for (String h : hobbies) {
				UserHobby userHobby = new UserHobby();
				userHobby.setHobby(Long.parseLong(h));
				userHobby.setUid(user.getUid());
				userMybatisDao.createUserHobby(userHobby);
				List<Long> tags= userInitJdbcDao.getTagFromUserHobby(userHobby.getHobby());
				userHobbyTags.addAll(tags);
			}
			userInitJdbcDao.batchInsertUserLikeTags(user.getUid(),userHobbyTags);
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
        if(null == modifyUserProfileDto.getAvatar() || "".equals(modifyUserProfileDto.getAvatar())){
        	userProfile.setAvatar(null);
        }else{
        	userProfile.setAvatar(modifyUserProfileDto.getAvatar());
        }
        userProfile.setIntroduced(modifyUserProfileDto.getIntroduced());
        
        //设置为0不需要第三方登录检查昵称了 昵称唯一
//        userProfile.setIsClientLogin(0);
        //修改用户爱好
        if(!StringUtils.isEmpty(modifyUserProfileDto.getHobby())){
            ModifyUserHobbyDto modifyUserHobbyDto = new ModifyUserHobbyDto();
            modifyUserHobbyDto.setUid(modifyUserProfileDto.getUid());
            modifyUserHobbyDto.setHobby(modifyUserProfileDto.getHobby());
            this.modifyUserHobby(modifyUserHobbyDto);
            log.info("modify user hobby");
        }
        userProfile.setUpdateTime(new Date());
        userMybatisDao.modifyUserProfile(userProfile);
        try {
        	String modifyName = "";
        	if(!StringUtils.isEmpty(modifyUserProfileDto.getNickName())){
        		modifyName = modifyUserProfileDto.getNickName();
        	}
        	String modifyAvatar = "";
        	if(StringUtils.isEmpty(modifyUserProfileDto.getAvatar())){
        		modifyAvatar = Constant.QINIU_DOMAIN + "/" + modifyUserProfileDto.getAvatar();
        	}
        	
        	if(StringUtils.isEmpty(modifyUserProfileDto.getNickName())){
        		smsService.refreshUser(userProfile.getUid()+"", modifyName, modifyAvatar);
        	}
		} catch (Exception e) {
			log.error("user modify refresh IM error");
		}
        log.info("user modify profile success");
        log.info("modifyUserProfile end ...");
        return Response.success(ResponseStatus.USER_MODIFY_PROFILE_SUCCESS.status,ResponseStatus.USER_MODIFY_PROFILE_SUCCESS.message);
    }

    @Override
    public UserProfile getUserProfileByUid(long uid) {
        return userMybatisDao.getUserProfileByUid(uid);
    }

    @Override
    public User getUserByUidAndTime(long uid, Date startDate, Date endDate) {
        return userMybatisDao.getUserByUidAndTime(uid,startDate,endDate);
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
    public Response userNotice(UserNoticeDto userNoticeDto){
        log.info("getUserNotice start ...");
        ShowUserNoticeDto showUserNoticeDto = new ShowUserNoticeDto();
        List<UserNotice> list = userMybatisDao.userNotice(userNoticeDto);
        log.info("getUserNotice data success");
        List<Long> uidList = new ArrayList<Long>();
        List<Long> topicIdList = new ArrayList<Long>();
        List<Long> aggregationApplyIdList = new ArrayList<Long>();
        JSONObject obj = null;
        for (UserNotice userNotice : list){
        	if(!uidList.contains(userNotice.getFromUid())){
        		uidList.add(userNotice.getFromUid());
        	}
        	if(!uidList.contains(userNotice.getToUid())){
        		uidList.add(userNotice.getToUid());
        	}
        	if(userNotice.getNoticeType() == Specification.UserNoticeType.LIVE_TAG.index
            		|| userNotice.getNoticeType() == Specification.UserNoticeType.LIVE_REVIEW.index
            		|| userNotice.getNoticeType() == Specification.UserNoticeType.LIVE_INVITED.index
            		|| userNotice.getNoticeType() == Specification.UserNoticeType.REMOVE_SNS_CIRCLE.index){
        		//这些是王国相关的消息
        		if(!topicIdList.contains(userNotice.getCid())){
        			topicIdList.add(userNotice.getCid());
        		}
        	}else if(userNotice.getNoticeType() == Specification.UserNoticeType.CORE_CIRCLE_APPLY.index
        			|| userNotice.getNoticeType() == Specification.UserNoticeType.CORE_CIRCLE_NOTICE.index
        			|| userNotice.getNoticeType() == Specification.UserNoticeType.AGGREGATION_APPLY.index
        			|| userNotice.getNoticeType() == Specification.UserNoticeType.AGGREGATION_NOTICE.index){//核心圈和聚合相关
        		if(userNotice.getNoticeType() == Specification.UserNoticeType.AGGREGATION_APPLY.index){
        			aggregationApplyIdList.add(userNotice.getCid());
        		}
        		//新增的消息类型
        		String extra = userNotice.getExtra();
        		if(!StringUtils.isEmpty(extra)){
        			obj = JSON.parseObject(extra);
        			if(null != obj && !obj.isEmpty()){
        				if(null != obj.get("coverTopicId")){
        					long ctId = obj.getLongValue("coverTopicId");
        					if(ctId > 0 && !topicIdList.contains(Long.valueOf(ctId))){
        						topicIdList.add(ctId);
        					}
        				}
        				if(null != obj.get("textTopicId")){
        					long ttId = obj.getLongValue("textTopicId");
        					if(ttId > 0 && !topicIdList.contains(Long.valueOf(ttId))){
        						topicIdList.add(ttId);
        					}
        				}
        			}
        		}
        	}
        }
        List<UserProfile> userProfileList = this.getUserProfilesByUids(uidList);
        Map<String, UserProfile> userProfileMap = new HashMap<String, UserProfile>();
        if(null != userProfileList && userProfileList.size() > 0){
        	for(UserProfile up : userProfileList){
        		userProfileMap.put(String.valueOf(up.getUid()), up);
        	}
        }
        
        Map<String, Map<String, Object>> topicMap = new HashMap<String, Map<String, Object>>();
        List<Map<String,Object>> topicList = liveForUserJdbcDao.getTopicListByIds(topicIdList);
        if(null != topicList && topicList.size() > 0){
        	Long topicId = null;
        	for(Map<String,Object>  map : topicList){
        		topicId = (Long)map.get("id");
        		topicMap.put(topicId.toString(), map);
        	}
        }
        
        Map<String, Map<String, Object>> aggregationApplyMap = new HashMap<String, Map<String, Object>>();
        List<Map<String,Object>> aggregationApplyList = liveForUserJdbcDao.getTopicAggregationApplyListByIds(aggregationApplyIdList);
        if(null != aggregationApplyList && aggregationApplyList.size() > 0){
        	Long applyId = null;
        	for(Map<String, Object> aa : aggregationApplyList){
        		applyId = (Long)aa.get("id");
        		aggregationApplyMap.put(applyId.toString(), aa);
        	}
        }
        
        UserProfile fromUser = null;
        UserProfile toUser = null;
        Map<String, Object> topic = null;
        Map<String, Object> aggregationApply = null;
        for (UserNotice userNotice : list){
            ShowUserNoticeDto.UserNoticeElement userNoticeElement = new ShowUserNoticeDto.UserNoticeElement();
            userNoticeElement.setId(userNotice.getId());
            userNoticeElement.setTag(userNotice.getTag());
            if(!StringUtils.isEmpty( userNotice.getCoverImage())) {
                userNoticeElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + userNotice.getCoverImage());
            }else{
                userNoticeElement.setCoverImage("");
            }
            fromUser = userProfileMap.get(String.valueOf(userNotice.getFromUid()));
            userNoticeElement.setFromAvatar(Constant.QINIU_DOMAIN + "/" + fromUser.getAvatar());
            userNoticeElement.setNoticeType(userNotice.getNoticeType());
            userNoticeElement.setFromNickName(fromUser.getNickName());
            userNoticeElement.setFromUid(userNotice.getFromUid());
            userNoticeElement.setReadStatus(userNotice.getReadStatus());
            userNoticeElement.setCreateTime(userNotice.getCreateTime());
            userNoticeElement.setLikeCount(userNotice.getLikeCount());
            userNoticeElement.setSummary(userNotice.getSummary());
            userNoticeElement.setToUid(userNotice.getToUid());
            userNoticeElement.setReview(userNotice.getReview());
            userNoticeElement.setV_lv(fromUser.getvLv());
            if (fromUser.getLevel() != null){
                userNoticeElement.setLevel(fromUser.getLevel());
            }
            if(!StringUtils.isEmpty(fromUser.getAvatarFrame())){
            	userNoticeElement.setFromAvatarFrame(Constant.QINIU_DOMAIN + "/" + fromUser.getAvatarFrame());
            }

            toUser = userProfileMap.get(String.valueOf(userNotice.getToUid()));
            userNoticeElement.setTo_v_lv(toUser.getvLv());
            userNoticeElement.setLevel(toUser.getLevel());
            userNoticeElement.setToNickName(toUser.getNickName());
            userNoticeElement.setCid(userNotice.getCid());
            if(userNotice.getNoticeType() == Specification.UserNoticeType.LIVE_TAG.index
            		|| userNotice.getNoticeType() == Specification.UserNoticeType.LIVE_REVIEW.index
            		|| userNotice.getNoticeType() == Specification.UserNoticeType.LIVE_INVITED.index
            		|| userNotice.getNoticeType() == Specification.UserNoticeType.REMOVE_SNS_CIRCLE.index){
            	//这个是王国相关的消息，则需要设置王国身份信息
            	topic = topicMap.get(String.valueOf(userNotice.getCid()));
            	if(null != topic){
            		userNoticeElement.setInternalStatus(this.getInternalStatus(topic, userNoticeDto.getUid()));
            		userNoticeElement.setFromInternalStatus(this.getInternalStatus(topic, userNotice.getFromUid()));
            		userNoticeElement.setContentType((Integer)topic.get("type"));
            	}
            }else if(userNotice.getNoticeType() == Specification.UserNoticeType.CORE_CIRCLE_APPLY.index
        			|| userNotice.getNoticeType() == Specification.UserNoticeType.CORE_CIRCLE_NOTICE.index
        			|| userNotice.getNoticeType() == Specification.UserNoticeType.AGGREGATION_APPLY.index
        			|| userNotice.getNoticeType() == Specification.UserNoticeType.AGGREGATION_NOTICE.index){//聚核心圈和聚合相关
            	if(userNotice.getNoticeType() == Specification.UserNoticeType.AGGREGATION_APPLY.index){
            		aggregationApply = aggregationApplyMap.get(String.valueOf(userNotice.getCid()));
            		if(null != aggregationApply){
            			userNoticeElement.setApplyId(userNotice.getCid());
            			userNoticeElement.setApplyStatus((Integer)aggregationApply.get("result"));
            		}
            	}
            	
            	String extra = userNotice.getExtra();
        		if(!StringUtils.isEmpty(extra)){
        			obj = JSON.parseObject(extra);
        			if(null != obj && !obj.isEmpty()){
        				if(null != obj.get("coverTopicId")){
        					long coverTopicId = obj.getLongValue("coverTopicId");
        					userNoticeElement.setCoverTopicId(coverTopicId);
        					topic = topicMap.get(String.valueOf(coverTopicId));
        					if(null != topic){
        						userNoticeElement.setInternalStatus(this.getInternalStatus(topic, userNoticeDto.getUid()));
        					}
        				}
        				if(null != obj.get("coverImage")){
        					userNoticeElement.setCoverImage(Constant.QINIU_DOMAIN + "/" + obj.getString("coverImage"));
        				}
        				if(null != obj.get("coverTitle")){
        					userNoticeElement.setCoverTitle(obj.getString("coverTitle"));
        				}
        				if(null != obj.get("coverType")){
        					userNoticeElement.setCoverType(obj.getIntValue("coverType"));
        				}
        				if(null != obj.get("textTopicId")){
        					long textTopicId = obj.getLongValue("textTopicId");
        					userNoticeElement.setTextTopicId(textTopicId);
        					topic = topicMap.get(String.valueOf(textTopicId));
        					if(null != topic){
        						userNoticeElement.setTextInternalStatus(this.getInternalStatus(topic, userNoticeDto.getUid()));
        					}
        				}
        				if(null != obj.get("textImage")){
        					userNoticeElement.setTextImage(Constant.QINIU_DOMAIN + "/" + obj.getString("textImage"));
        				}
        				if(null != obj.get("textTitle")){
        					userNoticeElement.setTextTitle(obj.getString("textTitle"));
        				}
        				if(null != obj.get("textType")){
        					userNoticeElement.setTextType(obj.getIntValue("textType"));
        				}
        			}
        		}
            }
            
            showUserNoticeDto.getUserNoticeList().add(userNoticeElement);
        }
        
        if(userNoticeDto.getLevel() == 1){//如果是查询的一级页面，则需要知道是否有二级按钮是否要红点
        	String t = cacheService.get("my:notice:level2:"+userNoticeDto.getUid());
        	if(null != t && "1".equals(t)){//有新消息
        		showUserNoticeDto.setHasNextNew(1);
        	}else{
        		showUserNoticeDto.setHasNextNew(0);
        	}
        	//并且将第一级目录中全部置为已读
        	userMybatisDao.clearUserNoticeUnreadByLevel(userNoticeDto.getUid(), Specification.UserNoticeLevel.LEVEL_1.index);
        }else if(userNoticeDto.getLevel() == 2){//如果是查看二级页面，则消除二级按钮红点
        	cacheService.set("my:notice:level2:"+userNoticeDto.getUid(), "0");
        	//并且将第二级目录中全部置为已读
        	userMybatisDao.clearUserNoticeUnreadByLevel(userNoticeDto.getUid(), Specification.UserNoticeLevel.LEVEL_2.index);
        }
        
        log.info("getUserNotice end ...");
        return Response.success(ResponseStatus.GET_USER_NOTICE_SUCCESS.status,ResponseStatus.GET_USER_NOTICE_SUCCESS.message,showUserNoticeDto);
    }
    
    //判断核心圈身份
    private int getInternalStatus(Map<String, Object> topic, long uid) {
    	int internalStatus = 0;
        String coreCircle = (String)topic.get("core_circle");
        if(null != coreCircle){
        	JSONArray array = JSON.parseArray(coreCircle);
        	for (int i = 0; i < array.size(); i++) {
                if (array.getLong(i) == uid) {
                    internalStatus = Specification.SnsCircle.CORE.index;
                    break;
                }
            }
        }
        
//        if (internalStatus == 0) {
//            internalStatus = this.getUserInternalStatus(uid, (Long)topic.get("uid"));
//        }

        return internalStatus;
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
    public long createUserNoticeAndReturnId(UserNotice userNotice) {
        userMybatisDao.createUserNotice(userNotice);
        return userNotice.getId();
    }
    
    public void createUserNoticeUnread(UserNoticeUnread userNoticeUnread){
    	userMybatisDao.createUserNoticeUnread(userNoticeUnread);
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
            
            applicationEventBus.post(new FollowEvent(followDto.getSourceUid(), followDto.getTargetUid()));
            
            //关注提醒
//            UserProfile sourceUser = getUserProfileByUid(followDto.getSourceUid());
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("messageType",Specification.PushMessageType.FOLLOW.index);
//            jsonObject.addProperty("type",Specification.PushObjectType.SNS_CIRCLE.index);
//            String alias = String.valueOf(followDto.getTargetUid());
//            jPushService.payloadByIdExtra(alias, sourceUser.getNickName() + "关注了你！", JPushUtils.packageExtra(jsonObject));
//
//            //粉丝数量红点
//            log.info("follow fans add push start");
//            jsonObject = new JsonObject();
//            jsonObject.addProperty("fansCount","1");
//            alias = String.valueOf(followDto.getTargetUid());
//            jPushService.payloadByIdForMessage(alias,jsonObject.toString());
//            log.info("follow fans add push end ");
//
//            log.info("follow push success");
            //monitorService.post(new MonitorEvent(Specification.MonitorType.ACTION.index,Specification.MonitorAction.FOLLOW.index,0,followDto.getSourceUid()));
//            log.info("monitor success");
            CoinRule coinRule =  getCoinRules().get(Rules.FOLLOW_USER_KEY);
            coinRule.setExt(followDto.getTargetUid());
            ModifyUserCoinDto modifyUserCoinDto = coinRule(followDto.getSourceUid(),coinRule);
            log.info("follow end ...");
           Response response = Response.success(ResponseStatus.USER_FOLLOW_SUCCESS.status, ResponseStatus.USER_FOLLOW_SUCCESS.message);
           response.setData(modifyUserCoinDto);
           return response;
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
    
    public List<UserFollow> getAllFollows(long uid, List<Long> uids){
    	if(null == uids || uids.size() == 0){
    		return null;
    	}
    	return userMybatisDao.getAllFollows(uid, uids);
    }

    @Override
    public UserToken getUserByUidAndToken(long uid, String token) {
        return userMybatisDao.getUserTokenByUid(uid, token);
    }
    
    @Override
    public UserToken getUserTokenByUid(long uid){
    	return userMybatisDao.getUserTokenByUid(uid);
    }

    @Override
    public List<UserToken> getUserTokenByUids(List<Long> uids){
    	return userMybatisDao.getUserTokensByUids(uids);
    }

    public Response getUser(long targetUid, long sourceUid){
        UserProfile userProfile =  getUserProfileByUid(targetUid);
        UserInfoDto.User user = new UserInfoDto.User();
        user.setV_lv(userProfile.getvLv());
        user.setNickName(userProfile.getNickName());
        user.setAvatar(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
        if(!StringUtils.isEmpty(userProfile.getAvatarFrame())){
        	user.setAvatarFrame(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatarFrame());
        }
        user.setGender(userProfile.getGender());
        user.setUid(userProfile.getUid());
        user.setIsFollowed(isFollow(targetUid,sourceUid));
        user.setIsFollowMe(isFollow(sourceUid,targetUid));
        user.setCreateTime(userProfile.getCreateTime());
        user.setLevel(userProfile.getLevel());
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
            element.setV_lv(userProfile.getvLv());
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
            element.setV_lv(userProfile.getvLv());
            element.setLevel(userProfile.getLevel());
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
        if(list!=null && list.size()>0) {
            //兼容emoji 才这样判断
            for(UserProfile userProfile : list){
                if(nickName.equals(userProfile.getNickName())) {
                    return false;
                }
            }
        }

//        if(list!=null&&list.size()>0){
//            return false;
//        }
//        else{
//            return true;
//        }

        return true;
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
    public Response getUserProfile(long uid, int vFlag) {
        log.info("getUserProfile start ...");
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
        log.info("getUserProfile getUserData success . uid : " + uid);
        ShowUserProfileDto showUserProfileDto = new ShowUserProfileDto();
        UserProfile profile = userMybatisDao.getUserProfileByUid(uid);
        showUserProfileDto.setV_lv(profile.getvLv());
        showUserProfileDto.setUid(userProfile.getUid());
        showUserProfileDto.setNickName(userProfile.getNickName());
        showUserProfileDto.setAvatar(Constant.QINIU_DOMAIN + "/" +userProfile.getAvatar());
        if(!StringUtils.isEmpty(userProfile.getAvatarFrame())){
        	showUserProfileDto.setAvatarFrame(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatarFrame());
        }
        showUserProfileDto.setBirthday(userProfile.getBirthday());
        showUserProfileDto.setGender(userProfile.getGender());
        showUserProfileDto.setUserName(userProfile.getMobile());
        showUserProfileDto.setIsPromoter(userProfile.getIsPromoter());
//        showUserProfileDto.setUgcCount(userInitJdbcDao.getUGCount(uid, vFlag));
        showUserProfileDto.setLiveCount(userInitJdbcDao.getLiveCount(uid));
//        showUserProfileDto.setAcCount(userInitJdbcDao.getLiveAcCount(uid));
        // 获取用户级别和可用米汤币
        showUserProfileDto.setAvailableCoin(userProfile.getAvailableCoin());
        showUserProfileDto.setLevel(userProfile.getLevel());
        String value = getAppConfigByKey(USER_PERMISSIONS);
//        log.info("infos: " + value);
        UserPermissionDto userPermissionDto = JSON.parseObject(value, UserPermissionDto.class);
        for(UserPermissionDto.UserLevelDto userLevelDto : userPermissionDto.getLevels()){
            if(userProfile.getLevel()==userLevelDto.getLevel()){
                showUserProfileDto.setLevelIcon(userLevelDto.getIcon());
                break;
            }
        }
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
        //米币转换人民币
        String exchangeRate = this.getAppConfigByKey("EXCHANGE_RATE");
        if (StringUtils.isEmpty(exchangeRate)){
            exchangeRate = "100";
        }
        Integer  i = showUserProfileDto.getAvailableCoin();
        Double d = i.doubleValue();
        showUserProfileDto.setPriceRMB(d/Integer.parseInt(exchangeRate));
        return  Response.success(showUserProfileDto);
    }

    @Override
    public UserProfile getUserProfileByMobile(String mobile) {
        return userMybatisDao.getUserProfileByMobile(mobile);
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
        if(limit == 1){
            start = 10100000;
            end =11000000;
        }else if(limit == 2){
            start = 11000000;
            end =12000000;
        } else if(limit == 3){
            start = 12000000;
            end =13000000;
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
    public Response versionControl(String version,int platform,String ip,String channel,String device) {
//        if(!StringUtils.isEmpty(params)) {
//            WapxIosEvent event = new WapxIosEvent();
//            try {
//                WapxParams wapxParams = com.alibaba.dubbo.common.json.JSON.parse(params, WapxParams.class);
//                event.setIdfa(wapxParams.getIdfa());
//            } catch (ParseException e) {
//                log.error("params parse error");
//            }
//            applicationEventBus.post(event);
//        }
        //记录打开次数
        log.info("ip address :" + ip);
        log.info("add channel count start ...");
        OpenDeviceCount openDeviceCount = new OpenDeviceCount();
        openDeviceCount.setChannel(channel);
        openDeviceCount.setVersion(version);
        openDeviceCount.setDevice(device);
        openDeviceCount.setCreatTime(new Date());
        openDeviceCount.setIpAddress(ip);
        openDeviceCount.setPlatform(platform);
        userMybatisDao.createOpenCount(openDeviceCount);
        log.info("add channel count end ...");

        VersionControlDto versionControlDto = new VersionControlDto();

        //春节特定标识
        Map<String ,Object> appUi = activityJdbcDao.getAppUiControl();
        if(appUi != null){
            versionControlDto.setResourceCode((Integer)appUi.get("source_code"));
        }else{
            versionControlDto.setResourceCode(0);
        }

        boolean hasNewVersion = false;
        VersionControl newestVersion = userMybatisDao.getNewestVersion(platform);
        if(null != newestVersion && !StringUtils.isEmpty(newestVersion.getVersion())
        		&& !StringUtils.isEmpty(version)){
        	hasNewVersion = this.hasNewVersion(version, newestVersion.getVersion());
        }
        if(!hasNewVersion){
        	return Response.success(versionControlDto);
        }
        
        //根据渠道配置进行提醒
        VersionChannelDownload vcd = userMybatisDao.getVersionChannelDownloadByChannel(channel);
        if(null != vcd && vcd.getType() == 1 && !StringUtils.isEmpty(vcd.getVersionAddr())){
        	versionControlDto.setId(newestVersion.getId());
            versionControlDto.setUpdateDescription(newestVersion.getUpdateDescription());
            versionControlDto.setUpdateTime(newestVersion.getUpdateTime());
            versionControlDto.setPlatform(newestVersion.getPlatform());
            versionControlDto.setVersion(newestVersion.getVersion());
            versionControlDto.setUpdateUrl(vcd.getVersionAddr());
            if(newestVersion.getForceUpdate() == 1){
                versionControlDto.setIsUpdate(Specification.VersionStatus.FORCE_UPDATE.index);
            }else{
                versionControlDto.setIsUpdate(Specification.VersionStatus.UPDATE.index);
            }
        }
        
        //monitorService.post(new MonitorEvent(Specification.MonitorType.BOOT.index,Specification.MonitorAction.BOOT.index,0,0));
        return Response.success(versionControlDto);
    }
    
    private boolean hasNewVersion(String currentVersion, String newestVersion){
    	String[] cv = currentVersion.split("\\.");
    	String[] nv = newestVersion.split("\\.");
    	if(cv.length>=3 && nv.length>=3){
        	try{
        		int cv1 = Integer.valueOf(cv[0]);
        		int nv1 = Integer.valueOf(nv[0]);
        		if(cv1>nv1){
        			return false;
        		}else if(cv1<nv1){
        			return true;
        		}else{
        			int cv2 = Integer.valueOf(cv[1]);
            		int nv2 = Integer.valueOf(nv[1]);
            		if(cv2>nv2){
            			return false;
            		}else if(cv2<nv2){
            			return true;
            		}else{
            			int cv3 = Integer.valueOf(cv[2]);
                		int nv3 = Integer.valueOf(nv[2]);
                		if(cv3>nv3){
                			return false;
                		}else if(cv3<nv3){
                			return true;
                		}else{
                			if(cv.length > 3){
                				if(nv.length > 3){
                					if(nv[3].compareTo(cv[3]) > 0){
                						return true;
                					}else{
                						return false;
                					}
                				}else{
                					return false;
                				}
                			}else{
                				return false;
                			}
                		}
            		}
        		}
        	}catch(Exception ignore){
        	}
    	}
    	return false;
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
        //性别默认给-1
        userProfile.setGender(-1);
        //生日默认给一个不可能的值
        userProfile.setBirthday("1800-1-1");
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
        //不管你爽不爽，就是要卖你3个王国
        give3Kingdoms(userProfile);
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
        if(!StringUtils.isEmpty(userProfile.getAvatarFrame())){
        	dto.setAvatarFrame(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatarFrame());
        }
        dto.setNickName(userProfile.getNickName());
        dto.setRegUrl(reg_web+uid);
        dto.setV_lv(userProfile.getvLv());
        dto.setLevel(userProfile.getLevel());
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
    public Response getFansOrderByTime(FansParamsDto fansParamsDto) {
        log.info("getFans start ...");
        List<UserFansDto> list = userMybatisDao.getFansOrderByTime(fansParamsDto);
        log.info("getFans getData success");
        for(UserFansDto userFansDto : list){
            userFansDto.setAvatar(Constant.QINIU_DOMAIN + "/" + userFansDto.getAvatar());
            int followMe = this.isFollow(fansParamsDto.getUid(),userFansDto.getUid());
            userFansDto.setIsFollowMe(followMe);
            int followed = this.isFollow(userFansDto.getUid(),fansParamsDto.getUid());
            userFansDto.setIsFollowed(followed);
            UserProfile userProfile = userMybatisDao.getUserProfileByUid(userFansDto.getUid());
            userFansDto.setIntroduced(userProfile.getIntroduced());
            userFansDto.setV_lv(userProfile.getvLv());
            userFansDto.setLevel(userProfile.getLevel());
            if(!StringUtils.isEmpty(userProfile.getAvatarFrame())){
            	userFansDto.setAvatarFrame(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatarFrame());
            }else{
            	userFansDto.setAvatarFrame(null);
            }
        }
        ShowUserFansDto showUserFansDto = new ShowUserFansDto();
        showUserFansDto.setResult(list);
        log.info("getFans end ...");
        return Response.success(ResponseStatus.SHOW_USER_FANS_LIST_SUCCESS.status, ResponseStatus.SHOW_USER_FANS_LIST_SUCCESS.message,showUserFansDto);
    }

    @Override
    public Response getFollowsOrderByTime(FollowParamsDto followParamsDto) {
        log.info("getFollowsOrderByTime start ...");
        List<UserFollowDto> list = userMybatisDao.getFollowsOrderByTime(followParamsDto);
        log.info("getFollowsOrderByTime getData success");
        ShowUserFollowDto showUserFollowDto = new ShowUserFollowDto();
        for(UserFollowDto userFollowDto : list){
            userFollowDto.setAvatar(Constant.QINIU_DOMAIN + "/" + userFollowDto.getAvatar());
            int followMe = this.isFollow(followParamsDto.getUid(),userFollowDto.getUid());
            userFollowDto.setIsFollowMe(followMe);
            int followed = this.isFollow(userFollowDto.getUid(),followParamsDto.getUid());
            userFollowDto.setIsFollowed(followed);
            UserProfile userProfile = userMybatisDao.getUserProfileByUid(userFollowDto.getUid());
            userFollowDto.setIntroduced(userProfile.getIntroduced());
            userFollowDto.setV_lv(userProfile.getvLv());
            userFollowDto.setLevel(userProfile.getLevel());
            if(!StringUtils.isEmpty(userProfile.getAvatarFrame())){
            	userFollowDto.setAvatarFrame(Constant.QINIU_DOMAIN + "/" + userProfile.getAvatarFrame());
            }else{
            	userFollowDto.setAvatarFrame(null);
            }
        }
        showUserFollowDto.setResult(list);
        log.info("getFollowsOrderByTime end ...");
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
            UserProfile profile = userMybatisDao.getUserProfileByUid(userProfile.getUid());
            element.setV_lv(profile.getvLv());
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
        ThirdPartUser users = userMybatisDao.getThirdPartUser(thirdPartSignUpDto.getThirdPartOpenId() ,thirdPartSignUpDto.getThirdPartType());

        //先判断是否H5微信登陆过 如果登陆过 isClientLogin为1 需要修改昵称
        if(!StringUtils.isEmpty(thirdPartSignUpDto.getNewNickName()) && !StringUtils.isEmpty(thirdPartSignUpDto.getUnionId())){
            List<ThirdPartUser> thirdPartUsers = userMybatisDao.getThirdPartUserByUnionId(thirdPartSignUpDto.getUnionId() ,thirdPartSignUpDto.getThirdPartType());
            if(thirdPartUsers.size()>0 && thirdPartUsers !=null && StringUtils.isEmpty(thirdPartUsers.get(0).getThirdPartOpenId())){
                long uid = thirdPartUsers.get(0).getUid();
                UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
                    //将用户名后面加随机大写英文字母 , 直至 不重复;
                   boolean flag = true ;
                    while(flag){
                        if(!this.existsNickName(thirdPartSignUpDto.getNickName())){
                            String str="";
                            for(int i=0;i<1;i++){//你想生成几个字符的，就把3改成几，如果改成１,那就生成一个随机字母．
                                str= str+(char) (Math.random ()*26+'A');
                            }
                            thirdPartSignUpDto.setNickName(thirdPartSignUpDto.getNickName()+str);
                   }else{
                            flag = false ;
                        }
                }
                userProfile.setNickName(thirdPartSignUpDto.getNickName());
                userProfile.setIsClientLogin(0);
                userMybatisDao.modifyUserProfile(userProfile);
                try {
                	if(!StringUtils.isEmpty(userProfile.getNickName())){
                		smsService.refreshUser(userProfile.getUid()+"", userProfile.getNickName(), "");
                	}
        		} catch (Exception e) {
        			log.error("user modify refresh IM error");
        		}
            }
        }

        //兼容老版本
        if(StringUtils.isEmpty(thirdPartSignUpDto.getUnionId())){
            if (users !=null) {
                long uid = users.getUid();
                UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
                UserToken userToken = userMybatisDao.getUserTokenByUid(uid);
                loginSuccessDto.setUid(userProfile.getUid());
                loginSuccessDto.setGender(userProfile.getGender());
                loginSuccessDto.setNickName(userProfile.getNickName());
                loginSuccessDto.setToken(userToken.getToken());
                loginSuccessDto.setLevel(userProfile.getLevel());
                if(checkUserDisable(loginSuccessDto.getUid())){
                	return Response.failure(ResponseStatus.USER_ACCOUNT_DISABLED.status, ResponseStatus.USER_ACCOUNT_DISABLED.message);
                }
                this.saveUserDeviceInfo(userProfile.getUid(), thirdPartSignUpDto.getIp(), 2, thirdPartSignUpDto.getDeviceData());
                
                this.activityH5RegisterUser(userProfile);
                
                return Response.success(ResponseStatus.USER_EXISTS.status, ResponseStatus.USER_EXISTS.message, loginSuccessDto);
            } else {
                buildThirdPart(thirdPartSignUpDto, loginSuccessDto);
            }
            if(checkUserDisable(loginSuccessDto.getUid())){
            	return Response.failure(ResponseStatus.USER_ACCOUNT_DISABLED.status, ResponseStatus.USER_ACCOUNT_DISABLED.message);
            }
            return Response.success(ResponseStatus.USER_LOGIN_SUCCESS.status, ResponseStatus.USER_LOGIN_SUCCESS.message, loginSuccessDto);
        }
        else{
            if(thirdPartSignUpDto.getThirdPartType() == Specification.ThirdPartType.WEIXIN.index && thirdPartSignUpDto.getH5type() == 1){
                List<ThirdPartUser> thirdPartUsers = userMybatisDao.getThirdPartUserByUnionId(thirdPartSignUpDto.getUnionId() ,thirdPartSignUpDto.getThirdPartType());
                if(thirdPartUsers.size() > 0){
                    UserProfile userProfile = userMybatisDao.getUserProfileByUid(thirdPartUsers.get(0).getUid());
                    UserToken userToken = userMybatisDao.getUserTokenByUid(thirdPartUsers.get(0).getUid());
                    loginSuccessDto.setUid(userProfile.getUid());
                    loginSuccessDto.setGender(userProfile.getGender());
                    loginSuccessDto.setNickName(userProfile.getNickName());
                    loginSuccessDto.setToken(userToken.getToken());
                    loginSuccessDto.setLevel(userProfile.getLevel());
                    if(checkUserDisable(loginSuccessDto.getUid())){
                    	return Response.failure(ResponseStatus.USER_ACCOUNT_DISABLED.status, ResponseStatus.USER_ACCOUNT_DISABLED.message);
                    }
                    this.saveUserDeviceInfo(userProfile.getUid(), thirdPartSignUpDto.getIp(), 2, thirdPartSignUpDto.getDeviceData());
                    this.activityH5RegisterUser(userProfile);
                    return Response.success(ResponseStatus.USER_EXISTS.status, ResponseStatus.USER_EXISTS.message, loginSuccessDto);
                }
                //h5微信登录
                thirdPartSignUpDto.setPlatform(3);
                buildThirdPart(thirdPartSignUpDto, loginSuccessDto);
                if(checkUserDisable(loginSuccessDto.getUid())){
                	return Response.failure(ResponseStatus.USER_ACCOUNT_DISABLED.status, ResponseStatus.USER_ACCOUNT_DISABLED.message);
                }
                return Response.success(ResponseStatus.USER_LOGIN_SUCCESS.status, ResponseStatus.USER_LOGIN_SUCCESS.message, loginSuccessDto);
            }
            else if(users !=null && StringUtils.isEmpty(users.getThirdPartUnionid()) && thirdPartSignUpDto.getThirdPartType() == users.getThirdPartType()){
                //如果openid用户有，unionId没有 先看有没有其他unionId和这个相同的，有把他置为失效，然后保存unionId，后返回登录信息
                //app登录
                List<ThirdPartUser> thirdPartUsers = userMybatisDao.getThirdPartUserByUnionId(thirdPartSignUpDto.getUnionId() ,thirdPartSignUpDto.getThirdPartType());
                if(thirdPartUsers.size() >0){
                    thirdPartUsers.get(0).setStatus(0);
                    userMybatisDao.updateThirdPartUser(thirdPartUsers.get(0));
                }
                users.setThirdPartUnionid(thirdPartSignUpDto.getUnionId());
                userMybatisDao.updateThirdPartUser(users);
                ResponseThirdPartUser(users ,loginSuccessDto);
                if(checkUserDisable(loginSuccessDto.getUid())){
                	return Response.failure(ResponseStatus.USER_ACCOUNT_DISABLED.status, ResponseStatus.USER_ACCOUNT_DISABLED.message);
                }
                this.saveUserDeviceInfo(users.getUid(), thirdPartSignUpDto.getIp(), 2, thirdPartSignUpDto.getDeviceData());
                return Response.success(ResponseStatus.USER_EXISTS.status, ResponseStatus.USER_EXISTS.message, loginSuccessDto);

            } else if(users !=null && !StringUtils.isEmpty(users.getThirdPartUnionid()) && thirdPartSignUpDto.getThirdPartType() == users.getThirdPartType()){
                //如果openid用户有，unionId有 直接登录
                ResponseThirdPartUser(users ,loginSuccessDto);
                if(checkUserDisable(loginSuccessDto.getUid())){
                	return Response.failure(ResponseStatus.USER_ACCOUNT_DISABLED.status, ResponseStatus.USER_ACCOUNT_DISABLED.message);
                }
                this.saveUserDeviceInfo(users.getUid(), thirdPartSignUpDto.getIp(), 2, thirdPartSignUpDto.getDeviceData());
                return Response.success(ResponseStatus.USER_EXISTS.status, ResponseStatus.USER_EXISTS.message, loginSuccessDto);
            }
            else if(users ==null){
                //根据openid查询不到的话，用unionId去查 如果有 保存openId 直接返回登录信息
                List<ThirdPartUser> thirdPartUsers = userMybatisDao.getThirdPartUserByUnionId(thirdPartSignUpDto.getUnionId(),thirdPartSignUpDto.getThirdPartType());
                if(thirdPartUsers.size() >0) {
                    thirdPartUsers.get(0).setThirdPartOpenId(thirdPartSignUpDto.getThirdPartOpenId());
                    userMybatisDao.updateThirdPartUser(thirdPartUsers.get(0));

                    List<ThirdPartUser> thirdPartUser = userMybatisDao.getThirdPartUserByUnionId(thirdPartSignUpDto.getUnionId(),thirdPartSignUpDto.getThirdPartType());
                    UserProfile userProfile = userMybatisDao.getUserProfileByUid(thirdPartUser.get(0).getUid());
                    UserToken userToken = userMybatisDao.getUserTokenByUid(thirdPartUser.get(0).getUid());
                    loginSuccessDto.setUid(userProfile.getUid());
                    loginSuccessDto.setGender(userProfile.getGender());
                    loginSuccessDto.setNickName(userProfile.getNickName());
                    loginSuccessDto.setToken(userToken.getToken());
                    loginSuccessDto.setLevel(userProfile.getLevel());
                    //h5先登录了 app未登录过 为首次登录 返回注册过了 但是值是1需要修改昵称
//                    loginSuccessDto.setIsClientLogin(userProfile.getIsClientLogin());
                    //openId没有unionId有的情况 肯定是h5微信登录的 所以要修改昵称 前台检测过传来的
//                    userProfile.setNickName(thirdPartSignUpDto.getNickName());
//                    userMybatisDao.modifyUserProfile(userProfile);
                    if(checkUserDisable(loginSuccessDto.getUid())){
                    	return Response.failure(ResponseStatus.USER_ACCOUNT_DISABLED.status, ResponseStatus.USER_ACCOUNT_DISABLED.message);
                    }
                    this.saveUserDeviceInfo(userProfile.getUid(), thirdPartSignUpDto.getIp(), 2, thirdPartSignUpDto.getDeviceData());
                    this.activityH5RegisterUser(userProfile);
                    return Response.success(ResponseStatus.USER_EXISTS.status, ResponseStatus.USER_EXISTS.message, loginSuccessDto);
                }else{
                    buildThirdPart(thirdPartSignUpDto, loginSuccessDto);
                    if(checkUserDisable(loginSuccessDto.getUid())){
                    	return Response.failure(ResponseStatus.USER_ACCOUNT_DISABLED.status, ResponseStatus.USER_ACCOUNT_DISABLED.message);
                    }
                    return Response.success(ResponseStatus.USER_LOGIN_SUCCESS.status, ResponseStatus.USER_LOGIN_SUCCESS.message, loginSuccessDto);
                }

            }
            else{
                if (users !=null) {
                    ResponseThirdPartUser(users ,loginSuccessDto);
                    if(checkUserDisable(loginSuccessDto.getUid())){
                    	return Response.failure(ResponseStatus.USER_ACCOUNT_DISABLED.status, ResponseStatus.USER_ACCOUNT_DISABLED.message);
                    }
                    this.saveUserDeviceInfo(users.getUid(), thirdPartSignUpDto.getIp(), 2, thirdPartSignUpDto.getDeviceData());
                    return Response.success(ResponseStatus.USER_EXISTS.status, ResponseStatus.USER_EXISTS.message, loginSuccessDto);
                } else {
                    buildThirdPart(thirdPartSignUpDto, loginSuccessDto);
                }
                if(checkUserDisable(loginSuccessDto.getUid())){
                	return Response.failure(ResponseStatus.USER_ACCOUNT_DISABLED.status, ResponseStatus.USER_ACCOUNT_DISABLED.message);
                }
                return Response.success(ResponseStatus.USER_LOGIN_SUCCESS.status, ResponseStatus.USER_LOGIN_SUCCESS.message, loginSuccessDto);
            }

        }

    }
    
    private void activityH5RegisterUser(UserProfile userProfile){
    	if(userProfile.getIsActivate() != 0){//不需要激活
    		return;
    	}
    	//开始激活逻辑
    	userProfile.setIsActivate(1);
    	userMybatisDao.modifyUserProfile(userProfile);
    	
    	if(userProfile.getRefereeUid() <= 0){//不存在邀请
    		return;
    	}
    	
    	//并且把奖励送上,保存邀请历史
    	int invitingCoins = 0;//邀请的奖励
        int invitedCoins = 0;//被邀请的奖励
        try{
        	String invitingCoinsStr = this.getAppConfigByKey("INVITING_COINS");
        	if(!StringUtils.isEmpty(invitingCoinsStr)){
        		invitingCoins = Integer.valueOf(invitingCoinsStr);
        		if(invitingCoins<0){
        			invitingCoins = 0;
        		}
        	}
        	String invitedCoinsStr = this.getAppConfigByKey("INVITED_COINS");
        	if(!StringUtils.isEmpty(invitedCoinsStr)){
        		invitedCoins = Integer.valueOf(invitedCoinsStr);
        		if(invitedCoins<0){
        			invitedCoins = 0;
        		}
        	}
        }catch(Exception e){
        	log.error("邀请或被邀请的配置错误", e);
        }
        //保存邀请奖励
        Date now = new Date();
        UserInvitationHis invitingHis = new UserInvitationHis();
        invitingHis.setCoins(invitingCoins);
        invitingHis.setCreateTime(now);
        invitingHis.setFromCid(userProfile.getSocialClass());
        invitingHis.setFromUid(userProfile.getUid());
        invitingHis.setStatus(0);
        invitingHis.setType(Specification.UserInvitationType.INVITING.index);
        invitingHis.setUid(userProfile.getRefereeUid());
        userMybatisDao.saveUserInvitationHis(invitingHis);
        
        //保存被邀请奖励
        UserInvitationHis invitedHis = new UserInvitationHis();
        invitedHis.setCoins(invitedCoins);
        invitedHis.setCreateTime(now);
        invitedHis.setFromCid(userProfile.getSocialClass());
        invitedHis.setFromUid(userProfile.getRefereeUid());
        invitedHis.setStatus(0);
        invitedHis.setType(Specification.UserInvitationType.INVITED.index);
        invitedHis.setUid(userProfile.getUid());
        userMybatisDao.saveUserInvitationHis(invitedHis);
    }
    
    /**
     * 保存设备相关信息
     * @param uid
     * @param ip
     * @param type  1注册 2登录
     * @param deviceDataJson
     */
    private void saveUserDeviceInfo(long uid, String ip, int type, String deviceDataJson){
    	UserDeviceInfo udi = new UserDeviceInfo();
		udi.setCreateTime(new Date());
		udi.setUid(uid);
		udi.setIp(ip);
		udi.setType(type);
        if(!StringUtils.isEmpty(deviceDataJson)){
        	JSONObject deviceData = null;
        	try{
        		deviceData = JSONObject.parseObject(deviceDataJson);
        	}catch(Exception e){
        		log.error("json处理异常", e);
        	}
        	if(null != deviceData){
        		
        		if(null != deviceData.get("deviceCode")){
        			udi.setDeviceCode(deviceData.getString("deviceCode"));
        		}
        		if(null != deviceData.get("mobileModel")){
        			udi.setMobileModel(deviceData.getString("mobileModel"));
        		}
        		if(null != deviceData.get("systemVersion")){
        			udi.setSystemVersion(deviceData.getString("systemVersion"));
        		}
        	}
        }
        userMybatisDao.saveUserDeviceInfo(udi);
    }
    
    private boolean checkUserDisable(long uid){
    	User user = userMybatisDao.getUserByUid(uid);
    	if(null != user && user.getDisableUser() == 1){
    		return true;
    	}
    	return false;
    }

    public void ResponseThirdPartUser(ThirdPartUser users,LoginSuccessDto loginSuccessDto){
        long uid = users.getUid();
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
        UserToken userToken = userMybatisDao.getUserTokenByUid(uid);
        loginSuccessDto.setUid(userProfile.getUid());
        loginSuccessDto.setGender(userProfile.getGender());
        loginSuccessDto.setNickName(userProfile.getNickName());
        loginSuccessDto.setToken(userToken.getToken());
        
        this.activityH5RegisterUser(userProfile);
    }

    //第三方登录公共方法
    public void buildThirdPart(ThirdPartSignUpDto thirdPartSignUpDto ,LoginSuccessDto loginSuccessDto) {
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
        // 第三方数据统计
        spreadChannelCount(thirdPartSignUpDto.getParams(),thirdPartSignUpDto.getSpreadChannel(),user);

        //IM新用户获得token
        try {
            ImUserInfoDto imUserInfoDto = smsService.getIMUsertoken(user.getUid(),thirdPartSignUpDto.getNickName(),Constant.QINIU_DOMAIN + "/" + thirdPartSignUpDto.getAvatar());
            if(imUserInfoDto != null){
                ImConfig imConfig = new ImConfig();
                imConfig.setUid(user.getUid());
                imConfig.setToken(imUserInfoDto.getToken());
                userMybatisDao.createImConfig(imConfig);
                log.info("create IM Config success");
            }
        } catch (Exception e) {
            log.error("get im token failure");
        }

        log.info("get user by username");
        User user1 = userMybatisDao.getUserByUserName(thirdPartSignUpDto.getThirdPartOpenId());
        UserProfile userProfile = new UserProfile();
        userProfile.setUid(user1.getUid());
        userProfile.setAvatar(thirdPartSignUpDto.getAvatar());
        //将用户名后面加随机大写英文字母 , 直至 不重复;
        boolean flag = true ;
        while(flag){
            if(!this.existsNickName(thirdPartSignUpDto.getNickName())){
                String str="";
                for(int i=0;i<1;i++){//你想生成几个字符的，就把3改成几，如果改成１,那就生成一个随机字母．
                    str= str+(char) (Math.random ()*26+'A');
                }
                thirdPartSignUpDto.setNickName(thirdPartSignUpDto.getNickName()+str);

            }else{
                flag = false ;
            }
        }
        userProfile.setNickName(thirdPartSignUpDto.getNickName());
        userProfile.setAvatar(thirdPartSignUpDto.getAvatar());
        userProfile.setGender(thirdPartSignUpDto.getGender());
        //生日默认给一个不可能的值
        userProfile.setBirthday("1800-1-1");
        userProfile.setCreateTime(new Date());
        userProfile.setChannel(thirdPartSignUpDto.getChannel());
        userProfile.setPlatform(thirdPartSignUpDto.getPlatform());
        userProfile.setRegisterVersion(thirdPartSignUpDto.getRegisterVersion());
        //第三方h5微信登录首次登录设置为1 下一次app登录的时候如果是1 app弹出修改昵称页面 0为不需要修改(默认为0)
        if(thirdPartSignUpDto.getH5type() ==1){
            userProfile.setIsClientLogin(1);
        }
        if(thirdPartSignUpDto.getThirdPartOpenId().length() > 11) {
            String openId = thirdPartSignUpDto.getThirdPartOpenId();
            userProfile.setMobile(openId.substring(0,11));
        }else{
            userProfile.setMobile(thirdPartSignUpDto.getThirdPartOpenId());
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
        
        if(thirdPartSignUpDto.getThirdPartType() == Specification.ThirdPartType.WEIXIN.index && thirdPartSignUpDto.getH5type() == 1){
        	//如果是微信注册，并且是H5上来注册的，则需要设置推广人，并且为未激活
        	if(thirdPartSignUpDto.getFromUid() > 0){
        		userProfile.setRefereeUid(thirdPartSignUpDto.getFromUid());
        		userProfile.setSocialClass(thirdPartSignUpDto.getFromTopicId());
        		userProfile.setIsActivate(0);//未激活
        		//这里没有奖励，要等到该用户在APP上用本微信账号登录后，才能算激活，并且才能有奖励
        	}
        }else{
        	//App登录
            this.processReferee(thirdPartSignUpDto.getOpeninstallData(), user1.getUid(), userProfile);
        }
        
        userMybatisDao.createUserProfile(userProfile);
        log.info("UserProfile is create");

        //添加设备相关信息new
        this.saveUserDeviceInfo(userProfile.getUid(), thirdPartSignUpDto.getIp(), 1, thirdPartSignUpDto.getDeviceData());
        
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
        loginSuccessDto.setIsClientLogin(userProfile1.getIsClientLogin());
        loginSuccessDto.setIsNew(1);

        if(thirdPartSignUpDto.getH5type() ==1) {//h5微信注册openid不存数据库
            ThirdPartUser thirdPartUser = new ThirdPartUser();
            thirdPartUser.setUid(user1.getUid());
            thirdPartUser.setThirdPartToken(thirdPartSignUpDto.getThirdPartToken());
            thirdPartUser.setThirdPartUnionid(thirdPartSignUpDto.getUnionId());
            thirdPartUser.setCreateTime(new Date());
            thirdPartUser.setThirdPartType(thirdPartSignUpDto.getThirdPartType());
            userMybatisDao.creatThirdPartUser(thirdPartUser);
            log.info("ThirdPartUser is create");
        }else{//app都正常存 openid 和 unionId都存数据库
            ThirdPartUser thirdPartUser = new ThirdPartUser();
            thirdPartUser.setUid(user1.getUid());
            thirdPartUser.setThirdPartToken(thirdPartSignUpDto.getThirdPartToken());
            thirdPartUser.setThirdPartOpenId(thirdPartSignUpDto.getThirdPartOpenId());
            thirdPartUser.setThirdPartUnionid(thirdPartSignUpDto.getUnionId());
            thirdPartUser.setCreateTime(new Date());
            thirdPartUser.setThirdPartType(thirdPartSignUpDto.getThirdPartType());
            userMybatisDao.creatThirdPartUser(thirdPartUser);
            log.info("ThirdPartUser is create");
        }

        //添加默认关注v2.1.4
        defaultFollow(user1.getUid());
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
        //不管你爽不爽，就是要卖你3个王国
        give3Kingdoms(userProfile);

        //新用户注册放入cach,机器人自动回复用
        String key = KeysManager.SEVEN_DAY_REGISTER_PREFIX+user.getUid().toString();
        cacheService.setex(key,user.getUid().toString(),7*24*60*60);
    }

    //第三方绑定方法
    public void buildThirdPart2(ThirdPartSignUpDto thirdPartSignUpDto){

        ThirdPartUser thirdPartUser = new ThirdPartUser();
        thirdPartUser.setUid(thirdPartSignUpDto.getUid());
        thirdPartUser.setThirdPartToken(thirdPartSignUpDto.getThirdPartToken());
        thirdPartUser.setThirdPartOpenId(thirdPartSignUpDto.getThirdPartOpenId());
        thirdPartUser.setThirdPartUnionid(thirdPartSignUpDto.getUnionId());
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
        if(userNickNameDto.getThirdPartType() == 2){//兼容新老版本 老版本是没有这个字段的 不会走这里(只有微信会传unionId)
            if(!StringUtils.isEmpty(userNickNameDto.getOpenid()) && !StringUtils.isEmpty(userNickNameDto.getUnionId())){
                ThirdPartUser thirdPartUserOpenId = userMybatisDao.checkOpenId(userNickNameDto.getOpenid());
                ThirdPartUser thirdPartUserUnionId = userMybatisDao.checkUnionId(userNickNameDto.getUnionId());
                if(thirdPartUserOpenId != null && thirdPartUserUnionId != null) {//同一用户
                   UserProfile userProfile = userMybatisDao.getUserProfileByUid(thirdPartUserUnionId.getUid());
                    if(userProfile.getIsClientLogin() == 1) {//h5登录过需要检查昵称
                        UserProfile4H5Dto Dto = new UserProfile4H5Dto();
                        Dto.setNickName(userProfile.getNickName());
                        return Response.success(ResponseStatus.THIRDPARTUSER_EXISTS.status,ResponseStatus.THIRDPARTUSER_EXISTS.message,Dto);
                    }
                    return Response.success(ResponseStatus.USER_EXISTS.status,ResponseStatus.USER_EXISTS.message);
                }else if(thirdPartUserOpenId != null && thirdPartUserUnionId == null){//app老用户登陆过 返回注册过 h5没登陆过不需要检查 老用户检查过昵称的
                    return Response.success(ResponseStatus.USER_EXISTS.status,ResponseStatus.USER_EXISTS.message);
                }else if(thirdPartUserOpenId == null && thirdPartUserUnionId !=null){
                    UserProfile userProfile = userMybatisDao.getUserProfileByUid(thirdPartUserUnionId.getUid());
                    if(userProfile.getIsClientLogin() == 1) {//h5登录过需要检查昵称
                        UserProfile4H5Dto Dto = new UserProfile4H5Dto();
                        Dto.setNickName(userProfile.getNickName());
                        return Response.success(ResponseStatus.THIRDPARTUSER_EXISTS.status,ResponseStatus.THIRDPARTUSER_EXISTS.message,Dto);
                    }
                    return Response.success(ResponseStatus.USER_EXISTS.status,ResponseStatus.USER_EXISTS.message);
                }else {//都为空的情况下提示不存在 无该用户
                    return Response.success(ResponseStatus.OPENID_DONT_EXISTS.status, ResponseStatus.OPENID_DONT_EXISTS.message);
                }
            }
            else{//以防出错没传openId和unionId报错
                return Response.success(ResponseStatus.OPENID_DONT_EXISTS.status,ResponseStatus.OPENID_DONT_EXISTS.message);
            }
        }else if(!StringUtils.isEmpty(userNickNameDto.getOpenid())) {// qq weibo weixin 老版本
            ThirdPartUser thirdPartUser = userMybatisDao.checkOpenId(userNickNameDto.getOpenid());
            if(thirdPartUser!=null) {
                return Response.success(ResponseStatus.USER_EXISTS.status,ResponseStatus.USER_EXISTS.message);
            }else{
                return  Response.success(ResponseStatus.OPENID_DONT_EXISTS.status,ResponseStatus.OPENID_DONT_EXISTS.message);
            }
        }else{
            String nickName = userNickNameDto.getNickName();
            List<UserProfile> list = userMybatisDao.checkUserNickName(nickName);
            //兼容emoji 这样判断才可以
            if(list != null && list.size() > 0) {
                for(UserProfile userProfile : list){
                    if (nickName.equals(userProfile.getNickName())) {
                        return Response.failure(ResponseStatus.USER_NICKNAME_EXISTS.status, ResponseStatus.USER_NICKNAME_EXISTS.message);
                    }
                }
            }
//            if(list.size()>0){
//                return Response.failure(ResponseStatus.USER_NICKNAME_EXISTS.status,ResponseStatus.USER_NICKNAME_EXISTS.message);
//            }
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

            //检测手机通讯录推送V2.2.5
            ContactsMobilePushEvent event = new ContactsMobilePushEvent();
            event.setUid(user.getUid());
            event.setMobile(thirdPartSignUpDto.getMobile());
            this.applicationEventBus.post(event);
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
                if(!StringUtils.isEmpty(thirdPartSignUpDto.getUnionId())){//新版本
                	List<ThirdPartUser> thirdPartUser = userMybatisDao.getThirdPartUserByUnionId(thirdPartSignUpDto.getUnionId(),thirdPartSignUpDto.getThirdPartType());
                	if(null != thirdPartUser && thirdPartUser.size() > 0){
                		return Response.success(ResponseStatus.WEIXIN_BIND_EXISTS.status,ResponseStatus.WEIXIN_BIND_EXISTS.message);
                	}
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

    @Override
    public Response addV(UserVDto userVDto) {
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(userVDto.getCustomerId());
        //判断是否是大V
        if(userProfile.getvLv() == Specification.VipLevel.isV.index){
            return Response.success(ResponseStatus.USER_V_EXISTS.status,ResponseStatus.USER_V_EXISTS.message);
        }
        userProfile.setvLv(Specification.VipLevel.isV.index);
        userMybatisDao.modifyUserProfile(userProfile);
        return Response.success();
    }

    @Override
    public List<UserProfile> getUserProfilesByUids(List<Long> uids) {
    	if(null == uids || uids.size() == 0){
    		return new ArrayList<UserProfile>();
    	}
        return userMybatisDao.getUserProfilesByUids(uids);
    }

    @Override
    public Response gag(GagDto dto) {
        log.info("gag start ... request:"+JSON.toJSONString(dto));
        UserGag gag = (UserGag) CommonUtils.copyDto(dto, new UserGag());
        switch (dto.getType()) {
            case 0:
                if (!isAdmin(dto.getUid())) {
                    return Response.failure(ResponseStatus.GAG_IS_NOT_ADMIN.status, ResponseStatus.GAG_IS_NOT_ADMIN.message);
                }
                break;
            case 1:
                long king = liveForUserJdbcDao.getKingFromTopic(dto.getCid());
                if (dto.getUid() != king) {
                    return Response.failure(ResponseStatus.GAG_IS_NOT_KING.status, ResponseStatus.GAG_IS_NOT_KING.message);
                }
                break;
            case 2:
                long author = ugcForUserJdbcDao.getAuthorFromUgc(dto.getCid());
                if(dto.getUid()!=author){
                    return Response.failure(ResponseStatus.GAG_IS_NOT_AUTHOR.status,ResponseStatus.GAG_IS_NOT_AUTHOR.message);
                }
                break;
        }
        //action==0禁言，1解除禁言
        if (dto.getAction() == 0) {
            UserGag userGag = userMybatisDao.getUserGag(gag);
            if(userGag!=null){
                return Response.failure(ResponseStatus.USER_HAS_GAGGED.status,ResponseStatus.USER_HAS_GAGGED.message);
            }
            userMybatisDao.createGag(gag);
        } else {
            userMybatisDao.deleteGag(gag);
        }

        log.info("gag end ...");
        return Response.success();
    }

    @Override
    public boolean checkGag(UserGag gag) {

        return userMybatisDao.checkGag(gag);
    }

    @Override
    public SystemConfig getSystemConfig() {
       SystemConfig systemConfig =  userMybatisDao.getSystemConfig();
        return systemConfig;
    }

    @Override
    public boolean isAdmin(long uid) {
        Set<String> admins = cacheService.smembers("power:key");
        return admins.contains(uid+"");
    }

    @Override
    public Response getEntryPageConfig(EntryPageDto dto) {
        log.info("get entry_page_config start ...");
        List<EntryPageConfig> list =  userMybatisDao.getEntryPageConfig(dto);
        int maxVersion=dto.getCversion();
        EntryPageReturnDto returnDto  = new EntryPageReturnDto();
        EntryPageReturnDto.EntryPageElement element;
        for(EntryPageConfig config : list){
            element = (EntryPageReturnDto.EntryPageElement) CommonUtils.copyDto(config,returnDto.createElement());
            returnDto.getEntryPageElements().add(element);
            if(config.getCversion()>maxVersion){
                maxVersion = config.getCversion();
            }
        }

        returnDto.setCversion(maxVersion);
        log.info("get entry_page_config end ...");
        return Response.success(returnDto);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Response searchUserPage(String nickName, String mobile, int vLv, int status, String startTime, String endTime, long meCode, int page, int pageSize){
    	int start = (page-1)*pageSize;
    	List<Map<String, Object>> list = userInitJdbcDao.searchUserProfilesByPage(nickName, mobile, vLv, status, startTime, endTime, meCode, start, pageSize);
        SearchUserProfileDto dto = new SearchUserProfileDto();
        dto.setTotalRecord(userInitJdbcDao.countUserProfilesByPage(nickName, mobile, vLv, status, startTime, endTime));
        int totalPage = (dto.getTotalRecord() + pageSize - 1) / pageSize;
        dto.setTotalPage(totalPage);
        for(Map<String, Object> map : list){
            SearchUserProfileDto.UserProfileElement e = dto.createUserProfileElement();
            e.setAvatar(Constant.QINIU_DOMAIN + "/" + map.get("avatar"));
            e.setCreateTime((Date)map.get("create_time"));
            e.setGender((Integer)map.get("gender"));
            e.setMobile((String)map.get("mobile"));
            e.setNickName((String)map.get("nick_name"));
            e.setThirdPartBind((String)map.get("third_part_bind"));
            e.setUid((Long)map.get("uid"));
            e.setVlv((Integer)map.get("v_lv"));
            e.setBirthday((String)map.get("birthday"));
            e.setStatus((Integer)map.get("disable_user"));
            e.setMeCode((Long)map.get("me_number"));
            e.setLevel((Integer)map.get("level"));
            dto.getResult().add(e);
        }
        return Response.success(dto);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Response optionV(int action, long uid) {
        UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
        if (null != userProfile) {
            if (action == 1) {//上大V
                userProfile.setvLv(Specification.VipLevel.isV.index);
            } else {
                userProfile.setvLv(Specification.VipLevel.noV.index);
            }
            userMybatisDao.modifyUserProfile(userProfile);
            return Response.success();
        } else {
            return Response.failure("user is not exists");
        }
    }
    
    @Override
    public void modifyUserLevel(long uid, int level){
    	UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
    	if(null != userProfile){
    		userProfile.setLevel(level);
    		userMybatisDao.modifyUserProfile(userProfile);
    	}
    }

    @SuppressWarnings("rawtypes")
	@Override
	public Response getVersionList(String version, int platform) {
		List<VersionControl> list = userMybatisDao.getVersionListByVersionAndPlatform(version, platform);
		ShowVersionControlDto dto = new ShowVersionControlDto();
		if(null != list && list.size() > 0){
			VersionControlDto vcdto = null;
			for(VersionControl vc : list){
				vcdto = new VersionControlDto();
				vcdto.setId(vc.getId());
				vcdto.setIsUpdate(vc.getForceUpdate());
				vcdto.setPlatform(vc.getPlatform());
				vcdto.setUpdateDescription(vc.getUpdateDescription());
				vcdto.setUpdateTime(vc.getUpdateTime());
				vcdto.setUpdateUrl(vc.getUpdateUrl());
				vcdto.setVersion(vc.getVersion());
				dto.getResult().add(vcdto);
			}
		}
		return Response.success(dto);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Response getVersionById(long id) {
		VersionControl vc = userMybatisDao.getVersionById(id);
		VersionControlDto vcdto = new VersionControlDto();
		if(null != vc){
			vcdto.setId(vc.getId());
			vcdto.setIsUpdate(vc.getForceUpdate());
			vcdto.setPlatform(vc.getPlatform());
			vcdto.setUpdateDescription(vc.getUpdateDescription());
			vcdto.setUpdateTime(vc.getUpdateTime());
			vcdto.setUpdateUrl(vc.getUpdateUrl());
			vcdto.setVersion(vc.getVersion());
		}
		return Response.success(vcdto);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Response saveOrUpdateVersion(VersionControlDto dto) {
		VersionControl vc = new VersionControl();
		if(dto.getId() > 0){
			vc.setId(dto.getId());
		}
		vc.setForceUpdate(dto.getIsUpdate());
		vc.setPlatform(dto.getPlatform());
		vc.setUpdateDescription(dto.getUpdateDescription());
		vc.setUpdateTime(new Date());
		vc.setUpdateUrl(dto.getUpdateUrl());
		vc.setVersion(dto.getVersion());
		userMybatisDao.saveOrUpdateVersion(vc);
		
		return Response.success();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Response getVersion(String version, int platform) {
		VersionControl vc = userMybatisDao.getVersion(version, platform);
		if(null != vc){
			VersionControlDto vcdto = new VersionControlDto();
			vcdto.setId(vc.getId());
			vcdto.setIsUpdate(vc.getForceUpdate());
			vcdto.setPlatform(vc.getPlatform());
			vcdto.setUpdateDescription(vc.getUpdateDescription());
			vcdto.setUpdateTime(vc.getUpdateTime());
			vcdto.setUpdateUrl(vc.getUpdateUrl());
			vcdto.setVersion(vc.getVersion());
			return Response.success(vcdto);
		}
		
		return Response.failure("版本不存在");
	}

	@Override
	public Response getGagUserPageByTargetUid(long targetUid, int page, int pageSize) {
		if(page < 1){
			page = 0;
		}
		if(pageSize < 1){
			pageSize = 10;
		}
		ShowUsergagDto dto = new ShowUsergagDto();
		dto.setTotalRecord(userMybatisDao.countGagUserPageByTargetUid(targetUid));
        int totalPage = (dto.getTotalRecord() + pageSize - 1) / pageSize;
        dto.setTotalPage(totalPage);
		List<UserGag> list = userMybatisDao.getGagUserPageByTargetUid(targetUid, page, pageSize);
		if(null != list && list.size() > 0){
			List<Long> uidList = new ArrayList<Long>();
			for(UserGag ug : list){
				if(ug.getUid() > 0){
					uidList.add(ug.getUid());
				}
				uidList.add(ug.getTargetUid());
			}
			Map<String, String> map = new HashMap<String, String>();
			if(uidList.size() > 0){
				List<UserProfile> ulist = userMybatisDao.getUserProfilesByUids(uidList);
				if(null != ulist && ulist.size() > 0){
					for(UserProfile u : ulist){
						map.put(String.valueOf(u.getUid()), u.getNickName());
					}
				}
			}
			
			for(UserGag ug : list){
				ShowUsergagDto.UsergagElement e = ShowUsergagDto.createUsergagElement();
				e.setCid(ug.getCid());
				e.setGagLevel(ug.getGagLevel());
				e.setId(ug.getId());
				e.setTargetUid(ug.getTargetUid());
				e.setType(ug.getType());
				e.setUid(ug.getUid());
				e.setTargetUserName(map.get(String.valueOf(ug.getTargetUid())));
				e.setUserName(map.get(String.valueOf(ug.getUid())));
				dto.getResult().add(e);
			}
		}
		
		return Response.success(dto);
	}

	@Override
	public Response deleteGagUserById(long id) {
		userMybatisDao.deleteGagUserById(id);
		return Response.success();
	}

	@Override
	public Response addGagUser(UserGag gag) {
		if(!userMybatisDao.checkGag(gag)){
			userMybatisDao.createGag(gag);
		}
		
		return Response.success();
	}

	@Override
	public Response updateSystemConfig(SystemConfig config) {
		userMybatisDao.updateSystemConfig(config);
		return Response.success();
	}

    @Override
    public Response touristLogin() {
        SignUpSuccessDto signUpSuccessDto = new SignUpSuccessDto();
        User user = new User();
        String salt = SecurityUtils.getMask();
        user.setEncrypt(SecurityUtils.md5("",salt));
        user.setSalt(salt);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setStatus(Specification.UserStatus.NORMAL.index);
        //游客设定为1
        user.setTouristtype(Specification.UserStatus.LOCK.index);
        userMybatisDao.createUser(user);

        // 保存用户token信息
        UserToken userToken = new UserToken();
        userToken.setUid(user.getUid());
        userToken.setToken(SecurityUtils.getToken());
        userMybatisDao.createUserToken(userToken);
        signUpSuccessDto.setUid(userToken.getUid());
        signUpSuccessDto.setToken(userToken.getToken());
        return Response.success(ResponseStatus.TOURIST_LOGIN_SUCCESS.status,ResponseStatus.TOURIST_LOGIN_SUCCESS.message,signUpSuccessDto);

    }

	@Override
	public Response optionDisableUser(int action, long uid) {
		User user = userMybatisDao.getUserByUidPrimaryKey(uid);
		if(null != user){
			if(action == 1){//禁止
				//先将用户请求置失效。。重置用户token
				UserToken userToken = userMybatisDao.getUserTokenByUid(uid);
				userToken.setToken(SecurityUtils.getToken());
				userMybatisDao.updateUserToken(userToken);
				//将用户状态置成失效状态
				user.setDisableUser(1);
				userMybatisDao.modifyUser(user);
			}else{//恢复
				//只要将用户状态放开就行了
				user.setDisableUser(0);
				userMybatisDao.modifyUser(user);
			}
			return Response.success();
		}else{
			return Response.failure("user is not exists");
		}
	}

	@Override
	public Response testPush(long uid, String msg, String jsonData) {
		JSONObject obj = JSON.parseObject(jsonData);
		Map<String, String> map = Maps.newHashMap();
		Set<Map.Entry<String, Object>> set = obj.entrySet();
		for(Map.Entry<String, Object> entry : set){
			map.put(entry.getKey(), entry.getValue().toString().replace("\"",""));
		}

        String alias = String.valueOf(uid);

        jPushService.payloadByIdExtra(alias,  msg, map);
		return Response.success();
	}
	
	@Override
	public List<Long> getAllUids(){
		return oldUserJdbcDao.getAllUids();
	}

	@Override
	public void updateUserSex(long uid, int sex) {
		UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
		if(null != userProfile){
			if(userProfile.getGender() != sex){
				userProfile.setGender(sex);
				userMybatisDao.updateUserProfile(userProfile);
			}
		}
	}

	@Override
	public void noticeCountPush(long uid){
		NoticeCountPushEvent event = new NoticeCountPushEvent();
		event.setUid(uid);
		this.applicationEventBus.post(event);
	}
	
	@Override
	public void noticeMessagePush(long targetUid, String message, int level){
		NoticeMessagePushEvent event = new NoticeMessagePushEvent();
		event.setLevel(level);
		event.setMessage(message);
		event.setTargetUid(targetUid);
		this.applicationEventBus.post(event);
	}

	@Override
	public void pushWithExtra(String uid,String message,Map<String,String> extraMaps){
		PushExtraEvent event = new PushExtraEvent();
		event.setUid(uid);
		event.setMessage(message);
		event.setExtraMaps(extraMaps);
		this.applicationEventBus.post(event);
	}

    @Override
    public Response userRecomm(long uid ,long targetUid ,int action) {
        if(isAdmin(uid)) {
            UserFamous userFamous = userMybatisDao.getUserFamousByUid(targetUid);
            if (action == 0) {
                //推荐
                if (userFamous != null) {
                    userFamous.setUpdateTime(new Date());
                    userMybatisDao.updateUserFamous(userFamous);
                } else {
                    UserFamous newUserFamous = new UserFamous();
                    newUserFamous.setUid(targetUid);
                    newUserFamous.setUpdateTime(new Date());
                    userMybatisDao.createUserFamous(newUserFamous);
                }
            } else if (action == 1) {
                //取消
                if (userFamous != null) {
                    userMybatisDao.deleteUserFamous(targetUid);
                } else {
                    return Response.failure("数据不存在");
                }
            }
            return Response.success(200, "操作成功");
        }

        return Response.failure(ResponseStatus.YOU_ARE_NOT_ADMIN.status ,ResponseStatus.YOU_ARE_NOT_ADMIN.message);

    }
    
    @Override
    public List<UserFamous> getUserFamousPage(int page, int pageSize, List<Long> blacklistUids){
    	if(page <= 0){
    		page = 1;
    	}
    	if(pageSize <= 0){
    		pageSize = 20;
    	}
    	int start = (page-1)*pageSize;
    	
    	return userMybatisDao.getUserFamousList(start, pageSize, blacklistUids);
    }
    
    @Override
    public boolean isUserFamous(long uid){
    	UserFamous userFamous = userMybatisDao.getUserFamousByUid(uid);
    	if(null != userFamous){
    		return true;
    	}
    	return false;
    }

    @Override
    public ResponseWapx iosWapxUserRegist(WapxIosDto dto) {
        IosWapx iosWapx = userMybatisDao.getWapxByIdfa(dto.getIdfa() ,0);
        IosWapx wapx = new IosWapx();
        BeanUtils.copyProperties(dto ,wapx);
        if(iosWapx != null){
            //未激活 更新
            wapx.setId(iosWapx.getId());
            if(iosWapx.getStatus() == 0){
                userMybatisDao.updateWapx(wapx);
                log.info("because status 0 so update wapx");
            }
        }else {
            userMybatisDao.createWapx(wapx);
            log.info("create wapx success");
        }
        return ResponseWapx.success("成功",true);
    }
    
    @Override
    public List<VersionChannelDownload> queryVersionChannelDownloads(String channel){
    	return userMybatisDao.queryVersionChannelDownloads(channel);
    }
    
    @Override
    public VersionChannelDownload getVersionChannelDownloadByChannel(String channel){
    	return userMybatisDao.getVersionChannelDownloadByChannel(channel);
    }
    
    public void saveVersionChannelDownload(VersionChannelDownload vcd){
    	userMybatisDao.saveVersionChannelDownload(vcd);
    }
    
    public VersionChannelDownload getVersionChannelDownloadById(long id){
    	return userMybatisDao.getVersionChannelDownloadById(id);
    }
    
    public void updateVersionChannelDownload(VersionChannelDownload vcd){
    	userMybatisDao.updateVersionChannelDownload(vcd);
    }
    
    public void deleteVersionChannelDownload(long id){
    	userMybatisDao.deleteVersionChannelDownload(id);
    }

    @Override
    public int spreadCheckUnique(int spreadChannel, String idfa) {
        return userMybatisDao.spreadIdfaExists(spreadChannel,idfa)?1:0;
    }

    @Override
    public Response click(int type, DaoDaoDto daoDaoDto) {
        IosWapx iosWapx = userMybatisDao.getWapxByIdfa(daoDaoDto.getIdfa() ,type);
        if(iosWapx == null){
            IosWapx daodao = new IosWapx();
            daodao.setChannelTyp(type);
            daodao.setApp(String.valueOf(daoDaoDto.getAppid()));
            daodao.setCallbackurl(daoDaoDto.getCallback());
            daodao.setIp(daoDaoDto.getIp());
            daodao.setIdfa(daoDaoDto.getIdfa());
            daodao.setStatus(0);
            daodao.setUid(0l);
            userMybatisDao.createWapx(daodao);
            log.info("create daodao success");
        }else {
            return Response.failure("数据已存在");
        }
        return Response.success();
    }

    @Override
    public Response getIMUsertoken(long customerId) {
        ImUserInfoDto dto = new ImUserInfoDto();
        ImConfig imConfig = userMybatisDao.getImConfig(customerId);
        if(imConfig != null){
            dto.setToken(imConfig.getToken());
            dto.setUserId(String.valueOf(customerId));
            return Response.success(dto);
        }
        return Response.failure(ResponseStatus.QI_QUERY_FAILURE.message);
    }

    @Override
    public Response registAllIMtoken() {
    	log.info("im register all user start...");
        List<User> userList = userMybatisDao.getAllUser();
        if(userList.size() > 0 && userList != null){
        	log.info("total " + userList.size() + " users!");
        	int count = 0;
            for(User user : userList){
            	count++;
                try {
                    ImConfig imConfig = userMybatisDao.getImConfig(user.getUid());
                    if(imConfig == null) {
                    	UserProfile userProfile = userMybatisDao.getUserProfileByUid(user.getUid());
                    	if(userProfile!=null){
                            ImUserInfoDto imUserInfoDto = smsService.getIMUsertoken(user.getUid(),userProfile.getNickName(),Constant.QINIU_DOMAIN + "/" + userProfile.getAvatar());
                            ImConfig im = new ImConfig();
                            im.setToken(imUserInfoDto.getToken());
                            im.setUid(Long.valueOf(imUserInfoDto.getUserId()));
                            userMybatisDao.createImConfig(im);
                    	}
                    }
                } catch (Exception e) {
                    return Response.failure("出错了");
                }
                if(count%1000 == 0){
                	log.info("process "+count+" users");
                }
            }
            if(count>0){
            	log.info("process "+count+" users");
            }
        }
        log.info("im register all user end!");
        return Response.success();
    }

    @Override
	public PageBean<SearchUserDto> searchUserPage(PageBean page, Map<String, Object> queries) {
		return userMybatisDao.searchUserPage(page,queries);
	}

	@Override
	public void countUserByDay() {
		userMybatisDao.countUserByDay();
	}

	@Override
	public Response mobileQuery(String mobiles, long uid){
		//先去通讯录除红点记录
		ContactsReddot cr = new ContactsReddot(uid, "1");
		cacheService.hDel(cr.getKey(), cr.getField());
		
		ShowMobileDTO result = new ShowMobileDTO();
		if(StringUtils.isEmpty(mobiles)){
			return Response.success(result);
		}
		List<String> mobileList = new ArrayList<String>();
		String[] tmp = mobiles.split(",");
		if(null != tmp && tmp.length > 0){
			for(String m : tmp){
				if(!StringUtils.isEmpty(m)){
					mobileList.add(m);
				}
			}
		}
		
		if(mobileList.size() > 0){
			List<UserProfile> userList = userMybatisDao.getUserProfilesByMobiles(mobileList);
			Map<String, UserProfile> userMap = new HashMap<String, UserProfile>();
			List<Long> uidList = new ArrayList<Long>();
			if(null != userList && userList.size() > 0){
				List<String> mobileUidList = new ArrayList<String>();
				for(UserProfile u : userList){
					userMap.put(u.getMobile(), u);
					uidList.add(u.getUid());
					
					if(!mobileUidList.contains(u.getMobile())){
						mobileUidList.add(u.getMobile());
					}
				}
				result.setTotalAppUser(mobileUidList.size());
			}
			
			//一次性查询关注信息
	        Map<String, String> followMap = new HashMap<String, String>();
			if(uidList.size() > 0){
		        List<UserFollow> userFollowList = userMybatisDao.getAllFollows(uid, uidList);
		        if(null != userFollowList && userFollowList.size() > 0){
		            for(UserFollow uf : userFollowList){
		                followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
		            }
		        }
			}
			
			ShowMobileDTO.MobileElement e = null;
			UserProfile user = null;
			for(String mobile : mobileList){
				e = new ShowMobileDTO.MobileElement();
				e.setMobile(mobile);
				user = userMap.get(mobile);
				if(null == user){
					e.setIsAppUser(0);
				}else{
					e.setIsAppUser(1);
					e.setUid(user.getUid());
					e.setNickName(user.getNickName());
					e.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
					if(!StringUtils.isEmpty(user.getAvatarFrame())){
						e.setAvatarFrame(Constant.QINIU_DOMAIN + "/" + user.getAvatarFrame());
					}
					e.setV_lv(user.getvLv());
					e.setIntroduced(user.getIntroduced());
					e.setLevel(user.getLevel());
					if(null != followMap.get(uid+"_"+user.getUid().toString())){
		                e.setIsFollowed(1);
		            }else{
		                e.setIsFollowed(0);
		            }
		            if(null != followMap.get(user.getUid().toString()+"_"+uid)){
		                e.setIsFollowMe(1);
		            }else{
		                e.setIsFollowMe(0);
		            }
				}
				result.getMobileContactData().add(e);
			}
			
			//保存用户通讯录
			ContactsMobileEvent event = new ContactsMobileEvent();
			event.setUid(uid);
			event.setMobileList(mobileList);
			this.applicationEventBus.post(event);
		}
		
		return Response.success(result);
	}
	
	@Override
	public Response contacts(int page, String mobiles, long uid){
		//先去通讯录除红点记录
		ContactsReddot cr = new ContactsReddot(uid, "1");
		cacheService.hDel(cr.getKey(), cr.getField());
		
		int pageSize = 20;
		if(page < 1){
			page = 1;
		}
		
		List<String> mobileList = new ArrayList<String>();
		List<UserSeekFollow> seekList = null;
		if(page == 1){//第一页，需要返回手机联系人 和 求关注列表
			//手机联系人电话
			if(!StringUtils.isEmpty(mobiles)){
				String[] tmp = mobiles.split(",");
				if(null != tmp && tmp.length > 0){
					for(String m : tmp){
						if(!StringUtils.isEmpty(m)){
							mobileList.add(m);
						}
					}
				}
			}
			//求关注列表
			seekList = userMybatisDao.getUserSeekFollows(uid, Long.MAX_VALUE, 10);
		}
		
		ShowContactsDTO result = new ShowContactsDTO();
		int total = userMybatisDao.getUserFollowCount(uid);
		result.setTotalPage(total%pageSize==0?(total/pageSize):((total/pageSize)+1));

		int start = (page-1)*pageSize;
		List<Map<String, Object>> followList = userInitJdbcDao.getUserFollowInfoPage(null, uid, start, pageSize);
		this.builderContactsResult(uid, result, mobileList, seekList, followList);
		return Response.success(result);
	}
	
	private void builderContactsResult(long uid, ShowContactsDTO result, List<String> mobileList, List<UserSeekFollow> seekList, List<Map<String, Object>> followList){
		List<Long> uidList = new ArrayList<Long>();
		if(null != seekList && seekList.size() > 0){
			for(UserSeekFollow usf : seekList){
				if(!uidList.contains(usf.getUid())){
					uidList.add(usf.getUid());
				}
			}
		}
		
		Map<String, UserProfile> userProfileMap = new HashMap<String, UserProfile>();
		if(uidList.size() > 0){
			List<UserProfile> userList = userMybatisDao.getUserProfilesByUids(uidList);
			if(null != userList && userList.size() > 0){
				for(UserProfile u : userList){
					userProfileMap.put(u.getUid().toString(), u);
				}
			}
		}
		
		Map<String, UserProfile> mobileUserMap = new HashMap<String, UserProfile>();
		if(null != mobileList && mobileList.size() > 0){
			List<UserProfile> mobileUserList = userMybatisDao.getUserProfilesByMobiles(mobileList);
			if(null != mobileUserList && mobileUserList.size() > 0){
				for(UserProfile u : mobileUserList){
					mobileUserMap.put(u.getMobile(), u);
					if(!uidList.contains(u.getUid())){
						uidList.add(u.getUid());
					}
				}
			}
		}
		if(null != followList && followList.size() > 0){
			Long fu = null;
			for(Map<String, Object> m : followList){
				fu = (Long)m.get("uid");
				if(!uidList.contains(fu)){
					uidList.add(fu);
				}
			}
		}
		//一次性查询关注信息
        Map<String, String> followMap = new HashMap<String, String>();
		if(uidList.size() > 0){
	        List<UserFollow> userFollowList = userMybatisDao.getAllFollows(uid, uidList);
	        if(null != userFollowList && userFollowList.size() > 0){
	            for(UserFollow uf : userFollowList){
	                followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
	            }
	        }
		}
		
		if(null != mobileList && mobileList.size() > 0){
			UserProfile user = null;
			ShowContactsDTO.MobileContactElement e = null;
			for(String mobile : mobileList){
				e = new ShowContactsDTO.MobileContactElement();
				e.setMobile(mobile);
				user = mobileUserMap.get(mobile);
				if(null == user){
					e.setIsAppUser(0);
				}else{
					e.setIsAppUser(1);
					e.setUid(user.getUid());
					e.setNickName(user.getNickName());
					e.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
					e.setV_lv(user.getvLv());
					e.setLevel(user.getLevel());
					e.setIntroduced(user.getIntroduced());
					if(!StringUtils.isEmpty(user.getAvatarFrame())){
						e.setAvatarFrame(Constant.QINIU_DOMAIN + "/" + user.getAvatarFrame());
					}
					if(null != followMap.get(uid+"_"+user.getUid().toString())){
		                e.setIsFollowed(1);
		            }else{
		                e.setIsFollowed(0);
		            }
		            if(null != followMap.get(user.getUid().toString()+"_"+uid)){
		                e.setIsFollowMe(1);
		            }else{
		                e.setIsFollowMe(0);
		            }
				}
				result.getMobileContactData().add(e);
			}
		}
		if(null != seekList && seekList.size() > 0){
			ShowContactsDTO.SeekFollowElement e = null;
			UserProfile user = null;
			 for (int i = 0; i < seekList.size(); i++) {
					UserSeekFollow usf = seekList.get(i);
				e = new ShowContactsDTO.SeekFollowElement();
				user = userProfileMap.get(usf.getUid().toString());
				if(null == user){
					continue;
				}
				e.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
				e.setIntroduced(user.getIntroduced());
				if(null != followMap.get(uid+"_"+user.getUid().toString())){
	                e.setIsFollowed(1);
	                seekList.remove(i);
	                i--;
	                continue;
	            }else{
	                e.setIsFollowed(0);
	            }
	            if(null != followMap.get(user.getUid().toString()+"_"+uid)){
	                e.setIsFollowMe(1);
	            }else{
	                e.setIsFollowMe(0);
	            }
				e.setNickName(user.getNickName());
				e.setUid(user.getUid());
				e.setV_lv(user.getvLv());
				e.setLevel(user.getLevel());
				if(!StringUtils.isEmpty(user.getAvatarFrame())){
					e.setAvatarFrame(Constant.QINIU_DOMAIN + "/" + user.getAvatarFrame());
				}
				result.getSeekFollowData().add(e);
			}
		}

		if(null != followList && followList.size() > 0){
			ShowContactsDTO.MyFollowElement e = null;
			for(Map<String, Object> followUser : followList){
				e = new ShowContactsDTO.MyFollowElement();
				e.setAvatar(Constant.QINIU_DOMAIN + "/" + (String)followUser.get("avatar"));
				e.setGroup((String)followUser.get("name_group"));
				e.setIntroduced((String)followUser.get("introduced"));
				e.setNickName((String)followUser.get("nick_name"));
				e.setUid((Long)followUser.get("uid"));
				e.setV_lv((Integer)followUser.get("v_lv"));
				e.setLevel((Integer)followUser.get("level"));
				if(null != followMap.get(uid+"_"+e.getUid())){
	                e.setIsFollowed(1);
	            }else{
	                e.setIsFollowed(0);
	            }
	            if(null != followMap.get(e.getUid()+"_"+uid)){
	                e.setIsFollowMe(1);
	            }else{
	                e.setIsFollowMe(0);
	            }
	            if(!StringUtils.isEmpty((String)followUser.get("avatar_frame"))){
					e.setAvatarFrame(Constant.QINIU_DOMAIN + "/" + (String)followUser.get("avatar_frame"));
				}
				result.getMyFollowData().add(e);
			}
		}
	}
	
	@Override
	public void initNameGroup(){
		int total = userMybatisDao.getNoNameGroupUserProfileCount();
		log.info("==本次共["+total+"]个无分组的用户昵称需要调整");
		List<UserProfile> ulist = null;
		int count = 0;
		while(true){
			ulist = userMybatisDao.getNoNameGroupUserProfiles(1000);
			if(null == ulist || ulist.size() == 0){
				break;
			}
			for(UserProfile u : ulist){
				userMybatisDao.updateUserProfile(u);
			}
			count = count + ulist.size();
			log.info("==处理了["+ulist.size()+"]个用户，共处理了["+count+"]个用户");
		}
		log.info("==初始化用户昵称分组完成");
	}
	
	@Override
	public Response seekFollowsQuery(long uid, long sinceId){
		if(sinceId <= 0){
			sinceId = Long.MAX_VALUE;
		}
		
		ShowSeekFollowsQueryDTO result = new ShowSeekFollowsQueryDTO();
		//先看看自己有没有求关注过
		UserSeekFollow seek = userMybatisDao.getUserSeekFollowByUid(uid);
		if(null != seek){
			result.setIsSeek(1);
		}else{
			result.setIsSeek(0);
		}
		
		List<UserSeekFollow> seekList = userMybatisDao.getUserSeekFollows(uid, sinceId, 20);
		if(null != seekList && seekList.size() > 0){
			List<Long> uidList = new ArrayList<Long>();
			for(UserSeekFollow usf : seekList){
				uidList.add(usf.getUid());
			}
			Map<String, UserProfile> userProfileMap = new HashMap<String, UserProfile>();
			//一次性查询关注信息
	        Map<String, String> followMap = new HashMap<String, String>();
			if(uidList.size() > 0){
				List<UserProfile> userList = userMybatisDao.getUserProfilesByUids(uidList);
				if(null != userList && userList.size() > 0){
					for(UserProfile u : userList){
						userProfileMap.put(u.getUid().toString(), u);
					}
				}
				List<UserFollow> userFollowList = userMybatisDao.getAllFollows(uid, uidList);
		        if(null != userFollowList && userFollowList.size() > 0){
		            for(UserFollow uf : userFollowList){
		                followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
		            }
		        }
			}
			
			ShowSeekFollowsQueryDTO.SeekFollowElement e = null;
			UserProfile user = null;
			for(UserSeekFollow usf : seekList){
				e = new ShowSeekFollowsQueryDTO.SeekFollowElement();
				user = userProfileMap.get(usf.getUid().toString());
				if(null == user){
					continue;
				}
				e.setSinceId(usf.getId());
				e.setAvatar(Constant.QINIU_DOMAIN + "/" + user.getAvatar());
				e.setIntroduced(user.getIntroduced());
				if(null != followMap.get(uid+"_"+user.getUid().toString())){
	                e.setIsFollowed(1);
	            }else{
	                e.setIsFollowed(0);
	            }
	            if(null != followMap.get(user.getUid().toString()+"_"+uid)){
	                e.setIsFollowMe(1);
	            }else{
	                e.setIsFollowMe(0);
	            }
				e.setNickName(user.getNickName());
				e.setUid(user.getUid());
				e.setV_lv(user.getvLv());
				e.setLevel(user.getLevel());
				if(!StringUtils.isEmpty(user.getAvatarFrame())){
					e.setAvatarFrame(Constant.QINIU_DOMAIN + "/" + user.getAvatarFrame());
				}
				result.getSeekFollowData().add(e);
			}
		}
		
		return Response.success(result);
	}
	
	@Override
	public Response seekFollow(long uid){
		UserSeekFollow seek = userMybatisDao.getUserSeekFollowByUid(uid);
		if(null != seek){
			return Response.success(500, "重复操作");
		}
		
		seek = new UserSeekFollow();
		seek.setUid(uid);
		seek.setCreateTime(new Date());
		userMybatisDao.saveUserSeekFollow(seek);
		
		return Response.success(ResponseStatus.OPERATION_SUCCESS.status, ResponseStatus.OPERATION_SUCCESS.message);
	}
	
	@Override
	public void cleanSeekFollow(int hour){
		Date now = new Date();
		long lastTime = now.getTime() - hour*60*60*1000l;
		Date lastDate = new Date(lastTime);
		userMybatisDao.deleteOvertimeSeek(lastDate);
	}
	
	@Override
	public Response myFollowsQuery(long uid, String name, int page){
		int pageSize = 20;
		int start = (page-1)*pageSize;
		
		ShowMyFollowsQueryDTO result = new ShowMyFollowsQueryDTO();
		int total = userInitJdbcDao.countUserFollowInfo(name, uid);
		result.setTotalPage(total%pageSize==0?(total/pageSize):((total/pageSize)+1));
		
		List<Map<String, Object>> followList = userInitJdbcDao.getUserFollowInfoPage(name, uid, start, pageSize);
		if(null != followList && followList.size() > 0){
			List<Long> uidList = new ArrayList<Long>();
			for(Map<String, Object> followUser : followList){
				uidList.add((Long)followUser.get("uid"));
			}
			Map<String, String> followMap = new HashMap<String, String>();
			if(uidList.size() > 0){
				List<UserFollow> userFollowList = userMybatisDao.getAllFollows(uid, uidList);
		        if(null != userFollowList && userFollowList.size() > 0){
		            for(UserFollow uf : userFollowList){
		                followMap.put(uf.getSourceUid()+"_"+uf.getTargetUid(), "1");
		            }
		        }
			}
			
			ShowMyFollowsQueryDTO.MyFollowElement e = null;
			for(Map<String, Object> followUser : followList){
				e = new ShowMyFollowsQueryDTO.MyFollowElement();
				e.setAvatar(Constant.QINIU_DOMAIN + "/" + (String)followUser.get("avatar"));
				e.setGroup((String)followUser.get("name_group"));
				e.setIntroduced((String)followUser.get("introduced"));
				e.setNickName((String)followUser.get("nick_name"));
				e.setUid((Long)followUser.get("uid"));
				e.setV_lv((Integer)followUser.get("v_lv"));
				e.setLevel((int)followUser.get("level"));
				if(null != followMap.get(uid+"_"+e.getUid())){
	                e.setIsFollowed(1);
	            }else{
	                e.setIsFollowed(0);
	            }
	            if(null != followMap.get(e.getUid()+"_"+uid)){
	                e.setIsFollowMe(1);
	            }else{
	                e.setIsFollowMe(0);
	            }
	            if(!StringUtils.isEmpty((String)followUser.get("avatar_frame"))){
	            	e.setAvatarFrame(Constant.QINIU_DOMAIN + "/" + (String)followUser.get("avatar_frame"));
	            }
				result.getMyFollowData().add(e);
			}
		}
		
		return Response.success(result);
	}
	
	@Override
	public Response batchFollow(long uid, String targetUids){
		List<Long> targetUidList = new ArrayList<Long>();
		if(!StringUtils.isEmpty(targetUids)){
			String[] tmp = targetUids.split(";");
			if(null != tmp && tmp.length > 0){
				Long tuid = null;
				for(String u : tmp){
					if(!StringUtils.isEmpty(u)){
						tuid = Long.valueOf(u);
						if(!targetUidList.contains(tuid)){
							targetUidList.add(tuid);
						}
					}
				}
			}
		}
		
		if(targetUidList.size() > 0){
			//判断其中是否有已经关注过的用户
			List<UserFollow> list = userMybatisDao.getUserFollowsBySourceUidAndTargetUids(uid, targetUidList);
			if(null != list && list.size() > 0){
				for(UserFollow uf : list){
					targetUidList.remove(uf.getTargetUid());
				}
			}
		}
		
		if(targetUidList.size() > 0){
			userInitJdbcDao.batchUserFollowInsertIntoDB(uid, targetUidList);
			//推送
			BatchFollowEvent event = new BatchFollowEvent();
			event.setSourceUid(uid);
			event.setTargetUids(targetUidList);
			this.applicationEventBus.post(event);
		}
		
		return Response.success(ResponseStatus.OPERATION_SUCCESS.status, ResponseStatus.OPERATION_SUCCESS.message);
	}
	
	@Override
	public Response personaModify(long uid, int type, String params){
		
		
		switch(type){
		case 1://性别
			int sex = Integer.valueOf(params);
			if(sex != 1){
				sex = 0;
			}
			userInitJdbcDao.updateUserProfileParam4Number(uid, sex, "gender");
			break;
		case 2://性取向
			int sexOrientation = Integer.valueOf(params);
			userInitJdbcDao.updateUserProfileParam4Number(uid, sexOrientation, "like_gender");
			break;
		case 3://兴趣爱好
	        this.modifyUserHobby4Persona(uid, params);
	        log.info("modify user hobby");
	        UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
	        userProfile.setUpdateTime(new Date());
			userMybatisDao.updateUserProfile(userProfile);
			break;
		case 4://年龄段
			int ageGroup = Integer.valueOf(params);
			userInitJdbcDao.updateUserProfileParam4Number(uid, ageGroup, "age_group");
			break;
		case 5://职业
			int career = Integer.valueOf(params);
			userInitJdbcDao.updateUserProfileParam4Number(uid, career, "occupation");
			break;
		default:
			return Response.failure(ResponseStatus.ILLEGAL_REQUEST.status, ResponseStatus.ILLEGAL_REQUEST.message);
		}
		
		return Response.success(ResponseStatus.OPERATION_SUCCESS.status, ResponseStatus.OPERATION_SUCCESS.message);
	}
	
	private void modifyUserHobby4Persona(long uid, String hobbys){
		UserHobby deleteUserHobby = new UserHobby();
		deleteUserHobby.setUid(uid);
		userMybatisDao.deleteUserHobby(deleteUserHobby);
		if(!StringUtils.isEmpty(hobbys)){
			Set<Long> userHobbyTags = new LinkedHashSet<>();
			String[] hobbies = hobbys.split(",");
			for (String h : hobbies) {
				if(!StringUtils.isEmpty(h)){
					UserHobby userHobby = new UserHobby();
					userHobby.setHobby(Long.parseLong(h));
					userHobby.setUid(uid);
					userMybatisDao.createUserHobby(userHobby);
					List<Long> tags= userInitJdbcDao.getTagFromUserHobby(userHobby.getHobby());
					userHobbyTags.addAll(tags);
				}
			}
			if(!userHobbyTags.isEmpty()){
				userInitJdbcDao.batchInsertUserLikeTags(uid,userHobbyTags);
			}
		}
	}
	
	// 170190
	@Override
	public Response testSendMessage(long templateId, String mobiles){
		if(StringUtils.isEmpty(mobiles)){
			return Response.failure(500, "没有手机号");
		}
		List<String> mobileList = new ArrayList<String>();
		if("ALL".equals(mobiles)){//全体发送
			List<String> mList = userInitJdbcDao.getAllUserMobilesInApp();
			if(null != mList && mList.size() > 0){
				for(String m : mList){
					if(!StringUtils.isEmpty(m)){
						mobileList.add(m);
					}
				}
			}
		}else{
			String[] tmp = mobiles.split(",");
			for(String t : tmp){
				if(!StringUtils.isEmpty(t) && !mobileList.contains(t)){
					mobileList.add(t);
				}
			}
		}
		
		if(mobileList.size() > 0){
			int total = mobileList.size();
			log.info("total ["+total+"] mobiles!");
			List<String> sendList = new ArrayList<String>();
			int sends = 0;
			for(String mobile : mobileList){
				sendList.add(mobile);
				if(sendList.size() >= 180){
					smsService.send7dayCommon(String.valueOf(templateId), sendList, null);
					sends = sends + sendList.size();
					log.info("send ["+sendList.size()+"], total send ["+sends+"], remian ["+(total-sends)+"]");
					sendList.clear();
				}
			}
			if(sendList.size() > 0){
				smsService.send7dayCommon(String.valueOf(templateId), sendList, null);
				sends = sends + sendList.size();
				log.info("send ["+sendList.size()+"], total send ["+sends+"], remian ["+(total-sends)+"]");
			}
		}
		
		return Response.success();
	}
	
	private boolean checkMobile(String mobile){
    	if(!StringUtils.isEmpty(mobile)){
    		if(!mobile.startsWith("100") && !mobile.startsWith("111")
    				&& !mobile.startsWith("123") && !mobile.startsWith("1666")
    				&& !mobile.startsWith("180000") && !mobile.startsWith("18888888")
    				&& !mobile.startsWith("18900") && !mobile.startsWith("19000")
    				&& !mobile.startsWith("2") && !mobile.startsWith("8")){
    			return true;
    		}
    	}
    	return false;
    }
	
	@Override
	public void deleteUserProfile(long id){
		userMybatisDao.deleteUserProfile(id);
	}
	
	@Override
	public Response noticeReddotQuery(long uid){
		ShowNoticeReddotQueryDTO result = new ShowNoticeReddotQueryDTO();
		
		int unreadCount = userMybatisDao.countUnreadNotice(uid);
		result.setUnreadCount(unreadCount);
		
		ContactsReddot cr = new ContactsReddot(uid, "1");
		if(StringUtils.isEmpty(cacheService.hGet(cr.getKey(), cr.getField()))){
			result.setContactReddot(0);
		}else{
			result.setContactReddot(1);
		}
		
		return Response.success(result);
	}
	
	@Override
	public void clearUserNoticeUnreadByCid(long uid, int contentType, long cid){
		userMybatisDao.clearUserNoticeUnreadByCid(uid, contentType, cid);
	}
	
	@Override
	public List<EmotionRecord> getUserEmotionRecords(long uid, int pageSize){
		return userMybatisDao.getUserEmotionRecord(uid, pageSize);
	}
	
	@Override
	public List<EmotionInfo> getEmotionInfosByIds(List<Long> ids){
		return userMybatisDao.getEmotionInfosByIds(ids);
	}

	@Override
	public Response<MBTIDto> getMBTIResult(long uid) {
		List<UserMbtiHistory> userHistory = userMybatisDao.getMBTIHistoryByUid(uid);
		boolean shared = false;
		boolean tested = false;
		
		for(UserMbtiHistory history:userHistory){
			tested=true;
			if(history.getShared()==1){
				shared=true;
				break;
			}
		}
		MBTIDto dto = new MBTIDto();
		if(!userHistory.isEmpty()){
			String mbti= userHistory.get(0).getMbti();
			dto.setMbti(mbti);
			Long kingdomId =userMybatisDao.getKingdomIdByMBTI(mbti);
			if(kingdomId!=null){
				dto.setKingdomId(kingdomId);
			}
		}
		dto.setShared(shared);
		dto.setTested(tested);
		UserProfile up = getUserProfileByUid(uid);
		dto.setUid(up.getUid());
		dto.setAvatar(up.getAvatar());
		dto.setNickName(up.getNickName());
		dto.setVLv(up.getvLv());
		dto.setLevel(up.getLevel());
		return Response.success(dto);
	}

	@Override
	public Response<MBTIDto> saveMBTIResult(long uid, String mbti) {
		userMybatisDao.saveMBTIResult(uid, mbti);
		userInitJdbcDao.updateUserProfileParam4String(uid, mbti, "mbti");
		MBTIDto dto = new MBTIDto();
		Long kingdomId =userMybatisDao.getKingdomIdByMBTI(mbti);
		if(kingdomId!=null){
			dto.setKingdomId(kingdomId);
		}
		dto.setTested(true);
		boolean isShared =userMybatisDao.isMBTIShared(uid);
		dto.setShared(isShared);
		dto.setMbti(mbti);
		return Response.success(dto);
	}

	@Override
	public Response saveMBTIShareResult(long uid) {
		userInitJdbcDao.saveMBTIShareResult(uid);
		return Response.success();
	}

	@Override
	public void addMBTIMapping(MbtiMapping mapping) {
		mapping.setCreatetime(new Date());
		this.userMybatisDao.addMBTIMapping(mapping);
	}

	@Override
	public void delMBTIMapping(long mappingId) {
		this.userMybatisDao.deleteMBTIMappingById(mappingId);
	}

	@Override
	public void modifyMBTIMapping(MbtiMapping mapping) {
		this.userMybatisDao.updateMBTIMapping(mapping);
		
	}

	@Override
	public List<MbtiMapping> getMBTIMappingPage() {
		return this.userMybatisDao.getAllMBTIMapping();
	}

	@Override
	public MbtiMapping getMBTIMappingById(long id) {
		return this.userMybatisDao.getMBTIMappingById(id);
	}
	
	@Override
	public List<EmotionInfo> getEmotionInfoList(){
		List<EmotionInfo> datas = userMybatisDao.getEmotionInfoList();
		return userMybatisDao.getEmotionInfoList();
	}

	@Override
	public EmotionInfo getEmotionInfoByKey(Long id){
		return userMybatisDao.getEmotionInfoByKey(id);
	}
	
	@Override
	public void updateEmotionInfoByKey(EmotionInfo emotionInfo) {
		userMybatisDao.updateEmotionInfoByKey(emotionInfo);
	}
	
	@Override
	public Integer addEmotionInfo(EmotionInfo emotionInfo) {
		return userMybatisDao.addEmotionInfo(emotionInfo);
	}
	@Override
	public EmotionInfo getEmotionInfoByValue(int happyValue,int freeValue) {
		return userMybatisDao.getEmotionInfoByValue( happyValue, freeValue);
	}
	@Override
	public Integer addEmotionRecord(EmotionRecord emotionRecord) {
		return userMybatisDao.addEmotionRecord(emotionRecord);
	}
	@Override
	public boolean exsitEmotionRecord(long uid,Date mondayDate,Date sundayDate){
		return userMybatisDao.exsitEmotionRecord( uid, mondayDate, sundayDate);
	}
	@Override
	public EmotionRecord getLastEmotionRecord(long uid) {
		return userMybatisDao.getLastEmotionRecord( uid);
	}
	@Override
	public int getEmotionRecordCount(long uid){
		return userMybatisDao.getEmotionRecordCount( uid);
	}
	
	@Override
	public Response getSummaryEmotionInfo(long uid,long time) {
		try {
			SummaryEmotionInfoDto dto = new SummaryEmotionInfoDto();
		Date date = null;
		if(time==0){
			date = new Date();
		}else{
			date = new Date(time);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		 Calendar cal1 = Calendar.getInstance();
		 cal1.setTime(date);
		  int n = -1;
		  if(cal1.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
			  n=-2;
		  }
		  String monday;
		  cal1.add(Calendar.DATE, n*7);
		  cal1.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
		  monday = sdf1.format(cal1.getTime());
		  Date mondayDate=null;
			mondayDate = sdf1.parse(monday);
			 Calendar cal2 = Calendar.getInstance();
			 cal2.setTime(date);
			  int m = 0;
			  if(cal2.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
				  m=-1;
			  }
		  String sunday;
		  cal2.add(Calendar.DATE, m*7);
		  cal2.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
		  sunday = sdf2.format(cal2.getTime());
		  Date sundayDate = sdf2.parse(sunday);
		  List<Map<String, Object>> list = userInitJdbcDao.getSummaryEmotionInfo(uid,monday,sunday);
		  double count = 0.0;
//		  int count = 0;
		  for (Map<String, Object> map:list) {
			  count+=((Long)map.get("countNum")).doubleValue();
//			count+= Integer.parseInt(map.get("countNum").toString());
		 }
		  
		  int totalPercent = 0;
		  SummaryEmotionInfoDto.EmotionData emotionData = null;
		  EmotionInfo emotionInfo = null;
		  for(Map<String, Object> map : list){
			  emotionData = SummaryEmotionInfoDto.create();
			  emotionInfo = userMybatisDao.getEmotionInfoByKey((Long)map.get("emotionId"));
			  emotionData.setEmotionName(emotionInfo.getEmotionname());
			  int percentage = new BigDecimal(((Long)map.get("countNum")).doubleValue()*100.0/count).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			  if(percentage == 0){
				  percentage = 1;
			  }
			  totalPercent = totalPercent + percentage;
			  emotionData.setPercentage(percentage);
			  emotionData.setHappyValue((emotionInfo.getHappymin()+emotionInfo.getHappymax())/2);
			  emotionData.setFreeValue((emotionInfo.getFreemin()+emotionInfo.getFreemax())/2);
//			  emotionData.setHappyValue(Long.valueOf( map.get("happyValue").toString()));
//			  emotionData.setFreeValue(Long.valueOf( map.get("freeValue").toString()));
			  dto.getEmotionData().add(emotionData);
		  }
		  if(dto.getEmotionData().size() > 0){
			  int t = dto.getEmotionData().size();
			  SummaryEmotionInfoDto.EmotionData ed = null;
			  //开始计算偏移量
			  if(totalPercent > 100){//多了
				  int offset = totalPercent-100;
				  //多了，则需要减(从后往前减)
				  while(offset > 0){
					  for(int i=t-1;i>=0;i--){
						  if(offset<=0){
							  break;
						  }
						  ed = dto.getEmotionData().get(i);
						  int p = ed.getPercentage();
						  if(p>1){
							  ed.setPercentage(p-1);
							  offset--;
						  }
					  }
				  }
			  }else if(totalPercent < 100){//少了
				  int offset = 100-totalPercent;
				  //少了，则需要加(从前往后加)
				  while(offset > 0){
					  for(int i=0;i<t;i++){
						  if(offset<=0){
							  break;
						  }
						  ed = dto.getEmotionData().get(i);
						  int p = ed.getPercentage();
						  ed.setPercentage(p+1);
						  offset--;
					  }
				  }
			  }
		  }
		  
		  /*
		  int max = 0;
		  int maxIndex = 0;
		  int min= 101;
		  int minIndex = 0;
		  int percentageCount = 0;
		  for (int i=0;i<list.size();i++) {
			  Map<String, Object> map  =list.get(i);
			  String emotionId = map.get("emotionId").toString();
			  EmotionInfo emotionInfo = userMybatisDao.getEmotionInfoByKey(Long.valueOf(emotionId));
			  SummaryEmotionInfoDto.EmotionData emotionData = SummaryEmotionInfoDto.create();
			  emotionData.setEmotionName(emotionInfo.getEmotionname());
			  BigDecimal countNum = new BigDecimal(map.get("countNum").toString()).setScale(3, BigDecimal.ROUND_UP);
			  int  percentage = countNum.divide( new BigDecimal(count),3).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			  percentageCount+=percentage;
			  if(percentage>max){
		    	 max = percentage;
		    	 maxIndex = i;
		      }
			  if(percentage<min){
			    	 min = percentage;
			    	 minIndex = i;
			      }
			  emotionData.setPercentage(percentage);
			  emotionData.setHappyValue(Long.valueOf( map.get("happyValue").toString()));
			  emotionData.setFreeValue(Long.valueOf( map.get("freeValue").toString()));
			  dto.getEmotionData().add(emotionData);
		  }
		  if(percentageCount<100){
			  int temp = 100-percentageCount;
			  SummaryEmotionInfoDto.EmotionData  emotionData = dto.getEmotionData().get(minIndex);
			  emotionData.setPercentage(emotionData.getPercentage()+temp);
		  }
		  if(percentageCount>100){
			  int temp = percentageCount-100;
			  SummaryEmotionInfoDto.EmotionData  emotionData = dto.getEmotionData().get(maxIndex);
			  emotionData.setPercentage(emotionData.getPercentage()-temp);
		  }
		  */
		  //按百分比数值排序
		    Collections.sort(dto.getEmotionData(), new Comparator<Object>() {
			      @Override
			      public int compare(Object o1, Object o2) {
			    	  int comp1 = ((SummaryEmotionInfoDto.EmotionData)o1).getPercentage();
			    	  int comp2 = ((SummaryEmotionInfoDto.EmotionData)o2).getPercentage();
			    	  if(comp1>comp2){
			    		  return -1;
			    	  }else{
			    		  return 1;
			    	  }
			      }
			    });
		  SimpleDateFormat dsdf = new SimpleDateFormat("M月dd日");
		  String dateStr = dsdf.format(mondayDate)+"-"+dsdf.format(sundayDate);
		  dto.setDateStr(dateStr);
		  
            //周总结状态记录
			EmotionSummaryModel EmotionSummaryModel = new EmotionSummaryModel(sdf.format(cal1.getTime()), uid, "0");
			cacheService.hSet(EmotionSummaryModel.getKey(), EmotionSummaryModel.getField(), EmotionSummaryModel.getValue());
		  
			return  Response.success(dto);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return  Response.failure(500,"时间转换错误");
		}
	
	}
	@Override
	public List<Map<String,Object>> getEmotionRecordByStartAndEnd(String start,String end){
		return userInitJdbcDao.getEmotionRecordByStartAndEnd(start, end);
	}
	@Override
	public boolean existsEmotionInfoByName(EmotionInfo emotionInfo){
		return userMybatisDao.existsEmotionInfoByName(emotionInfo);
	}
	
	@Override
	public String getAppConfigByKey(String key){
		String result = cacheService.get(CacheConstant.APP_CONFIG_KEY_PRE + key);
		if(null != result){
			return result;
		}
		AppConfig config = userMybatisDao.getAppConfigByKey(key);
		if(null != config && null != config.getConfigValue()){
			cacheService.set(CacheConstant.APP_CONFIG_KEY_PRE + key, config.getConfigValue());
			return config.getConfigValue();
		}
		return null;
	}

    @Override
    public Integer getIntegerAppConfigByKey(String key) {
        String result = cacheService.get(CacheConstant.APP_CONFIG_KEY_PRE + key);
        if(!StringUtils.isEmpty(result)){
            return Integer.parseInt(result);
        }
        AppConfig config = userMybatisDao.getAppConfigByKey(key);
        if(null != config && !StringUtils.isEmpty(config.getConfigValue())){
            cacheService.set(CacheConstant.APP_CONFIG_KEY_PRE + key, config.getConfigValue());
            return Integer.parseInt(config.getConfigValue());
        }
        return null;
    }

    @Override
	public Map<String, String> getAppConfigsByKeys(List<String> keys){
		if(null == keys || keys.size() == 0){
			return null;
		}else{
			log.info("===="+keys);
		}
		Map<String, String> result = new HashMap<String, String>();
		
		List<AppConfig> list = userMybatisDao.getAppConfigByKeys(keys);
		if(null != list && list.size() > 0){
			for(AppConfig c : list){
				result.put(c.getConfigKey(), c.getConfigValue());
			}
		}
		return result;
	}
	
	@Override
	public void saveAppConfig(String key, String value, String desc){
		if(StringUtils.isEmpty(key) || value == null){
			return;
		}
		AppConfig config = userMybatisDao.getAppConfigByKey(key);
		if(null == config){
			config = new AppConfig();
			config.setConfigKey(key);
			config.setConfigValue(value);
			config.setName(desc);
			userMybatisDao.saveAppConfig(config);
		}else{
			config.setConfigValue(value);
			userMybatisDao.updateAppConfig(config);
		}
		cacheService.set(CacheConstant.APP_CONFIG_KEY_PRE + key, value);
	}
	@Override
	public void saveAppConfig(String key, String value){
		if(StringUtils.isEmpty(key) || value==null){
			return;
		}
		AppConfig config = userMybatisDao.getAppConfigByKey(key);
		if(null == config){
			config = new AppConfig();
			config.setConfigKey(key);
			config.setConfigValue(value);
			userMybatisDao.saveAppConfig(config);
		}else{
			config.setConfigValue(value);
			userMybatisDao.updateAppConfig(config);
		}
		cacheService.set(CacheConstant.APP_CONFIG_KEY_PRE + key, value);
	}
	@Override
	public List<AppConfig> getAllAppConfig(){
		return userMybatisDao.getAllAppConfig();
	}

    @Override
    public Response getLevelList() {
        String value = getAppConfigByKey(USER_PERMISSIONS);
        UserPermissionDto userPermissionDto = JSON.parseObject(value, UserPermissionDto.class);
        for(UserPermissionDto.UserLevelDto  userLevelDto : userPermissionDto.getLevels()){
            userLevelDto.setPermissions(null);
        }
        String levelDefinition = getAppConfigByKey(LEVEL_DEFINITION);
        userPermissionDto.setLevelDefinition(levelDefinition);
        return Response.success(userPermissionDto);
    }
    /**
     * 获取用户权限
     * @author zhangjiwei
     * @date Jun 21, 2017
     * @param uid
     * @return
     */
    public PermissionDescriptionDto getUserPermission(long uid){
    	UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
    	 //获取权限内容
        String level = userProfile.getLevel().toString();
        String value2 = getAppConfigByKey("LEVEL_"+level);
        PermissionDescriptionDto permissionDescriptionDto = JSON.parseObject(value2, PermissionDescriptionDto.class);
        return permissionDescriptionDto;
    }
    @Override
    public Response getMyLevel(long uid) {
	    UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
	    MyLevelDto myLevelDto = new MyLevelDto();
        MyLevelDto.InnerLevel currentLevel = myLevelDto.createInnerLevel();
        currentLevel.setLevel(userProfile.getLevel());
        MyLevelDto.InnerLevel nextLevel = myLevelDto.createInnerLevel();
        if(userProfile.getLevel() != 9){
            nextLevel.setLevel( userProfile.getLevel()+1);
        }

        MyLevelDto.InnerLevel preLevel = myLevelDto.createInnerLevel();
        if (userProfile.getLevel() > 1){
            preLevel.setLevel(userProfile.getLevel()-1);
        }
        myLevelDto.setAvailableCoin(userProfile.getAvailableCoin());
        myLevelDto.setAvatar(Constant.QINIU_DOMAIN + "/"+ userProfile.getAvatar());
        String value = getAppConfigByKey(USER_PERMISSIONS);
      /*  log.info("infos: " + value);*/
        UserPermissionDto userPermissionDto = JSON.parseObject(value, UserPermissionDto.class);
        for(UserPermissionDto.UserLevelDto userLevelDto : userPermissionDto.getLevels()){
            if (userProfile.getLevel() == userLevelDto.getLevel()){
                currentLevel.setName(userLevelDto.getName());
            }
            if (userProfile.getLevel()+1 == userLevelDto.getLevel()  && userProfile.getLevel() != 9){
                nextLevel.setName(userLevelDto.getName());
            }
            if (userProfile.getLevel()-1 == userLevelDto.getLevel() && userProfile.getLevel() > 1 ){
                preLevel.setName(userLevelDto.getName());
            }
            if((userProfile.getLevel()+1) == userLevelDto.getLevel()){
                myLevelDto.setNextLevelCoin(userLevelDto.getNeedCoins()-userProfile.getAvailableCoin());
            }
        }
        myLevelDto.setCurrentLevel(currentLevel);
        if (userProfile.getLevel()>1){
            myLevelDto.setPreLevel(preLevel);
        }
        myLevelDto.setNextLevel(nextLevel);
        //获取权限内容
        String level = userProfile.getLevel().toString();
        String value2 = getAppConfigByKey("LEVEL_"+level);
        PermissionDescriptionDto permissionDescriptionDto = JSON.parseObject(value2, PermissionDescriptionDto.class);
        List<PermissionDescriptionDto.PermissionNodeDto> list = Lists.newArrayList();
     /*   for(PermissionDescriptionDto.PermissionNodeDto nodeDto : permissionDescriptionDto.getNodes()){
            list.add(nodeDto);
        }*/

/**
 *                以下3个for循环为弱智排序
 */

        for(PermissionDescriptionDto.PermissionNodeDto nodeDto : permissionDescriptionDto.getNodes()){
            if(nodeDto.getCode()==1){
                list.add(nodeDto);
            }else if (nodeDto.getCode()==2){
                list.add(nodeDto);
            }else  if(nodeDto.getCode()==3){
                list.add(nodeDto);
            }else if(nodeDto.getCode()==6){
                list.add(nodeDto);
                break;
            }
        }
        for(PermissionDescriptionDto.PermissionNodeDto nodeDto : permissionDescriptionDto.getNodes()){
            if(nodeDto.getCode()==5){
                list.add(nodeDto);
            }else if(nodeDto.getCode()==7){
                list.add(nodeDto);
                break;
            }
        }
        for(PermissionDescriptionDto.PermissionNodeDto nodeDto : permissionDescriptionDto.getNodes()){
            if(nodeDto.getCode()==4){
                list.add(nodeDto);
                break;
            }
        }



        for(PermissionDescriptionDto.PermissionNodeDto nodeDto : permissionDescriptionDto.getNodes()){
            if(nodeDto.getIsShow()==1){
                nodeDto.setIsShow(null);
                /*list.add(nodeDto);*/
                if(nodeDto.getStatus()!=1){
                    // 找寻哪个级别开通该功能
                    int openLevel = checkIsOpenLevel(nodeDto.getCode());

                    nodeDto.setOpenLevel(openLevel);
                }
            }
        }
        permissionDescriptionDto.setNodes(list);

        myLevelDto.setPermissions(permissionDescriptionDto);
        //随机获取可偷王国id
        List stealList= userInitJdbcDao.getCanStealTopicId(uid);

        Random random = new Random();
        if(stealList==null||stealList.isEmpty()){
            myLevelDto.setStealTopicId(0);
        }else {
            int stealKey = random.nextInt(stealList.size());
            Map<String, Object> stealMap = (Map<String, Object>) stealList.get(stealKey);
            myLevelDto.setStealTopicId((long) stealMap.get("topic_id"));
        }
        //随机评论王国id
        List randomList= userInitJdbcDao.getCanSpeakTopicId(uid);
        int randomKey = random.nextInt(randomList.size());
        Map<String,Object> map = (Map<String, Object>) randomList.get(randomKey);
        myLevelDto.setRandomTopicId((long)map.get("id"));
        // 排序结果集合
      /* List<PermissionDescriptionDto.PermissionNodeDto> nodes = myLevelDto.getPermissions().getNodes();*/
    /*   int k = 0 ;
        for (int i = 0 ; i<= list.size()-k ; i++){
            if (list.get(i).getIsShow() != null){
            if (list.get(i).getIsShow() == 0){
                list.remove(i);
                k++;
            }}
            if (list.get(i).getCode()== 4){
                list.remove(i);
                k++;
            }
        }
        Collections.sort(list);
        for(PermissionDescriptionDto.PermissionNodeDto nodeDto : permissionDescriptionDto.getNodes()){
            if (nodeDto.getCode() == 4){
                list.add(nodeDto);
            }
        }*/
        //转换米汤币为人民币
        String exchangeRate = this.getAppConfigByKey("EXCHANGE_RATE");
        if (StringUtils.isEmpty(exchangeRate)){
            exchangeRate = "100";
        }
        Integer  i = myLevelDto.getAvailableCoin();
        Double d = i.doubleValue();
        BigDecimal bg = new BigDecimal(d/Integer.parseInt(exchangeRate));
        double RMB = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        myLevelDto.setPriceRMB(RMB);
        return Response.success(myLevelDto);
    }

    @Override
    public ModifyUserCoinDto modifyUserCoin(long uid , int coin) {
	    ModifyUserCoinDto modifyUserCoinDto = new ModifyUserCoinDto();
	    UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
        modifyUserCoinDto.setCurrentLevel(userProfile.getLevel());
        int modifyCoin = userProfile.getAvailableCoin()+coin;
	    userInitJdbcDao.modifyUserCoin(uid,modifyCoin);
        String permissions = getAppConfigByKey(USER_PERMISSIONS);
        UserPermissionDto userPermissionDto = JSON.parseObject(permissions, UserPermissionDto.class);
        int lv = 0;
        for(UserPermissionDto.UserLevelDto userLevelDto : userPermissionDto.getLevels()){
            if(  modifyCoin >= userLevelDto.getNeedCoins()){
                lv++;
            }
        }
        if ( lv > 9){
            lv = 9;
        }
        if(lv <= userProfile.getLevel()){
            return modifyUserCoinDto;
        }else{
            for (UserPermissionDto.UserLevelDto userLevelDto : userPermissionDto.getLevels()) {
                if ((lv - 1) == userLevelDto.getLevel() && modifyCoin >= userLevelDto.getNeedCoins()) {
                    modifyUserCoinDto.setUpgrade(1);
                    userInitJdbcDao.modifyUserLevel(uid, lv);
                    modifyUserCoinDto.setCurrentLevel(lv);
                    break;
                }
            }
            return modifyUserCoinDto;
        }
    }
    
    @Override
    public ModifyUserCoinDto currentUserLevelStatus(long uid, int addCoin){
    	userInitJdbcDao.incrUserCoin(uid, addCoin);
    	ModifyUserCoinDto modifyUserCoinDto = new ModifyUserCoinDto();
    	UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
    	modifyUserCoinDto.setCurrentLevel(userProfile.getLevel());
    	modifyUserCoinDto.setUpgrade(0);
    	int currentCoin = userProfile.getAvailableCoin();
    	String permissions = getAppConfigByKey(USER_PERMISSIONS);
        UserPermissionDto userPermissionDto = JSON.parseObject(permissions, UserPermissionDto.class);
        int lv = 0;
        for(UserPermissionDto.UserLevelDto userLevelDto : userPermissionDto.getLevels()){
            if(  currentCoin >= userLevelDto.getNeedCoins()){
                lv++;
            }
        }
        if ( lv > 9){
            lv = 9;
        }
        if(lv > userProfile.getLevel()){
        	modifyUserCoinDto.setUpgrade(1);
            userInitJdbcDao.modifyUserLevel(uid, lv);
            modifyUserCoinDto.setCurrentLevel(lv);
        }
        
        return modifyUserCoinDto;
    }

    @Override
    public ModifyUserCoinDto coinRule(long uid,CoinRule rule) {
	    UserProfile userProfile = getUserProfileByUid(uid);
	    // 根据等级获取今日上限值
        String limits = getAppConfigByKey("GET_COIN_LEVEL_LIMITS");
        List<Integer> array = JSON.parseArray(limits,Integer.class);
        Map<Integer,Integer> map = Maps.newConcurrentMap();
        for(int i = 0;i<array.size();i++){
            map.put(i+1,array.get(i));
        }
        // 日志拉取今日的累计值
        int allDayPoints = userInitJdbcDao.getDayCoins(uid) + userInitJdbcDao.getDayCoins2(uid);
        
        if(allDayPoints < map.get(userProfile.getLevel())){
            // 并且规则是否允许重复
            if(!rule.isRepeatable()){
                boolean result = userInitJdbcDao.isNotExistsRuleLogByDay(rule.getCode(),uid);
                if(result) {
                    //记录日志
                    userInitJdbcDao.writeRuleLog(uid,rule);
                    return  modifyUserCoin(uid, rule.getPoint());
                }
            }else{
                if(rule.getExt()>0){
                    boolean result = userInitJdbcDao.isNotExistsRuleLogByDay(rule.getCode(),uid,rule.getExt());
                    if(result){
                        userInitJdbcDao.writeRuleLog(uid,rule);
                        return  modifyUserCoin(uid, rule.getPoint());
                    }
                }else{
                    userInitJdbcDao.writeRuleLog(uid,rule);
                    return  modifyUserCoin(uid, rule.getPoint());
                }
            }

        }else{
            return  modifyUserCoin(uid, 0);
        }
        return  modifyUserCoin(uid, 0);
    }


    private List<PermissionDescriptionDto> getPermissionConfig(int lastLevel){
        List<PermissionDescriptionDto> ret = Lists.newArrayList();
        for(int i = 1;i<=lastLevel;i++){
            String configValue = getAppConfigByKey("LEVEL_"+i);
            PermissionDescriptionDto permissionDescriptionDto = JSON.parseObject(configValue, PermissionDescriptionDto.class);
            ret.add(permissionDescriptionDto);
        }
        return ret;
    }
    private int checkIsOpenLevel(int code){
        List<PermissionDescriptionDto> config = getPermissionConfig(9);
        for(PermissionDescriptionDto permissionDescriptionDto : config){
            for(PermissionDescriptionDto.PermissionNodeDto node : permissionDescriptionDto.getNodes()){
                if(node.getStatus()==1 && node.getCode().equals(code)){
                    return permissionDescriptionDto.getLevel();
                }
            }
        }
        return 0;
    }

	@Override
	public List<AppConfig> getAppConfigsByType(String type) {
		return userMybatisDao.getAllAppConfigByType(type);
	}

	@Override
	public void refreshConfigCache(){
		List<AppConfig> list = userMybatisDao.getAllAppConfig();
		if(null != list){
			for(AppConfig c : list){
				cacheService.set(CacheConstant.APP_CONFIG_KEY_PRE + c.getConfigKey(), c.getConfigValue());
			}
		}
	}
	@Override
	public UserNo getUserNoByMeNumber(long meNumber){
		return userMybatisDao.getUserNoByMeNumber(meNumber);
	}


    
	@Override
	public boolean isBlacklist(long uid, long targetUid){
		UserBlackList ubl = userMybatisDao.getUserBlackListByUidAndTargetUid(uid, targetUid);
		if(null != ubl){
			return true;
		}
		return false;
	}
	/**
	 * 随机获取n个王国封面。
	 * @author zhangjiwei
	 * @date Jul 20, 2017
	 * @param count
	 * @return
	 */
	@Override
	public List<String> getRandomKingdomCover(int count){
		List<String> picList = this.liveForUserJdbcDao.getRandomKingdomCover(count);
		return picList;
	}

    @Override
    public Response obtainRedBag(ObtainRedBagDto obtainRedBagDto) {
        List<Map<String,Object>> list = userInitJdbcDao.getRedBag(obtainRedBagDto.getUid()+999999999);
	    if (list == null || list.size() == 0){
            String redBag = this.getAppConfigByKey("RED_BAG");
            ModifyUserCoinDto modifyUserCoinDto = this.modifyUserCoin(obtainRedBagDto.getUid(),Integer.parseInt(redBag));
            obtainRedBagDto.setCurrentLevel(modifyUserCoinDto.getCurrentLevel());
            obtainRedBagDto.setUpgrade(modifyUserCoinDto.getUpgrade());
            obtainRedBagDto.setCue("恭喜你获得"+Integer.parseInt(redBag)+"个米汤币");
            //利用现有逻辑.完成领取红包只能一次
            userInitJdbcDao.redBagInsert(obtainRedBagDto.getUid()+999999999,Integer.parseInt(redBag));
            return Response.success(obtainRedBagDto);
        }else {
            return Response.failure(ResponseStatus.ERR_RED_BAG.status,ResponseStatus.ERR_RED_BAG.message);
        }
    }


    @Override
    public Response isObtainRedBag(IsObtainRedBag isObtainRedBag) {
        List<Map<String,Object>> list = userInitJdbcDao.getRedBag(isObtainRedBag.getUid()+999999999);
        if (list == null || list.size() == 0){
            isObtainRedBag.setIsObtainRedBag(0);
            return  Response.success(isObtainRedBag);
        }else {
            isObtainRedBag.setIsObtainRedBag(1);
            return Response.success(isObtainRedBag);
        }
    }

    /**
	 * 赠送3个王国
	 * @author zhangjiwei
	 * @date Jul 19, 2017
	 */
	public void give3Kingdoms(UserProfile profile){
		String nickName = profile.getNickName();
		List<String> picList= getRandomKingdomCover(2);
		if(nickName!=null && nickName.matches("用户\\d+.*")){
			nickName="我";
		}
		if(null != picList && picList.size() >= 2){
			//this.liveForUserJdbcDao.createGiveTopic(profile.getUid(),picList.get(0),nickName+"的生活记录","吃喝玩乐，记录我的日常。","非典型性话痨",1);
			this.liveForUserJdbcDao.createGiveTopic(profile.getUid(),picList.get(0),nickName+"的兴趣爱好","把我的兴趣爱好和你们分享。","玩物不丧志",0);
			this.liveForUserJdbcDao.createGiveTopic(profile.getUid(),picList.get(1),nickName+"的每日一拍","所有美好的事物我统统都要拍下来！","声音和光影",0);
		}else{
			log.info("当前系统赠送王国封面量不足，无法创建赠送王国");
		}
	}
	@Override
	public Response blacklist(long uid, long targetUid, int action){
		if(uid == targetUid){
			return Response.failure(500, "不能自己操作自己");
		}
		
		UserBlackList ubl = userMybatisDao.getUserBlackListByUidAndTargetUid(uid, targetUid);
		if(action == 0){//设置黑名单
			if(null == ubl){
				ubl = new UserBlackList();
				ubl.setUid(uid);
				ubl.setTargetUid(targetUid);
				ubl.setCreateTime(new Date());
				userMybatisDao.saveUserBlackList(ubl);
			}//如果已经存在，则无所谓设置了
		}else if(action == 1){//取消黑名单
			if(null != ubl){
				userMybatisDao.deleteUserBlackListById(ubl.getId());
			}//如果已经不存在了，则无所谓取消了
		}else{
			return Response.failure(500, "不支持的操作类型");
		}
		
		return Response.success(200, "操作成功，请重新刷新列表");
	}
	
	/**
	 * 获取导游信息
	 * @return
	 */
	@Override
	public Response getGuideInfo(){
		String uidStr =   getAppConfigByKey("GUIDE_UID");
		if(StringUtils.isEmpty(uidStr)){
			return Response.failure(500, "没有配置导游用户");
		}else{
			long uid  = Long.parseLong(uidStr);
			GuideInfoDto dto = new GuideInfoDto();
			UserProfile userProfile = userMybatisDao.getUserProfileByUid(uid);
			dto.setUid(uid);
			dto.setNickName(userProfile.getNickName());
			dto.setAvatar(Constant.QINIU_DOMAIN + "/"+ userProfile.getAvatar());
			return Response.success(dto);
		}
		
	}
	
	@Override
	public User getUserByUid(long uid) {
		return userMybatisDao.getUserByUid(uid);
	}
	
	@Override
	public boolean isUserFirst(long uid, int actionType){
		boolean result = true;
		List<UserFirstLog> list = userMybatisDao.getUserFirstLogByUidAndActionType(uid, actionType);
		if(null != list && list.size() > 0){
			result = false;
		}
		return result;
	}
	
	@Override
	public void saveUserFistLog(long uid, int actionType){
		UserFirstLog ufl = new UserFirstLog();
		ufl.setUid(uid);
		ufl.setActionType(actionType);
		ufl.setCreateTime(new Date());
		userMybatisDao.saveUserFirstLog(ufl);
	}
	
	@Override
	public Response awardByInvitation(long uid, long fromUid, int type){
		UserInvitationHis uih = userMybatisDao.getUserInvitationHisByUidAndFromUidAndTypeAndStatus(uid, fromUid, type, 0);//获取未领取过的
		if(null == uih){
			return Response.failure(500, "已经领取过，不能重复领取");
		}
		//增加个人米汤币
		int addCoin = 0;
		if(uih.getCoins().intValue() > 0){
			addCoin = uih.getCoins().intValue();
		}
		ModifyUserCoinDto dto = this.currentUserLevelStatus(uid, uih.getCoins().intValue());
		//标记领取状态
		userMybatisDao.updateUserInvitationReceive(uid, fromUid, type);
		
		
		return Response.success(200, "领取成功", dto);
	}
	
	@Override
	public UserInvitationHis userLastestInvitation(long uid){
		return userMybatisDao.getUserLastestInvitation(uid, 0);
	}
}
