package com.me2me.live.event;

import com.me2me.common.web.BaseEntity;
import lombok.Data;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/8/25
 * Time :10:42
 */
@Data
public class SpeakEvent implements BaseEntity {

    private long topicId;

    private int type;

    private long uid;

    private String atUids;

    private long fragmentId;

}
