package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 郭世同
 * Date: 2017/6/12 0012.
 */
@Data
public class RechargeToKingdomDto implements BaseEntity{


    private int amount;

    private long uid;

    private long topicId;

}
