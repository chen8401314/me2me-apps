package com.me2me.live.event;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/8/15
 * Time :16:58
 */
@Data
public class CacheLiveEvent implements BaseEntity{

    private long uid;

    private long topicId;

    public CacheLiveEvent(long uid,long topicId){
        this.uid = uid;
        this.topicId = topicId;
    }
}
