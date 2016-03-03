package com.me2me.web;

import com.me2me.common.web.Response;
import com.me2me.im.dto.GroupDto;
import com.me2me.im.dto.GroupMemberDto;
import com.me2me.im.service.ImService;
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
 * Date: 2016/3/1.
 */
@Controller
@RequestMapping(value = "/api/im")
public class Im {

    @Autowired
    private ImService imService;

    /**
     * 创建好有关系
     * @return
     */
    @RequestMapping(value = "/addFriend",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response addFriend(FriendRequest request){
        return imService.addFriend(request.getUid(),request.getFid());
    }

    /**
     * 移除好友关系
     * @param request
     * @return
     */
    @RequestMapping(value = "/removeFriend",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response removeFriend(FriendRequest request){
        return imService.removeFriend(request.getUid(),request.getFid());
    }

    @RequestMapping(value = "/createGroup",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response createGroup(CreateGroupRequest request){
        GroupDto groupDto = new GroupDto();
        groupDto.setUid(request.getUid());
        groupDto.setGroupName(request.getGroupName());
        return imService.createGroup(groupDto);
    }

    @RequestMapping(value = "/addGroupMember",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response addGroupMember(AddGroupMemberRequest request){
        GroupMemberDto groupMemberDto = new GroupMemberDto();
        groupMemberDto.setUid(request.getUid());
        groupMemberDto.setGid(request.getGid());
        return imService.addGroupMember(groupMemberDto);
    }

    @RequestMapping(value = "/removeGroupMember",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response removeGroupMember(RemoveGroupMemberRequest request){
        return imService.removeGroupMember(request.getGroupMemberId()   );
    }

    @RequestMapping(value = "/showFriends",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response showFriends(BasicRequest request){
        return imService.getFriends(request.getUid());
    }

    @RequestMapping(value = "/showGroups",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response showGroups(BasicRequest request){
        return imService.loadGroups(request.getUid());
    }

    @RequestMapping(value = "/showGroupMembers",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response showGroupMembers(ShowGroupMemberRequest request){
        return imService.loadGroupMembers(request.getGid());
    }

}
