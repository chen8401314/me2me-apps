package com.me2me.user.event;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Author: 马秀成
 * Date: 2017/3/9
 */
@Data
public class WapxIosEvent implements BaseEntity {

    private String idfa;

    private long uid;

    private int type;

    public WapxIosEvent(String idfa,long uid,int type){
        this.idfa = idfa;
        this.uid = uid;
        this.type = type;
    }

}
