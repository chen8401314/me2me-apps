package com.me2me.web.request;

import com.me2me.common.web.Request;
import lombok.Getter;
import lombok.Setter;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/4/12
 * Time :12:05
 */
public class SpeakRequest extends Request{

    @Getter
    @Setter
    private long topicId;

    @Getter
    @Setter
    private int contentType;

    @Getter
    @Setter
    private int type;

    @Getter
    @Setter
    private String fragmentImage;

    @Getter
    @Setter
    private String fragment;

    @Getter
    @Setter
    private long topId;

    @Getter
    @Setter
    private long bottomId;

    @Getter
    @Setter
    private int isAt;

    @Getter
    @Setter
    private long atUid;


    @Getter
    @Setter
    private int mode;

    @Getter
    @Setter
    private int source;

    @Getter
    @Setter
    private String extra;
}
