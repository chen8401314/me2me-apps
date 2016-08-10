package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/23
 * Time :19:54
 */
public class TopicCountRequest extends Request {

    @Getter
    @Setter
    private int likeCount;

    @Getter
    @Setter
    private int reviewCount;

    @Getter
    @Setter
    private String startDate;

    @Getter
    @Setter
    private String endDate;

    @Getter
    @Setter
    private long kingUid;

    @Getter
    @Setter
    private String nickName;
}
