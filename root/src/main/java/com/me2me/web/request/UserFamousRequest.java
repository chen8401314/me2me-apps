package com.me2me.web.request;

import com.me2me.common.web.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Author: 马秀成
 * Date: 2017/3/6
 */
public class UserFamousRequest implements BaseEntity {

    @Getter
    @Setter
    private long uid;

    @Getter
    @Setter
    private long targetUid;

    @Getter
    @Setter
    private int action;

}
