package com.me2me.web;

import com.me2me.common.security.SecurityUtils;
import com.me2me.common.web.Response;
import com.me2me.user.dto.*;
import com.me2me.user.model.UserTags;
import com.me2me.user.service.UserService;
import com.me2me.web.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/25.
 */
@Controller
@RequestMapping(value = "/api/user")
public class Users {

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
        modifyUserProfileDto.setAction(request.getAction());
        modifyUserProfileDto.setUserName(request.getUserName());
        modifyUserProfileDto.setBearStatus(request.getBearStatus());
        modifyUserProfileDto.setGender(request.getGender());
        modifyUserProfileDto.setIndustry(request.getIndustry());
        modifyUserProfileDto.setMarriageStatus(request.getMarriageStatus());
        modifyUserProfileDto.setStartId(request.getStartId());
        modifyUserProfileDto.setNickName(request.getNickName());
        modifyUserProfileDto.setSocialClass(request.getSocialClass());
        modifyUserProfileDto.setYearsId(request.getYearsId());
        modifyUserProfileDto.setUid(request.getUid());
        modifyUserProfileDto.setAvatar(request.getAvatar());
       return  userService.modifyUserProfile(modifyUserProfileDto);
    }

    /**
     * 修改用户爱好
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
     * 全量接口
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
     * 贴标签
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
     * 用户消息列表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/userNotice",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response userNotice(UserNoticeRequest request){
      return null;
    }

}
