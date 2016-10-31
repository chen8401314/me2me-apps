package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * Created by pc188 on 2016/10/31.
 */
@Data
public class GetLiveUpdateDto implements BaseEntity{
    private long topicId;

    private long sinceId;

    private int offset;
}
