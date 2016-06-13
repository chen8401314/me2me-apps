package com.me2me.monitor.event;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/6/13.
 */
@Data
public class MonitorEvent implements BaseEntity {

    public MonitorEvent(int type,int actionType,int channel,long uid){
        this.type = type;
        this.actionType = actionType;
        this.channel = channel;
        this.uid = uid;
    }

    private int type;

    private int actionType;

    private int channel;

    private long uid;


}
