package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/28
 * Time :9:36
 */
@Data
public class MyPublishDto implements BaseEntity {


    private long sinceId;

    private long uid;

    private int type;

    private long updateTime;

    private int isOwner;
    
    private int flag;
}
