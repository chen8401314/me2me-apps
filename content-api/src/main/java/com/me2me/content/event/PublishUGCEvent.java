package com.me2me.content.event;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/13.
 */
@Data
public class PublishUGCEvent implements BaseEntity {

    public PublishUGCEvent(long cid){
        this.cid = cid;
    }
    private long cid;

}
