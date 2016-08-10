package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/7/25
 * Time :13:39
 */
@Data
public class GetLiveTimeLineDto2  implements BaseEntity{

    private long topicId;

    private long sinceId;

    private long uid;

    private int direction;

    private int first;

    private int mode;

    private int forms;
}
