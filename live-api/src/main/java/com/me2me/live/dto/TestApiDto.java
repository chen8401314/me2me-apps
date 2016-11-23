package com.me2me.live.dto;

import com.me2me.common.web.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by 马秀成 on 2016/11/23.
 */
@Data
public class TestApiDto implements BaseEntity {

    private Long topicId;

    private Long uid;

    private String fragmentImage;

    private Integer type;

    private Integer contentType;

    private Long topId;

    private Long bottomId;

    private Date createTime = new Date();

    private Long atUid;

    private Integer source;

    private String extra;

    private Integer status;

    private String fragment;
}
