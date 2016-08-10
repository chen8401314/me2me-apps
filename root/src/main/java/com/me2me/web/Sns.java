package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.sns.dto.GetSnsCircleDto;
import com.me2me.sns.service.SnsService;
import com.me2me.user.dto.FollowDto;
import com.me2me.web.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/27
 * Time :16:01
 */
@Controller
@RequestMapping(value = "/api/sns")
public class Sns extends BaseController {

    @Autowired
    private SnsService snsService;

    /**
     * 获取成员列表(废弃)
     * @param request
     * @return
     */
    @RequestMapping(value = "/showMemberConsole",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response showMemberConsole(ShowMemberConsoleRequest request){
        return snsService.showMemberConsole(request.getUid(),request.getTopicId());
    }

    /**
     * 获取成员列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/showMembers",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response showMembers(ShowMembersRequest request){
        GetSnsCircleDto getSnsCircleDto = new GetSnsCircleDto();
        getSnsCircleDto.setType(request.getType());
        getSnsCircleDto.setUid(request.getUid());
        getSnsCircleDto.setSinceId((request.getSinceId()-1)*10);
        getSnsCircleDto.setTopicId(request.getTopicId());
        return snsService.showMembers(getSnsCircleDto);
    }

    /**
     * 修改社交关系
     * @param request
     * @return
     */
    @RequestMapping(value = "/modifyCircle",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response modifyCircle(ModifyCircleRequest request){
        return snsService.modifyCircle(request.getUid(),request.getTopicId(),request.getMemberUid(),request.getAction());
    }

    /**
     * 获取个圈子成员
     * @param request
     * @return
     */
    @RequestMapping(value = "/getCircleByType",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response getCircleByType(GetCircleByTypeRequest request){
        GetSnsCircleDto getSnsCircleDto = new GetSnsCircleDto();
        getSnsCircleDto.setTopicId(request.getTopicId());
        getSnsCircleDto.setUid(request.getUid());
        getSnsCircleDto.setType(request.getType());
        getSnsCircleDto.setSinceId((request.getSinceId()-1)*10);
        return snsService.circleByType(getSnsCircleDto);
    }


    /**
     * 订阅
     * @param request
     * @return
     */
    @RequestMapping(value = "/subscribed",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response subscribed(SubscribedRequest request) {
        return snsService.subscribed(request.getUid(),request.getTopicId(),request.getTopId(),request.getBottomId(),request.getAction());
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
        return snsService.follow(request.getAction(),request.getTargetUid(),request.getUid());
    }

}
