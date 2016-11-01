package com.me2me.user.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Created by pc188 on 2016/11/1.
 */
@Data
public class GagDto implements BaseEntity{
    private long uid;

    private long targetUid;

    private int type;

    private long cid;

    private int gagLevel;
}
