package com.me2me.im.service;

import com.me2me.common.web.Response;
import com.me2me.common.web.ResponseStatus;
import com.me2me.im.dao.ImMybatisDao;
import com.me2me.im.dto.*;
import com.me2me.im.model.Friend;
import com.me2me.im.model.Group;
import com.me2me.im.model.GroupMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Service
public class ImServiceImpl implements ImService {

    @Autowired
    private ImMybatisDao imMybatisDao;

    /**
     * 添加好友关系
     * @param uid
     * @param fid
     * @return
     */
    public Response addFriend(long uid, long fid) {
        if(uid==fid){
            // 不能添加自己为好友
            return Response.failure(ResponseStatus.USER_ADD_FRIEND_ERROR.status,ResponseStatus.USER_ADD_FRIEND_ERROR.message);
        }
        Friend friend = new Friend();
        friend.setUid(uid);
        friend.setFid(fid);
        imMybatisDao.addFriend(friend);
        friend = new Friend();
        friend.setUid(fid);
        friend.setFid(uid);
        imMybatisDao.addFriend(friend);
        return Response.success(ResponseStatus.USER_ADD_FRIEND_SUCCESS.status,ResponseStatus.USER_ADD_FRIEND_SUCCESS.message);
    }

    /**
     * 删除好友关系（和产品确认过是双向删除）
     * @param uid
     * @param fid
     * @return
     */
    public Response removeFriend(long uid,long fid) {
        Friend friend = imMybatisDao.getFriendByUidAndFid(uid,fid);
        imMybatisDao.removeFriend(friend);
        // 双向删除
        Friend disFriend = imMybatisDao.getFriendByUidAndFid(fid,uid);
        imMybatisDao.removeFriend(disFriend);
        return Response.success(ResponseStatus.USER_REMOVE_FRIEND_SUCCESS.status,ResponseStatus.USER_REMOVE_FRIEND_SUCCESS.message);
    }

    /**
     * 用户创建群组
     * @param groupDto
     * @return
     */
    public Response createGroup(GroupDto groupDto) {
        Group group = new Group();
        group.setOwner(groupDto.getUid());
        group.setGroupName(groupDto.getGroupName());
        imMybatisDao.createGroup(group);
        String groupMember = groupDto.getGroupMember();
        if(!StringUtils.isEmpty(groupMember)){
            String[] members = groupMember.split(",");
            for (String m : members){
                GroupMember member = new GroupMember();
                member.setUid(Long.parseLong(m));
                member.setGid(group.getId());
                imMybatisDao.addGroupMember(member);
            }
        }
        return Response.success(ResponseStatus.USER_CREATE_GROUP_SUCCESS.status,ResponseStatus.USER_CREATE_GROUP_SUCCESS.message);
    }

    public Response addGroupMember(GroupMemberDto groupMemberDto) {
        GroupMember groupMember = new GroupMember();
        groupMember.setUid(groupMemberDto.getUid());
        groupMember.setGid(groupMemberDto.getGid());
        imMybatisDao.addGroupMember(groupMember);
        return Response.success(ResponseStatus.ADD_GROUP_MEMBER_SUCCESS.status,ResponseStatus.ADD_GROUP_MEMBER_SUCCESS.message);
    }

    public Response removeGroupMember(long groupMemberId) {
        imMybatisDao.removeGroupMember(groupMemberId);
        return Response.success(ResponseStatus.REMOVE_GROUP_MEMBER_SUCCESS.status,ResponseStatus.REMOVE_GROUP_MEMBER_SUCCESS.message);
    }

    public Response getFriends(long uid) {
        FriendDto friendDto = new FriendDto();
        List<Friend> list = imMybatisDao.getFriends(uid);
        for(Friend friend : list){
            FriendDto.FriendElement friendElement = friendDto.createFriendElement();
            friendElement.setAvatar("");
            friendElement.setFid(friend.getFid());
            friendElement.setNickName("nickName");
            friendDto.getResult().add(friendElement);
        }
        return Response.success(friendDto);
    }

    public Response loadGroups(long uid) {
        ShowGroupDto showGroupDto = new ShowGroupDto();
        showGroupDto.setResult(imMybatisDao.getGroupList(uid));
        return Response.success(showGroupDto);
    }

    public Response loadGroupMembers(long gid) {
        ShowGroupMemberDto showGroupMemberDto = new ShowGroupMemberDto();
        showGroupMemberDto.setResult(imMybatisDao.getGroupMembersByGid(gid));
        return Response.success(showGroupMemberDto);
    }

    public Response search() {
        return null;
    }

    public Response modifyGroup() {
        return null;
    }
}
