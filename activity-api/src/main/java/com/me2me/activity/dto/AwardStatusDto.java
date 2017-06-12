package com.me2me.activity.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Created by 马秀成 on 2016/10/26.
 */
@Data
public class AwardStatusDto implements BaseEntity {

    private int activityName;

    private String channel;

    private String version;
}
