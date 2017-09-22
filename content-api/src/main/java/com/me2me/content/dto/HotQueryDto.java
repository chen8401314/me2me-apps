package com.me2me.content.dto;

import com.me2me.common.web.BaseEntity;

import lombok.Data;

import java.util.List;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 郭世同
 * Date: 2017/7/9 0009.
 */
@Data
public class HotQueryDto implements BaseEntity {

    private long sinceId;

    private int type;
    
    private int start;

    private int pageSize;

    private List<String> ids;

    private List<Long> blacklistUids;
    
    private long uid;
    
    private String blackTagIds;

}
