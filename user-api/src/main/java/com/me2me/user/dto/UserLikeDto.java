package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/12
 * Time :19:01
 */
@Data
public class UserLikeDto implements BaseEntity{

    private long tid;

    private long customerId;

    private long uid;
}
