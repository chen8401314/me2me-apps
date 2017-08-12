package com.me2me.web;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.me2me.cache.service.CacheService;
import com.me2me.common.web.BaseEntity;
import com.me2me.common.web.Response;
import com.me2me.io.service.FileTransferService;
import com.me2me.live.dto.*;
import com.me2me.live.service.LiveService;
import com.me2me.user.dto.LoginSuccessDto;
import com.me2me.user.dto.ThirdPartSignUpDto;
import com.me2me.user.service.UserService;
import com.me2me.web.dto.WxUser;
import com.me2me.web.request.MobileLiveDetailRequest;
import com.me2me.web.utils.VersionUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc329 on 2017/4/5.
 */
@Slf4j
@Controller
@RequestMapping("/api/mobile")
@CrossOrigin(origins = "*")
public class Mobile extends BaseController {


    private static final long DEFAULT_UID = 100;

    @Autowired
    private LiveService liveService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private FileTransferService fileTransferService;

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/live-cover")
    @ResponseBody
    public Response liveCover(MobileLiveDetailRequest request) {
        int vflag = 0;
        if (VersionUtil.isNewVersion(request.getVersion(), "2.2.3")) {
            vflag = 1;
        }
        return liveService.liveCover(request.getTopicId(), DEFAULT_UID, vflag, 1,request.getFromUid());//这里肯定是APP外的
    }

    @RequestMapping(value = "/showLiveDetails")
    @ResponseBody
    public Response showLiveDetails(MobileLiveDetailRequest request) {

        int offset = request.getOffset() == 0 ? 50 : request.getOffset();
        int pageNo = request.getPageNo() == 0 ? 1 : request.getPageNo();

        // 获取王国基本信息
        int vflag = 0;
        if (VersionUtil.isNewVersion(request.getVersion(), "2.2.3")) {
            vflag = 1;
        }
        Response response = liveService.getLiveByCid(request.getTopicId(), DEFAULT_UID, vflag);
        ShowLiveDto showLiveDto = (ShowLiveDto) response.getData();
        // 总页数
        GetLiveUpdateDto getLiveUpdateDto = new GetLiveUpdateDto();
        getLiveUpdateDto.setOffset(offset);
        getLiveUpdateDto.setSinceId(request.getSinceId());
        getLiveUpdateDto.setTopicId(request.getTopicId());
        LiveUpdateDto pagination = (LiveUpdateDto) liveService.getLiveUpdate(getLiveUpdateDto).getData();
        // 分页取出数据
        MobileLiveDetailsDto mobileLiveDetailsDto = new MobileLiveDetailsDto();
        mobileLiveDetailsDto.setLiveBasicData(showLiveDto);
        mobileLiveDetailsDto.setLivePaginationData(pagination);
        GetLiveDetailDto getLiveDetailDto = new GetLiveDetailDto();
        getLiveDetailDto.setTopicId(request.getTopicId());
        getLiveDetailDto.setSinceId(request.getSinceId());
        getLiveDetailDto.setDirection(request.getDirection());
        getLiveDetailDto.setPageNo(pageNo);
        getLiveDetailDto.setOffset(offset);
        getLiveDetailDto.setUid(DEFAULT_UID);
        LiveDetailDto liveDetailDto = (LiveDetailDto) liveService.getLiveDetail(getLiveDetailDto).getData();
        mobileLiveDetailsDto.setLiveDetailData(liveDetailDto);
        return Response.success(mobileLiveDetailsDto);
    }

