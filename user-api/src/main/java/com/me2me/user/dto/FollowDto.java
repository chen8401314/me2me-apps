package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/4/13.
 */
@Data
public class FollowDto implements BaseEntity {

    /**
     * 0 关注 1 取消关注
     */
    private int action;

    /**
     * 关注者的UID 为当前用户
     */
    private long sourceUid;

    /**
     * 关注目标的UID
     */
    private long targetUid;




}
