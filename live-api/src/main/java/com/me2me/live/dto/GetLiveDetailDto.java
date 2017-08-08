package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/11
 * Time :21:38
 */
@Data
public class GetLiveDetailDto implements BaseEntity {

    private long topicId;

    private int offset;

    private int pageNo;

    private long uid;

    private long sinceId;

    private int startIndex;
    
    private int direction;
    
    private int versionFlag;
    
    
    private int currentCount = 0;
}
