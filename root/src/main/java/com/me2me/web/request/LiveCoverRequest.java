package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/13
 * Time :19:19
 */
public class LiveCoverRequest extends Request{

    @Getter
    @Setter
    private long topicId;
    @Getter
    @Setter
    private int source;//0APP内，1APP外
    @Getter
    @Setter
    private Long fromUid;	// 谁带进来的
}
