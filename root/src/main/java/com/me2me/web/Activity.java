package com.me2me.web;

import com.me2me.activity.dto.Activity7DayMiliDTO;
import com.me2me.activity.dto.QiUserDto;
import com.me2me.activity.service.ActivityService;
import com.me2me.common.web.Response;
import com.me2me.web.request.AcceptTaskRequest;
import com.me2me.web.request.ActivityMiliRequest;
import com.me2me.web.request.AnchorListRequest;
import com.me2me.web.request.AreaHotRequest;
import com.me2me.web.request.AreaSupportRequest;
import com.me2me.web.request.AwardRequest;
import com.me2me.web.request.ChatQueryRequest;
import com.me2me.web.request.ChatRequest;
import com.me2me.web.request.CheckUserActivityKindomRequest;
import com.me2me.web.request.EnterAnchorRequest;
import com.me2me.web.request.GameReceiveCoinsRequest;
import com.me2me.web.request.GameResultRequest;
import com.me2me.web.request.GameUserInfoRequest;
import com.me2me.web.request.GetTaskListRequest;
import com.me2me.web.request.OptForcedPairingRequest;
import com.me2me.web.request.QiUserRequest;
import com.me2me.web.request.RecommendHistoryRequest;
import com.me2me.web.request.SpecailTopicBillboardRequest;
import com.me2me.web.request.Top10SupportChatQueryRequest;
import com.me2me.web.request.UserTaskStatusRequest;

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
/**
 * @author pc308
 *
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
        qiUserDto.setUid(request.getUid());
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
        return activityService.getAliveInfo(request.getUid() ,request.getTopicName() ,request.getNickName() ,
                request.getPageNum() ,request.getPageSize());
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

    /**
     * 抢亲申请接口
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/bridApply",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response bridApply(QiUserRequest request){
        return activityService.bridApply(request.getUid() ,request.getTargetUid());
    }

    /**
     * 抢亲查询接口
     * 获取我抢别人的 列表
     * 获取别人抢我的 列表
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/rob-apply-list",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response bridSearch(QiUserRequest request){
        return activityService.bridSearch(request.getUid() ,request.getType() ,request.getPageNum() ,request.getPageSize());
    }

    /**
     * 抢亲操作接口
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/operaBrid",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response operaBrid(QiUserRequest request){
        return activityService.operaBrid(request.getUid() ,request.getApplyId() ,request.getOperaStatus());
    }
    
    /**
     * 七天活动米粒说接口
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/milidata",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response milidata(ActivityMiliRequest request){
    	if(request.getActivityId() == 0 || request.getActivityId() == 1){
    		Activity7DayMiliDTO dto = new Activity7DayMiliDTO();
        	dto.setAuid(request.getAuid());
        	dto.setIsApp(request.getIsApp());
        	dto.setIsFirst(request.getIsFirst());
    		return activityService.genActivity7DayMiliList(dto);
    	}else if(request.getActivityId() == 2){
    		return activityService.genMiliList4Spring(request.getUid());
    	}
    	
    	return Response.failure(500, "不识别的活动");
    }

    /**
     * 双人王国状态接口(包含抢亲次数)
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/doublueLiveState",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response doublueLiveState(QiUserRequest request){
        return activityService.doublueLiveState(request.getUid());
    }

    /**
     * 离婚接口
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/divorce",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response divorce(QiUserRequest request){
        return activityService.divorce(request.getUid() ,request.getTargetUid());
    }
    
    /**
     * 推荐历史列表接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/recommendHistory",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response recommendHistory(RecommendHistoryRequest request){
    	return activityService.recommendHistory(request.getAuid(), request.getPage(), request.getPageSize());
    }
    
    /**
     * 强配操作接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/optForcedPairing",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response optForcedPairing(OptForcedPairingRequest request){
    	return activityService.optForcedPairing(request.getUid(), request.getAction());
    }

    /**
     * 抢亲列表查询
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getBridList",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getBridList(QiUserRequest request){
        //（0异性，1同性，2男，3女，4所有）现在没人接收异性0
        return activityService.getBridList(request.getUid() ,request.getTopicName() ,request.getNickName() ,
                request.getPageNum() ,request.getPageSize() ,request.getType());
    }
    
    /**
     * 获取任务列表接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getTaskList",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response getTaskList(GetTaskListRequest request){
    	return activityService.getTaskList(request.getUid(), request.getPage(), request.getPageSize());
    }
    
    /**
     * 接受任务接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/acceptTask",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response acceptTask(AcceptTaskRequest request){
    	return activityService.acceptTask(request.getTid(), request.getUid());
    }
    
    /**
     * 用户任务状态查询接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/userTaskStatus",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response userTaskStatus(UserTaskStatusRequest request){
    	return activityService.userTaskStatus(request.getTid(), request.getUid());
    }

    /**
     * 推荐列表（我可以抢亲的列表）
     * 暂时调用了getBridList接口 只是多传了个参数 显示顺序不同
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/recommendBridList",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response recommendBridList(QiUserRequest request){
        //（0异性，1同性，2男，3女，4所有）现在没人接收异性0
        return activityService.getBridList(request.getUid() ,request.getTopicName() ,request.getNickName() ,
                request.getPageNum() ,request.getPageSize() ,request.getType());
    }

    /**
     * 活动王国是否有资格创建接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/checkUserActivityKindom",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public Response checkUserActivityKindom(CheckUserActivityKindomRequest request){
    	Response resp = null;
    	if(request.getActivityId() == 2){//春节活动
    		resp = activityService.checkUserActivityKindom4Spring(request.getUid());
    	}else{//默认为七天活动
    		resp = activityService.checkUserActivityKindom(request.getUid(), request.getType(), request.getUid2());
    	}
    	
    	if(null != resp){
    		if(resp.getCode() == 200){
    			return Response.success();
    		}else{
    			return Response.failure(500, (String)resp.getData());
    		}
    	}
    	return Response.failure("失败");
    }

    /**
     * 春节活动王国信息查询
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getNewYearLiveInfo")
    public Response getNewYearLiveInfo(QiUserRequest request){
        return activityService.getNewYearLiveInfo(request.getUid() ,request.getActivityId());
    }
    
    @ResponseBody
    @RequestMapping(value = "/abcdefggggggg123")
    public Response getNewYearLiveInfo2(QiUserRequest request){
        return activityService.getNewYearLiveInfo(request.getUid() ,request.getActivityId());
    }

    /**
     * 春节活动王国搜索接口
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getAllNewYearLiveInfo")
    public Response getAllNewYearLiveInfo(QiUserRequest request){
        return activityService.getAllNewYearLiveInfo(request.getUid() ,request.getActivityId()
                ,request.getPageNum() ,request.getPageSize() ,request.getTopicName() ,request.getNickName());
    }

    /**
     * 灯箱内容获取接口
     *
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getlightboxInfo")
    public Response getlightboxInfo(){
        return activityService.getlightboxInfo();
    }

    /**
     * 实时&历史榜单查询接口
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getActualAndHistoryList")
    public Response getActualAndHistoryList(QiUserRequest request){
        return activityService.getActualAndHistoryList(request.getUid() ,request.getType() ,request.getDate() ,request.getActivityId());
    }

    /**
     * 活动王国/用户榜单获取接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/billboard")
    public Response specailTopicBillboard(SpecailTopicBillboardRequest request){
    	return activityService.specailTopicBillboard(request.getType(), request.getSearchUid());
    }
    
    /**
     * 地区热度查询接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/areaHot")
    public Response areaHot(AreaHotRequest request){
    	return activityService.areaHot(request.getTopicId());
    }
    
    /**
     * 地区支持接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/areaSupport")
    public Response areaSupport(AreaSupportRequest request){
    	return activityService.areaSupport(request.getOptUid(), request.getTopicId());
    }
    
    /**
     * 广播聊天查询接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/chatQuery")
    public Response chatQuery(ChatQueryRequest request){
    	return activityService.chatQuery(request.getSinceId());
    }
    
    /**
     * top10用户支持信息获取接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/top10SupportChatQuery")
    public Response top10SupportChatQuery(Top10SupportChatQueryRequest request){
    	return activityService.top10SupportChatQuery();
    }
    
    /**
     * 广播发言接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/chat")
    public Response chat(ChatRequest request){
    	return activityService.chat(request.getChatUid(), request.getMessage());
    }
    
    /**
     * 主播活动主播列表获取接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/anchorList")
    public Response anchorList(AnchorListRequest request){
    	return activityService.anchorList(request.getUid());
    }
    
    /**
     * 主播活动报名接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/enterAnchor")
    public Response enterAnchor(EnterAnchorRequest request){
    	return activityService.enterAnchor(request.getUid(), request.getAid());
    }
    
    /**
     * 游戏活动信息查询接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/gameUserInfo")
    public Response gameUserInfo(GameUserInfoRequest request){
    	return activityService.gameUserInfo(request.getGameUid(),request.getGameChannel(),request.getUid());
    }
    
    
    /**游戏活动游戏结果接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/gameResult")
    public Response gameResult(GameResultRequest request){
    	return activityService.gameResult(request.getUid(),request.getGameId(),request.getRecord());
    }
    
    /**游戏活动领取奖金池接口
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/gameReceiveCoins")
    public Response gameReceiveCoins(GameReceiveCoinsRequest request){
    	return activityService.gameReceiveCoins(request.getUid());
    }
}
