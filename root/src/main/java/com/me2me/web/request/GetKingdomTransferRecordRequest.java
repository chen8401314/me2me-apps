package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author:陈翔
 * Date: 2017/6/8
 * Time :15:51
 */
public class GetKingdomTransferRecordRequest extends Request {

    @Getter
    @Setter
    private long topicId;

    @Getter
    @Setter
    private int sinceId;

}
