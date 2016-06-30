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
    Response showMemberConsole(long owner,long topicId);


    /**
     * 邀请列表
     * @return
     */
    Response showMembers(long owner,long topicId ,long sinceId,int type);


    /**
     * 修改社交关系
     * @return
     */
    Response modifyCircle(long owner,long topicId,long memberUid,int action);

    Response getCircleByType(long owner, long topicId, long sinceId,int type);

    Response subscribed(long uid,long topicId,long topId,long bottomId,int action);

    Response follow(int action,long targetUid,long sourceUid);

}
