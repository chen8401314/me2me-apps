package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Author: 马秀成
 * Date: 2017/2/5
 */
@Data
public class AggregationOptDto implements BaseEntity {

    private long uid;

    private int action;

    private long ceTopicId;

    private long acTopicId;

    private int type;

    private int applyId;

}
