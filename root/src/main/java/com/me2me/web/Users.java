package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.user.dto.*;
import com.me2me.user.service.UserService;
import com.me2me.web.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

    /**
     * 用户注册接口
     * @return
     */
    @RequestMapping(value = "/signUp",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response signUp(SignUpRequest request){
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        userSignUpDto.setMobile(request.getMobile());
        userSignUpDto.setGender(request.getGender());
        userSignUpDto.setStar(request.getStart());
        userSignUpDto.setEncrypt(request.getEncrypt());
        userSignUpDto.setNickName(request.getNickName());
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
    public Response verify(VerifyRequest request){
        VerifyDto verifyDto = new VerifyDto();
        verifyDto.setAction(request.getAction());
        verifyDto.setMobile(request.getMobile());
        verifyDto.setVerifyCode(request.getVerifyCode());
        return userService.verify(verifyDto);
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
       return userService.modifyUserProfile(modifyUserProfileDto);
    }

    /**
     * 修改用户爱好（废弃）
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
     * 感觉意义不是很大 推荐用全量接口
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
     * 全量接口（废弃）
     */
    @ResponseBody
    @RequestMapping(value = "/getBasicData",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getBasicData(){
        return userService.getBasicData();
    }

    /**
     * 第三方登录
     */
    @ResponseBody
    @RequestMapping(value = "/thirdPartAuth",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response thirdPartAuth(ThirdPartAuthRequest request){
        return null;
    }


    /**
     * 收藏夹
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
        if(request.getSinceId() == -1){
            request.setSinceId(Integer.MAX_VALUE);
        }
        UserNoticeDto userNoticeDto = new UserNoticeDto();
        userNoticeDto.setUid(request.getUid());
        userNoticeDto.setSinceId(request.getSinceId());
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
        followDto.setSourceUid(request.getUid());
        followDto.setTargetUid(request.getTargetUid());
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
        if(request.getSinceId()==-1) {
            followParamsDto.setSinceId(Integer.MAX_VALUE);
        }else{
            followParamsDto.setSinceId(request.getSinceId());
        }
        return userService.getFollows(followParamsDto);
    }


    /**
     * 用户关注列表
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

        return userService.getUserProfile(request.getUid());
    }

    @ResponseBody
    @RequestMapping(value = "/init",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public void init(UserProfileRequest request){
        userService.initUserNumber();
        System.out.println("init data success ... ");
    }

    /**
     * 前台获取版本信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/versionControl",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response versionControl(VersionControlRequest request){
        String version = request.getVersion();
        int platform = request.getPlatform();
        return userService.versionControl(version,platform);
    }

    /**
     * 后台添加版本信息
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

}
