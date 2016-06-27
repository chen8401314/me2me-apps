package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.sns.service.SnsService;
import com.me2me.web.request.ModifyCircleRequest;
import com.me2me.web.request.ShowMemberConsoleRequest;
import com.me2me.web.request.ShowMembersRequest;
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
     * 获取成员列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/showMemberConsole",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response showMemberConsole(ShowMemberConsoleRequest request){
        return snsService.showMemberConsole(request.getUid(),request.getTopicId(),request.getSinceId());
    }

    /**
     * 获取成员列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/showMembers",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response showMembers(ShowMembersRequest request){
        return snsService.showMembers(request.getUid(),request.getTopicId(),request.getSinceId());
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

}
