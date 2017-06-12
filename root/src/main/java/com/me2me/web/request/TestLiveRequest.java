package com.me2me.web.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by 马秀成 on 2016/11/23.
 */
public class TestLiveRequest {

    @Getter
    @Setter
    private Long topicId;

    @Getter
    @Setter
    private Long uid;

    @Getter
    @Setter
    private String fragmentImage;

    @Getter
    @Setter
    private Integer type;

    @Getter
    @Setter
    private Integer contentType;

    @Getter
    @Setter
    private Long topId;

    @Getter
    @Setter
    private Long bottomId;

    @Getter
    @Setter
    private Date createTime = new Date();

    @Getter
    @Setter
    private Long atUid;

    @Getter
    @Setter
    private Integer source;

    @Getter
    @Setter
    private String extra;

    @Getter
    @Setter
    private Integer status;

    @Getter
    @Setter
    private String fragment;

}
