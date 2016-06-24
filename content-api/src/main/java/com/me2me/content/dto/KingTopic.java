package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/23
 * Time :20:01
 */
@Data
public class KingTopic implements BaseEntity{

    private int likeCount;

    private int reviewCount;

    private Date startDate;

    private Date endDate;

    private long uid;

    private String nickName;
}
