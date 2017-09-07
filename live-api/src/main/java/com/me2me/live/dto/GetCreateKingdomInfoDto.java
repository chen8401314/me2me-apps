package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: chenxiang
 * Date: 2017/9/7
 * Time :18:23
 */
@Data
public class GetCreateKingdomInfoDto implements BaseEntity {

    private int needPrice;

    private int myPrice;

    private int createKingdomCount;

}
