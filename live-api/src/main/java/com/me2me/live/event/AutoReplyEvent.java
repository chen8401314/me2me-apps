package com.me2me.live.event;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2017/7/20.
 */
@Data
public class AutoReplyEvent implements BaseEntity {

    private long uid;

    private long topicId;

    private Date createTime;


    public AutoReplyEvent(long uid,long topicId,Date createTime){
        this.uid = uid;
        this.topicId = topicId;
        this.createTime = createTime;
    }

}
