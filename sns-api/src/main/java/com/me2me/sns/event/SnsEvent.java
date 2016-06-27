package com.me2me.sns.event;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/27
 * Time :15:35
 */
@Data
public class SnsEvent implements BaseEntity {

    private int action;

    private long owner;

    private long uid;

    public SnsEvent(int action ,long owner, long uid){
        this.uid = uid;
        this.action = action;
        this.owner = owner;
    }
}
