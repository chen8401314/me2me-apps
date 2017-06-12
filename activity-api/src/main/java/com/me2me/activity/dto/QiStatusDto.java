package com.me2me.activity.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Created by 马秀成 on 2016/12/5.
 */
@Data
public class QiStatusDto implements BaseEntity {

    private int status;

    private long auid;

    private int isBind;//否 0 是 1

}
