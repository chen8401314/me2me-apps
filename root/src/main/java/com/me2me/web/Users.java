package com.me2me.web;

import com.me2me.common.utils.CommonUtils;
import com.me2me.common.web.Response;
import com.me2me.kafka.service.KafkaService;
import com.me2me.sms.dto.AwardXMDto;
import com.me2me.sms.dto.VerifyDto;
import com.me2me.sms.service.ChannelType;
import com.me2me.user.dto.*;
import com.me2me.user.service.UserService;
import com.me2me.web.request.*;
import com.me2me.web.utils.VersionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/25.
 */
@Controller
@RequestMapping(value = "/api/user")
public class Users extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private KafkaService kafkaService;

    /**
     * 用户注册接口
     * @return
     */
    @RequestMapping(value = "/signUp",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response signUp(SignUpRequest request,HttpServletRequest req){
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        userSignUpDto.setMobile(request.getMobile());
        userSignUpDto.setGender(request.getGender());
        userSignUpDto.setStar(request.getStart());
        userSignUpDto.setEncrypt(request.getEncrypt());
        userSignUpDto.setNickName(request.getNickName());
        userSignUpDto.setDeviceNo(request.getDeviceNo());
        userSignUpDto.setPlatform(request.getPlatform());
        userSignUpDto.setOs(request.getOs());
        userSignUpDto.setIntroduced(request.getIntroduced());
        userSignUpDto.setChannel(request.getChannel());

        //埋点
//        kafkaService.saveClientLog(request,req.getHeader("User-Agent"),Specification.ClientLogAction.REG_PAGE2_SAVE);

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
        userLoginDto.setOs(request.getOs());
        userLoginDto.setPlatform(request.getPlatform());
        userLoginDto.setDeviceNo(request.getDeviceNo());
        userLoginDto.setJPushToken(request.getJPushToken());
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
        modifyEncryptDto.setFirstEncrypt(request.getFirstEncrypt());
        modifyEncryptDto.setSecondEncrypt(request.getSecondEncrypt());
        return userService.modifyEncrypt(modifyEncryptDto);
    }

    /**
     * 找回密码
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findEncrypt",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response findEncrypt(FindEncryptRequest request){
        FindEncryptDto findEncryptDto = new FindEncryptDto();
        findEncryptDto.setUserName(request.getUserName());
        findEncryptDto.setFirstEncrypt(request.getFirstEncrypt());
        findEncryptDto.setSecondEncrypt(request.getSecondEncrypt());
        return userService.retrieveEncrypt(findEncryptDto);
    }

    /**
     * 获取验证码接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/verify",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response verify(VerifyRequest request,HttpServletRequest req){
        //用的容联
        VerifyDto verifyDto = new VerifyDto();
        verifyDto.setAction(request.getAction());
        verifyDto.setMobile(request.getMobile());
        verifyDto.setVerifyCode(request.getVerifyCode());
        if(request.getChannelAdapter()==0){
            // 兼容老版本
            verifyDto.setChannel(ChannelType.NORMAL_SMS.index);
        }else {
            verifyDto.setChannel(request.getChannelAdapter());
        }
        verifyDto.setIsTest(request.getIsTest());
        
       //埋点
       /* if(request.getAction()==0) {
            kafkaService.saveClientLog(request,req.getHeader("User-Agent"),Specification.ClientLogAction.REG_PAGE1_GET_VERIFY);
        }else{
            kafkaService.saveClientLog(request,req.getHeader("User-Agent"),Specification.ClientLogAction.REG_PAGE1_NEXT);
        }*/
        return userService.verify(verifyDto);
    }

    /**
     * 发送中奖信息短信接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/sendAwardMessage",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response sendAwardMessage(UserAwardRequest request){
        AwardXMDto awardXMDto = new AwardXMDto();
        awardXMDto.setNickName(request.getNickName());
        awardXMDto.setAwardName(request.getAwardName());
        awardXMDto.setMobile(request.getMobile());
        return userService.sendAwardMessage(awardXMDto);
    }

    /**
     * 用户资料修改
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/modifyUserProfile",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response modifyUserProfile(ModifyUserProfileRequest request){
        ModifyUserProfileDto modifyUserProfileDto = new ModifyUserProfileDto();
        modifyUserProfileDto.setGender(request.getGender());
        modifyUserProfileDto.setNickName(request.getNickName());
        modifyUserProfileDto.setYearsId(request.getYearsId());
        modifyUserProfileDto.setUid(request.getUid());
        modifyUserProfileDto.setAvatar(request.getAvatar());
        modifyUserProfileDto.setBirthday(request.getBirthday());
        modifyUserProfileDto.setHobby(request.getHobby());
        modifyUserProfileDto.setIntroduced(request.getIntroduced());
       return userService.modifyUserProfile(modifyUserProfileDto);
    }

    /**
     * 修改用户爱好（暂未启用）
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/modifyUserHobby",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response modifyUserHobby(ModifyUserHobbyRequest request){
        ModifyUserHobbyDto modifyUserHobbyDto = new ModifyUserHobbyDto();
        modifyUserHobbyDto.setUserName(request.getUserName());
        modifyUserHobbyDto.setHobby(request.getHobby());
        return userService.modifyUserHobby(modifyUserHobbyDto);
    }

    /**
     * 获取用户基础数据
     *
     */
    @ResponseBody
    @RequestMapping(value = "/getBasicDataByType",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getBasicDataByType(BasicDataRequest request){
        BasicDataDto basicDataDto = new BasicDataDto();
        basicDataDto.setType(request.getType());
        return userService.getBasicDataByType(basicDataDto);
    }

    /**
     * 获取用户基础数据
     * 全量接口（暂未启用）
     */
    @ResponseBody
    @RequestMapping(value = "/getBasicData",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getBasicData(){
        return userService.getBasicData();
    }

    /**
     * 第三方登录（暂未启用）
     */
    @ResponseBody
    @RequestMapping(value = "/thirdPartAuth",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response thirdPartAuth(ThirdPartAuthRequest request){
        return null;
    }

    /**
     * 收藏夹（废弃）
     */
    @ResponseBody
    @RequestMapping(value = "/favorite",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response favorite(ThirdPartAuthRequest request){
        return null;
    }

    /**
     * 贴标签（废弃）
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/pasteTag",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response pasteTags(TagRequest request){
        PasteTagDto pasteTagDto = new PasteTagDto();
        pasteTagDto.setTag(request.getTag());
        pasteTagDto.setTargetUid(request.getTargetUid());
        pasteTagDto.setFromUid(request.getFromUid());
        return userService.writeTag(pasteTagDto);
    }

    /**
     * 获取用户标签列表（废弃）
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/showUserTags",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response showUserTags(GetTagRequest request){
        return userService.showUserTags(request.getUid());
    }

    /**
     * 获取用户标签点赞（废弃）
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/likes",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response likes(LikesRequest request){
        UserLikeDto userLikeDto = new UserLikeDto();
        userLikeDto.setCustomerId(request.getCustomerId());
        userLikeDto.setTid(request.getTid());
        userLikeDto.setUid(request.getUid());
        return userService.likes(userLikeDto);
    }

    /**
     * 用户消息列表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/userNotice",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response userNotice(UserNoticeRequest request){
        if(request.getSinceId() <= 0){
            request.setSinceId(Integer.MAX_VALUE);
        }
        UserNoticeDto userNoticeDto = new UserNoticeDto();
        userNoticeDto.setUid(request.getUid());
        userNoticeDto.setSinceId(request.getSinceId());
        userNoticeDto.setLevel(request.getLevel());
        return userService.getUserNotice(userNoticeDto);
    }

    /**
     * 轮寻方式获取用户消息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/userTips",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response userTips(ShowUserTipsRequest request){
        return userService.getUserTips(request.getUid());
    }

    /**
     * 轮寻方式获取用户消息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/cleanUserTips",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response cleanUserTips(ShowUserTipsRequest request){
        return userService.cleanUserTips(request.getUid());
    }

    /**
     * 举报接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/userReport",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response userReport(UserReportRequest request){
        UserReportDto userReportDto = new UserReportDto();
        userReportDto.setUid(request.getUid());
        userReportDto.setCid(request.getCid());
        userReportDto.setReason(request.getReason());
        userReportDto.setAttachment(request.getAttachment());
        return userService.userReport(userReportDto);
    }

    /**
     * 用户关注|取消关注
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/follow",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response follow(UserFollowRequest request){
        FollowDto followDto = new FollowDto();
        followDto.setAction(request.getAction());
        followDto.setTargetUid(request.getTargetUid());
        followDto.setSourceUid(request.getUid());
        return userService.follow(followDto);
    }

    /**
     * 用户粉丝列表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/showFans",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response showFans(ShowFansRequest request){
        FansParamsDto fansParamsDto = new FansParamsDto();
        fansParamsDto.setTargetUid(request.getCustomerId());
        fansParamsDto.setUid(request.getUid());
        if(request.getSinceId()==-1) {
            fansParamsDto.setSinceId(Integer.MAX_VALUE);
        }else{
            fansParamsDto.setSinceId(request.getSinceId());
        }
        return userService.getFans(fansParamsDto);
    }

    /**
     * 用户关注列表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/showFollows",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response showFollows(ShowFansRequest request){
        FollowParamsDto followParamsDto = new FollowParamsDto();
        followParamsDto.setSourceUid(request.getCustomerId());
        followParamsDto.setUid(request.getUid());
        if(request.getSinceId()==-1) {
            followParamsDto.setSinceId(Integer.MAX_VALUE);
        }else{
            followParamsDto.setSinceId(request.getSinceId());
        }
        return userService.getFollows(followParamsDto);
    }

    /**
     * 用户粉丝列表(根据关注时间排序)
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/showFans2",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response showFans2(ShowFansRequest request){
        FansParamsDto fansParamsDto = new FansParamsDto();
        fansParamsDto.setTargetUid(request.getCustomerId());
        fansParamsDto.setUid(request.getUid());
        if(request.getSinceId() <1) {
           request.setSinceId(1);
        }
        fansParamsDto.setSinceId((request.getSinceId() - 1) * 10);
        return userService.getFansOrderByTime(fansParamsDto);
    }

    /**
     * 用户关注列表(根据关注时间排序)
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/showFollows2",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response showFollows2(ShowFansRequest request){
        FollowParamsDto followParamsDto = new FollowParamsDto();
        followParamsDto.setSourceUid(request.getCustomerId());
        followParamsDto.setUid(request.getUid());
        if(request.getSinceId() < 1) {
            request.setSinceId(1);
        }
        followParamsDto.setSinceId((request.getSinceId() - 1) * 10);
        return userService.getFollowsOrderByTime(followParamsDto);
    }

    /**
     * 用户关注列表(在直播中启用)
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getUser",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getUser(UserRequest request){
        return userService.getUser(request.getTargetUid(),request.getUid());
    }

    /**
     * 获取用户信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getUserProfile",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getUserProfile(UserProfileRequest request){
    	int vflag = 0;
        if(VersionUtil.isNewVersion(request.getVersion(), "2.2.0")){
        	vflag = 1;
        }
        return userService.getUserProfile(request.getUid(), vflag);
    }

    /**
     * 初始化me号
     * @param request
     */
    @ResponseBody
    @RequestMapping(value = "/init",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public void init(ActivityRequest request){
        userService.initUserNumber(request.getSinceId());
    }

    /**
     * 前台获取版本信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/versionControl",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response versionControl(VersionControlRequest request ,HttpServletRequest rq){
        //获取ipaddress信息
        String ip = rq.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = rq.getHeader("Proxy-Client-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = rq.getHeader("WL-Proxy-Client-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = rq.getHeader("HTTP_CLIENT_IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = rq.getHeader("HTTP_X_FORWARDED_FOR");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        	ip = rq.getHeader("X-Real-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
            ip = rq.getRemoteAddr();
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip))
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            }
            catch (UnknownHostException unknownhostexception) {
            }
        return userService.versionControl(request.getVersion(),request.getPlatform(),ip,request.getChannel(),request.getDevice());
    }

    /**
     * 后台添加版本信息(运营使用，暂未启用)
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateVersion",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response updateVersion(UpdateVersionRequest request){
        VersionDto controlDto = new VersionDto();
        controlDto.setUpdateUrl(request.getUpdateUrl());
        controlDto.setVersion(request.getVersion());
        controlDto.setPlatform(request.getPlatform());
        controlDto.setUpdateDescription(request.getUpdateDescription());
        return userService.updateVersion(controlDto);
    }

    /**
     * 给老徐提供查询人员信息接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getSpecialUserProfile",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getSpecialUserProfile(SpecialUserProfileRequest request){
        return userService.getSpecialUserProfile(request.getUid());
    }

    /**
     * 后台设置大V接口(运营使用，暂未启用)
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/userExcellent",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response userExcellent(UserExcellentRequest request){
        return userService.setUserExcellent(request.getUid());
    }

    /**
     * 退出接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/logout",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response logout(LogoutRequest request){
        return userService.logout(request.getUid());
    }

    /**
     * s生成推广二维码
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/qrcoe",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response circle(QrCodeRequest request){
        return userService.genQRcode(request.getUid());
    }

    /**
     * 推广用户注册
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/refereeSignUp",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response refereeSignUp(RefereeSignUpRequest request){
        UserRefereeSignUpDto userRefereeSignUpDto = new UserRefereeSignUpDto();
        userRefereeSignUpDto.setMobile(request.getMobile());
        userRefereeSignUpDto.setGender(request.getGender());
        userRefereeSignUpDto.setStar(request.getStart());
        userRefereeSignUpDto.setEncrypt(request.getEncrypt());
        userRefereeSignUpDto.setNickName(request.getNickName());
        userRefereeSignUpDto.setDeviceNo(request.getDeviceNo());
        userRefereeSignUpDto.setPlatform(request.getPlatform());
        userRefereeSignUpDto.setOs(request.getOs());
        userRefereeSignUpDto.setIntroduced(request.getIntroduced());
        userRefereeSignUpDto.setRefereeUid(request.getRefereeUid());
        return userService.refereeSignUp(userRefereeSignUpDto);
    }

//    /**
//     * 推广页面获取用户信息
//     * @param request
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping(value = "/getUserProfile4H5",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
//    public Response getUserProfile4H5(GetUserProfile4H5Request request){
//        return userService.getUserProfile4H5(request.getUid());
//    }

    @ResponseBody
    @RequestMapping(value = "/getRefereeProfile",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getRefereeProfile(UserProfileRequest request){
        return userService.getRefereeProfile(request.getUid());
    }

    /**
     * 第三方登录接口
     */
    @ResponseBody
    @RequestMapping(value = "/thirdPartLogin",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response thirdPartLogin(ThirdPartRequest request){
        ThirdPartSignUpDto dto = new ThirdPartSignUpDto();
        dto.setThirdPartOpenId(request.getThirdPartOpenId());
        dto.setThirdPartToken(request.getThirdPartToken());
        dto.setAvatar(request.getAvatar());
        dto.setThirdPartType(request.getThirdPartType());
        dto.setNickName(request.getNickName());
        dto.setGender(request.getGender());
        dto.setJPushToken(request.getJPushToken());
//        dto.setUid(request.getUid());
        dto.setUnionId(request.getUnionId());
        dto.setH5type(request.getH5type());
        dto.setNewNickName(request.getNewNickName());
        dto.setChannel(request.getChannel());
        dto.setPlatform(request.getPlatform());
        return userService.thirdPartLogin(dto);
    }

    /**
     * 广告模式接口
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/activityModel",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response activityModel(){
        ActivityModelDto dto = new ActivityModelDto();
        return userService.activityModel(dto);
    }

    /**
     * 检查用户名是否存在接口，判断OPENID是否存在是否还需要上传头像接口
     */
    @ResponseBody
    @RequestMapping(value = "/checkNameOpenId",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response checkNickName(CheckRequest request){
        UserNickNameDto userNickNameDto = new UserNickNameDto();
        if(request.getNickName()!=null) {
            userNickNameDto.setNickName(request.getNickName());
        }else {
            userNickNameDto.setOpenid(request.getOpenId());
            userNickNameDto.setUnionId(request.getUnionId());
            userNickNameDto.setThirdPartType(request.getThirdPartType());
        }
        return userService.checkNameOpenId(userNickNameDto);
    }

    /**
     * 绑定接口
     */
    @ResponseBody
    @RequestMapping(value = "/bind",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response bind(ThirdPartRequest request){
        ThirdPartSignUpDto dto = new ThirdPartSignUpDto();
        dto.setUid(request.getUid());
        dto.setThirdPartType(request.getThirdPartType());
        dto.setMobile(request.getMobile());
        dto.setEncrypt(request.getEncrypt());
        dto.setThirdPartOpenId(request.getThirdPartOpenId());
        dto.setThirdPartToken(request.getThirdPartToken());
        dto.setUnionId(request.getUnionId());
        return userService.bind(dto);
    }

    /**
     * 禁言接口
     */
    @ResponseBody
    @RequestMapping(value = "/gag",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response gag(GagRequest request){
        GagDto dto = (GagDto) CommonUtils.copyDto(request,new GagDto());

        return userService.gag(dto);
    }

    /**
     * 获取入口页配置接口
     */
    @ResponseBody
    @RequestMapping(value = "/entryPageConfig",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response entryPageConfig(EntryPageRequest request){

        EntryPageDto dto = (EntryPageDto) CommonUtils.copyDto(request,new EntryPageDto());

        return userService.getEntryPageConfig(dto);
    }
    /**
     * 上大V接口
     */
//    @ResponseBody
//    @RequestMapping(value = "/addV",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
//    public Response addV(UserInfoRequest request){
//        UserVDto vDto = new UserVDto();
//        vDto.setCustomerId(request.getCustomerId());
//        return userService.addV(vDto);
//    }

    /**
     * 游客模式登录(app)
     */
    @ResponseBody
    @RequestMapping(value = "/touristLogin",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response touristLogin(){
        return userService.touristLogin();
    }

    /**
     * 推送测试入口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/testPush",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response testPush(TestPushRequest request){
    	return userService.testPush(request.getUid(), request.getMsg(), request.getJsonData());
    }

    /**
     * 用户推荐接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/Recommend",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response userRecomm(UserFamousRequest request){
        return userService.userRecomm(request.getUid() ,request.getTargetUid() ,request.getAction());
    }

}
