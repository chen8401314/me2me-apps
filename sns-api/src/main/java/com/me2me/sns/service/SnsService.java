package com.me2me.sns.service;

import com.me2me.common.web.Response;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/27.
 */
public interface SnsService {


    /**
     * 获取成员列表
     * @return
     */
    Response showMemberConsole(long owner);


    /**
     * 邀请列表
     * @return
     */
    Response showMembers();


    /**
     * 修改社交关系
     * @return
     */
    Response modifyCircle();

}
