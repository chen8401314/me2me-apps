package com.me2me.im.dao;

import com.me2me.im.mapper.FriendMapper;
import com.me2me.im.mapper.GroupMapper;
import com.me2me.im.mapper.GroupMemberMapper;
import com.me2me.im.mapper.GroupProfileMapper;
import com.me2me.im.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/1.
 */
@Repository
public class ImMybatisDao {

    @Autowired
    private FriendMapper friendMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private GroupProfileMapper groupProfileMapper;
    @Autowired
    private GroupMemberMapper groupMemberMapper;

    public void addFriend(Friend friend){
        friendMapper.insertSelective(friend);
    }

    public Friend getFriendByUidAndFid(long uid,long fid){
        FriendExample example = new FriendExample();
        FriendExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        criteria.andFidEqualTo(fid);
        List<Friend> list =  friendMapper.selectByExample(example);
        return (list!=null&&list.size()>0)?list.get(0):null;
    }

    public void removeFriend(Friend friend){
        friendMapper.deleteByPrimaryKey(friend.getId());
    }

    public void createGroup(Group group){
        groupMapper.insertSelective(group);
    }

    public void createGroupProfile(GroupProfile groupProfile){
        groupProfileMapper.insertSelective(groupProfile);
    }

    public void addGroupMember(GroupMember groupMember){
        groupMemberMapper.insertSelective(groupMember);
    }

    public void removeGroupMember(long id){
        groupMemberMapper.deleteByPrimaryKey(id);
    }

    public List<Friend> getFriends(long uid) {
        FriendExample example = new FriendExample();
        FriendExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        return friendMapper.selectByExample(example);
    }

    public List<Group> getGroupList(long uid) {
        GroupMemberExample example = new GroupMemberExample();
        GroupMemberExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        List<GroupMember> list = groupMemberMapper.selectByExample(example);
        List<Long> gids = new ArrayList<Long>();
        for(GroupMember groupMember : list){
            gids.add(groupMember.getGid());
        }
        if(gids != null && gids.size() > 0) {
            GroupExample g = new GroupExample();
            GroupExample.Criteria c = g.createCriteria();
            c.andIdIn(gids);
            return groupMapper.selectByExample(g);
        }else{
            return null;
        }
    }

    public List<GroupMember> getGroupMembersByGid(long gid) {
        GroupMemberExample example = new GroupMemberExample();
        GroupMemberExample.Criteria criteria = example.createCriteria();
        criteria.andGidEqualTo(gid);
        return groupMemberMapper.selectByExample(example);
    }
}
