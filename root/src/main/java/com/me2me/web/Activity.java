package com.me2me.web;

import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Response;
import com.me2me.web.request.AwardRequest;
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
 * Created by 马秀成 on 2016/10/18.
 */
@Controller
@RequestMapping(value = "/api/activity")
public class Activity {

    @Autowired
    private ActivityService activityService;

    @ResponseBody
    @RequestMapping(value = "/LuckAward",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response luckAward(AwardRequest request , HttpServletRequest rq){
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

        return activityService.luckAward(request.getUid() ,ip ,request.getActivityName() ,request.getChannel() ,request.getVersion());
    }

    /**
     * 获取抽奖次数接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getAwardCount",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getAwardCount(AwardRequest request){
        return activityService.getAwardCount(request.getUid());
    }


    /**
     * 活动分享次数累加接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/awardShare",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response awardShare(AwardRequest request){
        return activityService.awardShare(request.getUid() ,request.getActivityName());
    }

    /**
     * 检查是否有抽奖资格
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/checkIsAward",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response checkIsAward(AwardRequest request){
        return activityService.checkIsAward(request.getUid() ,request.getActivityName(),request.getChannel() ,request.getVersion() ,request.getToken());
    }

    /**
     * 获取用户中奖信息
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getUserAwardInfo",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getUserAwardInfo(AwardRequest request){
        return activityService.getUserAwardInfo(request.getUid());
    }

    /**
     * 获取抽奖信息(供h5使用)
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getAwardStatus",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getAwardStatus(AwardRequest request){
        return activityService.getAwardStatus(request.getActivityName());
    }

    /**
     * 记录中奖用户信息(供h5使用)
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addWinners",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response addWinners(AwardRequest request){

        return activityService.addWinners(request.getUid() ,request.getActivityName() ,request.getMobile() ,request.getAwardId() ,request.getAwardName());
    }
}
