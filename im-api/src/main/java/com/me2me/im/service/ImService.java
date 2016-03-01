package com.me2me.im.service;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/29.
 */
public interface ImService {


    /**
     * 添加好友接口
     */
    void addFriend();

    /**
     * 移除好友
     */
    void removeFriend();

    /**
     * 创建群组
     */
    void createGroup();


    /**
     * 添加群成员
     */
    void addGroupMember();

    /**
     * 移除群成员
     */
    void removeGroupMember();

    /**
     * 获取好友列表
     */
    void getFriends();

    /**
     * 获取群列表
     */
    void loadGroups();

    /**
     * 获取群成员列表
     */
    void loadGroupMembers();

    /**
     * 搜索接口支持（好友、群）
     */
    void search();

    /**
     * 修改群信息
     */
    void modifyGroup();

}
