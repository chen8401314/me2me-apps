package com.me2me.web;

import com.me2me.activity.dto.QiUserDto;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Response;
import com.me2me.sms.dto.VerifyDto;
import com.me2me.web.request.AwardRequest;
import com.me2me.web.request.QiUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * Created by 马秀成 on 2016/10/18.
 */
@Controller
@RequestMapping(value = "/api/activity")
public class Activity extends BaseController {

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

    /**
     * 七天活动报名用户查询接口
     * 根据传递进来的uid，到七天活动报名用户和系统用户关联表查询是否有，有说明报过名了，再判断该报名用户是否审核通过，如果审核
     * 通过则返回该手机号，并返回该手机号创建的单人王国、双人王国等信息，（还要其他信息等需要了再添加），没有则直接返回没有。
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getActivityUser",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getActivityUser(QiUserRequest request){

        return activityService.getActivityUser(request.getUid());
    }

    /**
     * 七天活动报名接口
     *
     * 接收传入参数，入库即可，状态审核中。
     * 其中需验证短信验证码（短信/语音）
     * 入库后判断如果系统里存在了该手机号（手机号注册的或者第三方登陆但绑定了手机号），则自动默认建立关联。
     * 返回报名成功，并发送报名成功短信
     * 约束条件，手机号必填，并且一个手机号只能报名一次，并且在七天活动中，而且必须处于第1阶段（报名阶段）。
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/enterActivity",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response enterActivity(QiUserRequest request){
        QiUserDto qiUserDto = new QiUserDto();
        qiUserDto.setMobile(request.getMobile());
        qiUserDto.setName(request.getName());
        qiUserDto.setActivityId(request.getActivityId());
        qiUserDto.setAge(request.getAge());
        qiUserDto.setAuditDesc(request.getAuditDesc());
        qiUserDto.setChannel(request.getChannel());
        qiUserDto.setSex(request.getSex());
        qiUserDto.setLiveness(request.getLiveness());
//        qiUserDto.setStatus(request.getStatus());
        qiUserDto.setVerifyCode(request.getVerifyCode());
        return activityService.enterActivity(qiUserDto);
    }

    /**
     * 七天活动报名状态查询绑定接口
     *
     * 验证验证码（短信/语音）
     * 如果是APP过来的（有UID），则判断该报名手机号是否已经绑定过，没有绑定过，则直接和当前UID进行绑定
     * 返回审核状态
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/bindGetActivityUser",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response bindGetActivity(QiUserRequest request){
        return activityService.bindGetActivity(request.getUid(),request.getMobile(),request.getVerifyCode());
    }

    /**
     * 活动信息查询接口
     *
     * 返回活动详情，当前处于的阶段信息，可以返回多个阶段，表示这些阶段同时进行着。
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getActivityInfo",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getActivityInfo(QiUserRequest request){
        return activityService.getActivityInfo(request.getActivityId());
    }

    /**
     * 一键审核功能（七天活动）
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/oneKeyAudit",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response oneKeyAudit(){
        return activityService.oneKeyAudit();
    }

    /**
     * 七天活动创建王国接口
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/createAlive",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response createAlive(){
//        return activityService.createAlive();
        return null;
    }

    /**
     * 活动王国查询接口
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getAliveInfo",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getAliveInfo(QiUserRequest request){
        //（0异性，1同性，2男，3女，4所有）现在没人接收异性0
        return activityService.getAliveInfo(request.getUid() ,request.getTopicName() ,request.getNickName());
    }

    /**
     * 双人王国创建申请接口
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/createApplyDoubleLive",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response createDoubleLive(QiUserRequest request){
        return activityService.createDoubleLive(request.getUid() ,request.getTargetUid() ,request.getActivityId());
    }

    /**
     * 申请信息列表查询接口
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getApplyInfo",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getApplyInfo(QiUserRequest request){
        //类型（0全部，1只要别人给我的申请，2只要我给别人的申请）
        return activityService.getApplyInfo(request.getUid() ,request.getType() ,request.getPageNum() ,request.getPageSize());
    }

    /**
     * 双人王国申请操作接口
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/applyDoubleLive",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response applyDoubleLive(QiUserRequest request){
        return activityService.applyDoubleLive(request.getUid() ,request.getApplyId() ,request.getOperaStatus());
    }

}
