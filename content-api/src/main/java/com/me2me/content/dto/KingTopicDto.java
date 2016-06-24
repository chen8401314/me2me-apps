package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;


/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/23
 * Time :20:01
 */
@Data
public class KingTopicDto implements BaseEntity{

    private int likeCount;

    private int reviewCount;

    private String startDate;

    private String endDate;

    private long uid;

    private String nickName;
}
