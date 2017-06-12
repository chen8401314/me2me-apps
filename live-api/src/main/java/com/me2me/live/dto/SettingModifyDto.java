package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Author: 马秀成
 * Date: 2017/2/4
 */
@Data
public class SettingModifyDto implements BaseEntity {

    private int action;

    private String params;

    private long topicId;

    private long uid;

}