    /**
     * 微信授权
     *
     * @param id 王国ID
     * @return
     */
    @RequestMapping(value = "/wxOauth")
    public String wxOauth(String id) {
        // 过期时间为5分钟
        cacheService.setex("wxOauth:" + id, id, 600);
        // product env
        /*String api = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx06b8675378eb1a62&redirect_uri=https://webapp.me-to-me.com/wx_login_callback&response_type=code&scope=snsapi_login&state=" + id + "#wechat_redirect";
        return "redirect:" + api;*/

        // local test env
        String api = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx06b8675378eb1a62&redirect_uri=http://webapp.me-to-me.com/api/mobile/wx_login_callback&response_type=code&scope=snsapi_login&state=" + id + "#wechat_redirect";
        return "redirect:" + api;
    }

/*    @RequestMapping(value = "/wxAuthCallback")
    @ResponseBody
    public Response wxAuthCallback(String uid) {
        // 过期时间为5分钟
        String loginResponse = cacheService.get("WX-LOGIN:" + uid);
        if(!StringUtils.isEmpty(loginResponse)){
            Map object = JSON.parseObject(loginResponse,Map.class);
            LoginSuccessResponse loginSuccessResponse = new LoginSuccessResponse();
            loginSuccessResponse.setData(object);
            return Response.success(200,"ok",loginSuccessResponse);
        }else{
            return Response.failure(44444,"获取用户信息失败!");
        }
    }*/

/*    @Data
    class LoginSuccessResponse implements BaseEntity{
        private Object data;
    }*/


    @RequestMapping(value = "/wx_login_callback")
    public void wxLoginCallback(HttpServletResponse response, HttpServletRequest request) throws IOException {
        //判断是否注册
        String id = request.getParameter("state");
        String redirectUrl = "http://192.168.89.187:8080/#/ld/" + id;
        String code = request.getParameter("code");
        if (id.equals(cacheService.get("wxOauth:" + id))) {
            // 已经受过权了
            // 获取用户信息
            try {
                String userInfoRet = fileTransferService.getUserInfo(code);
                WxUser wxUser = JSON.parseObject(userInfoRet, WxUser.class);
                ThirdPartSignUpDto thirdPartSignUpDto = new ThirdPartSignUpDto();
                thirdPartSignUpDto.setThirdPartOpenId(wxUser.getOpenid());
                thirdPartSignUpDto.setThirdPartToken(wxUser.getAccess_token());
                thirdPartSignUpDto.setAvatar(wxUser.getHeadimgurl());
                // 微信登陆
                thirdPartSignUpDto.setThirdPartType(2);
                thirdPartSignUpDto.setNickName(wxUser.getNickname());
                thirdPartSignUpDto.setGender(wxUser.getSex()!=null&&wxUser.getSex().equals("1") ? 1 : 0);
                // h5方式登陆
                thirdPartSignUpDto.setH5type(1);
                thirdPartSignUpDto.setUnionId(wxUser.getUnionid());

                Response loginRet = userService.thirdPartLogin(thirdPartSignUpDto);
                LoginSuccessDto loginSuccessDto = (LoginSuccessDto) loginRet.getData();
                // set redis cache data mock j2ee session
                // avatar : user_info.avatar,
                //                nickName,
                //                type : 1,
                //                contentType : 0,
                //                fragment : value
                Map<String,Object> map = Maps.newConcurrentMap();
                map.put("nickName",loginSuccessDto.getNickName());
                map.put("avatar",thirdPartSignUpDto.getAvatar());
                map.put("uid",loginSuccessDto.getUid());
                String json = JSON.toJSONString(map);
                cacheService.setex("WX-LOGIN:"+loginSuccessDto.getUid(),json,600);
                if (loginRet.getCode() == 2001  ||  loginRet.getCode() == 20062) {
                    response.sendRedirect(redirectUrl+"?uid="+loginSuccessDto.getUid());
                } else {
                    redirectUrl += "?err=1";
                    response.sendRedirect(redirectUrl);
                }


            } catch (Exception e) {
                e.printStackTrace();
                log.error("invoke userInfo for wx platform has a exception {}", e.getMessage());
            }


        }else{
            //已经注册直接跳转王国页面
            response.sendRedirect(redirectUrl);
        }
    }
}





