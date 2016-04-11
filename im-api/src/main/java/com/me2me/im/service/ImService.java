package com.me2me.im.service;

import com.me2me.common.web.Response;
import com.me2me.im.dto.GroupDto;
import com.me2me.im.dto.GroupMemberDto;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/29.
 */
public interface ImService {


    /**
     * 添加好友接口
     */
    Response addFriend(long uid, long fid);

    /**
     * 移除好友
     */
    Response removeFriend(long uid,long fid);

    /**
     * 创建群组
     */
    Response createGroup(GroupDto groupDto);


    /**
     * 添加群成员
     */
    Response addGroupMember(GroupMemberDto groupMemberDto);

    /**
     * 移除群成员
     */
    Response removeGroupMember(long groupMemberId);

    /**
     * 获取好友列表
     */
    Response getFriends(long uid);

    /**
     * 获取群列表
     */
    Response loadGroups(long uid);

    /**
     * 获取群成员列表
     */
    Response loadGroupMembers(long gid);

    /**
     * 搜索接口支持（好友、群）
     */
    Response search();

    /**
     * 修改群信息
     */
    Response modifyGroup();

}
